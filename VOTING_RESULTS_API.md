# Endpoint de Resultados de Votação

## GET /v1/votes/results/{agendaId}

### Descrição
Retorna os resultados da votação para uma agenda específica, incluindo:
- Porcentagem de votos SIM e NÃO em tempo real
- Resultado final se a sessão já tiver terminado
- Informações sobre o vencedor quando aplicável

### Parâmetros
- `agendaId` (UUID) - ID da agenda para obter os resultados

### Resposta de Sucesso (200 OK)
```json
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

### Campos da Resposta
- `agendaId`: ID da agenda
- `agendaTitle`: Título da agenda
- `yesCount`: Número de votos SIM
- `noCount`: Número de votos NÃO
- `totalVotes`: Total de votos registrados
- `yesPercentage`: Porcentagem de votos SIM (com 2 casas decimais)
- `noPercentage`: Porcentagem de votos NÃO (com 2 casas decimais)
- `sessionEnded`: Indica se a sessão de votação já terminou
- `winner`: Vencedor da votação (YES/NO) - apenas quando a sessão terminou
- `result`: Resultado textual da votação

### Possíveis Resultados
- **"Votação em andamento"**: Quando a sessão ainda está ativa
- **"Aprovado"**: Quando a sessão terminou e há mais votos SIM
- **"Rejeitado"**: Quando a sessão terminou e há mais votos NÃO
- **"Empate"**: Quando a sessão terminou com empate
- **"Nenhum voto registrado"**: Quando a sessão terminou sem votos

### Respostas de Erro

#### 400 Bad Request
```json
{
  "code": 400,
  "message": "Agenda not found with id: {agendaId}"
}
```

#### 500 Internal Server Error
```json
{
  "code": 500,
  "message": "Internal server error"
}
```

### Exemplo de Uso

#### Votação em Andamento
```bash
curl -X GET http://localhost:8080/v1/votes/results/550e8400-e29b-41d4-a716-446655440000
```

Resposta:
```json
{
  "agendaId": "550e8400-e29b-41d4-a716-446655440000",
  "agendaTitle": "Aprovação do novo orçamento",
  "yesCount": 12,
  "noCount": 5,
  "totalVotes": 17,
  "yesPercentage": 70.59,
  "noPercentage": 29.41,
  "sessionEnded": false,
  "winner": null,
  "result": "Votação em andamento"
}
```

#### Votação Finalizada
```json
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
