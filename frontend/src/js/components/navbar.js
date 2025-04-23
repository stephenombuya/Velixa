/**
 * Navbar Component for Velixa
 */

import { isAuthenticated, logout } from '../auth/auth.js';
import { getUserInfo } from '../auth/authStorage.js';

/**
 * Initialize the navbar component
 */
export const initNavbar = () => {
    const navbarElement = document.getElementById('main-nav');
    if (!navbarElement) return;
    
    // Render navbar HTML
    navbarElement.innerHTML = createNavbarHTML();
    
    // Attach event listeners
    attachNavbarEventListeners();
    
    // Update navbar based on auth state
    updateNavbarAuthState(isAuthenticated());
};

/**
 * Create the navbar HTML
 * @returns {string} - Navbar HTML string
 */
const createNavbarHTML = () => {
    return `
        <div class="navbar">
            <div class="navbar-brand">
                <a href="#" data-page="home">
                    <img src="assets/images/logo.svg" alt="Velixa Logo" class="navbar-logo" onerror="this.src='assets/images/logo-placeholder.png'">
                    <span>Velixa</span>
                </a>
            </div>
            <div class="navbar-menu">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a href="#" class="nav-link" data-page="home">Home</a>
                    </li>
                    <li class="nav-item auth-link" style="display: none;">
                        <a href="#" class="nav-link" data-page="dashboard">Dashboard</a>
                    </li>
                    <li class="nav-item auth-link" style="display: none;">
                        <a href="#" class="nav-link" data-page="profile">Profile</a>
                    </li>
                    <li class="nav-item no-auth-link">
                        <a href="#" class="nav-link" data-page="login">Login</a>
                    </li>
                    <li class="nav-item no-auth-link">
                        <a href="#" class="nav-link" data-page="register">Register</a>
                    </li>
                    <li class="nav-item auth-link" style="display: none;">
                        <a href="#" class="nav-link" id="logout-button">Logout</a>
                    </li>
                </ul>
            </div>
            <div class="navbar-user auth-link" style="display: none;">
                <div class="user-avatar">
                    <span id="user-initial"></span>
                </div>
                <span id="user-name">User</span>
            </div>
            <div class="navbar-toggle">
                <button class="navbar-toggler">
                    <span class="navbar-toggler-icon">☰</span>
                </button>
            </div>
        </div>
    `;
};

/**
 * Attach event listeners to navbar elements
 */
const attachNavbarEventListeners = () => {
    // Handle nav link clicks
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        const page = link.getAttribute('data-page');
        if (page) {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                navigateToPage(page);
            });
        }
    });
