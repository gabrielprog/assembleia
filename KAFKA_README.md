# 🔄 Kafka Configuration - Sistema de Assembleia

Este documento descreve a configuração e uso do Apache Kafka no sistema de votação de assembleias.

## 🏗️ Arquitetura

### Componentes do Kafka
- **Zookeeper**: Coordenação e configuração do cluster Kafka
- **Kafka Broker**: Servidor de mensagens principal
- **Kafka UI**: Interface web para monitoramento (http://localhost:8081)

### Tópicos Criados
- `session-events`: Eventos relacionados a sessões de votação
- `agenda-events`: Eventos relacionados a pautas/agendas
- `vote-events`: Eventos relacionados a votos registrados
- `voting-results`: Resultados e estatísticas de votação

## 🚀 Como Executar

### 1. Subir a Infraestrutura Completa
```bash
# Subir todos os serviços (PostgreSQL, Kafka, Zookeeper, App)
docker-compose up -d

# Verificar status dos containers
docker-compose ps

# Ver logs da aplicação
docker-compose logs -f app

# Ver logs do Kafka
docker-compose logs -f kafka
```

### 2. Acessar Interfaces
- **Aplicação**: http://localhost:8080/api
- **Kafka UI**: http://localhost:8081
- **PostgreSQL**: localhost:5432

## 📨 Eventos Publicados

### 1. SessionCreatedEvent
**Tópico**: `session-events`
```json
{
  "sessionId": "123e4567-e89b-12d3-a456-426614174000",
  "startDate": "2025-01-25T10:00:00",
  "endDate": "2025-01-25T12:00:00",
  "createdAt": "2025-01-25T09:30:00",
  "eventType": "SESSION_CREATED"
}
```

### 2. AgendaCreatedEvent
**Tópico**: `agenda-events`
```json
{
  "agendaId": "456e7890-e89b-12d3-a456-426614174001",
  "title": "Votação do novo sistema",
  "description": "Discussão sobre implementação do novo sistema",
  "sessionId": "123e4567-e89b-12d3-a456-426614174000",
  "createdAt": "2025-01-25T09:35:00",
  "eventType": "AGENDA_CREATED"
}
```

### 3. VoteRegisteredEvent
**Tópico**: `vote-events`
```json
{
  "voteId": "789a0123-e89b-12d3-a456-426614174002",
  "agendaId": "456e7890-e89b-12d3-a456-426614174001",
  "cpf": "12345678901",
  "vote": "YES",
  "votedAt": "2025-01-25T10:30:00",
  "eventType": "VOTE_REGISTERED"
}
```

## 🔧 Configuração

### Variáveis de Ambiente
```properties
# Desenvolvimento (application-dev.properties)
spring.kafka.bootstrap-servers=localhost:29092

# Produção (application-prod.properties)
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
```

### Configurações do Consumer
- **Group ID**: `assembleia-group`
- **Auto Offset Reset**: `earliest`
- **Key Deserializer**: `StringDeserializer`
- **Value Deserializer**: `JsonDeserializer`

### Configurações do Producer
- **Key Serializer**: `StringSerializer`
- **Value Serializer**: `JsonSerializer`

## 🔄 Fluxo de Eventos

### 1. Criação de Sessão
```
SessionController.create() 
    → SessionUseCase.save() 
    → SessionGateway.save() 
    → AssembleiaEventProducer.publishSessionCreatedEvent() 
    → Kafka Topic: session-events 
    → SessionEventConsumer.consumeSessionCreatedEvent()
```

### 2. Criação de Agenda
```
AgendaController.create() 
    → AgendaUseCase.createAgenda() 
    → AgendaGateway.save() 
    → AssembleiaEventProducer.publishAgendaCreatedEvent() 
    → Kafka Topic: agenda-events 
    → AgendaEventConsumer.consumeAgendaCreatedEvent()
```

### 3. Registro de Voto
```
VoteController.create() 
    → VoteUseCase.registerVote() 
    → VoteGateway.save() 
    → AssembleiaEventProducer.publishVoteRegisteredEvent() 
    → Kafka Topic: vote-events 
    → VoteEventConsumer.consumeVoteRegisteredEvent()
```

## 🛠️ Monitoramento

### Via Kafka UI (http://localhost:8081)
- Visualizar tópicos e mensagens
- Monitorar consumers e lag
- Visualizar configurações do cluster

### Via Logs da Aplicação
```bash
# Logs de produção de eventos
docker-compose logs -f app | grep "Publicando evento"

# Logs de consumo de eventos
docker-compose logs -f app | grep "Recebido evento"

# Logs de erro
docker-compose logs -f app | grep "ERROR"
```

## 🧪 Testando os Eventos

### 1. Criar Sessão e Verificar Evento
```bash
# Criar sessão
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2025-01-25T10:00:00",
    "endDate": "2025-01-25T12:00:00"
  }'

# Verificar no Kafka UI: Topic session-events
```

### 2. Criar Agenda e Verificar Evento
```bash
# Criar agenda (substitua {sessionId})
curl -X POST http://localhost:8080/api/v1/agendas \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nova Pauta",
    "description": "Descrição da pauta",
    "sessionId": "{sessionId}"
  }'

# Verificar no Kafka UI: Topic agenda-events
```

### 3. Registrar Voto e Verificar Evento
```bash
# Registrar voto (substitua {agendaId})
curl -X POST http://localhost:8080/api/v1/votes \
  -H "Content-Type: application/json" \
  -d '{
    "agendaId": "{agendaId}",
    "cpf": "11144477735",
    "vote": "YES"
  }'

# Verificar no Kafka UI: Topic vote-events
```

## 🚨 Troubleshooting

### Problemas Comuns

#### 1. Kafka não conecta
```bash
# Verificar se o Kafka está rodando
docker-compose ps kafka

# Verificar logs do Kafka
docker-compose logs kafka

# Reiniciar Kafka
docker-compose restart kafka
```

#### 2. Mensagens não estão sendo produzidas
```bash
# Verificar logs da aplicação
docker-compose logs -f app | grep -i kafka

# Verificar configuração do bootstrap servers
```

#### 3. Consumers não estão consumindo
```bash
# Verificar group consumers no Kafka UI
# Verificar lag dos consumers
# Verificar se há mensagens nos tópicos
```

#### 4. Reset de Offsets (se necessário)
```bash
# Parar aplicação
docker-compose stop app

# Conectar no container do Kafka
docker exec -it kafka bash

# Reset offset para um group específico
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group assembleia-group \
  --reset-offsets \
  --to-earliest \
  --topic session-events \
  --execute

# Reiniciar aplicação
docker-compose start app
```

## 📊 Casos de Uso dos Eventos

### 1. Auditoria
- Todos os eventos são registrados para auditoria completa
- Rastreabilidade de ações no sistema

### 2. Integração com Sistemas Externos
- Notificações por email quando sessão é criada
- Integração com sistemas de BI para analytics
- Webhooks para sistemas terceiros

### 3. Cache e Performance
- Atualização de caches baseada em eventos
- Invalidação de cache quando dados mudam

### 4. Analytics em Tempo Real
- Contadores de votos em tempo real
- Dashboards atualizados automaticamente
- Detecção de padrões de votação

## 🔄 Próximos Passos

1. **Implementar Dead Letter Queue (DLQ)** para mensagens com falha
2. **Adicionar métricas** de performance dos consumers
3. **Implementar retry policies** configuráveis
4. **Adicionar compactação** de tópicos para otimização
5. **Implementar SAGA pattern** para operações distribuídas

## 📚 Referências

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Kafka UI Documentation](https://docs.kafka-ui.provectus.io/)
