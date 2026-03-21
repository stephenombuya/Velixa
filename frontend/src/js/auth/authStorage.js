/**
 * authStorage.js
 * Manages JWT tokens in localStorage with helper accessors.
 */

const KEYS = {
    ACCESS:  'velixa_access_token',
    REFRESH: 'velixa_refresh_token',
    USER:    'velixa_user',
};

export function setTokens(accessToken, refreshToken) {
    localStorage.setItem(KEYS.ACCESS,  accessToken);
    localStorage.setItem(KEYS.REFRESH, refreshToken);
}

export function getAccessToken()  { return localStorage.getItem(KEYS.ACCESS);  }
export function getRefreshToken() { return localStorage.getItem(KEYS.REFRESH); }

export function setUser(user) {
    localStorage.setItem(KEYS.USER, JSON.stringify(user));
}

export function getUser() {
    const raw = localStorage.getItem(KEYS.USER);
    try { return raw ? JSON.parse(raw) : null; } catch { return null; }
}

export function clearTokens() {
    Object.values(KEYS).forEach(k => localStorage.removeItem(k));
}

export function isAuthenticated() {
    return !!getAccessToken();
}

/** Decode JWT payload without verifying signature (display only). */
export function decodeToken(token) {
    try {
        const payload = token.split('.')[1];
        return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    } catch {
        return null;
    }
}

export function isTokenExpired(token) {
    const decoded = decodeToken(token);
    if (!decoded?.exp) return true;
    return decoded.exp * 1000 < Date.now();
}
