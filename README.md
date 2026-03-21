# Velixa

> **Modern microservices workspace platform** — Spring Boot · MongoDB · Redis · Vanilla JS SPA

Velixa is a production-grade microservices application demonstrating enterprise Java patterns: JWT authentication with token rotation, role-based access control, API Gateway rate-limiting, service discovery, and a dark-themed frontend SPA — all containerised with Docker Compose.

---

## Architecture Overview

```
┌──────────────┐        ┌─────────────────────────────────────────┐
│   Browser    │        │               Docker Network              │
│  (port 3000) │        │                                           │
└──────┬───────┘        │  ┌──────────┐     ┌─────────────────┐   │
       │ HTTP           │  │  Nginx   │────▶│   API Gateway   │   │
       └────────────────┼─▶│  :3000   │     │     :8080       │   │
                        │  └──────────┘     └────────┬────────┘   │
                        │                            │ lb://       │
                        │              ┌─────────────┼──────────┐  │
                        │              ▼             ▼          ▼  │
                        │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │
                        │  │Auth Service  │ │  Workspace   │ │  Activity    │ │
                        │  │   :8081      │ │  Service     │ │  Service     │ │
                        │  │              │ │   :8082      │ │   :8083      │ │
                        │  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ │
                        │         └─────────────────┴────────────────┘        │
                        │                           │                          │
                        │              ┌────────────▼─────────────┐            │
                        │              │         MongoDB           │            │
                        │              │          :27017           │            │
                        │              └───────────────────────────┘            │
                        │                                                       │
                        │  ┌──────────────────┐   ┌──────────────────┐         │
                        │  │ Service Registry │   │   Config Server  │         │
                        │  │ (Eureka) :8761   │   │     :8888        │         │
                        │  └──────────────────┘   └──────────────────┘         │
                        │                                                       │
                        │  ┌──────────────────┐                                 │
                        │  │   Redis :6379    │  (rate limiting + token cache)  │
                        │  └──────────────────┘                                 │
                        └───────────────────────────────────────────────────────┘
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2, Spring Cloud 2023 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway (WebFlux) |
| **Database** | MongoDB 7 |
| **Cache / Rate Limit** | Redis 7 |
| **Auth** | JWT (jjwt 0.12), BCrypt |
| **API Docs** | SpringDoc OpenAPI 3 |
| **Frontend** | HTML5, CSS3 (custom design system), ES Modules |
| **Containerisation** | Docker, Docker Compose |
| **Build** | Maven 3.8+ |

---

## Quick Start

### Prerequisites

- Docker Desktop ≥ 24 and Docker Compose v2
- (Optional for local dev) Java 17+, Maven 3.8+

### 1. Clone and configure

```bash
git clone https://github.com/stephenombuya/Velixa
cd Velixa
cp .env.example .env
# Edit .env — at minimum set a strong JWT_SECRET:
#   openssl rand -hex 32
```

### 2. Start the full stack

```bash
docker compose up -d
```

Docker Compose starts services in dependency order (MongoDB and Redis first, then Service Registry, then Config Server, then the three domain services, then the Gateway). Allow ~60–90 seconds for all health checks to pass.

### 3. Access the application

| Service | URL |
|---|---|
| **Frontend** | http://localhost:3000 |
| **API Gateway** | http://localhost:8080 |
| **Eureka Dashboard** | http://localhost:8761 (user: `eureka` / pass: `eureka_secret`) |
| **Auth Swagger UI** | http://localhost:8081/swagger-ui.html |
| **Workspace Swagger UI** | http://localhost:8082/swagger-ui.html |
| **Activity Swagger UI** | http://localhost:8083/swagger-ui.html |

---

## Project Structure

```
Velixa/
├── docker-compose.yml
├── .env.example
├── scripts/
│   └── mongo-init.js               # DB bootstrap (indexes, collections)
├── backend/
│   ├── service-registry/           # Eureka Server (port 8761)
│   ├── config-server/              # Spring Cloud Config (port 8888)
│   ├── api-gateway/                # Spring Cloud Gateway (port 8080)
│   │   ├── filter/AuthenticationFilter.java  # JWT → X-User-* headers
│   │   └── config/GatewayConfig.java         # Rate limiter key resolvers
│   ├── auth-service/               # JWT auth (port 8081)
│   │   ├── model/       User, RefreshToken, Role
│   │   ├── service/     AuthService, UserDetailsServiceImpl
│   │   ├── util/        JwtUtil (HS256, sign/verify/extract)
│   │   └── controller/  AuthController (register, login, refresh, logout, /me)
│   ├── workspace-service/          # Team management (port 8082)
│   │   ├── model/       Workspace, WorkspaceMember, WorkspaceRole, WorkspaceVisibility
│   │   ├── service/     WorkspaceService (CRUD, role hierarchy, soft-delete)
│   │   └── controller/  WorkspaceController (10 endpoints + member management)
│   └── activity-service/           # Audit log (port 8083)
│       ├── model/       Activity, ActivityType (15 event types)
│       ├── service/     ActivityService (paginated feed, stats)
│       └── controller/  ActivityController (7 endpoints)
└── frontend/
    ├── nginx.conf                  # SPA serving + /api/ proxy
    ├── public/
    │   ├── index.html → login.html
    │   ├── login.html
    │   ├── register.html
    │   ├── dashboard.html
    │   ├── workspaces.html
    │   └── profile.html
    └── src/
        ├── css/
        │   ├── main.css            # Full design system (variables, resets, components)
        │   └── components/
        │       ├── auth.css        # Login / register layout
        │       └── dashboard.css   # App shell, sidebar, cards
        └── js/
            ├── api/
            │   ├── apiClient.js           # fetch wrapper, JWT injection, auto-refresh
            │   └── endpoints/
            │       └── workspaceEndpoints.js
            ├── auth/
            │   ├── auth.js               # login, register, logout, guards
            │   └── authStorage.js        # localStorage tokens + decode helpers
            ├── components/
            │   ├── navbar.js             # Sidebar + topbar renderer
            │   └── notification.js       # Toast notifications
            └── utils/
                ├── validator.js          # Schema-based form validation
                └── formatter.js          # Dates, slugs, initials, time-ago
```

---

## API Reference

### Auth Service  `/api/v1/auth`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register` | Public | Create account |
| POST | `/login` | Public | Get access + refresh tokens |
| POST | `/refresh` | Public | Rotate refresh token |
| POST | `/logout` | Bearer | Revoke refresh tokens |
| GET  | `/validate` | Header | Validate token (gateway use) |
| GET  | `/me` | Bearer | Get current user profile |
| PATCH | `/me` | Bearer | Update profile |
| POST | `/me/change-password` | Bearer | Change password |

### Workspace Service  `/api/v1/workspaces`

| Method | Path | Role required | Description |
|---|---|---|---|
| GET    | `/`                           | Any member | List accessible workspaces |
| POST   | `/`                           | Authenticated | Create workspace |
| GET    | `/{id}`                       | Member | Get workspace detail |
| GET    | `/slug/{slug}`                | Member | Get by slug |
| PATCH  | `/{id}`                       | ADMIN+ | Update workspace |
| DELETE | `/{id}`                       | OWNER | Soft-delete workspace |
| POST   | `/{id}/members`               | ADMIN+ | Invite member |
| PATCH  | `/{id}/members/{uid}`         | OWNER | Update member role |
| DELETE | `/{id}/members/{uid}`         | ADMIN+ | Remove member |

### Activity Service  `/api/v1/activities`

| Method | Path | Description |
|---|---|---|
| POST   | `/`                                    | Record activity (internal) |
| GET    | `/{id}`                                | Get single activity |
| GET    | `/workspace/{wid}?page=0&size=20`      | Paginated workspace feed |
| GET    | `/workspace/{wid}/type/{type}`         | Feed filtered by type |
| GET    | `/user/{uid}`                          | User's activity history |
| GET    | `/workspace/{wid}/stats`               | Activity statistics |
| DELETE | `/workspace/{wid}`                     | Purge workspace activities |

---

## Security Model

```
Client → API Gateway (JWT validation + X-User-* header injection)
       → Service (trusts X-User-* headers, applies role checks)
```

- **JWT** tokens are signed with HS256. Expiry: 24h (access), 7d (refresh).  
- **Token rotation**: every refresh call issues a new refresh token and revokes the old one.  
- **Rate limiting** on public endpoints (10 req/s per IP) and authenticated routes (50 req/s per user) using Redis-backed Spring Cloud Gateway `RequestRateLimiter`.  
- **HTTPS**: configure a reverse proxy (e.g. Nginx, Cloudflare) in front of the Gateway for production TLS termination.  
- **Non-root containers**: all service Dockerfiles create a `velixa` user.

---

## Running Tests

```bash
# Auth service (18 tests)
cd backend/auth-service && mvn test

# Workspace service (9 tests)
cd backend/workspace-service && mvn test

# Activity service
cd backend/activity-service && mvn test

# All services from root (requires Maven wrapper or mvn in PATH)
find backend -name pom.xml -not -path '*/target/*' \
  -exec sh -c 'cd $(dirname {}) && mvn test -q' \;
```

---

## Local Development (without Docker)

```bash
# 1. Start infrastructure only
docker compose up -d mongodb redis

# 2. Start Service Registry
cd backend/service-registry && mvn spring-boot:run

# 3. Start each service in its own terminal
cd backend/auth-service      && mvn spring-boot:run
cd backend/workspace-service && mvn spring-boot:run
cd backend/activity-service  && mvn spring-boot:run

# 4. Start API Gateway
cd backend/api-gateway        && mvn spring-boot:run

# 5. Serve frontend
cd frontend && npx live-server public --port=3000
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit with conventional commits: `git commit -m 'feat: add X'`
4. Push and open a Pull Request

---

## License

GNU General Public License v3.0 — see [LICENSE](LICENSE) for details.
