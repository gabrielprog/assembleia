# 🧪 Testando a API com Insomnia

Este arquivo contém uma coleção completa do Insomnia para testar todos os endpoints da Assembly System API.

## 📥 Como Importar

1. **Abra o Insomnia**
2. **Clique em "Import/Export"** no menu superior
3. **Selecione "Import Data"**
4. **Escolha "From File"**
5. **Selecione o arquivo `insomnia-collection.json`**
6. **Clique em "Import"**

## 🔧 Configuração

### Ambiente Base
A coleção já vem configurada com:
- **Base URL**: `http://localhost:8080/api`
- **Variáveis de ambiente**: `sessionId`, `agendaId`, `voteId`

### ⚠️ Importante
Certifique-se de que sua aplicação Spring Boot esteja rodando na porta 8080 antes de testar.

## 📋 Estrutura da Coleção

### 📁 Sessions
- **Create Session** - Cria uma nova sessão de votação
- **Get Session by ID** - Busca sessão por ID

### 📁 Agendas  
- **Create Agenda** - Cria uma nova pauta para uma sessão
- **Get Agenda by ID** - Busca pauta por ID

### 📁 Votes
- **Register Vote** - Registra um voto YES
- **Register Vote NO** - Registra um voto NO
- **Check if CPF Voted** - Verifica se CPF já votou

### 📁 Test Scenarios
- **Create Session (Already Ended)** - Testa sessão expirada
- **Create Session (Future)** - Testa sessão futura
- **Test Duplicate Vote** - Testa voto duplicado

### 📁 Miscellaneous
- **Hello World** - Health check da API

## 🎯 Fluxo de Teste Recomendado

### 1️⃣ **Criar Sessão Ativa**
```
POST /v1/sessions
```
- Use datas que incluam o momento atual
- Copie o `X-Session-ID` do header de resposta
- Cole no environment variable `sessionId`

### 2️⃣ **Criar Agenda**
```
POST /v1/agendas
```
- Usa automaticamente o `sessionId` do ambiente
- Copie o `X-Agenda-ID` do header de resposta
- Cole no environment variable `agendaId`

### 3️⃣ **Registrar Votos**
```
POST /v1/votes
```
- Teste com diferentes CPFs
- Teste votos YES e NO
- Copie o `X-Vote-ID` do header de resposta

### 4️⃣ **Verificar Voto**
```
GET /v1/votes/check/{agendaId}/{cpf}
```
- Confirme que CPFs já votaram

## 🧪 Cenários de Teste

### ✅ **Casos de Sucesso**
1. Criar sessão → Criar agenda → Votar → Verificar voto
2. Multiple votos com CPFs diferentes
3. Votos YES e NO na mesma agenda

### ❌ **Casos de Erro**
1. **Sessão Expirada**: Use "Create Session (Already Ended)"
2. **Sessão Futura**: Use "Create Session (Future)" 
3. **Voto Duplicado**: Use "Test Duplicate Vote"
4. **Agenda Inexistente**: Use UUID inválido
5. **CPF Duplicado**: Vote duas vezes com mesmo CPF

## 📊 **Códigos de Status Esperados**

| Cenário | Status | Mensagem |
|---------|--------|----------|
| Sucesso na criação | 201 | "Session/Agenda/Vote created successfully" |
| Consulta bem-sucedida | 200 | Dados do recurso |
| Sessão expirada | 403 | "Voting session has ended" |
| Sessão futura | 403 | "Voting session has not started yet" |
| CPF já votou | 409 | "Participant has already voted" |
| Recurso não encontrado | 404 | "Session/Agenda not found" |
| Dados inválidos | 400 | Mensagem específica do erro |

## 💡 **Dicas de Uso**

### 🔄 **Variáveis de Ambiente**
- Sempre atualize as variáveis `sessionId`, `agendaId`, `voteId` com os valores retornados nos headers
- Use `{{ _.sessionId }}` para referenciar variáveis

### 📝 **Headers Importantes**
- **Location**: URL do recurso criado
- **X-Session-ID**: ID da sessão criada  
- **X-Agenda-ID**: ID da agenda criada
- **X-Vote-ID**: ID do voto criado

### ⏰ **Testando Regras de Tempo**
- Modifique as datas nos requests de sessão para testar diferentes cenários
- Use datas passadas para testar sessões expiradas
- Use datas futuras para testar sessões que não começaram

## 🚀 **Exemplo de Fluxo Completo**

```bash
1. POST /v1/sessions (com datas atuais)
   → Copiar X-Session-ID para environment

2. POST /v1/agendas (usando sessionId)
   → Copiar X-Agenda-ID para environment

3. POST /v1/votes (CPF: 12345678900, vote: YES)
   → Sucesso: 201 Created

4. POST /v1/votes (mesmo CPF)
   → Erro: 409 Conflict

5. GET /v1/votes/check/{agendaId}/12345678900
   → 200 OK: "Participant has already voted"
```

**Happy Testing! 🎉**
