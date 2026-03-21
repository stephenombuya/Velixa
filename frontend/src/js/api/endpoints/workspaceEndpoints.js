/**
 * workspaceEndpoints.js
 * All workspace-related API calls.
 */

import api from '../apiClient.js';

export const workspaceApi = {
    list: ()                            => api.get('/workspaces'),
    getById: (id)                       => api.get(`/workspaces/${id}`),
    getBySlug: (slug)                   => api.get(`/workspaces/slug/${slug}`),
    create: (payload)                   => api.post('/workspaces', payload),
    update: (id, payload)               => api.patch(`/workspaces/${id}`, payload),
    delete: (id)                        => api.delete(`/workspaces/${id}`),

    // Members
    inviteMember: (id, payload)         => api.post(`/workspaces/${id}/members`, payload),
    updateMemberRole: (id, uid, role)   => api.patch(`/workspaces/${id}/members/${uid}`, { role }),
    removeMember: (id, uid)             => api.delete(`/workspaces/${id}/members/${uid}`),
};
