// Main JavaScript entry point for Velixa application

import { initNavbar } from './js/components/navbar.js';
import { initFooter } from './templates/footer.js';
import { initAuthListeners } from './js/auth/auth.js';
import { loadHomePage } from './js/pages/home.js';
import { setupRouting } from './js/utils/router.js';
import { initNotificationSystem } from './js/components/notification.js';

// Initialize app components
document.addEventListener('DOMContentLoaded', () => {
    console.log('Initializing Velixa application...');
    
    // Initialize core UI components
    initNavbar();
    initFooter();
    initNotificationSystem();
    
    // Initialize authentication listeners
    initAuthListeners();
    
    // Setup client-side routing
    setupRouting();
    
    // Load default page
    loadHomePage();
    
    console.log('Velixa application initialized');
});
