/**
 * Authentication module for Velixa
 * Handles login, registration, and token management
 */

import { api } from '../api/apiClient.js';
import { setAuthToken, getAuthToken, removeAuthToken, setUserInfo, getUserInfo, removeUserInfo } from './authStorage.js';
import { showNotification } from '../components/notification.js';
import { loadDashboardPage } from '../pages/dashboard.js';
import { loadLoginPage } from '../pages/login.js';

/**
 * Check if user is authenticated
 * @returns {boolean} - True if user has valid token
 */
export const isAuthenticated = () => {
    const token = getAuthToken();
    return !!token; // Convert to boolean
};

/**
 * Login user
 * @param {Object} credentials - User credentials
 * @returns {Promise} - Login response
 */
export const login = async (credentials) => {
    try {
        const response = await api.post('/auth/login', credentials);
        
        if (response && response.token) {
            // Store token and user info
            setAuthToken(response.token);
            setUserInfo(response.user);
            
            // Show success notification
            showNotification({
                type: 'success',
                message: 'Login successful!'
            });
            
            // Trigger authentication event
            document.dispatchEvent(new CustomEvent('authStateChanged', { 
                detail: { isAuthenticated: true }
            }));
            
            // Redirect to dashboard
            loadDashboardPage();
            
            return response;
        }
        
        throw new Error('Invalid login response');
    } catch (error) {
        console.error('Login error:', error);
        
        showNotification({
            type: 'error',
            message: error.message || 'Login failed. Please check your credentials.'
        });
        
        throw error;
    }
};

/**
 * Register new user
 * @param {Object} userData - User registration data
 * @returns {Promise} - Registration response
 */
export const register = async (userData) => {
    try {
        const response = await api.post('/auth/register', userData);
        
        showNotification({
            type: 'success',
            message: 'Registration successful! Please log in.'
        });
        
        // Redirect to login
        loadLoginPage();
        
        return response;
    } catch (error) {
        console.error('Registration error:', error);
        
        showNotification({
            type: 'error',
            message: error.message || 'Registration failed. Please try again.'
        });
        
        throw error;
    }
};

/**
 * Logout user
 */
export const logout = () => {
    try {
        // Clear auth data
        removeAuthToken();
        removeUserInfo();
        
        // Trigger authentication event
        document.dispatchEvent(new CustomEvent('authStateChanged', { 
            detail: { isAuthenticated: false }
        }));
        
        // Show notification
        showNotification({
            type: 'info',
            message: 'You have been logged out.'
        });
        
        // Redirect to home/login
        loadLoginPage();
    } catch (error) {
        console.error('Logout error:', error);
    }
};

/**
 * Get current user profile
 * @returns {Promise} - User profile data
 */
export const getCurrentUser = async () => {
    try {
        // First check local storage
        const storedUser = getUserInfo();
        if (storedUser) {
            return storedUser;
        }
        
        // If not available, fetch from API
        const user = await api.get('/auth/profile');
        setUserInfo(user);
        return user;
    } catch (error) {
        console.error('Get current user error:', error);
        return null;
    }
};

/**
 * Update user profile
 * @param {Object} profileData - Profile data to update
 * @returns {Promise} - Updated profile
 */
export const updateProfile = async (profileData) => {
    try {
        const response = await api.put('/auth/profile', profileData);
        
        // Update stored user info
        setUserInfo(response);
        
        showNotification({
            type: 'success',
            message: 'Profile updated successfully!'
        });
        
        return response;
    } catch (error) {
        console.error('Update profile error:', error);
        
        showNotification({
            type: 'error',
            message: error.message || 'Failed to update profile.'
        });
        
        throw error;
    }
};

/**
 * Initialize authentication listeners
 */
export const initAuthListeners = () => {
    // Handle expired tokens
    document.addEventListener('apiUnauthorized', () => {
        // If token is invalid or expired, logout user
        removeAuthToken();
        removeUserInfo();
        
        showNotification({
            type: 'warning',
            message: 'Your session has expired. Please log in again.'
        });
        
        loadLoginPage();
    });
    
    // Handle auth state change
    document.addEventListener('authStateChanged', (event) => {
        const { isAuthenticated } = event.detail;
        updateNavbarAuthState(isAuthenticated);
    });
    
    // Check initial auth state
    updateNavbarAuthState(isAuthenticated());
};

/**
 * Update navbar based on authentication state
 * @param {boolean} isAuth - Is user authenticated
 */
const updateNavbarAuthState = (isAuth) => {
    const authLinks = document.querySelectorAll('.auth-link');
    const noAuthLinks = document.querySelectorAll('.no-auth-link');
    
    if (isAuth) {
        authLinks.forEach(link => link.style.display = 'block');
        noAuthLinks.forEach(link => link.style.display = 'none');
    } else {
        authLinks.forEach(link => link.style.display = 'none');
        noAuthLinks.forEach(link => link.style.display = 'block');
    }
};
