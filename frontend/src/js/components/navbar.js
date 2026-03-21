/**
 * navbar.js
 * Renders the sidebar shell and top-bar into any app page.
 * Call: initShell() at the top of each protected page script.
 */

import { getUser, isAuthenticated, clearTokens } from '../auth/authStorage.js';
import { logout } from '../auth/auth.js';
import { initials } from '../utils/formatter.js';

const NAV_LINKS = [
  {
    section: 'Main',
    items: [
      { href: 'dashboard.html',  label: 'Dashboard',   icon: gridIcon() },
      { href: 'workspaces.html', label: 'Workspaces',  icon: workspaceIcon() },
      { href: 'activity.html',   label: 'Activity',    icon: activityIcon() },
    ],
  },
  {
    section: 'Account',
    items: [
      { href: 'profile.html', label: 'Profile',  icon: userIcon() },
      { href: 'settings.html',label: 'Settings', icon: settingsIcon() },
    ],
  },
];

export function initShell() {
  if (!isAuthenticated()) {
    window.location.href = 'login.html';
    return;
  }
  renderSidebar();
  renderTopbar();
  highlightActive();
  wireLogout();
  wireMobileToggle();
}

function renderSidebar() {
  const sidebar = document.getElementById('sidebar');
  if (!sidebar) return;

  const user = getUser();
  const avatarBg = userColorFromId(user?.id || '');

  const linksHtml = NAV_LINKS.map(group => `
    <div class="sidebar-section">
      <span class="sidebar-section-label">${group.section}</span>
      ${group.items.map(item => `
        <a href="${item.href}" class="sidebar-link" data-href="${item.href}">
          <span class="sidebar-link-icon">${item.icon}</span>
          ${item.label}
        </a>
      `).join('')}
    </div>
  `).join('');

  sidebar.innerHTML = `
    <a href="dashboard.html" class="sidebar-logo">
      <div class="sidebar-logo-mark">V</div>
      <span class="sidebar-logo-name">Velixa</span>
    </a>
    ${linksHtml}
    <div class="sidebar-footer">
      <a href="profile.html" class="sidebar-user">
        <div class="avatar avatar-md" style="background:${avatarBg};color:#fff">
          ${user?.avatarUrl
            ? `<img src="${user.avatarUrl}" alt="${user.firstName}" />`
            : initials(`${user?.firstName || ''} ${user?.lastName || ''}`)}
        </div>
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">${user?.firstName || ''} ${user?.lastName || ''}</div>
          <div class="sidebar-user-role">${user?.email || ''}</div>
        </div>
      </a>
    </div>
  `;
}

function renderTopbar() {
  const topbar = document.getElementById('topbar');
  if (!topbar) return;

  const user = getUser();
  const avatarBg = userColorFromId(user?.id || '');

  topbar.innerHTML = `
    <div class="topbar-left">
      <button class="topbar-icon-btn" id="mobileMenuBtn" aria-label="Toggle menu" style="display:none">
        ${menuIcon()}
      </button>
      <div class="topbar-search">
        <svg class="topbar-search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
        </svg>
        <input type="search" placeholder="Search workspaces, activity…" id="globalSearch" />
      </div>
    </div>
    <div class="topbar-right">
      <button class="topbar-icon-btn" title="Notifications">
        ${bellIcon()}
        <span class="notif-dot"></span>
      </button>
      <div class="dropdown">
        <button class="topbar-icon-btn" id="userMenuBtn" title="Account">
          <div class="avatar avatar-sm" style="background:${avatarBg};color:#fff;font-size:0.65rem">
            ${user?.avatarUrl
              ? `<img src="${user.avatarUrl}" alt="${user.firstName}" />`
              : initials(`${user?.firstName || ''} ${user?.lastName || ''}`)}
          </div>
        </button>
        <div class="dropdown-menu hidden" id="userMenu">
          <div style="padding:0.75rem 1rem;border-bottom:1px solid var(--clr-border)">
            <div style="font-size:0.875rem;font-weight:500;color:var(--clr-text)">${user?.firstName} ${user?.lastName}</div>
            <div style="font-size:0.75rem;color:var(--clr-text-faint)">${user?.email}</div>
          </div>
          <a href="profile.html" class="dropdown-item">${userIcon(14)} Profile</a>
          <a href="settings.html" class="dropdown-item">${settingsIcon(14)} Settings</a>
          <div class="dropdown-divider"></div>
          <button class="dropdown-item danger" id="logoutBtn">${logoutIcon(14)} Sign out</button>
        </div>
      </div>
    </div>
  `;
}

function highlightActive() {
  const current = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.sidebar-link[data-href]').forEach(link => {
    if (link.dataset.href === current) link.classList.add('active');
  });
}

function wireLogout() {
  document.addEventListener('click', (e) => {
    if (e.target.closest('#logoutBtn')) logout();

    // Toggle dropdown
    const userMenuBtn = document.getElementById('userMenuBtn');
    const userMenu    = document.getElementById('userMenu');
    if (userMenu) {
      if (e.target.closest('#userMenuBtn')) {
        userMenu.classList.toggle('hidden');
      } else if (!e.target.closest('#userMenu')) {
        userMenu.classList.add('hidden');
      }
    }
  });
}

function wireMobileToggle() {
  const btn     = document.getElementById('mobileMenuBtn');
  const sidebar = document.getElementById('sidebar');
  if (!btn || !sidebar) return;

  // Show mobile button on small screens
  const mq = window.matchMedia('(max-width: 768px)');
  const update = (m) => { btn.style.display = m.matches ? 'flex' : 'none'; };
  update(mq);
  mq.addEventListener('change', update);

  btn.addEventListener('click', () => sidebar.classList.toggle('open'));

  // Close on backdrop click
  document.addEventListener('click', (e) => {
    if (mq.matches && !e.target.closest('#sidebar') && !e.target.closest('#mobileMenuBtn')) {
      sidebar.classList.remove('open');
    }
  });
}

// ── Avatar colour helper ────────────────────────────────────
function userColorFromId(id) {
  const colours = ['#6c63ff','#ec4899','#f59e0b','#10b981','#3b82f6','#8b5cf6','#ef4444','#06b6d4'];
  let hash = 0;
  for (let i = 0; i < id.length; i++) hash = id.charCodeAt(i) + ((hash << 5) - hash);
  return colours[Math.abs(hash) % colours.length];
}

// ── SVG icon helpers ────────────────────────────────────────
function gridIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`;
}
function workspaceIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`;
}
function activityIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>`;
}
function userIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`;
}
function settingsIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>`;
}
function bellIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>`;
}
function logoutIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`;
}
function menuIcon(s=16) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>`;
}
