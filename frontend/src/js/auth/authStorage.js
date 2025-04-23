/**
 * Authentication Storage Helper
 * Manages auth tokens and user info in localStorage
 */

// Storage keys
const TOKEN_KEY = 'velixa_auth_token';
const USER_INFO_KEY = 'velixa_user_info';

/**
 * Get authentication token from storage
 * @returns {string|null} - Auth token or null if not found
 */
export const getAuthToken = () => {
    return localStorage.getItem(TOKEN_KEY);
};

/**
 * Set authentication token in storage
 * @param {string} token - JWT token
 */
export const setAuthToken = (token) => {
    localStorage.setItem(TOKEN_KEY, token);
};

/**
 * Remove authentication token from storage
 */
export const removeAuthToken = () => {
    localStorage.removeItem(TOKEN_KEY);
};

/**
 * Get user information from storage
 * @returns {Object|null} - User info object or null if not found
 */
export const getUserInfo = () => {
    const userInfo = localStorage.getItem(USER_INFO_KEY);
    return userInfo ? JSON.parse(userInfo) : null;
};

/**
 * Set user information in storage
 * @param {Object} userInfo - User information object
 */
export const setUserInfo = (userInfo) => {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
};

/**
 * Remove user information from storage
 */
export const removeUserInfo = () => {
    localStorage.removeItem(USER_INFO_KEY);
};

/**
 * Clear all authentication data from storage
 */
export const clearAuthData = () => {
    removeAuthToken();
    removeUserInfo();
};
