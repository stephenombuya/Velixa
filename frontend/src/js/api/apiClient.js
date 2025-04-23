/**
 * API Client for Velixa
 * Handles API requests to the backend services
 */

import { getAuthToken } from '../auth/authStorage.js';
import { showNotification } from '../components/notification.js';

// API Configuration
const API_CONFIG = {
    baseUrl: 'http://localhost:8080/api', // API Gateway URL
    timeout: 30000, // 30 seconds timeout
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
};

/**
 * Fetch with timeout and error handling
 * @param {string} url - API endpoint
 * @param {Object} options - Fetch options
 * @returns {Promise} - Response promise
 */
const fetchWithTimeout = async (url, options = {}) => {
    const controller = new AbortController();
    const { signal } = controller;
    
    const timeout = setTimeout(() => {
        controller.abort();
    }, API_CONFIG.timeout);
    
    try {
        const response = await fetch(url, {
            ...options,
            signal
        });
        
        clearTimeout(timeout);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({
                message: 'An unknown error occurred'
            }));
            
            throw {
                status: response.status,
                message: errorData.message || `Error: ${response.status} ${response.statusText}`,
                data: errorData
            };
        }
        
        // Return null for 204 No Content responses
        if (response.status === 204) {
            return null;
        }
        
        return await response.json();
    } catch (error) {
        clearTimeout(timeout);
        
        if (error.name === 'AbortError') {
            throw {
                status: 0,
                message: 'Request timeout, please try again'
            };
        }
        
        throw error;
    }
};

/**
 * Add authentication header to request if token exists
 * @param {Object} headers - Request headers
 * @returns {Object} - Headers with auth token if available
 */
const addAuthHeader = (headers = {}) => {
    const token = getAuthToken();
    
    if (token) {
        return {
            ...headers,
            'Authorization': `Bearer ${token}`
        };
    }
    
    return headers;
};

/**
 * Make API request
 * @param {string} endpoint - API endpoint
 * @param {Object} options - Request options
 * @returns {Promise} - Response promise
 */
export const apiRequest = async (endpoint, options = {}) => {
    const url = `${API_CONFIG.baseUrl}${endpoint}`;
    
    const requestOptions = {
        ...options,
        headers: {
            ...API_CONFIG.headers,
            ...options.headers,
            ...addAuthHeader(options.headers)
        }
    };
    
    try {
        return await fetchWithTimeout(url, requestOptions);
    } catch (error) {
        // Handle authentication errors
        if (error.status === 401) {
            // Clear session and redirect to login
            // This is handled by auth interceptor
        }
        
        console.error('API Request Error:', error);
        
        // Show error notification
        showNotification({
            type: 'error',
            message: error.message || 'An error occurred while processing your request'
        });
        
        throw error;
    }
};

/**
 * API Client methods
 */
export const api = {
    /**
     * GET request
     * @param {string} endpoint - API endpoint
     * @param {Object} options - Additional options
     * @returns {Promise} - Response promise
     */
    get: (endpoint, options = {}) => {
        return apiRequest(endpoint, {
            method: 'GET',
            ...options
        });
    },
    
    /**
     * POST request
     * @param {string} endpoint - API endpoint
     * @param {Object} data - Request payload
     * @param {Object} options - Additional options
     * @returns {Promise} - Response promise
     */
    post: (endpoint, data = {}, options = {}) => {
        return apiRequest(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
            ...options
        });
    },
    
    /**
     * PUT request
     * @param {string} endpoint - API endpoint
     * @param {Object} data - Request payload
     * @param {Object} options - Additional options
     * @returns {Promise} - Response promise
     */
    put: (endpoint, data = {}, options = {}) => {
        return apiRequest(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data),
            ...options
        });
    },
    
    /**
     * PATCH request
     * @param {string} endpoint - API endpoint
     * @param {Object} data - Request payload
     * @param {Object} options - Additional options
     * @returns {Promise} - Response promise
     */
    patch: (endpoint, data = {}, options = {}) => {
        return apiRequest(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data),
            ...options
        });
    },
    
    /**
     * DELETE request
     * @param {string} endpoint - API endpoint
     * @param {Object} options - Additional options
     * @returns {Promise} - Response promise
     */
    delete: (endpoint, options = {}) => {
        return apiRequest(endpoint, {
            method: 'DELETE',
            ...options
        });
    }
};

/**
 * Setup API interceptors
 */
export const setupApiInterceptors = () => {
    // Intercept 401 Unauthorized responses
    document.addEventListener('apiUnauthorized', () => {
        // Clear auth tokens and redirect to login
        // This would be implemented in auth.js
    });
};

// Initialize API interceptors
setupApiInterceptors();
