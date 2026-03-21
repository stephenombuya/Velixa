/**
 * formatter.js
 * Date, text, and display formatting helpers.
 */

/**
 * Relative time: "2 minutes ago", "3 days ago", etc.
 */
export function timeAgo(isoString) {
    const date = new Date(isoString);
    const now  = new Date();
    const diff = Math.floor((now - date) / 1000);

    if (diff < 60)             return 'just now';
    if (diff < 3600)           return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400)          return `${Math.floor(diff / 3600)}h ago`;
    if (diff < 7 * 86400)      return `${Math.floor(diff / 86400)}d ago`;
    if (diff < 30 * 86400)     return `${Math.floor(diff / (7 * 86400))}w ago`;
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

export function formatDate(isoString, opts = {}) {
    if (!isoString) return '—';
    return new Date(isoString).toLocaleDateString('en-US', {
        month: 'short', day: 'numeric', year: 'numeric', ...opts,
    });
}

export function formatDateTime(isoString) {
    if (!isoString) return '—';
    return new Date(isoString).toLocaleString('en-US', {
        month: 'short', day: 'numeric', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

/** "john doe" → "John Doe" */
export function titleCase(str) {
    return String(str).replace(/\b\w/g, c => c.toUpperCase());
}

/** "My Workspace" → "my-workspace" */
export function slugify(str) {
    return String(str)
        .toLowerCase()
        .trim()
        .replace(/[^a-z0-9\s-]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-');
}

/** Generate initials from a name string: "John Doe" → "JD" */
export function initials(name) {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
}

/** Truncate text to maxLen characters with ellipsis */
export function truncate(text, maxLen = 80) {
    if (!text) return '';
    return text.length > maxLen ? text.slice(0, maxLen - 1) + '…' : text;
}

/** Format bytes: 1024 → "1.0 KB" */
export function formatBytes(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${(bytes / Math.pow(k, i)).toFixed(1)} ${sizes[i]}`;
}

/** ActivityType enum → human-readable string */
export function formatActivityType(type) {
    return (type || '').replace(/_/g, ' ').toLowerCase()
        .replace(/\b\w/g, c => c.toUpperCase());
}
