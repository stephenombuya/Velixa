/**
 * Login Page Component for Velixa
 */

import { login } from '../auth/auth.js';
import { validateForm } from '../utils/validator.js';

/**
 * Load the login page
 */
export const loadLoginPage = () => {
    const contentElement = document.getElementById('page-content');
    
    if (!contentElement) {
        console.error('Content element not found');
        return;
    }
    
    // Update page title
    document.title = 'Login - Velixa';
    
    // Render login page
    contentElement.innerHTML = createLoginPageHTML();
    
    // Add form event listener
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginFormSubmit);
    }
};

/**
 * Create login page HTML
 * @returns {string} - Login page HTML
 */
const createLoginPageHTML = () => {
    return `
        <div class="auth-container">
            <div class="auth-card">
                <div class="auth-header">
                    <h2>Login to Velixa</h2>
                    <p>Enter your credentials to access your account</p>
                </div>
                
                <form id="login-form" class="auth-form">
                    <div class="form-group">
                        <label for="username">Username or Email</label>
                        <input type="text" id="username" name="username" class="form-control" required>
                        <div class="form-error" id="username-error"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="password-input-container">
                            <input type="password" id="password" name="password" class="form-control" required>
                            <button type="button" class="password-toggle" tabindex="-1">
                                <span class="show-icon">👁️</span>
                            </button>
                        </div>
                        <div class="form-error" id="password-error"></div>
                    </div>
                    
                    <div class="form-group form-check">
                        <input type="checkbox" id="remember-me" name="remember" class="form-check-input">
                        <label for="remember-me" class="form-check-label">Remember me</label>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block">Login</button>
                    </div>
                    
                    <div class="form-error" id="form-error"></div>
                </form>
                
                <div class="auth-footer">
                    <p>Don't have an account? <a href="#" data-page="register" class="register-link">Register</a></p>
                </div>
            </div>
        </div>
    `;
};

/**
 * Handle login form submission
 * @param {Event} event - Form submit event
 */
const handleLoginFormSubmit = async (event) => {
    event.preventDefault();
    
    // Get form data
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('remember-me').checked;
    
    // Validate form
    const validationRules = {
        username: { required: true },
        password: { required: true, minLength: 6 }
    };
    
    const formData = { username, password };
    const validationResult = validateForm(formData, validationRules);
    
    if (!validationResult.isValid) {
        // Display validation errors
        Object.keys(validationResult.errors).forEach(field => {
            const errorElement = document.getElementById(`${field}-error`);
            if (errorElement) {
                errorElement.textContent = validationResult.errors[field];
            }
        });
        return;
    }
    
    // Clear previous errors
    clearFormErrors();
    
    // Show loading state
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Logging in...';
    
    try {
        // Call login API
        await login({ username, password, rememberMe });
        
        // Login successful - the auth module will handle redirection
    } catch (error) {
        // Display error message
        const formError = document.getElementById('form-error');
        if (formError) {
            formError.textContent = error.message || 'Login failed. Please check your credentials.';
        }
        
        // Reset button
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    }
};

/**
 * Clear all form error messages
 */
const clearFormErrors = () => {
    const errorElements = document.querySelectorAll('.form-error');
    errorElements.forEach(element => {
        element.textContent = '';
    });
};

// Initialize event listeners when the module is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Handle register link click
    document.addEventListener('click', (event) => {
        if (event.target.matches('.register-link, .register-link *')) {
            event.preventDefault();
            // Navigate to register page
            const navigateEvent = new CustomEvent('navigate', {
                detail: { page: 'register' }
            });
            document.dispatchEvent(navigateEvent);
        }
    });
    
    // Handle password visibility toggle
    document.addEventListener('click', (event) => {
        if (event.target.matches('.password-toggle, .password-toggle *')) {
            const passwordInput = event.target.closest('.password-input-container').querySelector('input');
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
        }
    });
});
