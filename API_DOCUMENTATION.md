# API Endpoints - Sistema de Assembleia

## Base URL
```
http://localhost:8080/api
```

## 1. Sessões

### Criar Sessão
**POST** `/v1/sessoes`

**Body:**
```json
{
  "dataInicio": "2025-01-25T10:00:00",
  "dataFim": "2025-01-25T12:00:00"
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Sessão criada com sucesso",
  "timestamp": 1643097600000
}
```

### Buscar Sessão por ID
**GET** `/v1/sessoes/{id}`

**Response (200):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "dataInicio": "2025-01-25T10:00:00",
  "dataFim": "2025-01-25T12:00:00",
  "version": 0
}
```

---

## 2. Pautas

### Criar Pauta
**POST** `/v1/pautas`

**Body:**
```json
{
  "titulo": "Votação para novo sistema",
  "descricao": "Discussão sobre implementação do novo sistema de gestão",
  "sessaoId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Pauta criada com sucesso",
  "timestamp": 1643097600000
}
```

### Buscar Pauta por ID
**GET** `/v1/pautas/{id}`

**Response (200):**
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174001",
  "titulo": "Votação para novo sistema",
  "descricao": "Discussão sobre implementação do novo sistema de gestão",
  "sessaoId": "123e4567-e89b-12d3-a456-426614174000"
}
```

---

## 3. Votos

### Registrar Voto
**POST** `/v1/votos`

**Body:**
```json
{
  "pautaId": "456e7890-e89b-12d3-a456-426614174001",
  "cpf": "12345678900",
  "voto": "SIM"
}
```

**Valores válidos para voto:** `SIM`, `NAO`

**Response (201):**
```json
{
  "status": 201,
  "message": "Voto registrado com sucesso",
  "timestamp": 1643097600000
}
```

### Verificar se CPF já votou
**GET** `/v1/votos/verificar/{pautaId}/{cpf}`

**Response (200):**
```json
{
  "status": 200,
  "message": "Participante já votou nesta pauta",
  "timestamp": 1643097600000
}
```

ou

```json
{
  "status": 200,
  "message": "Participante ainda não votou nesta pauta",
  "timestamp": 1643097600000
}
```

---

## Códigos de Status HTTP

- **200 OK** - Sucesso na consulta
- **201 Created** - Recurso criado com sucesso
- **400 Bad Request** - Dados inválidos ou parâmetros incorretos
- **404 Not Found** - Recurso não encontrado
- **409 Conflict** - Conflito (ex: CPF já votou na pauta)
- **500 Internal Server Error** - Erro interno do servidor

---

## Regras de Negócio

1. **Sessões**: Duração mínima de 1 minuto (ajustada automaticamente se menor)
2. **Pautas**: Título obrigatório e deve estar associada a uma sessão válida
3. **Votos**: 
   - CPF pode votar apenas uma vez por pauta
   - Valores válidos: SIM ou NAO
   - Pauta deve existir para registrar voto

---

## Exemplo de Fluxo Completo

1. **Criar uma sessão**
2. **Criar uma pauta** associada à sessão
3. **Registrar votos** na pauta
4. **Verificar** se um CPF já votou

```bash
# 1. Criar sessão
curl -X POST http://localhost:8080/api/v1/sessoes \
  -H "Content-Type: application/json" \
  -d '{"dataInicio":"2025-01-25T10:00:00","dataFim":"2025-01-25T12:00:00"}'

# 2. Criar pauta (substitua {sessaoId} pelo ID retornado)
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Nova pauta","descricao":"Descrição da pauta","sessaoId":"{sessaoId}"}'

# 3. Registrar voto (substitua {pautaId} pelo ID retornado)
curl -X POST http://localhost:8080/api/v1/votos \
  -H "Content-Type: application/json" \
  -d '{"pautaId":"{pautaId}","cpf":"12345678900","voto":"SIM"}'

# 4. Verificar voto
curl http://localhost:8080/api/v1/votos/verificar/{pautaId}/12345678900
```
