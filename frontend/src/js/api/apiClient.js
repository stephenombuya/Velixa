/**
 * apiClient.js
 * Central HTTP client with JWT injection, auto-refresh, and error normalisation.
 */

import { getAccessToken, getRefreshToken, setTokens, clearTokens } from '../auth/authStorage.js';
import { showNotification } from '../components/notification.js';

const BASE_URL = '/api/v1';

let isRefreshing = false;
let refreshSubscribers = [];

function onRefreshed(token) {
    refreshSubscribers.forEach(cb => cb(token));
    refreshSubscribers = [];
}

function addRefreshSubscriber(cb) {
    refreshSubscribers.push(cb);
}

async function refreshAccessToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        clearTokens();
        window.location.href = '/login.html';
        return null;
    }

    const res = await fetch(`${BASE_URL}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
    });

    if (!res.ok) {
        clearTokens();
        window.location.href = '/login.html';
        return null;
    }

    const data = await res.json();
    setTokens(data.accessToken, data.refreshToken);
    return data.accessToken;
}

/**
 * Core request function.
 * @param {string} path  - e.g. '/workspaces'
 * @param {object} opts  - fetch options
 * @param {boolean} auth - whether to attach Authorization header (default true)
 */
export async function request(path, opts = {}, auth = true) {
    const url = `${BASE_URL}${path}`;
    const headers = {
        'Content-Type': 'application/json',
        ...(opts.headers || {}),
    };

    if (auth) {
        const token = getAccessToken();
        if (token) headers['Authorization'] = `Bearer ${token}`;
    }

    let response = await fetch(url, { ...opts, headers });

    // Handle 401 with token refresh
    if (response.status === 401 && auth) {
        if (isRefreshing) {
            return new Promise(resolve => {
                addRefreshSubscriber(newToken => {
                    headers['Authorization'] = `Bearer ${newToken}`;
                    resolve(fetch(url, { ...opts, headers }));
                });
            });
        }

        isRefreshing = true;
        const newToken = await refreshAccessToken();
        isRefreshing = false;

        if (!newToken) return response;

        onRefreshed(newToken);
        headers['Authorization'] = `Bearer ${newToken}`;
        response = await fetch(url, { ...opts, headers });
    }

    return response;
}

/**
 * Parses JSON or returns null; throws normalised Error on non-ok.
 */
async function handleResponse(res) {
    let body = null;
    const contentType = res.headers.get('Content-Type') || '';
    if (contentType.includes('application/json')) {
        body = await res.json();
    }

    if (!res.ok) {
        const msg = body?.message || `HTTP ${res.status}: ${res.statusText}`;
        throw Object.assign(new Error(msg), {
            status: res.status,
            errorCode: body?.errorCode,
            fieldErrors: body?.fieldErrors,
        });
    }

    return body;
}

export const api = {
    get:    (path, auth)         => request(path, { method: 'GET' }, auth).then(handleResponse),
    post:   (path, body, auth)   => request(path, { method: 'POST',  body: JSON.stringify(body) }, auth).then(handleResponse),
    put:    (path, body, auth)   => request(path, { method: 'PUT',   body: JSON.stringify(body) }, auth).then(handleResponse),
    patch:  (path, body, auth)   => request(path, { method: 'PATCH', body: JSON.stringify(body) }, auth).then(handleResponse),
    delete: (path, auth)         => request(path, { method: 'DELETE' }, auth).then(handleResponse),
};

export default api;
