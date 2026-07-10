# Music Streaming Platform

A production-ready microservices backend for music streaming, built with Java 21, Spring Boot 3, Apache Kafka, and MinIO. Inspired by Spotify's architecture.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Clients                              │
│                  (Postman / Frontend)                       │
└──────┬──────────────┬──────────────┬───────────────┬────────┘
       │              │              │               │
       ▼              ▼              ▼               ▼
┌──────────┐  ┌──────────────┐  ┌─────────┐  ┌───────────┐
│  User    │  │   Catalog    │  │ Stream  │  │ Analytics │
│ Service  │  │   Service    │  │ Service │  │  Service  │
│  :8081   │  │    :8082     │  │  :8083  │  │   :8084   │
└──────────┘  └──────────────┘  └─────────┘  └───────────┘
     │               │                │              │
     ▼               ▼                ▼              ▼
┌──────────┐  ┌──────────────┐  ┌─────────┐  ┌───────────┐
│ Postgres │  │   Postgres   │  │  MinIO  │  │ Postgres  │
│music_user│  │music_catalog │  │  :9000  │  │music_anal.│
└──────────┘  └──────┬───────┘  └─────────┘  └─────┬─────┘
                     │                               │
                     └──────────┬────────────────────┘
                                ▼
                        ┌───────────────┐
                        │     Kafka     │
                        │  topic:       │
                        │ track.played  │
                        └───────────────┘
```

---

## Services

### User Service (port 8081)
Handles authentication and user management.

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/users/auth/register` | Register new user | ❌ |
| POST | `/api/v1/users/auth/login` | Login | ❌ |
| POST | `/api/v1/users/auth/refresh` | Refresh access token | ❌ |
| POST | `/api/v1/users/auth/logout` | Logout | ✅ |
| GET | `/api/v1/users/me` | Get current user profile | ✅ |
| PUT | `/api/v1/users/me` | Update profile | ✅ |
| PUT | `/api/v1/users/me/password` | Change password | ✅ |
| PUT | `/api/v1/users/me/subscription` | Update subscription (FREE/PREMIUM) | ✅ |

### Catalog Service (port 8082)
Manages music catalog — artists, albums, tracks, playlists.

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/genres` | List all genres | ❌ |
| POST | `/api/v1/genres` | Create genre | ✅ |
| GET | `/api/v1/artists` | List all artists | ❌ |
| GET | `/api/v1/artists/search?name=` | Search artists | ❌ |
| POST | `/api/v1/artists` | Create artist | ✅ |
| GET | `/api/v1/albums` | List all albums | ❌ |
| GET | `/api/v1/albums/artist/{id}` | Albums by artist | ❌ |
| POST | `/api/v1/albums` | Create album | ✅ |
| GET | `/api/v1/tracks` | List all tracks | ❌ |
| GET | `/api/v1/tracks/search?title=` | Search tracks | ❌ |
| POST | `/api/v1/tracks` | Create track | ✅ |
| GET | `/api/v1/playlists/me` | My playlists | ✅ |
| POST | `/api/v1/playlists` | Create playlist | ✅ |
| POST | `/api/v1/playlists/{id}/tracks/{trackId}` | Add track to playlist | ✅ |
| DELETE | `/api/v1/playlists/{id}/tracks/{trackId}` | Remove track from playlist | ✅ |

### Stream Service (port 8083)
Handles audio file upload and streaming via presigned URLs.

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/stream/upload` | Upload audio file | ✅ |
| GET | `/api/v1/stream/{fileKey}` | Get presigned stream URL (15 min) | ✅ |
| DELETE | `/api/v1/stream/{fileKey}` | Delete audio file | ✅ |

### Analytics Service (port 8084)
Consumes Kafka events and provides listening statistics.

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/analytics/history` | My listening history | ✅ |
| GET | `/api/v1/analytics/top` | Top 10 tracks this week | ✅ |

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL 15 |
| ORM | Hibernate / Spring Data JPA |
| Messaging | Apache Kafka |
| File Storage | MinIO (S3-compatible) |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |

---

## Key Design Decisions

**Presigned URLs for streaming** — Stream Service never proxies audio data through the server. Instead it generates a temporary MinIO URL valid for 15 minutes, and the client downloads directly from MinIO. This is the same pattern used by Spotify and Netflix.

**Async analytics via Kafka** — Every track play publishes a `track.played` event to Kafka. Analytics Service consumes these events independently, so the catalog read path has zero overhead from analytics.

**Database per service** — Each microservice owns its own PostgreSQL database (`music_users`, `music_catalog`, `music_analytics`), following the database-per-service pattern to ensure loose coupling.

**JWT with refresh tokens** — Access tokens expire in 15 minutes. Refresh tokens (30 days) are stored in the database and can be revoked. This prevents token theft from being a long-term problem.

---

## Running Locally

### Prerequisites
- Docker Desktop
- Java 21
- Maven

### Start everything

```bash
# Clone the repo
git clone https://github.com/yourusername/music-platform

# Build all services
cd user-service && ./mvnw package -DskipTests
cd ../catalog-service && ./mvnw package -DskipTests
cd ../stream-service && ./mvnw package -DskipTests
cd ../analytics-service && ./mvnw package -DskipTests

# Start all containers
cd ..
docker-compose up --build
```

### Services will be available at:

| Service | URL |
|---------|-----|
| User Service | http://localhost:8081 |
| Catalog Service | http://localhost:8082 |
| Stream Service | http://localhost:8083 |
| Analytics Service | http://localhost:8084 |
| MinIO Console | http://localhost:9001 |
| Kafka | localhost:9092 |

---

## Quick API Test

```bash
# Register
curl -X POST http://localhost:8081/api/v1/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@gmail.com","password":"password123"}'

# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@gmail.com","password":"password123"}' | jq -r '.accessToken')

# Get genres
curl http://localhost:8082/api/v1/genres \
  -H "Authorization: Bearer $TOKEN"
```

---

## Project Structure

```
music-platform/
├── docker-compose.yml
├── init-db.sql
├── user-service/          # Auth, JWT, user profiles
├── catalog-service/       # Artists, albums, tracks, playlists
├── stream-service/        # MinIO audio upload & streaming
└── analytics-service/     # Kafka consumer, listening stats
```

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://postgres:5432/...` |
| `JWT_SECRET` | JWT signing secret (min 256-bit) | set in docker-compose |
| `MINIO_URL` | MinIO server URL | `http://minio:9000` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `kafka:9092` |
