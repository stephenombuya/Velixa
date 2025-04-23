/**
 * Form Validation Utilities for Velixa
 */

/**
 * Validate a form against validation rules
 * @param {Object} formData - Form data object
 * @param {Object} rules - Validation rules
 * @returns {Object} - Validation result with errors
 */
export const validateForm = (formData, rules) => {
    const errors = {};
    let isValid = true;
    
    // Validate each field
    Object.keys(rules).forEach(field => {
        const value = formData[field];
        const fieldRules = rules[field];
        
        // Check required
        if (fieldRules.required && isEmpty(value)) {
            errors[field] = 'This field is required';
            isValid = false;
            return; // Skip other validations for this field
        }
        
        // Skip remaining validations if field is empty and not required
        if (isEmpty(value) && !fieldRules.required) {
            return;
        }
        
        // Check minimum length
        if (fieldRules.minLength && value.length < fieldRules.minLength) {
            errors[field] = `Minimum length is ${fieldRules.minLength} characters`;
            isValid = false;
        }
        
        // Check maximum length
        if (fieldRules.maxLength && value.length > fieldRules.maxLength) {
            errors[field] = `Maximum length is ${fieldRules.maxLength} characters`;
            isValid = false;
        }
        
        // Check pattern/regex
        if (fieldRules.pattern && !fieldRules.pattern.test(value)) {
            errors[field] = fieldRules.patternMessage || 'Invalid format';
            isValid = false;
        }
        
        // Check email format
        if (fieldRules.email && !isValidEmail(value)) {
            errors[field] = 'Please enter a valid email address';
            isValid = false;
        }
        
        // Check if field matches another field (e.g., password confirmation)
        if (fieldRules.matches && value !== formData[fieldRules.matches]) {
            errors[field] = fieldRules.matchesMessage || `Must match ${fieldRules.matches}`;
            isValid = false;
        }
        
        // Check minimum value (for numbers)
        if (fieldRules.min !== undefined && Number(value) < fieldRules.min) {
            errors[field] = `Minimum value is ${fieldRules.min}`;
            isValid = false;
        }
        
        // Check maximum value (for numbers)
        if (fieldRules.max !== undefined && Number(value) > fieldRules.max) {
            errors[field] = `Maximum value is ${fieldRules.max}`;
            isValid = false;
        }
        
        // Custom validation function
        if (fieldRules.validate && typeof fieldRules.validate === 'function') {
            const customValidation = fieldRules.validate(value, formData);
            if (customValidation !== true) {
                errors[field] = customValidation || 'Invalid value';
                isValid = false;
            }
        }
    });
    
    return {
        isValid,
        errors
    };
};

/**
 * Check if a value is empty
 * @param {*} value - Value to check
 * @returns {boolean} - True if value is empty
 */
export const isEmpty = (value) => {
    return (
        value === undefined ||
        value === null ||
        (typeof value === 'string' && value.trim() === '') ||
        (Array.isArray(value) && value.length === 0)
    );
};

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} - True if email is valid
 */
export const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

/**
 * Validate password strength
 * @param {string} password - Password to validate
 * @returns {Object} - Validation result with score and message
 */
export const validatePasswordStrength = (password) => {
    let score = 0;
    let message = '';
    
    // Length check
    if (password.length < 6) {
        return {
            score: 0,
            message: 'Password is too short (minimum 6 characters)'
        };
    } else if (password.length >= 8) {
        score += 1;
    }
    
    // Contains lowercase letter
    if (/[a-z]/.test(password)) {
        score += 1;
    }
    
    // Contains uppercase letter
    if (/[A-Z]/.test(password)) {
        score += 1;
    }
    
    // Contains number
    if (/\d/.test(password)) {
        score += 1;
    }
    
    // Contains special character
    if (/[^A-Za-z0-9]/.test(password)) {
        score += 1;
    }
    
    // Determine message based on score
    if (score < 2) {
        message = 'Weak password';
    } else if (score < 4) {
        message = 'Moderate password';
    } else {
        message = 'Strong password';
    }
    
    return {
        score,
        message
    };
};
