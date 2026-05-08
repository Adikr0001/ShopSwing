from __future__ import annotations

import mimetypes
import re
import sqlite3
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
POPULATE_DB = ROOT / "src" / "main" / "java" / "com" / "shopswing" / "utils" / "PopulateDB.java"
IMAGES_DIR = ROOT / "src" / "main" / "webapp" / "images" / "products"
SQLITE_DB = Path.home() / "shopswing.db"


def extract_update_block(text: str) -> tuple[str, int, int]:
    start_marker = "Object[][] imageUpdates = {"
    start = text.index(start_marker)
    end = text.index("        };", start) + len("        };")
    return text[start:end], start, end


def choose_extension(url: str, content_type: str | None) -> str:
    if content_type:
        content_type = content_type.split(";")[0].strip().lower()
        guessed = mimetypes.guess_extension(content_type)
        if guessed in {".jpg", ".jpeg", ".png", ".webp", ".gif"}:
            return ".jpg" if guessed == ".jpeg" else guessed

    parsed = urllib.parse.urlparse(url)
    ext = Path(parsed.path).suffix.lower()
    if ext in {".jpg", ".jpeg", ".png", ".webp", ".gif"}:
        return ".jpg" if ext == ".jpeg" else ext

    return ".jpg"


def download_image(url: str, target: Path) -> None:
    request = urllib.request.Request(
        url,
        headers={
            "User-Agent": "Mozilla/5.0",
            "Accept": "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
            "Referer": urllib.parse.urlunparse(urllib.parse.urlparse(url)._replace(params="", query="", fragment="")),
        },
    )
    with urllib.request.urlopen(request, timeout=30) as response:
        content = response.read()
        if not content:
            raise ValueError("empty response")
        target.write_bytes(content)


def main() -> int:
    text = POPULATE_DB.read_text(encoding="utf-8")
    block, start, end = extract_update_block(text)

    entries = re.findall(r'\{\s*(\d+),\s*"([^"]+)"\s*\}', block)
    if not entries:
        print("No product image entries found.")
        return 1

    IMAGES_DIR.mkdir(parents=True, exist_ok=True)

    replacements: dict[str, str] = {}
    failures: list[tuple[str, str, str]] = []

    for product_id, url in entries:
        if not url.startswith("http"):
            replacements[product_id] = url
            continue

        try:
            request = urllib.request.Request(
                url,
                headers={
                    "User-Agent": "Mozilla/5.0",
                    "Accept": "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
                },
            )
            with urllib.request.urlopen(request, timeout=30) as response:
                content_type = response.headers.get("Content-Type")
                ext = choose_extension(url, content_type)
                local_rel = f"images/products/product_{product_id}{ext}"
                local_abs = ROOT / "src" / "main" / "webapp" / local_rel.replace("/", "\\")
                local_abs.parent.mkdir(parents=True, exist_ok=True)
                local_abs.write_bytes(response.read())
                if local_abs.stat().st_size == 0:
                    raise ValueError("downloaded empty file")
                replacements[product_id] = local_rel
                print(f"Downloaded {product_id} -> {local_rel}")
        except Exception as exc:  # noqa: BLE001
            failures.append((product_id, url, str(exc)))
            print(f"Failed {product_id}: {exc}")

    def repl(match: re.Match[str]) -> str:
        product_id = match.group(1)
        old_url = match.group(2)
        new_path = replacements.get(product_id)
        if not new_path:
            return match.group(0)
        return '{' + product_id + ', "' + new_path + '"}'

    updated_block = re.sub(r'\{\s*(\d+),\s*"([^"]+)"\s*\}', repl, block)
    if updated_block != block:
        POPULATE_DB.write_text(text[:start] + updated_block + text[end:], encoding="utf-8")
        print(f"Updated {POPULATE_DB.name} with {len(replacements)} local image paths.")

    if SQLITE_DB.exists():
        with sqlite3.connect(SQLITE_DB) as conn:
            for product_id, local_path in replacements.items():
                conn.execute(
                    "UPDATE products SET image_url = ? WHERE id = ?",
                    (local_path, int(product_id)),
                )
            conn.commit()
        print(f"Updated database image paths in {SQLITE_DB}.")
    else:
        print(f"Database not found at {SQLITE_DB}, skipped DB update.")

    print(f"Downloaded/localized {len(replacements)} images.")
    if failures:
        print(f"{len(failures)} downloads failed:")
        for product_id, url, error in failures:
            print(f"  {product_id}: {error} -> {url}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
