# 🏦 Sistema de Gestão de Benefícios

Sistema de gerenciamento de benefícios com **transferências seguras entre contas**, implementando a correção de um bug de concorrência no módulo EJB.

---

## 📋 Sobre o Projeto

Este projeto foi desenvolvido como parte de um desafio técnico.  
O objetivo era corrigir um bug de concorrência (problema de *lost update*) no módulo EJB e integrar um backend em Spring Boot para expor operações CRUD e de transferência.

Por limitação de tempo, o **frontend Angular não foi implementado**, mas o **backend e EJB estão completamente funcionais** e documentados.

---

## ✅ Funcionalidades Implementadas

| Funcionalidade | Status |
|----------------|---------|
| CRUD de Benefícios (Spring Boot) | ✅ |
| Transferência entre contas (EJB) | ✅ |
| Validação de saldo e contas ativas | ✅ |
| Lock otimista para evitar concorrência | ✅ |
| Logs (EJB) | ✅ Parcial — implementados apenas no EJB |
| Testes automatizados | ⚙️ Parcial — alguns testes unitários e de integração |
| Documentação Swagger | ✅ |
| Frontend Angular | ❌ Não implementado nesta entrega |

---

## 🏗️ Arquitetura Simplificada

```
┌─────────────────────────────┐
│       BACKEND (Spring)      │
│ ┌─────────────────────────┐ │
│ │ BeneficioController     │ │
│ │ BeneficioService        │ │
│ └──────────┬──────────────┘ │
│            │                │
│            ↓                │
│     BeneficioEjbService     │  ← (EJB corrigido)
└────────────┬────────────────┘
             │
             ↓
             PostgreSQL
```

---

## 🧩 Módulos

### 🧠 ejb-module
- Contém a regra de negócio principal da transferência (`BeneficioEjbService`)
- Implementa validações:
  - Impede transferência entre a mesma conta
  - Valida saldo positivo e suficiente
  - Garante locking otimista para evitar *lost updates*
- Logs implementados com SLF4J

### ⚙️ backend-module
- API REST com endpoints CRUD e transferência
- Integração direta com o módulo EJB
- Documentação automática com Swagger
- Testes básicos de integração

---

## 🧱 Tecnologias Utilizadas

| Tecnologia | Uso |
|-------------|-----|
| **Java 17** | Linguagem principal |
| **Spring Boot 3.2.5** | Backend REST |
| **Jakarta EE / EJB 3.2** | Módulo legado |
| **JPA / Hibernate** | ORM |
| **PostgreSQL** | Banco principal |
| **Flyway** | Migrations de banco |
| **SLF4J** | Logging |
| **Swagger / OpenAPI** | Documentação de API |
| **JUnit 5 / Mockito** | Testes |
| **Docker / Docker Compose** | Containerização e banco isolado |

---

## 🧩 Estrutura de Pastas

```
+---backend-module
|   Dockerfile
|   pom.xml
|   +---src
|   |   +---main/java/com/example/backend
|   |   |   BeneficioController.java
|   |   |   BeneficioRequest.java
|   |   |   BeneficioResponse.java
|   |   |   TransferenciaRequest.java
|   |   |   TransferenciaResponse.java
|   |   |   Transferencia.java
|   |   |   BeneficioMapper.java
|   |   |   TransferenciaMapper.java
|   |   |   BeneficioRepository.java
|   |   |   BeneficioService.java
|   |   \---resources/db/migration
|   |           V1__create_tables.sql
|   |           V2__seed_data.sql
+---ejb-module
|   pom.xml
|   +---src/main/java/com/example/ejb
|   |       Beneficio.java
|   |       BeneficioEjbService.java
|   |   \---exception
|   |           ConcorrenciaDetectadaException.java
|   |           ContaNaoEncontradaException.java
|   |           SaldoInsuficienteException.java
|   |           TransferenciaNaoPermitidaException.java
+---docs
|       README.md
```

---

## 🚀 Como Executar o Projeto

### 1️⃣ Subir o PostgreSQL via Docker

Na raiz do projeto, execute:

```bash
cd docker
docker-compose up -d
```

Verificar se está rodando:

```bash
docker-compose ps
```

Parar o container:

```bash
docker-compose down
```

Remover volumes (CUIDADO: apaga dados):

```bash
docker-compose down -v
```

### 2️⃣ Configurar o Banco Manualmente (opcional)

Se quiser usar banco local sem Docker:

```bash
psql -U postgres -f db/schema.sql
psql -U postgres -f db/seed.sql
```

### 3️⃣ Compilar o Projeto

```bash
mvn clean install
```

### 4️⃣ Executar o Backend

```bash
cd backend-module
mvn spring-boot:run
```

### 5️⃣ Acessar Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 Endpoints Principais

### Benefícios

| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/api/v1/beneficios` | Lista todos os benefícios |
| `GET` | `/api/v1/beneficios/{id}` | Busca benefício por ID |
| `POST` | `/api/v1/beneficios` | Cria um novo benefício |
| `PUT` | `/api/v1/beneficios/{id}` | Atualiza um benefício |
| `DELETE` | `/api/v1/beneficios/{id}` | Desativa um benefício |

### Transferências

| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `POST` | `/api/v1/beneficios/transfer` | Realiza transferência entre benefícios |

### ⚙️ Exemplo de Requisição

```json
POST /api/v1/beneficios/transfer
{
  "fromId": 1,
  "toId": 2,
  "valor": 150.00
}
```

**Resposta (200 OK):**
```json
{
  "status": "Transferência realizada com sucesso"
}
```

---

## 🧠 Principais Pontos Técnicos da Correção do EJB

- **Problema original:** Transferências simultâneas causavam inconsistências de saldo (*lost update*).
- **Correção aplicada:**
  - Uso de **`@Version`** e **`LockModeType.OPTIMISTIC_FORCE_INCREMENT`**.
  - Rollback automático em caso de erro.
  - Validações de saldo, contas e parâmetros.
  - Logging detalhado em cada etapa da operação.

---

## 🧩 O que ficou pendente

- Implementar logs também no backend.  
- Implementar o frontend Angular.  
- Melhorar cobertura de testes automatizados.  

---

## 👤 Autor

Desenvolvido por **Alita Kallyne do Nascimento**  
📧 Contato: [alytakallyne@gmail.com](mailto:alytakallyne@gmail.com)  
📅 Entrega: Novembro/2025

---
