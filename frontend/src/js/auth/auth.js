/**
 * auth.js
 * Authentication actions wiring the API and storage layers.
 */

import api from '../api/apiClient.js';
import { setTokens, setUser, clearTokens, isAuthenticated, isTokenExpired, getAccessToken } from './authStorage.js';

export async function login(email, password) {
    const data = await api.post('/auth/login', { email, password }, false);
    setTokens(data.accessToken, data.refreshToken);
    setUser(data.user);
    return data.user;
}

export async function register(firstName, lastName, username, email, password) {
    const data = await api.post('/auth/register',
        { firstName, lastName, username, email, password }, false);
    setTokens(data.accessToken, data.refreshToken);
    setUser(data.user);
    return data.user;
}

export function logout() {
    // Best-effort server-side revocation (fire-and-forget)
    api.post('/auth/logout', {}, true).catch(() => {});
    clearTokens();
    window.location.href = '/login.html';
}

export async function getProfile() {
    const data = await api.get('/auth/me');
    setUser(data);
    return data;
}

export async function updateProfile(payload) {
    const data = await api.patch('/auth/me', payload);
    setUser(data);
    return data;
}

export async function changePassword(currentPassword, newPassword) {
    return api.post('/auth/me/change-password', { currentPassword, newPassword });
}

/**
 * Guard: redirect to login if not authenticated or token expired.
 * Call at the top of every protected page.
 */
export function requireAuth() {
    const token = getAccessToken();
    if (!isAuthenticated() || isTokenExpired(token)) {
        clearTokens();
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

/**
 * Guard: redirect to dashboard if already authenticated.
 * Call on login/register pages.
 */
export function redirectIfAuthenticated() {
    if (isAuthenticated() && !isTokenExpired(getAccessToken())) {
        window.location.href = '/dashboard.html';
        return true;
    }
    return false;
}
