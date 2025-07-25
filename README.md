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
- PostgreSQL 
- Apache Kafka 

## Configuração e Execução

### 1. Executar com Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/gabrielprog/assembleia.git
cd assembleia

# Execute os serviços
docker-compose up -d

# A aplicação estará disponível em http://localhost:8080/api/
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

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

### Endpoints Principais

#### Sessões
- `POST /api/v1/sessions` - Criar nova sessão
- `GET /api/v1/sessions/{id}` - Buscar sessão por ID

#### Agendas
- `POST /api/v1/agendas` - Criar nova agenda
- `GET /api/v1/agendas/{id}` - Buscar agenda por ID

#### Votos
- `POST /api/v1/votes` - Registrar voto
- `GET /api/v1/votes/check/{agendaId}/{cpf}` - Verificar se CPF já votou
- `GET /api/v1/votes/results/{agendaId}` - Obter resultados da votação

#### Configuração Mobile
- `GET /api/v1/mobile-config` - Obter configurações para aplicação mobile
- `GET /api/v1/mobile-config/dynamic-data` - Obter dados dinâmicos para formulários

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

## Validações e Regras de Negócio

### Sessões
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

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

