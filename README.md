# Sistema de Assembleias - API REST

Sistema para gerenciamento de sessões de votação em assembleias, desenvolvido com Spring Boot e arquitetura limpa.

## Funcionalidades

- **Gerenciamento de Sessões**: Criação e consulta de sessões de votação com controle temporal
- **Gerenciamento de Agendas**: Criação de pautas para votação vinculadas a sessões
- **Sistema de Votação**: Registro de votos com validação de CPF e controle de duplicatas
- **Resultados em Tempo Real**: Consulta de resultados de votação com porcentagens
- **Configuração Mobile**: Endpoints para fornecer configurações de formulários para aplicações mobile
- **Documentação Swagger**: Interface web para teste e documentação da API

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.3
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Docker
- Swagger/OpenAPI 3
- Maven

## Pré-requisitos

- Java 21 ou superior
- Maven 3.8+
- Docker e Docker Compose
- PostgreSQL (se não usar Docker)
- Apache Kafka (se não usar Docker)

## Configuração e Execução

### 1. Executar com Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd assembleia

# Execute os serviços
docker-compose up -d

# A aplicação estará disponível em http://localhost:8080
```

### 2. Executar Localmente

#### 2.1. Configurar Banco de Dados

Certifique-se de ter PostgreSQL rodando e configure as variáveis de ambiente:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=assembleia
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha
```

#### 2.2. Configurar Kafka

Certifique-se de ter Apache Kafka rodando localmente na porta 9092.

#### 2.3. Executar a Aplicação

```bash
# Compilar o projeto
./mvnw clean compile

# Executar os testes
./mvnw test

# Executar a aplicação
./mvnw spring-boot:run
```

### 3. Executar em Diferentes Ambientes

A aplicação suporta diferentes perfis de configuração:

```bash
# Desenvolvimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Homologação
./mvnw spring-boot:run -Dspring-boot.run.profiles=homolog

# Produção
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Endpoints Principais

#### Sessões
- `POST /v1/sessions` - Criar nova sessão
- `GET /v1/sessions/{id}` - Buscar sessão por ID

#### Agendas
- `POST /v1/agendas` - Criar nova agenda
- `GET /v1/agendas/{id}` - Buscar agenda por ID

#### Votos
- `POST /v1/votes` - Registrar voto
- `GET /v1/votes/check/{agendaId}/{cpf}` - Verificar se CPF já votou
- `GET /v1/votes/results/{agendaId}` - Obter resultados da votação

#### Configuração Mobile
- `GET /v1/mobile-config` - Obter configurações para aplicação mobile
- `GET /v1/mobile-config/dynamic-data` - Obter dados dinâmicos para formulários

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/com/assembleia/assembleia/
│   │       ├── adapters/          # Camada de adaptadores
│   │       │   ├── controllers/   # Controllers REST
│   │       │   ├── dtos/          # DTOs de entrada e saída
│   │       │   ├── gateways/      # Interfaces de acesso a dados
│   │       │   └── repositories/  # Repositórios JPA
│   │       ├── application/       # Camada de aplicação
│   │       │   ├── usecases/      # Casos de uso
│   │       │   └── utils/         # Utilitários
│   │       ├── configs/           # Configurações
│   │       └── infra/             # Infraestrutura
│   │           ├── db/            # Entidades de banco
│   │           └── messaging/     # Configuração Kafka
│   └── resources/
│       ├── application*.properties # Configurações por ambiente
│       └── db/migration/          # Scripts Flyway
└── test/                          # Testes unitários e integração
```

## Configurações de Ambiente

### Desenvolvimento (application-dev.properties)
- Banco H2 em memória para desenvolvimento rápido
- Logs em nível DEBUG
- Kafka desabilitado

### Homologação (application-homolog.properties)
- PostgreSQL containerizado
- Kafka ativo
- Logs em nível INFO

### Produção (application-prod.properties)
- PostgreSQL gerenciado
- Kafka cluster
- Logs em nível WARN
- Configurações otimizadas para performance

## Validações e Regras de Negócio

### Sessões
- Data de início deve ser futura
- Data de fim deve ser posterior à data de início
- Sessões não podem se sobrepor

### Agendas
- Devem estar vinculadas a uma sessão válida
- Título é obrigatório (máximo 255 caracteres)
- Descrição é opcional (máximo 1000 caracteres)

### Votos
- CPF deve ser válido (11 dígitos)
- Cada CPF pode votar apenas uma vez por agenda
- Votos só são aceitos durante o período da sessão
- Valores aceitos: YES ou NO

## Monitoramento e Observabilidade

A aplicação inclui:

- **Actuator**: Endpoints de health check e métricas
- **Logs estruturados**: Formato JSON para facilitar análise
- **Kafka Events**: Eventos assíncronos para auditoria
- **Validation**: Validação robusta de entrada

### Health Check

```bash
# Verificar saúde da aplicação
curl http://localhost:8080/actuator/health

# Métricas da aplicação
curl http://localhost:8080/actuator/metrics
```

## Desenvolvimento

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=AgendaControllerTest

# Testes com perfil específico
./mvnw test -Dspring.profiles.active=test
```

### Build para Produção

```bash
# Gerar JAR executável
./mvnw clean package -Dmaven.test.skip=true

# O arquivo será gerado em target/assembleia-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
# Build da imagem
docker build -t assembleia-api .

# Executar container
docker run -p 8080:8080 assembleia-api
```

## Solução de Problemas

### Problema: Erro de conexão com banco de dados
**Solução**: Verifique se o PostgreSQL está rodando e as credenciais estão corretas

### Problema: Kafka não conecta
**Solução**: Verifique se o Kafka está rodando na porta 9092 ou configure o perfil dev

### Problema: Aplicação não inicia
**Solução**: Verifique os logs e certifique-se de que Java 21 está sendo usado

### Problema: Testes falham
**Solução**: Execute `./mvnw clean test` para garantir que não há conflitos de cache

## Contribuição

1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## Suporte

Para suporte técnico, entre em contato:

- **Email**: dev@assembleia.com
- **Documentação**: Acesse o Swagger UI em produção
- **Issues**: Use o sistema de issues do repositório

## Changelog

### v1.0.0
- Implementação inicial do sistema
- CRUD de sessões, agendas e votos
- Sistema de configuração mobile
- Documentação Swagger
- Integração com Kafka
- Suporte a múltiplos ambientes
