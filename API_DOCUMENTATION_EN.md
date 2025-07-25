# API Endpoints - Assembly System

## Base URL
```
http://localhost:8080/api
```

## 1. Sessions

### Create Session
**POST** `/v1/sessions`

**Body:**
```json
{
  "startDate": "2025-01-25T10:00:00",
  "endDate": "2025-01-25T12:00:00"
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Session created successfully",
  "timestamp": 1643097600000
}
```

### Get Session by ID
**GET** `/v1/sessions/{id}`

**Response (200):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "startDate": "2025-01-25T10:00:00",
  "endDate": "2025-01-25T12:00:00",
  "version": 0
}
```

---

## 2. Agendas

### Create Agenda
**POST** `/v1/agendas`

**Body:**
```json
{
  "title": "Voting for new system",
  "description": "Discussion about implementing the new management system",
  "sessionId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Agenda created successfully",
  "timestamp": 1643097600000
}
```

### Get Agenda by ID
**GET** `/v1/agendas/{id}`

**Response (200):**
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174001",
  "title": "Voting for new system",
  "description": "Discussion about implementing the new management system",
  "sessionId": "123e4567-e89b-12d3-a456-426614174000"
}
```

---

## 3. Votes

### Register Vote
**POST** `/v1/votes`

**Body:**
```json
{
  "agendaId": "456e7890-e89b-12d3-a456-426614174001",
  "cpf": "12345678900",
  "vote": "YES"
}
```

**Valid vote values:** `YES`, `NO`

**Response (201):**
```json
{
  "status": 201,
  "message": "Vote registered successfully",
  "timestamp": 1643097600000
}
```

### Check if CPF has already voted
**GET** `/v1/votes/check/{agendaId}/{cpf}`

**Response (200):**
```json
{
  "status": 200,
  "message": "Participant has already voted on this agenda",
  "timestamp": 1643097600000
}
```

or

```json
{
  "status": 200,
  "message": "Participant has not voted on this agenda yet",
  "timestamp": 1643097600000
}
```

---

## HTTP Status Codes

- **200 OK** - Successful query
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid data or incorrect parameters
- **404 Not Found** - Resource not found
- **409 Conflict** - Conflict (e.g., CPF has already voted on agenda)
- **500 Internal Server Error** - Internal server error

---

## Business Rules

1. **Sessions**: Minimum duration of 1 minute (automatically adjusted if smaller)
2. **Agendas**: Title is required and must be associated with a valid session
3. **Votes**: 
   - CPF can vote only once per agenda
   - Valid values: YES or NO
   - Agenda must exist to register vote

---

## Complete Flow Example

1. **Create a session**
2. **Create an agenda** associated with the session
3. **Register votes** on the agenda
4. **Check** if a CPF has already voted

```bash
# 1. Create session
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -d '{"startDate":"2025-01-25T10:00:00","endDate":"2025-01-25T12:00:00"}'

# 2. Create agenda (replace {sessionId} with returned ID)
curl -X POST http://localhost:8080/api/v1/agendas \
  -H "Content-Type: application/json" \
  -d '{"title":"New agenda","description":"Agenda description","sessionId":"{sessionId}"}'

# 3. Register vote (replace {agendaId} with returned ID)
curl -X POST http://localhost:8080/api/v1/votes \
  -H "Content-Type: application/json" \
  -d '{"agendaId":"{agendaId}","cpf":"12345678900","vote":"YES"}'

# 4. Check vote
curl http://localhost:8080/api/v1/votes/check/{agendaId}/12345678900
```
