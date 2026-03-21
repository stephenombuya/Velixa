// mongo-init.js
// Runs once on first container startup to create the database,
// application user, and required indexes.

db = db.getSiblingDB('velixadb');

// ── Users collection ────────────────────────────────────────
db.createCollection('users');
db.users.createIndex({ email: 1 },    { unique: true, name: 'users_email_unique' });
db.users.createIndex({ username: 1 }, { unique: true, name: 'users_username_unique' });
db.users.createIndex({ createdAt: -1 }, { name: 'users_created_desc' });

// ── Refresh tokens ──────────────────────────────────────────
db.createCollection('refresh_tokens');
db.refresh_tokens.createIndex({ token: 1 },  { unique: true, name: 'rt_token_unique' });
db.refresh_tokens.createIndex({ userId: 1 }, { name: 'rt_user_id' });
db.refresh_tokens.createIndex(
  { expiresAt: 1 },
  { expireAfterSeconds: 0, name: 'rt_ttl' }   // auto-remove expired tokens
);

// ── Workspaces ──────────────────────────────────────────────
db.createCollection('workspaces');
db.workspaces.createIndex({ slug: 1 },           { unique: true, name: 'ws_slug_unique' });
db.workspaces.createIndex({ ownerId: 1 },        { name: 'ws_owner' });
db.workspaces.createIndex({ 'members.userId': 1 }, { name: 'ws_member_user' });
db.workspaces.createIndex({ createdAt: -1 },     { name: 'ws_created_desc' });

// ── Activities ──────────────────────────────────────────────
db.createCollection('activities');
db.activities.createIndex(
  { workspaceId: 1, createdAt: -1 },
  { name: 'act_workspace_ts' }
);
db.activities.createIndex(
  { actorId: 1, createdAt: -1 },
  { name: 'act_actor_ts' }
);
db.activities.createIndex({ createdAt: -1 }, { name: 'act_created_desc' });

print('✅  velixadb initialised with collections and indexes.');
