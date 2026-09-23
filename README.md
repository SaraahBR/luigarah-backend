# 🛍️ Luigarah Backend - API RESTful

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-3ECF8E.svg)](https://supabase.com/)
[![Cloudflare R2](https://img.shields.io/badge/Cloudflare-R2-orange.svg)](https://www.cloudflare.com/products/r2/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Production](https://img.shields.io/badge/production-Render-blue.svg)](https://luigarah-backend.onrender.com)

> Sistema backend completo para e-commerce de moda com autenticação JWT, gerenciamento de produtos, carrinho de compras, lista de desejos e **upload de imagens em nuvem com Cloudflare R2**.

**🌐 Produção:** https://luigarah-backend.onrender.com  
**📚 Documentação API:** https://luigarah-backend.onrender.com/swagger-ui/index.html  
**🎨 Frontend:** https://luigarah.vercel.app

> 🔄 **Setembro/2026 — banco migrado do Oracle para o Supabase.** O projeto usava Oracle Autonomous
> Database; agora usa **Supabase (PostgreSQL 17)**. Detalhes em [Migração Oracle → Supabase](#-migração-oracle--supabase).

---

## 📑 Índice

- [🚀 Visão Geral](#-visão-geral)
- [✨ Funcionalidades](#-funcionalidades)
- [🏗️ Arquitetura e Design](#️-arquitetura-e-design)
- [💻 Tecnologias e Frameworks](#-tecnologias-e-frameworks)
- [📧 Sistema de Email (Brevo API)](#-sistema-de-email-brevo-api)
- [📂 Estrutura do Projeto](#-estrutura-do-projeto)
  - [📦 Estrutura Completa de Pastas e Arquivos](#-estrutura-completa-de-pastas-e-arquivos)
  - [📦 Módulos Funcionais](#-módulos-funcionais)
  - [🔐 Módulo de Autenticação](#-módulo-de-autenticação)
  - [📦 Módulo de Produtos](#-módulo-de-produtos)
  - [🛒 Módulo de Carrinho](#-módulo-de-carrinho)
  - [❤️ Módulo de Lista de Desejos](#️-módulo-de-lista-de-desejos)
  - [📏 Módulo de Tamanhos](#-módulo-de-tamanhos)
  - [🏷️ Módulo de Identidades](#️-módulo-de-identidades)
  - [📊 Módulo de Estoque](#-módulo-de-estoque)
  - [👥 Módulo de Administração de Usuários](#-módulo-de-administração-de-usuários)
  - [📸 Módulo de Upload de Imagens (Storage)](#-módulo-de-upload-de-imagens-storage)
  - [⚙️ Configurações Globais](#️-configurações-globais)
  - [🛠️ Utilitários](#️-utilitários)
  - [⚠️ Tratamento de Exceções](#️-tratamento-de-exceções)
- [🔒 Segurança e Autenticação](#-segurança-e-autenticação)
- [🗄️ Banco de Dados](#️-banco-de-dados)
- [📸 Sistema de Upload de Imagens](#-sistema-de-upload-de-imagens)
- [📡 Endpoints da API](#-endpoints-da-api)
- [🚀 Como Executar](#-como-executar)
- [🌐 Deploy em Produção](#-deploy-em-produção)
- [📖 Documentação](#-documentação)
- [🧪 Testes](#-testes)
- [👥 Contribuição](#-contribuição)

---

## 🚀 Visão Geral

O **Luigarah Backend** é uma API RESTful robusta e escalável desenvolvida para um e-commerce de moda. O sistema oferece gerenciamento completo de produtos, autenticação segura com JWT, carrinho de compras, lista de desejos, controle de estoque e **upload de imagens em nuvem com Cloudflare R2**.

### Características Principais

- ✅ **Arquitetura Modular** - Organizado seguindo Clean Architecture e DDD
- ✅ **Autenticação JWT** - Sistema completo com roles (USER/ADMIN)
- ✅ **OAuth2 Social Login** - Google, Facebook, GitHub
- ✅ **Segurança Avançada** - Spring Security + validação de senhas fortes (6-40 caracteres, maiúscula, minúscula, número e caractere especial)
- ✅ **Banco Supabase** - PostgreSQL gerenciado (schema `app_luigarah`)
- ✅ **Upload de Imagens** - Cloudflare R2 (S3-compatible) em produção
- ✅ **Storage Local** - Armazenamento em disco para desenvolvimento
- ✅ **Documentação Automática** - Swagger/OpenAPI 3.0 completo
- ✅ **Deploy Automatizado** - CI/CD no Render com Docker
- ✅ **Migração de Dados** - Flyway para versionamento do banco
- ✅ **Validação Robusta** - Bean Validation em todos os DTOs
- ✅ **CORS Configurado** - Pronto para frontend em React/Next.js
- ✅ **Conformidade LGPD** - Proteção total de dados sensíveis

---

## ✨ Funcionalidades

### 🔐 Autenticação e Autorização
- Login com email e senha (BCrypt)
- Registro de novos usuários com validação completa
- Tokens JWT com expiração configurável (24h padrão)
- Sistema de roles: **USER** (usuário comum) e **ADMIN** (administrador)
- Alteração de senha segura com validação de força (6-40 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 caractere especial)
- **🆕 OAuth2 Social Login** - Google, Facebook, GitHub
- **🆕 Sincronização OAuth** - Vinculação automática de contas sociais
- Perfil de usuário (visualizar e editar)
- **🆕 Gerenciamento de foto de perfil** - Upload ou URL

### 📧 Sistema de Email (Brevo API)
- **🆕 Verificação de conta** - Código de 6 dígitos válido por 12h
- **🆕 Email de boas-vindas** - Enviado após verificação ou login OAuth
- **🆕 Redefinição de senha** - Código de 6 dígitos para reset seguro
- **🆕 Templates HTML responsivos** - Design moderno e profissional
- **🆕 Integração Brevo API** - Envio confiável e escalável
- **🆕 Validação de códigos** - Proteção contra reutilização e expiração
- **🆕 Suporte a múltiplos idiomas** - Português BR implementado

### 👥 Administração de Usuários (ADMIN)
- **Visualizar usuários** - Listar todos os usuários com paginação
- **Buscar usuários** - Por nome, email, role ou status
- **Editar usuários** - Alterar nome, sobrenome, email, telefone e role
- **Desativar/Ativar usuários** - Soft delete (mantém dados no banco)
- **🆕 Gerenciar foto de perfil** - Alterar foto de qualquer usuário (URL ou upload)
- **Estatísticas** - Contadores de usuários ativos, inativos, por role
- **Conformidade LGPD** - Dados sensíveis protegidos (senha, documentos nunca expostos)
- **Auditoria** - Logs de todas as operações administrativas

> ⚠️ **LGPD:** Administradores podem visualizar dados básicos dos usuários, mas dados sensíveis como senhas, documentos e informações médicas são protegidos e nunca expostos pela API.

### 🛍️ Gerenciamento de Produtos
- CRUD completo de produtos (apenas ADMIN)
- Busca e filtragem de produtos (público)
- Produtos com múltiplas identidades (cores/variações)
- Sistema de tamanhos customizável
- **🆕 Upload e gerenciamento de imagens** - Cloudflare R2 (produção) ou local (desenvolvimento)
- Categorização e organização

### 📸 Upload de Imagens
- **🆕 Upload para Cloudflare R2** - Storage S3-compatible em produção
- **🆕 Armazenamento local** - Para desenvolvimento (perfil local)
- **🆕 Validação de arquivos** - Formatos aceitos: JPG, JPEG, PNG, WEBP, GIF
- **🆕 Limite de tamanho** - Máximo 5MB por arquivo
- **🆕 Upload múltiplo** - Até 10 imagens simultaneamente
- **🆕 Pastas organizadas** - produtos, usuarios, outros
- **🆕 URLs públicas** - Acesso direto às imagens uploadadas

### 🛒 Carrinho de Compras
- Adicionar produtos ao carrinho
- Atualizar quantidade (1-99 unidades)
- Remover itens individualmente
- Limpar carrinho completo
- Suporte a produtos com/sem tamanho
- Isolamento por usuário
- Contagem de itens

### ❤️ Lista de Desejos
- Adicionar produtos favoritos
- Remover produtos da lista
- Verificar se produto está nos favoritos
- Limpar lista completa
- Isolamento por usuário
- Contagem de itens

### 📊 Gestão de Estoque
- Controle de estoque por produto e tamanho
- Atualização de quantidades (ADMIN)
- Consulta de disponibilidade (público)
- Histórico de movimentações

### 📍 Endereços de Entrega
- Múltiplos endereços por usuário
- Endereço principal configurável
- Dados completos (país, estado, cidade, CEP, rua, número, complemento)

---

## 🏗️ Arquitetura e Design

### Princípios Aplicados

O projeto segue rigorosamente os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**:

#### 📐 Clean Architecture
- **Separação de Responsabilidades** - Cada camada tem uma função específica
- **Independência de Frameworks** - Lógica de negócio isolada
- **Testabilidade** - Componentes facilmente testáveis
- **Independência de UI** - Backend desacoplado do frontend

#### 🎯 Domain-Driven Design
- **Módulos por Domínio** - 9 módulos funcionais independentes
- **Bounded Contexts** - Contextos delimitados e isolados
- **Entities e Value Objects** - Modelagem rica de domínio
- **Repositories** - Abstração de persistência

### Estrutura em Camadas

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers + DTOs + Documentation)   │
├─────────────────────────────────────────┤
│         Application Layer               │
│        (Services + Mappers)             │
├─────────────────────────────────────────┤
│          Domain Layer                   │
│    (Models/Entities + Enums)            │
├─────────────────────────────────────────┤
│      Infrastructure Layer               │
│  (Repositories + Config + Security)     │
└─────────────────────────────────────────┘
```

---

## 💻 Tecnologias e Frameworks

### 🔷 Linguagem e Plataforma

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 LTS | Linguagem principal (Eclipse Temurin na imagem Docker) |
| **Maven** | 3.9+ | Gerenciamento de dependências e build |

### 📧 Sistema de Email (Brevo API)

O sistema de e-mail é integrado com a **Brevo API** (anteriormente SendInBlue) para envio confiável e escalável de e-mails transacionais.

#### Funcionalidades de E-mail

| Funcionalidade | Descrição | Validade |
|----------------|-----------|----------|
| **Verificação de Conta** | Código de 6 dígitos para confirmar email | 12 horas |
| **Redefinição de Senha** | Código de 6 dígitos para reset seguro | 12 horas |
| **Boas-vindas (Cadastro)** | Enviado após verificação de conta | - |
| **Boas-vindas (OAuth)** | Enviado no primeiro login via Google/Facebook/GitHub | - |

#### Configuração

As credenciais da Brevo API são configuradas via **variáveis de ambiente** por segurança:

```env
BREVO_API_KEY=xkeysib-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
BREVO_SENDER_EMAIL=luigarah@gmail.com
BREVO_SENDER_NAME=Luigarah
```

#### Templates de E-mail

Todos os e-mails seguem um design HTML responsivo e moderno:

1. **E-mail de Código de Verificação**
   - Assunto: "Código de Verificação - Luigarah"
   - Conteúdo: Código de 6 dígitos com validade de 12h
   - Design: Card centralizado com código destacado

2. **E-mail de Código de Redefinição de Senha**
   - Assunto: "Código de Redefinição de Senha - Luigarah"
   - Conteúdo: Código de 6 dígitos com instruções claras
   - Design: Card com aviso de segurança

3. **E-mail de Boas-vindas**
   - Assunto: "Bem-vindo ao Luigarah!"
   - Conteúdo: Mensagem de boas-vindas personalizada
   - Variações: Cadastro tradicional e OAuth

#### Endpoints de E-mail

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/api/auth/enviar-codigo-verificacao` | POST | Envia código de verificação |
| `/api/auth/verificar-codigo` | POST | Valida código e ativa conta |
| `/api/auth/solicitar-reset-senha` | POST | Envia código de reset |
| `/api/auth/redefinir-senha` | POST | Redefine senha com código |

> 📖 **Documentação Completa:** [`docs/API-AUTENTICACAO-EMAIL.md`](docs/API-AUTENTICACAO-EMAIL.md)

#### Validação de Códigos

- ✅ Códigos únicos de 6 dígitos numéricos
- ✅ Validade de 12 horas
- ✅ Proteção contra reutilização (cada código pode ser usado apenas uma vez)
- ✅ Limpeza automática de códigos antigos
- ✅ Validação de expiração no backend

#### Segurança

- 🔒 Credenciais Brevo externalizadas (nunca no código)
- 🔒 Códigos aleatórios e criptograficamente seguros
- 🔒 Rate limiting configurável (futuro)
- 🔒 Logs de auditoria de envios

### 🌱 Spring Framework

| Framework | Versão | Função |
|-----------|--------|--------|
| **Spring Boot** | 3.2.0 | Framework base da aplicação |
| **Spring Web** | 3.2.0 | API REST e controladores |
| **Spring Data JPA** | 3.2.0 | Persistência e repositórios |
| **Spring Security** | 3.2.0 | Autenticação e autorização |
| **Spring Validation** | 3.2.0 | Validação de dados (Bean Validation) |
| **Spring OAuth2 Client** | 3.2.0 | Preparado para OAuth2 (Google/Facebook) |
| **Spring DevTools** | 3.2.0 | Hot reload em desenvolvimento |

### 🔐 Segurança

| Biblioteca | Versão | Função |
|------------|--------|--------|
| **JJWT API** | 0.11.5 | Geração e validação de tokens JWT |
| **JJWT Impl** | 0.11.5 | Implementação JWT |
| **JJWT Jackson** | 0.11.5 | Serialização JSON para JWT |
| **BCrypt** | Incluído no Spring Security | Hash seguro de senhas |

### 🗄️ Banco de Dados

| Tecnologia | Versão | Função |
|------------|--------|--------|
| **PostgreSQL JDBC** | Gerenciado pelo Spring Boot | Driver de conexão com o Supabase |
| **Hibernate** | 6.2+ | ORM (Object-Relational Mapping) |
| **Flyway** | 9.22+ | Migração e versionamento do banco |
| **HikariCP** | 5.0+ | Pool de conexões (incluído no Spring Boot) |
| **Embedded Postgres** | 2.1.0 (PG 16) | Testes de integração das migrations e queries nativas |

> **Conexão com o Supabase:** use o *Session Pooler* (porta 5432), que funciona via IPv4
> (a conexão direta `db.<ref>.supabase.co` é somente IPv6 e não funciona no Render).
> As tabelas ficam no schema `app_luigarah`, fora da Data API do Supabase, e têm RLS ativado.

### 📸 Upload de Imagens e Storage

| Biblioteca | Versão | Função |
|------------|--------|--------|
| **AWS SDK S3** | 2.20+ | Cliente S3 para Cloudflare R2 |
| **AWS SDK Core** | 2.20+ | Core do SDK AWS |
| **Spring Multipart** | 3.2.0 | Upload de arquivos via HTTP |

> **⚠️ IMPORTANTE:** O Cloudflare R2 é 100% compatível com a API S3 da AWS. A região deve ser configurada como **"auto"**

### 📚 Documentação

| Biblioteca | Versão | Função |
|------------|--------|--------|
| **SpringDoc OpenAPI** | 2.2.0 | Geração automática de documentação |
| **Swagger UI** | Incluído | Interface visual da API |

### 🛠️ Utilitários

| Biblioteca | Função |
|------------|--------|
| **Lombok** | Redução de boilerplate (getters, setters, builders) |
| **Jackson** | Serialização/deserialização JSON |
| **SLF4J + Logback** | Sistema de logs |

### 🐳 Deploy e Containerização

| Tecnologia | Função |
|------------|--------|
| **Docker** | Containerização da aplicação |
| **Render** | Plataforma de deploy (PaaS) |
| **GitHub** | Versionamento e CI/CD |

### 📧 Email e Notificações

| Tecnologia | Versão | Função |
|------------|--------|--------|
| **Brevo API** | V3 | Envio de emails transacionais |
| **Spring Mail** | 3.2.0 | Suporte SMTP integrado |
| **RestTemplate** | 3.2.0 | Cliente HTTP para Brevo API |

---

## 📧 Sistema de Email (Brevo API)

O sistema de email utiliza a **Brevo API** (anteriormente Sendinblue) para envio confiável e escalável de emails transacionais.

### ✨ Funcionalidades de Email

#### 1. 📨 Verificação de Conta
Após cadastro tradicional (email/senha), o sistema:
- Gera código aleatório de **6 dígitos** (SecureRandom)
- Envia email com template HTML responsivo
- Código válido por **12 horas**
- Validação de uso único (não pode ser reutilizado)
- Email de boas-vindas após confirmação

#### 2. 🎉 Email de Boas-Vindas
Enviado automaticamente em duas situações:
- Após **verificação de conta** via código (cadastro tradicional)
- Na **primeira vez** que usuário faz login via OAuth (Google, Facebook, GitHub)
- Template moderno com gradiente e call-to-action

#### 3. 🔐 Redefinição de Senha
Para usuários que esqueceram a senha:
- Gera código de **6 dígitos** para reset
- Válido por **12 horas**
- Apenas contas **locais** (não OAuth) podem redefinir senha
- Validação de senhas coincidentes
- Atualização segura com BCrypt

### 🔌 Endpoints de Email

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/enviar-codigo-verificacao` | Envia código de verificação | ❌ Público |
| POST | `/api/auth/verificar-codigo` | Valida código e ativa conta | ❌ Público |
| POST | `/api/auth/solicitar-reset-senha` | Solicita código de reset | ❌ Público |
| POST | `/api/auth/redefinir-senha` | Redefine senha com código | ❌ Público |

### 📋 Exemplos de Request/Response

#### Enviar Código de Verificação
```bash
POST /api/auth/enviar-codigo-verificacao
Content-Type: application/json

{
  "email": "usuario@example.com"
}
```

**Response 200:**
```json
{
  "sucesso": true,
  "mensagem": "Código de verificação enviado com sucesso! Verifique seu email."
}
```

#### Verificar Código
```bash
POST /api/auth/verificar-codigo
Content-Type: application/json

{
  "email": "usuario@example.com",
  "codigo": "123456"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 1,
    "nome": "João",
    "email": "usuario@example.com",
    "emailVerificado": true,
    "role": "USER"
  }
}
```

### 🎨 Templates de Email

Todos os emails utilizam **templates HTML responsivos** com:
- ✅ Design moderno com gradientes
- ✅ Compatibilidade mobile/desktop
- ✅ Códigos destacados em caixas visuais
- ✅ Alertas de expiração e segurança
- ✅ Branding consistente (Luigarah)

### 🔒 Segurança e Validação de Senha

#### Regras de Senha Obrigatórias

**Todas as senhas devem atender aos seguintes requisitos:**

| Requisito | Descrição |
|-----------|-----------|
| **Comprimento mínimo** | 6 caracteres |
| **Comprimento máximo** | 40 caracteres |
| **Letra maiúscula** | Pelo menos 1 (A-Z) |
| **Letra minúscula** | Pelo menos 1 (a-z) |
| **Número** | Pelo menos 1 (0-9) |
| **Caractere especial** | Pelo menos 1 (!@#$%^&*(),.?":{}|<>) |

**Exemplos de senhas válidas:**
- `SenhaForte123!`
- `Luig@rAh2025`
- `Test#Pass456`

**Validação automática:**
- ✅ No registro de novo usuário
- ✅ Na redefinição de senha
- ✅ Na alteração de senha

#### Segurança de Códigos de Verificação

- **Códigos gerados com SecureRandom** - Máxima aleatoriedade
- **Expiração de 12 horas** - Códigos não ficam válidos indefinidamente
- **Uso único** - Código marcado como "usado" após validação
- **Validação de tipo de conta** - OAuth não pode redefinir senha
- **Rate limiting recomendado** - Evitar spam de códigos

### ⚙️ Configuração

As credenciais da Brevo estão **externalizadas** como variáveis de ambiente:

```properties
# Brevo SMTP
BREVO_SMTP_HOST=smtp-relay.brevo.com
BREVO_SMTP_PORT=587
BREVO_SMTP_USERNAME=${CREDENCIAL_PROTEGIDA}
BREVO_SMTP_PASSWORD=${CREDENCIAL_PROTEGIDA}

# Brevo API
BREVO_API_KEY=${CREDENCIAL_PROTEGIDA}
BREVO_SENDER_EMAIL=luigarah@gmail.com
BREVO_SENDER_NAME=Luigarah
```

> 🔐 **Segurança:** As credenciais reais são protegidas e configuradas via variáveis de ambiente no servidor (Render).

### 📚 Documentação para Frontend

Para integração completa com o frontend, consulte:
- 📄 **`DOCUMENTACAO_API_EMAIL_FRONTEND.md`** - Guia completo com exemplos React/TypeScript
- 📖 **Swagger UI** - Documentação interativa em `/swagger-ui`

---

## 📂 Estrutura do Projeto

### 📦 Estrutura Completa de Pastas e Arquivos

```
luigara-backend/
├── .git/                                    # Controle de versão Git
├── .github/                                 # Configurações GitHub
│   └── java-upgrade/                        # Histórico de upgrade Java
│       ├── .gitignore
│       └── 20251011164952/
│           ├── logs/
│           │   └── 0.log
│           └── progress.md
├── .idea/                                   # Configurações IntelliJ IDEA
├── .gitignore                               # Arquivos ignorados pelo Git
├── Dockerfile                               # Container Docker
├── LICENSE                                  # Licença MIT
├── pom.xml                                  # Configuração Maven e dependências
├── README.md                                # Este arquivo
├── SETUP_LOCAL.md                           # Guia de setup local
├── UPLOAD_IMAGES_GUIDE.md                   # 🆕 Guia de upload de imagens
│
├── src/                                     # Código-fonte
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── luigarah/
│   │   │           ├── AplicacaoLuigara.java                  # Classe principal Spring Boot
│   │   │           │
│   │   │           ├── config/                                # ⚙️ Configurações Globais
│   │   │           │   ├── ConfiguracaoCors.java              # CORS para frontend
│   │   │           │   ├── DotEnvConfig.java                  # Carrega o .env no profile local (DB_URL, ...)
│   │   │           │   ├── FlywayRepairConfig.java            # Configuração Flyway
│   │   │           │   ├── JacksonStringSanitizerConfig.java  # Sanitização JSON
│   │   │           │   ├── JwtAuthenticationFilter.java       # Filtro JWT
│   │   │           │   ├── JwtTokenProvider.java              # Geração/validação JWT
│   │   │           │   ├── LocalStorageConfig.java            # 🆕 Config storage local
│   │   │           │   ├── OpenApiConfig.java                 # Swagger/OpenAPI
│   │   │           │   ├── SecurityConfig.java                # Spring Security
│   │   │           │   └── WebMvcConfig.java                  # 🆕 Config MVC e recursos estáticos
│   │   │           │
│   │   │           ├── controller/                            # 🎮 Controladores REST
│   │   │           │   ├── autenticacao/
│   │   │           │   │   └── ControladorAutenticacao.java   # Login, registro, perfil, foto
│   │   │           │   ├── carrinho/
│   │   │           │   │   └── CarrinhoController.java        # Carrinho de compras
│   │   │           │   ├── doc/                               # Documentação OpenAPI
│   │   │           │   │   ├── AuthControllerDoc.java
│   │   │           │   │   ├── CarrinhoControllerDoc.java
│   │   │           │   │   ├── EstoqueControllerDoc.java
│   │   │           │   │   ├── IdentidadeControllerDoc.java
│   │   │           │   │   ├── ListaDesejoControllerDoc.java
│   │   │           │   │   ├── PadraoTamanhoControllerDoc.java
│   │   │           │   │   ├── ProdutoControllerDoc.java
│   │   │           │   │   ├── ProdutoIdentidadeControllerDoc.java
│   │   │           │   │   └── TamanhoControllerDoc.java
│   │   │           │   ├── estoque/
│   │   │           │   │   └── ControladorEstoque.java        # Gestão de estoque
│   │   │           │   ├── identidade/
│   │   │           │   │   └── ControladorIdentidade.java     # Cores/variações
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoController.java     # Lista de desejos
│   │   │           │   ├── produto/
│   │   │           │   │   ├── ControladorProduto.java        # CRUD produtos
│   │   │           │   │   └── ControladorProdutoIdentidade.java
│   │   │           │   ├── storage/                           # 🆕 Upload de Imagens
│   │   │           │   │   └── ImageUploadController.java     # Upload para R2/Local
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── ControladorTamanho.java        # CRUD tamanhos
│   │   │           │   │   └── PadraoTamanhoController.java   # Padrões (P, M, G)
│   │   │           │   └── usuario/
│   │   │           │       ├── EnderecoController.java        # Gerenciar endereços
│   │   │           │       └── UsuarioAdminController.java    # Admin usuários
│   │   │           │
│   │   │           ├── dto/                                   # 📦 Data Transfer Objects
│   │   │           │   ├── autenticacao/
│   │   │           │   │   ├── AlterarSenhaRequestDTO.java
│   │   │           │   │   ├── AuthResponseDTO.java
│   │   │           │   │   ├── LoginRequestDTO.java
│   │   │           │   │   ├── OAuthSyncRequest.java          # 🆕 Request OAuth Sync
│   │   │           │   │   └── RegistroRequestDTO.java
│   │   │           │   ├── carrinho/
│   │   │           │   │   ├── CarrinhoItemDTO.java
│   │   │           │   │   └── CarrinhoItemRequestDTO.java
│   │   │           │   ├── identidade/
│   │   │           │   │   ├── IdentidadeCreateDTO.java
│   │   │           │   │   ├── IdentidadeDTO.java
│   │   │           │   │   └── IdentidadeUpdateDTO.java
│   │   │           │   ├── jackson/
│   │   │           │   │   ├── ObjectFlexDeserializer.java
│   │   │           │   │   └── StringListFlexDeserializer.java
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoItemDTO.java
│   │   │           │   ├── produto/
│   │   │           │   │   ├── ProdutoCreateDTO.java
│   │   │           │   │   ├── ProdutoDTO.java
│   │   │           │   │   ├── ProdutoIdentidadeDTO.java
│   │   │           │   │   └── RespostaProdutoDTO.java
│   │   │           │   ├── storage/                           # 🆕 DTOs de Upload
│   │   │           │   │   └── ImageUploadResponse.java       # Response de upload
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── PadraoAtualizacaoDTO.java
│   │   │           │   │   ├── PadraoItemDTO.java
│   │   │           │   │   ├── ProdutoTamanhoDTO.java
│   │   │           │   │   └── TamanhoDTO.java
│   │   │           │   └── usuario/
│   │   │           │       ├── AtualizarPerfilRequest.java    # Request atualizar perfil
│   │   │           │       ├── EnderecoDTO.java
│   │   │           │       ├── UsuarioAdminDTO.java
│   │   │           │       ├── UsuarioAdminUpdateDTO.java
│   │   │           │       └── UsuarioDTO.java
│   │   │           │
│   │   │           ├── exception/                             # ⚠️ Exceções
│   │   │           │   ├── GlobalExceptionHandler.java        # Tratamento global
│   │   │           │   ├── ProductNotFoundException.java
│   │   │           │   ├── RecursoNaoEncontradoException.java
│   │   │           │   └── RegraDeNegocioException.java
│   │   │           │
│   │   │           ├── mapper/                                # 🔄 Mapeadores Entity ↔ DTO
│   │   │           │   ├── carrinho/
│   │   │           │   │   └── CarrinhoItemMapper.java
│   │   │           │   ├── identidade/
│   │   │           │   │   └── IdentidadeMapper.java
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoItemMapper.java
│   │   │           │   ├── produto/
│   │   │           │   │   └── ProdutoMapper.java
│   │   │           │   ├── tamanho/
│   │   │           │   │   └── TamanhoMapper.java
│   │   │           │   └── usuario/
│   │   │           │       ├── EnderecoMapper.java
│   │   │           │       └── UsuarioMapper.java
│   │   │           │
│   │   │           ├── model/                                 # 🗃️ Entidades JPA
│   │   │           │   ├── autenticacao/
│   │   │           │   │   ├── AuthProvider.java              # Enum (LOCAL, GOOGLE, FACEBOOK)
│   │   │           │   │   ├── Role.java                      # Enum (USER, ADMIN)
│   │   │           │   │   └── Usuario.java                   # Entidade usuário
│   │   │           │   ├── carrinho/
│   │   │           │   │   └── CarrinhoItem.java              # Item no carrinho
│   │   │           │   ├── enums/
│   │   │           │   │   └── PadraoTamanho.java             # Enum (PP, P, M, G, GG, etc)
│   │   │           │   ├── identidade/
│   │   │           │   │   └── Identidade.java                # Cores/variações
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoItem.java           # Item favorito
│   │   │           │   ├── produto/
│   │   │           │   │   └── Produto.java                   # Entidade produto
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── id/
│   │   │           │   │   │   └── ProdutoTamanhoId.java      # Chave composta
│   │   │           │   │   ├── ProdutoTamanho.java            # Relação produto-tamanho
│   │   │           │   │   └── Tamanho.java                   # Entidade tamanho
│   │   │           │   └── usuario/
│   │   │           │       └── Endereco.java                  # Endereço de entrega
│   │   │           │
│   │   │           ├── repository/                            # 🗄️ Repositórios JPA
│   │   │           │   ├── autenticacao/
│   │   │           │   │   ├── EnderecoRepository.java
│   │   │           │   │   └── UsuarioRepository.java
│   │   │           │   ├── carrinho/
│   │   │           │   │   └── CarrinhoItemRepository.java
│   │   │           │   ├── identidade/
│   │   │           │   │   └── RepositorioIdentidade.java
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoItemRepository.java
│   │   │           │   ├── produto/
│   │   │           │   │   └── RepositorioProduto.java
│   │   │           │   └── tamanho/
│   │   │           │       ├── RepositorioPadraoProduto.java
│   │   │           │       ├── RepositorioPadraoTamanho.java
│   │   │           │       ├── RepositorioProdutoTamanho.java
│   │   │           │       └── RepositorioTamanho.java
│   │   │           │
│   │   │           ├── service/                               # 🔧 Serviços (Lógica de Negócio)
│   │   │           │   ├── autenticacao/
│   │   │           │   │   ├── AuthService.java               # Login, registro, JWT
│   │   │           │   │   └── CustomUserDetailsService.java  # Spring Security
│   │   │           │   ├── carrinho/
│   │   │           │   │   └── CarrinhoService.java
│   │   │           │   ├── estoque/
│   │   │           │   │   ├── impl/
│   │   │           │   │   │   └── ServicoEstoqueImpl.java
│   │   │           │   │   └── ServicoEstoque.java
│   │   │           │   ├── identidade/
│   │   │           │   │   └── ServicoIdentidade.java
│   │   │           │   ├── listadesejos/
│   │   │           │   │   └── ListaDesejoService.java
│   │   │           │   ├── produto/
│   │   │           │   │   ├── impl/
│   │   │           │   │   │   └── ServicoProdutoImpl.java
│   │   │           │   │   ├── ServicoProduto.java
│   │   │           │   │   └── ServicoProdutoIdentidade.java
│   │   │           │   ├── storage/                           # 🆕 Serviços de Upload
│   │   │           │   │   ├── ImageStorageService.java       # Interface storage
│   │   │           │   │   ├── LocalImageStorageService.java  # Storage local (dev)
│   │   │           │   │   └── S3ImageStorageService.java     # Storage R2 (prod)
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── impl/
│   │   │           │   │   │   ├── ServicoPadraoTamanhoImpl.java
│   │   │           │   │   │   └── ServicoTamanhoImpl.java
│   │   │           │   │   ├── ServicoPadraoTamanho.java
│   │   │           │   │   └── ServicoTamanho.java
│   │   │           │   └── usuario/
│   │   │           │       └── UsuarioAdminService.java
│   │   │           │
│   │   │           ├── util/                                  # 🛠️ Utilitários
│   │   │           │   ├── GerarHashSenha.java                # Gerar hash BCrypt
│   │   │           │   ├── JsonStringCleaner.java             # Limpeza JSON
│   │   │           │   └── UrlCleaner.java                    # Limpeza URLs
│   │   │           │
│   │   │           └── web/                                   # 🌐 Controllers Web
│   │   │               └── HomeController.java                # Página inicial
│   │   │
│   │   └── resources/                                         # Recursos
│   │       ├── application.properties                         # Config principal
│   │       ├── application-local.properties                   # Config ambiente local
│   │       ├── application-prod.properties                    # Config produção + R2
│   │       ├── static/                                        # 🆕 Arquivos estáticos (HTML, CSS)
│   │       ├── uploads/                                       # 🆕 Upload local (development)
│   │       │   ├── produtos/                                  # Imagens de produtos
│   │       │   ├── usuarios/                                  # Fotos de perfil
│   │       │   └── outros/                                    # Outras imagens
│   │       └── db/
│   │           └── migration/                                 # Migrações Flyway
│   │               ├── V1__schema.sql                         # Schema completo (PostgreSQL)
│   │               ├── V2__seed_identidades_tamanhos.sql      # Identidades + catálogo de tamanhos
│   │               ├── V3__seed_produtos.sql                  # 135 produtos (backup do Oracle)
│   │               ├── V4__seed_estoque_inicial.sql           # Estoque inicial (10 por tamanho)
│   │               └── V5__novos_tamanhos_usa_e_sapatos.sql   # XXL/XXXL (USA) e 30/31 (sapatos)
│   │
│   └── test/                                                  # 🧪 Testes
│       └── java/
│           ├── TestCORS.java                                  # Teste CORS
│           └── com/
│               └── luigarah/
│                   ├── repository/
│                   │   └── RepositoriosPostgresTest.java      # Migrations + queries no PostgreSQL embarcado
│                   └── service/
│                       ├── PasswordTest.java                  # Teste de senhas
│                       └── impl/
│                           ├── ServicoEstoqueImplTest.java    # Teste estoque
│                           └── ServicoTamanhoImplTest.java    # Teste tamanhos
│
└── target/                                                    # Arquivos compilados (gerados)
    ├── classes/                                               # Classes compiladas
    ├── generated-sources/
    ├── generated-test-sources/
    └── test-classes/
```

### 📊 Estatísticas do Projeto

- **Total de Pacotes:** 40+
- **Total de Classes Java:** 120+
- **Controllers:** 12
- **Services:** 15+
- **Repositories:** 12
- **DTOs:** 30+
- **Entities (Models):** 12
- **Mappers:** 7
- **Configurações:** 10
- **Exceções Customizadas:** 4
- **Utilitários:** 3
- **Testes:** 5+

### 📦 Módulos Funcionais

O projeto está **100% organizado em 9 módulos funcionais independentes**:

```
src/main/java/com/luigarah/
│
├── 🔐 autenticacao/     # Autenticação, login, registro, JWT, OAuth
├── 📦 produto/          # Produtos e suas identidades
├── 🛒 carrinho/         # Carrinho de compras
├── ❤️  listadesejos/    # Lista de desejos/favoritos
├── 📏 tamanho/          # Tamanhos e padrões
├── 🏷️  identidade/      # Identidades de produtos (cores/variações)
├── 📊 estoque/          # Controle de estoque
├── 👥 usuario/          # Administração de usuários (ADMIN)
└── 📸 storage/          # 🆕 Upload de imagens (R2/Local)
```

Cada módulo possui suas próprias camadas isoladas:

```
controller/{modulo}/  → service/{modulo}/  → repository/{modulo}/
        ↓                     ↓                      ↓
    dto/{modulo}/        model/{modulo}/       mapper/{modulo}/
```

---

### 🔐 Módulo de Autenticação

**Localização:** `com.luigarah.controller.autenticacao`, `service.autenticacao`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ControladorAutenticacao.java` - Endpoints de autenticação
  - POST `/api/auth/login` - Login com email/senha
  - POST `/api/auth/registrar` - Registro de novo usuário
  - POST `/api/auth/oauth/sync` - 🆕 Sincronizar conta OAuth (Google/Facebook/GitHub)
  - GET `/api/auth/perfil` - Visualizar perfil (autenticado)
  - PUT `/api/auth/perfil` - Atualizar perfil (autenticado)
  - PUT `/api/auth/perfil/foto` - 🆕 Atualizar foto de perfil por URL (autenticado)
  - POST `/api/auth/perfil/foto/upload` - 🆕 Upload de foto de perfil (autenticado)
  - DELETE `/api/auth/perfil/foto` - 🆕 Remover foto de perfil (autenticado)
  - PUT `/api/auth/alterar-senha` - Alterar senha (autenticado)

**Services:**
- `AuthService.java` - Lógica de autenticação, registro, JWT
- `CustomUserDetailsService.java` - Carregamento de usuários para Spring Security

**Models:**
- `Usuario.java` - Entidade principal de usuário
  - Campos: id, nome, sobrenome, email, senha (hash BCrypt), telefone, dataNascimento, genero, role, provider, etc.
  - Relacionamentos: OneToMany com Endereco, CarrinhoItem, ListaDesejoItem
- `Role.java` - Enum de papéis (USER, ADMIN)
- `AuthProvider.java` - Enum de provedores (LOCAL, GOOGLE, FACEBOOK)

**DTOs:**
- `LoginRequestDTO.java` - Dados para login (email, senha)
- `RegistroRequestDTO.java` - Dados para registro (nome, email, senha, etc.)
- `AuthResponseDTO.java` - Resposta com token JWT e dados do usuário
- `AlterarSenhaRequestDTO.java` - Dados para alteração de senha

**Repositories:**
- `UsuarioRepository.java` - Acesso a dados de usuários
  - Métodos: findByEmail, existsByEmail, etc.
- `EnderecoRepository.java` - Acesso a dados de endereços

**Mappers:**
- `UsuarioMapper.java` - Conversão Usuario ↔ UsuarioDTO
- `EnderecoMapper.java` - Conversão Endereco ↔ EnderecoDTO

---

### 📦 Módulo de Produtos

**Localização:** `com.luigarah.controller.produto`, `service.produto`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ControladorProduto.java` - CRUD de produtos
  - GET `/api/produtos` - Listar todos (público)
  - GET `/api/produtos/{id}` - Buscar por ID (público)
  - POST `/api/produtos` - Criar produto (ADMIN)
  - PUT `/api/produtos/{id}` - Atualizar produto (ADMIN)
  - DELETE `/api/produtos/{id}` - Deletar produto (ADMIN)
- `ControladorProdutoIdentidade.java` - Gerenciar identidades de produtos

**Services:**
- `ServicoProduto.java` - Interface de serviço
- `ServicoProdutoImpl.java` - Implementação da lógica de negócio
- `ServicoProdutoIdentidade.java` - Lógica de identidades

**Models:**
- `Produto.java` - Entidade de produto
  - Campos: id, nome, descricao, preco, categoria, imagemUrl, ativo, etc.
  - Relacionamentos: ManyToOne com Identidade, OneToMany com ProdutoTamanho

**DTOs:**
- `ProdutoDTO.java` - Representação completa do produto
- `ProdutoCreateDTO.java` - Dados para criação
- `ProdutoIdentidadeDTO.java` - Produto com identidade
- `RespostaProdutoDTO.java` - Resposta customizada

**Repositories:**
- `RepositorioProduto.java` - Acesso a dados de produtos
  - Métodos: findByNomeContaining, findByCategoria, findByAtivo, etc.

**Mappers:**
- `ProdutoMapper.java` - Conversão Produto ↔ DTOs

---

### 🛒 Módulo de Carrinho

**Localização:** `com.luigarah.controller.carrinho`, `service.carrinho`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `CarrinhoController.java` - Gerenciamento do carrinho
  - GET `/api/carrinho` - Listar itens (autenticado)
  - POST `/api/carrinho` - Adicionar item (autenticado)
  - PUT `/api/carrinho/{id}` - Atualizar quantidade (autenticado)
  - DELETE `/api/carrinho/{id}` - Remover item (autenticado)
  - DELETE `/api/carrinho` - Limpar carrinho (autenticado)
  - GET `/api/carrinho/count` - Contar itens (autenticado)

**Services:**
- `CarrinhoService.java` - Lógica de carrinho isolada por usuário

**Models:**
- `CarrinhoItem.java` - Item no carrinho
  - Campos: id, usuario, produto, tamanho (opcional), quantidade, dataCriacao
  - Relacionamentos: ManyToOne com Usuario, Produto, Tamanho

**DTOs:**
- `CarrinhoItemDTO.java` - Representação do item
- `CarrinhoItemRequestDTO.java` - Dados para adicionar item

**Repositories:**
- `CarrinhoItemRepository.java` - Acesso a itens do carrinho
  - Métodos: findByUsuarioId, countByUsuarioId, deleteByUsuarioId, etc.

**Mappers:**
- `CarrinhoItemMapper.java` - Conversão CarrinhoItem ↔ DTOs

---

### ❤️ Módulo de Lista de Desejos

**Localização:** `com.luigarah.controller.listadesejos`, `service.listadesejos`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ListaDesejoController.java` - Gerenciamento de favoritos
  - GET `/api/lista-desejos` - Listar favoritos (autenticado)
  - POST `/api/lista-desejos/{produtoId}` - Adicionar (autenticado)
  - DELETE `/api/lista-desejos/{id}` - Remover item (autenticado)
  - DELETE `/api/lista-desejos/produto/{produtoId}` - Remover por produto (autenticado)
  - GET `/api/lista-desejos/verificar/{produtoId}` - Verificar se está nos favoritos (autenticado)
  - DELETE `/api/lista-desejos` - Limpar lista (autenticado)
  - GET `/api/lista-desejos/count` - Contar itens (autenticado)

**Services:**
- `ListaDesejoService.java` - Lógica de favoritos isolada por usuário

**Models:**
- `ListaDesejoItem.java` - Item na lista de desejos
  - Campos: id, usuario, produto, dataCriacao
  - Relacionamentos: ManyToOne com Usuario, Produto

**DTOs:**
- `ListaDesejoItemDTO.java` - Representação do item

**Repositories:**
- `ListaDesejoItemRepository.java` - Acesso a itens da lista
  - Métodos: findByUsuarioId, existsByUsuarioIdAndProdutoId, etc.

**Mappers:**
- `ListaDesejoItemMapper.java` - Conversão ListaDesejoItem ↔ DTOs

---

### 📏 Módulo de Tamanhos

**Localização:** `com.luigarah.controller.tamanho`, `service.tamanho`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ControladorTamanho.java` - CRUD de tamanhos
- `PadraoTamanhoController.java` - Gerenciar padrões (PP, P, M, G, GG, etc.)

**Services:**
- `ServicoTamanho.java` - Interface de serviço
- `ServicoTamanhoImpl.java` - Implementação
- `ServicoPadraoTamanho.java` - Lógica de padrões

**Models:**
- `Tamanho.java` - Entidade de tamanho
  - Campos: id, nome, sigla, ordem, ativo
- `ProdutoTamanho.java` - Relação produto-tamanho com estoque
  - Campos: id (composto), produto, tamanho, quantidadeEstoque
- `ProdutoTamanhoId.java` - Chave composta
- `PadraoTamanho.java` - Enum (PP, P, M, G, GG, XG, XXG, UNICO)

**DTOs:**
- `TamanhoDTO.java` - Representação de tamanho
- `ProdutoTamanhoDTO.java` - Tamanho com estoque
- `PadraoItemDTO.java` - Item de padrão
- `PadraoAtualizacaoDTO.java` - Atualização de padrão

**Repositories:**
- `RepositorioTamanho.java` - Acesso a tamanhos
- `RepositorioProdutoTamanho.java` - Relação produto-tamanho
- `RepositorioPadraoProduto.java` - Padrões de produtos
- `RepositorioPadraoTamanho.java` - Padrões de tamanhos

**Mappers:**
- `TamanhoMapper.java` - Conversão Tamanho ↔ DTOs

---

### 🏷️ Módulo de Identidades

**Localização:** `com.luigarah.controller.identidade`, `service.identidade`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ControladorIdentidade.java` - CRUD de identidades (cores/variações)
  - GET `/api/identidades` - Listar (público)
  - POST `/api/identidades` - Criar (ADMIN)
  - PUT `/api/identidades/{id}` - Atualizar (ADMIN)
  - DELETE `/api/identidades/{id}` - Deletar (ADMIN)

**Services:**
- `ServicoIdentidade.java` - Lógica de identidades

**Models:**
- `Identidade.java` - Entidade de identidade
  - Campos: id, nome, corHex, imagemUrl, ativo

**DTOs:**
- `IdentidadeDTO.java` - Representação completa
- `IdentidadeCreateDTO.java` - Dados para criação
- `IdentidadeUpdateDTO.java` - Dados para atualização

**Repositories:**
- `RepositorioIdentidade.java` - Acesso a identidades

**Mappers:**
- `IdentidadeMapper.java` - Conversão Identidade ↔ DTOs

---

### 📊 Módulo de Estoque

**Localização:** `com.luigarah.controller.estoque`, `service.estoque`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ControladorEstoque.java` - Gestão de estoque
  - GET `/api/estoque/produto/{id}` - Consultar estoque (público)
  - PATCH `/api/estoque/produto/{id}/tamanho/{tamanhoId}` - Atualizar (ADMIN)

**Services:**
- `ServicoEstoque.java` - Interface de serviço
- `ServicoEstoqueImpl.java` - Lógica de controle de estoque

---

### 👥 Módulo de Administração de Usuários

**Localização:** `com.luigarah.controller.usuario`, `service.usuario`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `UsuarioAdminController.java` - Gestão de usuários (ADMIN apenas)
  - GET `/api/admin/usuarios` - Listar todos os usuários
  - GET `/api/admin/usuarios/paginado` - Listar com paginação
  - GET `/api/admin/usuarios/{id}` - Buscar usuário por ID
  - GET `/api/admin/usuarios/buscar/nome` - Buscar por nome
  - GET `/api/admin/usuarios/buscar/email` - Buscar por email
  - GET `/api/admin/usuarios/buscar/role/{role}` - Buscar por role
  - GET `/api/admin/usuarios/buscar/status/{ativo}` - Buscar por status
  - PUT `/api/admin/usuarios/{id}` - Atualizar usuário
  - PATCH `/api/admin/usuarios/{id}/desativar` - Desativar usuário
  - PATCH `/api/admin/usuarios/{id}/ativar` - Ativar usuário
  - PUT `/api/admin/usuarios/{id}/foto` - 🆕 Atualizar foto por URL (ADMIN)
  - POST `/api/admin/usuarios/{id}/foto/upload` - 🆕 Upload de foto (ADMIN)
  - DELETE `/api/admin/usuarios/{id}/foto` - 🆕 Remover foto (ADMIN)
  - GET `/api/admin/usuarios/estatisticas` - Estatísticas de usuários

- `EnderecoController.java` - Gestão de endereços do usuário
  - GET `/api/enderecos` - Listar endereços do usuário autenticado
  - POST `/api/enderecos` - Adicionar novo endereço
  - PUT `/api/enderecos/{id}` - Atualizar endereço
  - DELETE `/api/enderecos/{id}` - Deletar endereço
  - PATCH `/api/enderecos/{id}/principal` - Definir como principal

**Services:**
- `UsuarioAdminService.java` - Lógica de administração de usuários
  - Listagem, busca, atualização de usuários
  - Desativação/ativação de contas
  - Gerenciamento de foto de perfil
  - Estatísticas e contadores
  - Conformidade LGPD

**Models:**
- `Usuario.java` - Entidade principal de usuário
- `Endereco.java` - Entidade de endereço

**DTOs:**
- `UsuarioAdminDTO.java` - DTO para visualização admin (sem dados sensíveis)
- `UsuarioAdminUpdateDTO.java` - DTO para atualização admin
- `EnderecoDTO.java` - DTO de endereço

**Repositories:**
- `UsuarioRepository.java` - Acesso a dados de usuários
- `EnderecoRepository.java` - Acesso a dados de endereços

**Mappers:**
- `UsuarioMapper.java` - Conversão Usuario ↔ DTOs
- `EnderecoMapper.java` - Conversão Endereco ↔ DTO

---

### 📸 Módulo de Upload de Imagens (Storage)

**Localização:** `com.luigarah.controller.storage`, `service.storage`, etc.

#### 📁 Arquivos e Funções

**Controllers:**
- `ImageUploadController.java` - Endpoints de upload de imagens
  - POST `/api/imagens/upload` - Upload de uma imagem
  - POST `/api/imagens/upload/multiple` - Upload de múltiplas imagens (máx. 10)

**Services:**
- `ImageStorageService.java` - **Interface de storage**
  - Abstração para diferentes implementações (local/cloud)
  - Métodos: save(), isValidImageType(), generateKey()

- `LocalImageStorageService.java` - **Implementação para desenvolvimento**
  - Ativo apenas no perfil `local`
  - Salva arquivos em `src/main/resources/uploads/`
  - URLs acessíveis via `http://localhost:8080/uploads/`

- `S3ImageStorageService.java` - **Implementação para produção**
  - Ativo em todos os perfis exceto `local`
  - Upload para Cloudflare R2 (S3-compatible)
  - URLs públicas configuráveis via `STORAGE_PUBLIC_BASE_URL`

**DTOs:**
- `ImageUploadResponse.java` - Resposta de upload com URL, metadata

**Configurações:**
- `LocalStorageConfig.java` - Configura pasta de uploads locais
- `WebMvcConfig.java` - Mapeia `/uploads/` para recursos estáticos

#### 🔑 Configuração por Ambiente

**Desenvolvimento (perfil local):**
```properties
# application-local.properties
storage.local.basePath=uploads
storage.local.baseUrl=http://localhost:8080/uploads
```

**Produção (Cloudflare R2):**
```properties
# application.properties (base)
storage.bucket=${STORAGE_BUCKET:luigarah-prod}
storage.publicBaseUrl=${STORAGE_PUBLIC_BASE_URL:}
aws.region=auto
aws.s3.endpoint=${AWS_S3_ENDPOINT:}
aws.credentials.accessKey=${AWS_ACCESS_KEY_ID:}
aws.credentials.secretKey=${AWS_SECRET_ACCESS_KEY:}
```

**Variáveis de Ambiente no Render:**
```bash
# Cloudflare R2 - Credenciais
AWS_ACCESS_KEY_ID=.....
AWS_SECRET_ACCESS_KEY=.....

# Cloudflare R2 - Configuração
R2_ACCOUNT_ID=....
STORAGE_BUCKET=....

# Endpoint PRIVADO (para upload via SDK)
AWS_S3_ENDPOINT=....

# Domínio PÚBLICO (para download/visualização)
STORAGE_PUBLIC_BASE_URL=....
```

> **📖 Guia Completo:** Consulte [CONFIGURACAO_RENDER_R2.md](./CONFIGURACAO_RENDER_R2.md) para instruções detalhadas de configuração do Cloudflare R2.

#### 🔄 Fluxo de Upload

**Upload de Imagem:**
```
1. Cliente → POST /api/imagens/upload (multipart/form-data)
2. Backend valida arquivo (tipo MIME, tamanho)
3. Backend gera key única: "produtos/1705234567890-produto.jpg"
4. Backend faz upload para R2 via AWS S3 SDK
5. Backend monta URL pública: publicBaseUrl + "/" + key
6. Backend retorna: "https://pub-xxxxx.r2.dev/produtos/1705234567890-produto.jpg"
7. Frontend usa URL para exibir imagem
```

**Diferença entre Endpoints:**

| Tipo | Uso | Requer Auth | Exemplo |
|------|-----|-------------|---------|
| **Endpoint Privado** | Upload via SDK | Sim (accessKey) | `https://[ACCOUNT_ID].r2.cloudflarestorage.com` |
| **Domínio Público** | Download/visualização | Não | `https://pub-xxxxx.r2.dev` |

#### 📦 Estrutura de Pastas no R2

```
luigara-prod/                    # Bucket
├── produtos/                     # Imagens de produtos
│   ├── 1705234567890-produto1.jpg
│   ├── 1705234568901-produto2.png
│   └── ...
├── usuarios/                     # Fotos de perfil
│   ├── 1705234569012-admin-luigarah-com.jpg
│   ├── 1705234570123-user-email-com.png
│   └── ...
└── outros/                       # Outras imagens
    └── ...
```

#### 🔐 AWS SDK para Java v2

**Dependências no pom.xml:**
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.26</version>
</dependency>
```

**Configuração do Cliente S3:**
```java
S3Client s3 = S3Client.builder()
    .region(Region.of("auto"))  // DEVE SER "auto"
    .endpointOverride(URI.create(endpoint))
    .credentialsProvider(StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
    ))
    .serviceConfiguration(S3Configuration.builder()
        .pathStyleAccessEnabled(true)  // Obrigatório para R2
        .build())
    .build();
```

**⚠️ Pontos Críticos:**
- ✅ Região: **"auto"** 
- ✅ `pathStyleAccessEnabled`: **true** (obrigatório para R2)
- ✅ Endpoint: URL completa com `https://`
- ✅ Domínio público: Configurar no painel R2 → Settings → Public Access

#### 📊 Limites e Validações

**Limites Configurados:**
- ✅ Tamanho máximo por arquivo: **5MB**
- ✅ Upload múltiplo: **10 arquivos** simultaneamente
- ✅ Tamanho máximo da requisição: **5MB**

**Formatos Aceitos:**
- ✅ `image/jpeg` ou `image/jpg`
- ✅ `image/png`
- ✅ `image/webp`
- ✅ `image/gif`

**Validações Implementadas:**
```java
// Validação de tipo MIME
if (!imageStorageService.isValidImageType(file.getContentType())) {
    return ResponseEntity.badRequest().body("Tipo de arquivo inválido");
}

// Validação de tamanho
if (file.getSize() > 5 * 1024 * 1024) {
    return ResponseEntity.badRequest().body("Arquivo muito grande (máx 5MB)");
}
```

#### 🔒 Segurança e Boas Práticas

**Implementado:**
- ✅ Validação de tipo MIME no backend (não confiar no frontend)
- ✅ Limite de tamanho rigoroso (5MB)
- ✅ Sanitização de nomes de arquivo (remove caracteres especiais)
- ✅ Geração de nomes únicos (timestamp + nome sanitizado)
- ✅ Organização em pastas lógicas (produtos/, usuarios/, outros/)
- ✅ Credenciais via variáveis de ambiente (nunca no código)
- ✅ Autenticação JWT obrigatória para upload
- ✅ Cache headers para CDN (`max-age=31536000, immutable`)

**Recomendado:**
- 🔄 Configurar CORS no R2 para acesso direto do frontend
- 🔄 Usar domínio personalizado (ex: `cdn.luigarah.com`)
- 🔄 Habilitar Cloudflare CDN para cache global
- 🔄 Implementar compressão de imagens antes do upload
- 🔄 Adicionar validação de dimensões (largura/altura)

**Configuração CORS no Cloudflare R2:**
```json
[
  {
    "AllowedOrigins": ["https://luigarah.vercel.app", "http://localhost:3000"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedHeaders": ["*"],
    "MaxAgeSeconds": 3600
  }
]
```

---

## 🗄️ Banco de Dados

O banco de dados é o **Supabase (PostgreSQL 17)**, região `sa-east-1` (São Paulo).
Todas as tabelas ficam no schema **`app_luigarah`**, criado e versionado pelo **Flyway**.

### 🔄 Migração Oracle → Supabase

Até setembro/2026 o backend usava **Oracle Autonomous Database (Always Free)**, com conexão via wallet
(TNS). O banco Oracle foi excluído e eu migrei o projeto para o **Supabase**.

**O que foi recuperado:**

| Dado | Origem |
|------|--------|
| 135 produtos (60 roupas, 49 sapatos, 26 bolsas) | Backup JSON da tabela `PRODUTOS` do Oracle → `V3__seed_produtos.sql` |
| 4 identidades (homem, mulher, unissex, infantil) | Reconstruídas a partir da documentação da API → `V2` |
| Catálogo de 33 tamanhos (BR PP–G2, USA XXXS–XXXL e sapatos 30–46) | Reconstruído a partir da documentação da API e do frontend → `V2` e `V5` |
| Estoque | **Não havia backup**: 10 unidades por tamanho/bolsa (`V4`), ajustável pelo painel admin |
| Usuários, endereços, carrinhos, listas de desejos | **Não recuperados**: os usuários precisam se cadastrar novamente |

**Correções de dados feitas na importação:**
- Produtos 93–151 tinham `imagens` e `destaques` gravados como arrays corrompidos (JSON dividido nas vírgulas); foram reconstruídos.
- Produto 151 (sapato) estava sem `padrao_tamanho`; recebeu `br`, como os demais sapatos.

**O que mudou no código:**

| Antes (Oracle) | Agora (PostgreSQL / Supabase) |
|----------------|-------------------------------|
| `ojdbc11` + wallet (`oraclepki`, `osdt_*`) | `org.postgresql:postgresql` |
| Wallet extraída no `entrypoint.sh` / `DotEnvConfig` | Conexão só com `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| Migrations em PL/SQL (triggers, `EXECUTE IMMEDIATE`) | Migrations em SQL PostgreSQL (`V1`–`V4`) |
| `MERGE ... USING (SELECT ... FROM dual)` | `INSERT ... ON CONFLICT ... DO UPDATE` |
| `NVL`, `DBMS_LOB.SUBSTR` | `COALESCE`, colunas `TEXT` |
| `@Lob` nos campos longos de `Produto` | `@Column(columnDefinition = "text")` |
| `NUMBER(1)` para booleanos | `BOOLEAN` |
| `SELECT 1 FROM DUAL` (teste de conexão) | `Connection.isValid()` do driver |

### 🔌 Conexão

Use o **Session Pooler** do Supabase (painel → **Connect** → **Direct** → Type **JDBC** → Method **Session pooler**).
A conexão direta (`db.<ref>.supabase.co`) é somente IPv6 e não funciona no Render.

```properties
DB_URL=jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require
DB_USERNAME=postgres.<project-ref>
DB_PASSWORD=<senha do banco>
```

- **Local:** coloque as variáveis no arquivo `.env` na raiz (carregado pelo `DotEnvConfig` no profile `local`).
- **Produção (Render):** configure as mesmas variáveis em *Environment*.

### 📋 Tabelas (schema `app_luigarah`)

| Tabela | Conteúdo |
|--------|----------|
| `produtos` | Catálogo de produtos (textos longos em `TEXT`, `imagens`/`destaques` como JSON em texto) |
| `identidades` | Identidades de produto (homem, mulher, unissex, infantil) |
| `tamanhos` | Catálogo de tamanhos por `categoria` + `padrao` (usa, br, sapatos) |
| `produtos_tamanhos` | Estoque por produto e tamanho (roupas e sapatos) |
| `produtos_estoque` | Estoque consolidado (bolsas) |
| `usuarios`, `enderecos`, `oauth_providers` | Contas, endereços e logins sociais |
| `verification_tokens` | Códigos de verificação de e-mail e redefinição de senha |
| `carrinho_itens`, `lista_desejo_itens` | Carrinho de compras e lista de desejos |

Todas as tabelas têm **RLS ativado sem policies**: o backend conecta como dono das tabelas (não é afetado),
mas qualquer acesso pela Data API pública do Supabase é bloqueado.

### 🧬 Migrations (Flyway)

| Arquivo | Conteúdo |
|---------|----------|
| `V1__schema.sql` | Schema completo: tabelas, sequences, índices, constraints e RLS |
| `V2__seed_identidades_tamanhos.sql` | Identidades e catálogo de tamanhos |
| `V3__seed_produtos.sql` | 135 produtos restaurados do backup |
| `V4__seed_estoque_inicial.sql` | Estoque inicial (10 unidades) |
| `V5__novos_tamanhos_usa_e_sapatos.sql` | Acrescenta XXL/XXXL (USA) e 30/31 (sapatos) e sorteia esses tamanhos, com estoque de 1 a 15, entre os produtos compatíveis |

> ⚠️ Nunca edite uma migration já aplicada — o Flyway valida o checksum e a aplicação não sobe.
> Para mudar o banco, crie um novo arquivo `V5__descricao.sql`.

### 👑 Criando um administrador

Cadastre-se normalmente pelo site e promova a conta no **SQL Editor** do Supabase:

```sql
UPDATE app_luigarah.usuarios SET role = 'ADMIN' WHERE email = 'seu@email.com';
```

### 🧪 Testes do banco

`RepositoriosPostgresTest` sobe um **PostgreSQL embarcado** (sem Docker), aplica as migrations, valida o
mapeamento das entidades (`ddl-auto=validate`) e executa as queries nativas dos repositórios:

```bash
mvn test
```

---

## 📸 Sistema de Upload de Imagens

O sistema de upload de imagens foi projetado para ser **flexível e escalável**, funcionando localmente em desenvolvimento e em nuvem (Cloudflare R2) em produção.

### 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────┐
│         Frontend (React/Next.js)                │
│   - Seleciona arquivo                           │
│   - Envia via multipart/form-data               │
└──────────────────┬──────────────────────────────┘
                   │ POST /api/imagens/upload
                   ↓
┌─────────────────────────────────────────────────┐
│         ImageUploadController                   │
│   - Valida tipo MIME                            │
│   - Valida tamanho                              │
│   - Chama ImageStorageService                   │
└──────────────────┬──────────────────────────────┘
                   │
        ┌──────────┴───────────┐
        ↓                      ↓
┌──────────────────┐  ┌────────────────────┐
│ LocalImageStorage│  │ S3ImageStorage     │
│ (perfil: local)  │  │ (perfil: prod)     │
│ - Salva em disco │  │ - Upload para R2   │
│ - URL: /uploads/ │  │ - URL: pub-xxx.dev │
└──────────────────┘  └────────────────────┘
```

### 🔄 Estratégia de Profiles

O projeto usa **Spring Profiles** para alternar automaticamente entre storage local e cloud:

```java
@Service
@Profile("local")  // Ativo APENAS em desenvolvimento
public class LocalImageStorageService implements ImageStorageService {
    // Salva em src/main/resources/uploads/
}

@Service
@Profile("!local")  // Ativo em todos os ambientes EXCETO local
public class S3ImageStorageService implements ImageStorageService {
    // Salva em Cloudflare R2 via AWS SDK
}
```

### 📝 Endpoints de Upload

#### 1. Upload de Uma Imagem
```http
POST /api/imagens/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

file: [arquivo.jpg]
folder: "produtos"  (opcional, padrão: "outros")
```

**Resposta (sucesso):**
```json
{
  "url": "https://pub-xxxxx.r2.dev/produtos/1705234567890-arquivo.jpg",
  "key": "produtos/1705234567890-arquivo.jpg",
  "size": 152340,
  "contentType": "image/jpeg",
  "folder": "produtos"
}
```

#### 2. Upload Múltiplo
```http
POST /api/imagens/upload/multiple
Content-Type: multipart/form-data
Authorization: Bearer <token>

files: [arquivo1.jpg, arquivo2.png, ...]  (máx 10)
folder: "produtos"  (opcional)
```

**Resposta (sucesso):**
```json
{
  "sucesso": true,
  "mensagem": "3 imagens enviadas com sucesso",
  "urls": [
    "https://pub-xxxxx.r2.dev/produtos/1705234567890-arquivo1.jpg",
    "https://pub-xxxxx.r2.dev/produtos/1705234568901-arquivo2.png",
    "https://pub-xxxxx.r2.dev/produtos/1705234569012-arquivo3.webp"
  ]
}
```

### 🔧 Configuração Passo a Passo

#### 1. Habilitar Acesso Público no Cloudflare R2

1. Acesse https://dash.cloudflare.com/
2. Vá em **R2** → **luigarah-prod**
3. Clique em **Settings** → **Public Access**
4. Clique em **Allow Access**
5. **Copie o domínio público** gerado (ex: `https://pub-xxxxx.r2.dev`)

#### 2. Configurar Variáveis no Render

No painel do Render, adicione as seguintes variáveis:

```bash
# Credenciais (obtidas em R2 → Manage R2 API Tokens)
AWS_ACCESS_KEY_ID=<sua-access-key-aqui>
AWS_SECRET_ACCESS_KEY=<sua-secret-key-aqui>

# Configuração do Bucket
R2_ACCOUNT_ID=<seu-account-id>
STORAGE_BUCKET=luigarah-prod

# Endpoint PRIVADO (para upload)
AWS_S3_ENDPOINT=https://<seu-account-id>.r2.cloudflarestorage.com

# ⚠️ CRÍTICO: Domínio PÚBLICO (para download)
STORAGE_PUBLIC_BASE_URL=https://pub-xxxxx.r2.dev
```

> **⚠️ ATENÇÃO:** O `STORAGE_PUBLIC_BASE_URL` deve ser o domínio público gerado no passo 1, **SEM** o nome do bucket no final.

#### 3. Verificar Configuração

**application.properties:**
```properties
# Base (usado em todos os ambientes)
storage.bucket=${STORAGE_BUCKET:luigarah-prod}
storage.publicBaseUrl=${STORAGE_PUBLIC_BASE_URL:}
aws.region=auto
aws.s3.endpoint=${AWS_S3_ENDPOINT:}
aws.credentials.accessKey=${AWS_ACCESS_KEY_ID:}
aws.credentials.secretKey=${AWS_SECRET_ACCESS_KEY:}
```

**Banco de dados (application.properties):**
```properties
# Supabase - Session Pooler (Project Settings > Database > Connection string > JDBC)
spring.datasource.url=${DB_URL}           # jdbc:postgresql://aws-0-<regiao>.pooler.supabase.com:5432/postgres?sslmode=require
spring.datasource.username=${DB_USERNAME} # postgres.<project-ref>
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.connection-init-sql=SET search_path TO app_luigarah, public
```

### 🧪 Testando o Sistema

#### Teste Local (Desenvolvimento)

```bash
# 1. Iniciar aplicação com perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 2. Fazer upload
curl -X POST http://localhost:8080/api/imagens/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@imagem.jpg" \
  -F "folder=produtos"

# 3. Verificar arquivo salvo
ls src/main/resources/uploads/produtos/

# 4. Acessar imagem no navegador
http://localhost:8080/uploads/produtos/1705234567890-imagem.jpg
```

#### Teste em Produção (Render + R2)

```bash
# 1. Fazer upload
curl -X POST https://luigarah-backend.onrender.com/api/imagens/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@imagem.jpg" \
  -F "folder=produtos"

# 2. Resposta esperada
{
  "url": "https://pub-xxxxx.r2.dev/produtos/1705234567890-imagem.jpg"
}

# 3. Acessar URL no navegador
https://pub-xxxxx.r2.dev/produtos/1705234567890-imagem.jpg
```

### 📊 Logs Esperados

**Sucesso:**
```
✅ S3ImageStorageService inicializado.
   endpoint=https://aef01bde77cd4e5689cde7c9784a36ee.r2.cloudflarestorage.com
   bucket=luigarah-prod
   publicBaseUrl=https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev

📤 Upload recebido: imagem.jpg (152340 bytes) → pasta: produtos
✅ Upload OK para key='produtos/1705234567890-imagem.jpg'
   → https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev/produtos/1705234567890-imagem.jpg
```

**Erro (configuração incorreta):**
```
❌ Falha no upload para key='produtos/xxx.jpg' no bucket 'luigarah-prod':
   The region name 'auto-r2' is not valid. Must be one of: auto
```
→ **Solução:** Alterar `aws.region=auto` (remover `-r2`)

### 🔍 Troubleshooting

| Erro | Causa | Solução |
|------|-------|---------|
| `The region name 'auto-r2' is not valid` | Região incorreta | Usar `aws.region=auto` |
| `<Error><Code>InvalidArgument</Code><Message>Authorization</Message>` | Tentando acessar endpoint privado | Configurar `STORAGE_PUBLIC_BASE_URL` |
| `Credenciais inválidas` | Access Key/Secret errados | Verificar tokens no painel R2 |
| `Bucket não encontrado` | Nome do bucket errado | Confirmar `STORAGE_BUCKET=luigarah-prod` |
| Imagem não carrega no frontend | URL pública incorreta | Habilitar acesso público no R2 |
