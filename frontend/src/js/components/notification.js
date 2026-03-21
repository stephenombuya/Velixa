/**
 * notification.js
 * Toast notification system — injected once, called anywhere.
 */

let container = null;

function ensureContainer() {
    if (container) return;
    container = document.createElement('div');
    container.id = 'notification-container';
    container.style.cssText = `
        position: fixed;
        top: 1.5rem;
        right: 1.5rem;
        z-index: 9999;
        display: flex;
        flex-direction: column;
        gap: 0.6rem;
        pointer-events: none;
        max-width: 360px;
    `;
    document.body.appendChild(container);
}

/**
 * @param {string} message
 * @param {'success'|'error'|'warning'|'info'} type
 * @param {number} duration  ms
 */
export function showNotification(message, type = 'info', duration = 4000) {
    ensureContainer();

    const icons = {
        success: `<svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>`,
        error:   `<svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/></svg>`,
        warning: `<svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>`,
        info:    `<svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/></svg>`,
    };

    const colors = {
        success: { bg: '#d1fae5', border: '#6ee7b7', text: '#065f46', icon: '#059669' },
        error:   { bg: '#fee2e2', border: '#fca5a5', text: '#7f1d1d', icon: '#dc2626' },
        warning: { bg: '#fef3c7', border: '#fcd34d', text: '#78350f', icon: '#d97706' },
        info:    { bg: '#dbeafe', border: '#93c5fd', text: '#1e3a5f', icon: '#2563eb' },
    };

    const c = colors[type] || colors.info;

    const toast = document.createElement('div');
    toast.style.cssText = `
        display: flex;
        align-items: flex-start;
        gap: 0.7rem;
        background: ${c.bg};
        border: 1px solid ${c.border};
        border-radius: 10px;
        padding: 0.85rem 1rem;
        box-shadow: 0 4px 24px rgba(0,0,0,0.12);
        pointer-events: all;
        cursor: pointer;
        animation: slideInRight 0.3s cubic-bezier(0.16,1,0.3,1) forwards;
        transition: opacity 0.3s;
    `;

    toast.innerHTML = `
        <span style="flex-shrink:0;width:1.2rem;height:1.2rem;color:${c.icon}">${icons[type]}</span>
        <span style="font-size:0.875rem;color:${c.text};line-height:1.4;font-family:'Inter',sans-serif">${message}</span>
    `;

    // Dismiss on click
    toast.addEventListener('click', () => dismiss(toast));

    container.appendChild(toast);

    // Auto-dismiss
    const timer = setTimeout(() => dismiss(toast), duration);
    toast.dataset.timer = timer;

    return toast;
}

function dismiss(toast) {
    clearTimeout(Number(toast.dataset.timer));
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
}

// Inject keyframe if not present
if (!document.getElementById('toast-styles')) {
    const style = document.createElement('style');
    style.id = 'toast-styles';
    style.textContent = `
        @keyframes slideInRight {
            from { opacity: 0; transform: translateX(120%); }
            to   { opacity: 1; transform: translateX(0); }
        }
    `;
    document.head.appendChild(style);
}
