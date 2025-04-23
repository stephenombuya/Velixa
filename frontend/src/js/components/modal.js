/**
 * Modal Component for Velixa
 * Creates and manages modal dialogs
 */

let modalCounter = 0;

/**
 * Create a modal dialog
 * @param {Object} options - Modal options
 * @param {string} options.title - Modal title
 * @param {string|HTMLElement} options.content - Modal content
 * @param {Object[]} options.buttons - Modal buttons
 * @param {string} options.size - Modal size (sm, md, lg)
 * @param {boolean} options.closable - Whether modal can be closed
 * @returns {Object} - Modal instance
 */
export const createModal = (options = {}) => {
    // Default options
    const settings = {
        id: `modal-${++modalCounter}`,
        title: 'Modal Title',
        content: '',
        buttons: [],
        size: 'md',
        closable: true,
        onClose: () => {},
        ...options
    };
    
    // Create modal container if it doesn't exist
    if (!document.getElementById('modal-container')) {
        const container = document.createElement('div');
        container.id = 'modal-container';
        document.body.appendChild(container);
    }
    
    // Create modal element
    const modalElement = document.createElement('div');
    modalElement.className = `modal ${settings.size}`;
    modalElement.id = settings.id;
    
    // Create modal content
    modalElement.innerHTML = `
        <div class="modal-overlay"></div>
        <div class="modal-dialog">
            <div class="modal-header">
                <h5 class="modal-title">${settings.title}</h5>
                ${settings.closable ? '<button class="modal-close">&times;</button>' : ''}
            </div>
            <div class="modal-body">
                ${typeof settings.content === 'string' ? settings.content : ''}
            </div>
            <div class="modal-footer">
                ${settings.buttons.map(btn => 
                    `<button class="btn ${btn.className || 'btn-secondary'}" 
                     id="${btn.id || `${settings.id}-btn-${btn.text.toLowerCase().replace(/\s+/g, '-')}`}">
                     ${btn.text}
                     </button>`
                ).join('')}
            </div>
        </div>
    `;
    
    // If content is an HTML element, append it to modal body
    if (settings.content instanceof HTMLElement) {
        const modalBody = modalElement.querySelector('.modal-body');
        modalBody.innerHTML = '';
        modalBody.appendChild(settings.content);
    }
    
    // Add modal to container
    document.getElementById('modal-container').appendChild(modalElement);
    
    // Add event listeners
    if (settings.closable) {
        // Close button
        const closeButton = modalElement.querySelector('.modal-close');
        if (closeButton) {
            closeButton.addEventListener('click', () => {
                closeModal(settings.id);
                settings.onClose();
            });
        }
        
        // Close on overlay click
        const overlay = modalElement.querySelector('.modal-overlay');
        if (overlay) {
            overlay.addEventListener('click', () => {
                closeModal(settings.id);
                settings.onClose();
            });
        }
        
        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && isModalOpen(settings.id)) {
                closeModal(settings.id);
                settings.onClose();
            }
        });
    }
    
    // Add button event listeners
    settings.buttons.forEach(btn => {
        const btnId = btn.id || `${settings.id}-btn-${btn.text.toLowerCase().replace(/\s+/g, '-')}`;
        const btnElement = modalElement.querySelector(`#${btnId}`);
        
        if (btnElement && btn.onClick) {
            btnElement.addEventListener('click', () => {
                btn.onClick();
            });
        }
    });
    
    // Methods for modal instance
    const modalInstance = {
        id: settings.id,
        
        /**
         * Open the modal
         */
        open: () => {
            openModal(settings.id);
            return modalInstance;
        },
        
        /**
         * Close the modal
         */
        close: () => {
            closeModal(settings.id);
            settings.onClose();
            return modalInstance;
        },
        
        /**
         * Destroy the modal
         */
        destroy: () => {
            destroyModal(settings.id);
            return null;
        },
        
        /**
         * Update modal content
         * @param {string|HTMLElement} content - New content
         */
        updateContent: (content) => {
            const modalBody = modalElement.querySelector('.modal-body');
            
            if (modalBody) {
                if (typeof content === 'string') {
                    modalBody.innerHTML = content;
                } else if (content instanceof HTMLElement) {
                    modalBody.innerHTML = '';
                    modalBody.appendChild(content);
                }
            }
            
            return modalInstance;
        },
        
        /**
         * Update modal title
         * @param {string} title - New title
         */
        updateTitle: (title) => {
            const titleElement = modalElement.querySelector('.modal-title');
            
            if (titleElement) {
                titleElement.textContent = title;
            }
            
            return modalInstance;
        }
    };
    
    return modalInstance;
};

/**
 * Open a modal dialog
 * @param {string} modalId - Modal ID to open
 */
export const openModal = (modalId) => {
    const modal = document.getElementById(modalId);
    
    if (modal) {
        // Add modal to DOM
        modal.style.display = 'block';
        
        // Prevent body scrolling
        document.body.classList.add('modal-open');
        
        // Animate in
        setTimeout(() => {
            modal.classList.add('modal-show');
        }, 10);
    }
};

/**
 * Close a modal dialog
 * @param {string} modalId - Modal ID to close
 */
export const closeModal = (modalId) => {
    const modal = document.getElementById(modalId);
    
    if (modal) {
        // Animate out
        modal.classList.remove('modal-show');
        
        // Remove from DOM after animation
        setTimeout(() => {
            modal.style.display = 'none';
            document.body.classList.remove('modal-open');
        }, 300); // Match CSS transition duration
    }
};

/**
 * Check if modal is open
 * @param {string} modalId - Modal ID to check
 * @returns {boolean} - True if modal is open
 */
export const isModalOpen = (modalId) => {
    const modal = document.getElementById(modalId);
    return modal && modal.classList.contains('modal-show');
};

/**
 * Destroy a modal dialog
 * @param {string} modalId - Modal ID to destroy
 */
export const destroyModal = (modalId) => {
    const modal = document.getElementById(modalId);
    
    if (modal) {
        // Close first
        closeModal(modalId);
        
        // Remove from DOM after closing animation
        setTimeout(() => {
            if (modal.parentNode) {
                modal.parentNode.removeChild(modal);
            }
        }, 300);
    }
};

/**
 * Create a confirmation modal
 * @param {Object} options - Modal options
 * @returns {Promise} - Resolves with true if confirmed, false if canceled
 */
export const confirm = (options = {}) => {
    return new Promise((resolve) => {
        const modal = createModal({
            title: options.title || 'Confirm',
            content: options.message || 'Are you sure?',
            size: options.size || 'sm',
            buttons: [
                {
                    text: options.confirmText || 'Confirm',
                    className: options.confirmClass || 'btn-primary',
                    onClick: () => {
                        modal.close();
                        setTimeout(() => modal.destroy(), 300);
                        resolve(true);
                    }
                },
                {
                    text: options.cancelText || 'Cancel',
                    className: options.cancelClass || 'btn-secondary',
                    onClick: () => {
                        modal.close();
                        setTimeout(() => modal.destroy(), 300);
                        resolve(false);
                    }
                }
            ],
            onClose: () => resolve(false),
            ...options
        });
        
        modal.open();
    });
};

/**
 * Create an alert modal
 * @param {Object} options - Modal options
 * @returns {Promise} - Resolves when alert is closed
 */
export const alert = (options = {}) => {
    return new Promise((resolve) => {
        const modal = createModal({
            title: options.title || 'Alert',
            content: options.message || '',
            size: options.size || 'sm',
            buttons: [
                {
                    text: options.okText || 'OK',
                    className: options.okClass || 'btn-primary',
                    onClick: () => {
                        modal.close();
                        setTimeout(() => modal.destroy(), 300);
                        resolve();
                    }
                }
            ],
            onClose: () => resolve(),
            ...options
        });
        
        modal.open();
    });
};

/**
 * Create a prompt modal
 * @param {Object} options - Modal options
 * @returns {Promise} - Resolves with input value if confirmed, null if canceled
 */
export const prompt = (options = {}) => {
    return new Promise((resolve) => {
        const inputId = `prompt-input-${modalCounter}`;
        const content = `
            <div class="form-group">
                <label for="${inputId}">${options.label || ''}</label>
                <input type="text" class="form-control" id="${inputId}" 
                       value="${options.defaultValue || ''}" 
                       placeholder="${options.placeholder || ''}">
            </div>
        `;
        
        const modal = createModal({
            title: options.title || 'Prompt',
            content: content,
            size: options.size || 'sm',
            buttons: [
                {
                    text: options.confirmText || 'OK',
                    className: options.confirmClass || 'btn-primary',
                    onClick: () => {
                        const input = document.getElementById(inputId);
                        const value = input ? input.value : '';
                        modal.close();
                        setTimeout(() => modal.destroy(), 300);
                        resolve(value);
                    }
                },
                {
                    text: options.cancelText || 'Cancel',
                    className: options.cancelClass || 'btn-secondary',
                    onClick: () => {
                        modal.close();
                        setTimeout(() => modal.destroy(), 300);
                        resolve(null);
                    }
                }
            ],
            onClose: () => resolve(null),
            ...options
        });
        
        modal.open();
        
        // Focus input
        setTimeout(() => {
            const input = document.getElementById(inputId);
            if (input) {
                input.focus();
                
                // Select all text
                if (options.defaultValue) {
                    input.select();
                }
            }
        }, 100);
    });
};
