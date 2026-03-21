/**
 * validator.js
 * Lightweight form validation helpers.
 */

export const rules = {
    required: (v) => (v && String(v).trim().length > 0) || 'This field is required',
    email:    (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)         || 'Enter a valid email address',
    minLen:   (n) => (v) => String(v).length >= n                  || `Minimum ${n} characters`,
    maxLen:   (n) => (v) => String(v).length <= n                  || `Maximum ${n} characters`,
    slug:     (v) => /^[a-z0-9-]+$/.test(v)                       || 'Only lowercase letters, numbers, and hyphens',
    noSpaces: (v) => !/\s/.test(v)                                 || 'No spaces allowed',
    match:    (other, label) => (v) => v === other                 || `Must match ${label}`,
};

/**
 * Validate a value against an array of rule functions.
 * Returns the first error string or null.
 */
export function validate(value, ruleList = []) {
    for (const rule of ruleList) {
        const result = rule(value);
        if (result !== true) return result;
    }
    return null;
}

/**
 * Apply visual error feedback to an input element.
 */
export function setFieldError(inputEl, message) {
    inputEl.classList.add('input-error');
    let err = inputEl.parentElement.querySelector('.field-error');
    if (!err) {
        err = document.createElement('span');
        err.className = 'field-error';
        inputEl.parentElement.appendChild(err);
    }
    err.textContent = message;
}

export function clearFieldError(inputEl) {
    inputEl.classList.remove('input-error');
    const err = inputEl.parentElement.querySelector('.field-error');
    if (err) err.remove();
}

/**
 * Validate an entire form described as:
 * { fieldName: { el: HTMLInputElement, rules: [rule, ...] } }
 * Returns { valid: boolean, errors: { fieldName: string } }
 */
export function validateForm(schema) {
    const errors = {};
    for (const [field, { el, rules: fieldRules }] of Object.entries(schema)) {
        const error = validate(el.value, fieldRules);
        if (error) {
            errors[field] = error;
            setFieldError(el, error);
        } else {
            clearFieldError(el);
        }
    }
    return { valid: Object.keys(errors).length === 0, errors };
}
