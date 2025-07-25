# Documentação API - Sistema de Assembleia

## Swagger/OpenAPI 3 Configuration

### 🚀 Acesso ao Swagger UI

Após iniciar a aplicação, você pode acessar a documentação interativa do Swagger em:

- **Desenvolvimento**: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- **Produção**: [https://api.assembleia.com/api/swagger-ui.html](https://api.assembleia.com/api/swagger-ui.html)

### 📚 API Docs (JSON)

Para acessar a especificação OpenAPI 3 em formato JSON:

- **API Docs**: [http://localhost:8080/api/api-docs](http://localhost:8080/api/api-docs)

### 🏷️ Endpoints Documentados

#### 1. **Sessões** (`/v1/sessions`)
- **POST** `/v1/sessions` - Criar nova sessão de votação
- **GET** `/v1/sessions/{id}` - Buscar sessão por ID

#### 2. **Agendas** (`/v1/agendas`)
- **POST** `/v1/agendas` - Criar nova agenda
- **GET** `/v1/agendas/{id}` - Buscar agenda por ID

#### 3. **Votos** (`/v1/votes`)
- **POST** `/v1/votes` - Registrar novo voto (com validação CPF)
- **GET** `/v1/votes/check/{agendaId}/{cpf}` - Verificar se CPF já votou
- **GET** `/v1/votes/results/{agendaId}` - Obter resultados da votação

### 🔧 Funcionalidades do Swagger

#### ✅ **Features Implementadas:**

1. **Documentação Completa**:
   - Descrições detalhadas para cada endpoint
   - Exemplos de request/response
   - Códigos de status HTTP documentados

2. **Validação de Entrada**:
   - Schemas para todos os DTOs
   - Validação de formatos (UUID, CPF)
   - Enum values documentados

3. **Resposta Estruturada**:
   - Modelos de resposta definidos
   - Tratamento de erros documentado
   - Headers de resposta especificados

4. **Interface Interativa**:
   - Teste direto dos endpoints
   - Autorização configurável
   - Download da especificação OpenAPI

### 📝 Exemplos de Uso

#### Criar Sessão
```json
POST /api/v1/sessions
{
  "startDate": "2025-01-01T10:00:00",
  "endDate": "2025-01-01T18:00:00"
}
```

#### Registrar Voto
```json
POST /api/v1/votes
{
  "agendaId": "550e8400-e29b-41d4-a716-446655440000",
  "cpf": "12345678901",
  "vote": "YES"
}
```

#### Resultado da Votação
```json
GET /api/v1/votes/results/550e8400-e29b-41d4-a716-446655440000

Response:
{
  "agendaId": "550e8400-e29b-41d4-a716-446655440000",
  "agendaTitle": "Aprovação do novo orçamento",
  "yesCount": 15,
  "noCount": 8,
  "totalVotes": 23,
  "yesPercentage": 65.22,
  "noPercentage": 34.78,
  "sessionEnded": true,
  "winner": "YES",
  "result": "Aprovado"
}
```

### ⚙️ Configuração

#### Dependências Maven
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

#### Properties
```properties
# SpringDoc OpenAPI 3
springdoc.api-docs.enabled=true
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.packages-to-scan=br.com.assembleia.assembleia.adapters.controllers
springdoc.paths-to-match=/v1/**
```

### 🎯 Features Especiais

1. **Validação CPF**: Documentada com exemplos e regras
2. **Resultados em Tempo Real**: Porcentagens calculadas automaticamente
3. **Status da Sessão**: Detecta automaticamente se votação terminou
4. **Kafka Events**: Documentação dos eventos assíncronos
5. **Clean Architecture**: Estrutura bem organizada e documentada

### 🔍 Tags Organizadas

- **Sessões**: Gerenciamento de sessões de votação
- **Agendas**: Gerenciamento de agendas de votação  
- **Votos**: Gerenciamento de votos e resultados

### 🚨 Códigos de Status HTTP

- **200**: Sucesso
- **201**: Criado com sucesso
- **400**: Dados inválidos
- **404**: Recurso não encontrado
- **409**: Conflito (ex: já votou)
- **500**: Erro interno

### 📱 Compatibilidade

- **OpenAPI 3.0+**
- **Spring Boot 3.5.3**
- **Java 21**
- **SpringDoc 2.3.0**

---

**🎉 A documentação está pronta para uso!** Acesse o Swagger UI para explorar todos os endpoints de forma interativa.
