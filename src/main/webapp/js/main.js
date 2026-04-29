/**
 * ShopSwing E-Commerce — Main JavaScript
 * Client-side utilities and interactions
 */

// ============================================================
// Toast Notification System
// ============================================================
function showToast(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed; bottom: 20px; right: 20px; z-index: 9999;
        padding: 12px 24px; border-radius: 8px; font-size: 14px;
        font-weight: 600; color: #fff; opacity: 0;
        transform: translateY(20px); transition: all 0.3s ease;
        max-width: 400px; box-shadow: 0 8px 32px rgba(0,0,0,0.4);
    `;

    const colors = {
        info: '#4F8CFF',
        success: '#34D399',
        error: '#F85C5C',
        warning: '#FBBF24'
    };
    toast.style.background = colors[type] || colors.info;
    if (type === 'warning') toast.style.color = '#000';

    document.body.appendChild(toast);

    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    });

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// ============================================================
// Form Validation Helpers
// ============================================================
function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validateRequired(fields) {
    for (const f of fields) {
        if (!f.value || !f.value.trim()) {
            f.focus();
            f.style.borderColor = '#F85C5C';
            setTimeout(() => f.style.borderColor = '', 2000);
            return false;
        }
    }
    return true;
}

// ============================================================
// Confirm Dialog
// ============================================================
function confirmAction(message, callback) {
    if (confirm(message)) callback();
}

// ============================================================
// Initialize on DOM ready
// ============================================================
document.addEventListener('DOMContentLoaded', function () {
    // Auto-hide messages after 5 seconds
    document.querySelectorAll('.error-msg, .success-msg').forEach(function (el) {
        if (el.textContent.trim()) {
            setTimeout(function () {
                el.style.opacity = '0';
                el.style.transition = 'opacity 0.5s ease';
            }, 5000);
        }
    });
});
