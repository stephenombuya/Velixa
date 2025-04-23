/**
 * Notification System for Velixa
 * Displays toast notifications for user feedback
 */

// Default notification settings
const DEFAULT_SETTINGS = {
    duration: 5000, // 5 seconds
    position: 'top-right',
    closable: true
};

/**
 * Initialize notification system
 */
export const initNotificationSystem = () => {
    // Create notification container if it doesn't exist
    if (!document.getElementById('notification-container')) {
        const container = document.createElement('div');
        container.id = 'notification-container';
        container.className = 'notification-container';
        document.body.appendChild(container);
    }
};

/**
 * Show a notification
 * @param {Object} options - Notification options
 * @param {string} options.type - Notification type (success, error, warning, info)
 * @param {string} options.message - Notification message
 * @param {number} options.duration - Display duration in ms
 * @param {boolean} options.closable - Whether notification has close button
 */
export const showNotification = (options) => {
    const settings = { ...DEFAULT_SETTINGS, ...options };
    const container = document.getElementById('notification-container');
    
    if (!container) {
        console.error('Notification container not found');
        return;
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${settings.type || 'info'}`;
    
    // Create message element
    const message = document.createElement('div');
    message.className = 'notification-message';
    message.textContent = settings.message;
    notification.appendChild(message);
    
    // Add close button if closable
    if (settings.closable) {
        const closeButton = document.createElement('button');
        closeButton.className = 'notification-close';
        closeButton.innerHTML = '&times;';
        closeButton.addEventListener('click', () => {
            closeNotification(notification);
        });
        notification.appendChild(closeButton);
    }
    
    // Add to container
    container.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.classList.add('notification-show');
    }, 10);
    
    // Auto close after duration
    if (settings.duration > 0) {
        setTimeout(() => {
            closeNotification(notification);
        }, settings.duration);
    }
    
    return notification;
};

/**
 * Close a notification
 * @param {HTMLElement} notification - Notification element to close
 */
const closeNotification = (notification) => {
    // Animate out
    notification.classList.remove('notification-show');
    notification.classList.add('notification-hide');
    
    // Remove from DOM after animation
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 300); // Match CSS animation duration
};

/**
 * Show a success notification
 * @param {string} message - Notification message
 * @param {Object} options - Additional options
 */
export const showSuccess = (message, options = {}) => {
    return showNotification({
        type: 'success',
        message,
        ...options
    });
};

/**
 * Show an error notification
 * @param {string} message - Notification message
 * @param {Object} options - Additional options
 */
export const showError = (message, options = {}) => {
    return showNotification({
        type: 'error',
        message,
        ...options
    });
};

/**
 * Show a warning notification
 * @param {string} message - Notification message
 * @param {Object} options - Additional options
 */
export const showWarning = (message, options = {}) => {
    return showNotification({
        type: 'warning',
        message,
        ...options
    });
};

/**
 * Show an info notification
 * @param {string} message - Notification message
 * @param {Object} options - Additional options
 */
export const showInfo = (message, options = {}) => {
    return showNotification({
        type: 'info',
        message,
        ...options
    });
};
