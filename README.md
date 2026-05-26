# ♠ Planning Poker — Real-Time Agile Estimation

> A full-stack real-time Planning Poker application built from scratch with Java Spring Boot and Angular, featuring WebSocket-based live collaboration, a poker-themed UI, and a complete CI/CD pipeline.

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Angular](https://img.shields.io/badge/Angular_20-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 📸 Overview

Planning Poker is a tool used in agile teams to estimate user stories collaboratively. This implementation runs entirely in real time — every vote, reveal, and round restart is instantly reflected for all participants, with no page refresh required.

The project was built as a learning exercise to explore WebSocket communication, domain-driven design, and modern full-stack development practices.

---

## ✨ Features

- **Create or join rooms** via shareable room code
- **Real-time voting** — all participants see updates instantly via WebSocket/STOMP
- **Fibonacci deck** — cards animate like a real poker hand
- **Simultaneous card reveal** — no one sees results before the moderator reveals
- **Average calculation** — automatically computed on reveal
- **Session persistence** — reconnect to your session after a page refresh
- **Moderator controls** — start voting, reveal cards, restart rounds
- **Poker-themed UI** — felt table, playing cards, gold typography

---

## 🏗️ Architecture

```
┌─────────────────┐        HTTP REST          ┌──────────────────────┐
│                 │ ──── POST /room ────────▶ │                      │
│  Angular 20     │                           │  Spring Boot 3       │
│  Frontend       │◀─── WebSocket/STOMP ────▶│  Backend             │
│  :4200          │   /topic/room/{code}/*    │  :8080               │
│                 │                           │                      │
└─────────────────┘                           └──────────┬───────────┘
                                                         │ JPA/Hibernate
                                                         ▼
                                              ┌──────────────────────┐
                                              │   PostgreSQL         │
                                              │   planning_poker     │
                                              └──────────────────────┘
```

### WebSocket Message Flow

```
Client ──── /app/room/{code}/vote ────▶ Server
                                           │
                                           ▼
Client ◀─── /topic/room/{code}/votes ──── Server (broadcast to all)
```

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 21 | Language |
| Spring Boot 3 | Application framework |
| Spring WebSocket + STOMP | Real-time communication |
| Spring Data JPA + Hibernate | ORM and database access |
| PostgreSQL | Relational database |
| Lombok | Boilerplate reduction |
| JUnit 5 + Mockito | Unit testing |

### Frontend
| Technology | Purpose |
|---|---|
| Angular 20 | SPA framework |
| @stomp/rx-stomp | WebSocket/STOMP client |
| RxJS | Reactive programming |
| TypeScript | Language |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker + Docker Compose | Containerization |
| GitHub Actions | CI/CD pipeline |
| GitHub Container Registry | Docker image registry |

---

## 📁 Project Structure

```
planning-poker/
├── planning-poker-backend/
│   └── src/
│       ├── main/java/com/agile/planning_poker/
│       │   ├── room/           # Room domain (entity, service, repository, controller)
│       │   ├── participant/    # Participant domain
│       │   ├── userstory/      # User Story domain
│       │   ├── vote/           # Vote domain + business logic
│       │   ├── websocket/      # WebSocket config, controller, DTOs
│       │   └── exception/      # Custom exceptions + global handler
│       └── test/               # Unit tests (JUnit 5 + Mockito)
│
├── planning-poker-frontend/
│   └── src/app/
│       ├── components/
│       │   ├── home/           # Landing page (create/join room)
│       │   └── room/           # Main room (voting, stories, results)
│       └── services/
│           ├── room.service.ts     # Room operations (REST + WebSocket)
│           └── websocket.service.ts # WebSocket connection management
│
├── docker-compose.yml
├── .env.example
└── .github/workflows/
    ├── ci.yml      # Runs on every push (test + build)
    └── release.yml # Runs on version tags (build images + create release)
```

---

## 🚀 Getting Started

### Prerequisites
- Docker and Docker Compose

### Running with Docker Compose

```bash
# Clone the repository
git clone https://github.com/feelipemarques/planning-poker.git
cd planning-poker

# Configure environment variables
cp .env.example .env
# Edit .env with your values

# Start all services
docker compose up --build
```

Access the app at `http://localhost:4200`

### Running locally (development)

**Backend:**
```bash
cd planning-poker-backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd planning-poker-frontend
npm install
ng serve
```

---

## 🧪 Tests

```bash
cd planning-poker-backend
./mvnw test
```

### What's tested
- `RoomService` — room creation, participant joining, owner assignment
- `UserStoryService` — story creation and validation
- `VoteService` — vote casting, duplicate vote prevention, estimate calculation, authorization

---

## 🔄 CI/CD Pipeline

Every push triggers the CI pipeline:
1. Run backend unit tests
2. Build backend JAR
3. Build frontend

Every version tag (`v*`) triggers the release pipeline:
1. Build Docker images
2. Push to GitHub Container Registry
3. Create GitHub Release with auto-generated notes

---

## 📡 WebSocket Contract

| Action | Client sends to | Server publishes to | Payload |
|---|---|---|---|
| Join room | `/app/room/{code}/join` | `/topic/room/{code}/participants` | participant list + owner |
| Add story | `/app/room/{code}/create` | `/topic/room/{code}/stories` | story details |
| Start voting | `/app/room/{code}/start` | `/topic/room/{code}/round` | story + status |
| Cast vote | `/app/room/{code}/vote` | `/topic/room/{code}/votes` | nickname + hasVoted |
| Reveal cards | `/app/room/{code}/reveal` | `/topic/room/{code}/votes` | votes + average |
| Restart round | `/app/room/{code}/restart` | `/topic/room/{code}/round` | status |

---

## 🗺️ Roadmap

- [x] **v1.0** — MVP: rooms, real-time voting, reveal, restart
- [ ] **v1.1** — Custom decks (T-Shirt, Powers of 2) + session history
- [ ] **v1.2** — Configurable voting timer
- [ ] **v1.3** — Moderator can remove participants
- [ ] **v1.4** — Export session to CSV
- [ ] **v1.5** — Jira integration (import stories)

---

## 💡 Key Learnings

This project was built as a hands-on learning experience. Key concepts explored:

- **WebSocket + STOMP** — real-time bidirectional communication, topic-based pub/sub, session management
- **Domain-Driven Design** — organizing code by domain (room, vote, participant) instead of by layer
- **JPA relationships** — `@OneToMany`, `@ManyToOne`, cascade operations, lazy loading
- **Spring Data** — derived queries, repository pattern
- **Angular reactivity** — RxJS Observables, component lifecycle, two-way binding
- **Unit testing** — Mockito mocks, ArgumentCaptor, test isolation without Spring context
- **Docker Compose** — multi-container orchestration with health checks and dependency ordering
- **GitHub Actions** — CI/CD pipelines, service containers, GHCR image publishing

---

## 👤 Author

**Felipe Marques**
- LinkedIn: [Felipe Marques](https://linkedin.com/in/feelipe-maarquees)
- GitHub: [Felipe Marques](https://github.com/feelipemarques)

