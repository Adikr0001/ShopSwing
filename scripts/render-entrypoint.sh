#!/bin/sh
set -e
# Render sets PORT (often 10000). Default matches Render docs when unset (local docker).
PORT="${PORT:-10000}"
sed -i "s/port=\"8080\"/port=\"${PORT}\"/" "$CATALINA_HOME/conf/server.xml"
exec catalina.sh run
