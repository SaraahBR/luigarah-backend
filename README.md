# 🛍️ Luigarah Backend - API RESTful

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-ADB-red.svg)](https://www.oracle.com/autonomous-database/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Production](https://img.shields.io/badge/production-Render-blue.svg)](https://luigarah-backend.onrender.com)

> Sistema backend completo para e-commerce de moda com autenticação JWT, gerenciamento de produtos, carrinho de compras e lista de desejos.

**🌐 Produção:** https://luigarah-backend.onrender.com  
**📚 Documentação API:** https://luigarah-backend.onrender.com/swagger-ui/index.html  
**🎨 Frontend:** https://luigarah.vercel.app

---

## 📑 Índice

- [🚀 Visão Geral](#-visão-geral)
- [✨ Funcionalidades](#-funcionalidades)
- [🏗️ Arquitetura e Design](#️-arquitetura-e-design)
- [💻 Tecnologias e Frameworks](#-tecnologias-e-frameworks)
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
  - [⚙️ Configurações Globais](#️-configurações-globais)
  - [🛠️ Utilitários](#️-utilitários)
  - [⚠️ Tratamento de Exceções](#️-tratamento-de-exceções)
- [🔒 Segurança e Autenticação](#-segurança-e-autenticação)
- [🗄️ Banco de Dados](#️-banco-de-dados)
- [📡 Endpoints da API](#-endpoints-da-api)
- [🚀 Como Executar](#-como-executar)
- [🌐 Deploy em Produção](#-deploy-em-produção)
- [📖 Documentação](#-documentação)
- [🧪 Testes](#-testes)
- [👥 Contribuição](#-contribuição)

---

## 🚀 Visão Geral

O **Luigarah Backend** é uma API RESTful robusta e escalável desenvolvida para um e-commerce de moda. O sistema oferece gerenciamento completo de produtos, autenticação segura com JWT, carrinho de compras, lista de desejos e controle de estoque.

### Características Principais

- ✅ **Arquitetura Modular** - Organizado seguindo Clean Architecture e DDD
- ✅ **Autenticação JWT** - Sistema completo com roles (USER/ADMIN)
- ✅ **Segurança Avançada** - Spring Security + validação de senhas fortes
- ✅ **Banco Oracle Cloud** - Oracle Autonomous Database (ADB) Always Free
- ✅ **Documentação Automática** - Swagger/OpenAPI 3.0 completo
- ✅ **Deploy Automatizado** - CI/CD no Render com Docker
- ✅ **Migração de Dados** - Flyway para versionamento do banco
- ✅ **Validação Robusta** - Bean Validation em todos os DTOs
- ✅ **CORS Configurado** - Pronto para frontend em React/Next.js

---

## ✨ Funcionalidades

### 🔐 Autenticação e Autorização
- Login com email e senha (BCrypt)
- Registro de novos usuários com validação completa
- Tokens JWT com expiração configurável (24h padrão)
- Sistema de roles: **USER** (usuário comum) e **ADMIN** (administrador)
- Alteração de senha segura com validação de força
- Suporte preparado para OAuth2 (Google/Facebook)
- Perfil de usuário (visualizar e editar)

### 👥 Administração de Usuários (ADMIN)
- **Visualizar usuários** - Listar todos os usuários com paginação
- **Buscar usuários** - Por nome, email, role ou status
- **Editar usuários** - Alterar nome, sobrenome, email, telefone e role
- **Desativar/Ativar usuários** - Soft delete (mantém dados no banco)
- **Estatísticas** - Contadores de usuários ativos, inativos, por role
- **Conformidade LGPD** - Dados sensíveis protegidos (senha, documentos nunca expostos)
- **Auditoria** - Logs de todas as operações administrativas

> ⚠️ **LGPD:** Administradores podem visualizar dados básicos dos usuários, mas dados sensíveis como senhas, documentos e informações médicas são protegidos e nunca expostos pela API.

### 🛍️ Gerenciamento de Produtos
- CRUD completo de produtos (apenas ADMIN)
- Busca e filtragem de produtos (público)
- Produtos com múltiplas identidades (cores/variações)
- Sistema de tamanhos customizável
- Upload e gerenciamento de imagens
- Categorização e organização

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
- **Módulos por Domínio** - 7 módulos funcionais independentes
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
| **Java** | 21 LTS | Linguagem principal (Oracle JDK) |
| **Maven** | 3.9+ | Gerenciamento de dependências e build |

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
| **Oracle JDBC** | 23.2.0.0 | Driver de conexão Oracle |
| **Hibernate** | 6.2+ | ORM (Object-Relational Mapping) |
| **Flyway** | 9.22+ | Migração e versionamento do banco |
| **Flyway Oracle** | 9.22+ | Suporte específico para Oracle |
| **HikariCP** | 5.0+ | Pool de conexões (incluído no Spring Boot) |

### 🔒 Oracle Security (Wallet/TLS)

| Biblioteca | Versão | Função |
|------------|--------|--------|
| **OraclePKI** | 21.11.0.0 | Suporte a PKI para wallet |
| **OSDT Core** | 21.11.0.0 | Oracle Security Developer Tools |
| **OSDT Cert** | 21.11.0.0 | Certificados SSL/TLS |

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
├── entrypoint.sh                            # Script de inicialização Docker
├── LICENSE                                  # Licença MIT
├── pom.xml                                  # Configuração Maven e dependências
├── README.md                                # Este arquivo
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
│   │   │           │   ├── FlywayRepairConfig.java            # Configuração Flyway
│   │   │           │   ├── JacksonStringSanitizerConfig.java  # Sanitização JSON
│   │   │           │   ├── JwtAuthenticationFilter.java       # Filtro JWT
│   │   │           │   ├── JwtTokenProvider.java              # Geração/validação JWT
│   │   │           │   ├── OpenApiConfig.java                 # Swagger/OpenAPI
│   │   │           │   └── SecurityConfig.java                # Spring Security
│   │   │           │
│   │   │           ├── controller/                            # 🎮 Controladores REST
│   │   │           │   ├── autenticao/
│   │   │           │   │   └── AuthController.java            # Login, registro, perfil
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
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── ControladorTamanho.java        # CRUD tamanhos
│   │   │           │   │   └── PadraoTamanhoController.java   # Padrões (P, M, G)
│   │   │           │   └── usuario/
│   │   │           │       └── UsuarioAdminController.java    # Admin usuários
│   │   │           │
│   │   │           ├── dto/                                   # 📦 Data Transfer Objects
│   │   │           │   ├── autenticacao/
│   │   │           │   │   ├── AlterarSenhaRequestDTO.java
│   │   │           │   │   ├── AuthResponseDTO.java
│   │   │           │   │   ├── LoginRequestDTO.java
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
│   │   │           │   ├── tamanho/
│   │   │           │   │   ├── PadraoAtualizacaoDTO.java
│   │   │           │   │   ├── PadraoItemDTO.java
│   │   │           │   │   ├── ProdutoTamanhoDTO.java
│   │   │           │   │   └── TamanhoDTO.java
│   │   │           │   └── usuario/
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
│   │       ├── application-local.properties                   # Config ambiente local
│   │       ├── application-prod.properties                    # Config produção
│   │       └── db/
│   │           └── migration/                                 # Migrações Flyway
│   │               ├── V1__schema.sql                         # Schema inicial
│   │               ├── V2__seed_tamanhos.sql                  # Seed tamanhos
│   │               └── V3__data.sql                           # Dados iniciais
│   │
│   └── test/                                                  # 🧪 Testes
│       └── java/
│           └── com/
│               └── luigarah/
│                   ├── PasswordTest.java                      # Teste de senhas
│                   └── service/
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

- **Total de Pacotes:** 32+
- **Total de Classes Java:** 100+
- **Controllers:** 10
- **Services:** 12+
- **Repositories:** 12
- **DTOs:** 25+
- **Entities (Models):** 12
- **Mappers:** 6
- **Configurações:** 7
- **Exceções Customizadas:** 4
- **Utilitários:** 3
- **Testes:** 3+

### 📦 Módulos Funcionais

O projeto está **100% organizado em 7 módulos funcionais independentes**:

```
src/main/java/com/luigarah/
│
├── 🔐 autenticacao/     # Autenticação, login, registro, JWT
├── 📦 produto/          # Produtos e suas identidades
├── 🛒 carrinho/         # Carrinho de compras
├── ❤️  listadesejos/    # Lista de desejos/favoritos
├── 📏 tamanho/          # Tamanhos e padrões
├── 🏷️  identidade/      # Identidades de produtos (cores/variações)
└── 📊 estoque/          # Controle de estoque
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
- `AuthController.java` - Endpoints de autenticação
  - POST `/api/auth/login` - Login com email/senha
  - POST `/api/auth/registrar` - Registro de novo usuário
  - GET `/api/auth/perfil` - Visualizar perfil (autenticado)
  - PUT `/api/auth/perfil` - Atualizar perfil (autenticado)
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

### ⚙️ Configurações Globais

**Localização:** `com.luigarah.config`

#### 📁 Arquivos e Funções

| Arquivo | Função |
|---------|--------|
| `SecurityConfig.java` | **Configuração de segurança**<br>- Define endpoints públicos vs autenticados<br>- Configura filtros JWT<br>- Habilita CORS<br>- Desabilita CSRF (API REST stateless) |
| `JwtTokenProvider.java` | **Provedor de tokens JWT**<br>- Gera tokens JWT assinados<br>- Valida tokens<br>- Extrai claims (usuário, role, expiração) |
| `JwtAuthenticationFilter.java` | **Filtro de autenticação**<br>- Intercepta requisições<br>- Extrai token do header Authorization<br>- Valida e autentica usuário no contexto do Spring Security |
| `ConfiguracaoCors.java` | **Configuração CORS**<br>- Define origens permitidas (localhost, Vercel)<br>- Habilita métodos HTTP (GET, POST, PUT, DELETE)<br>- Permite headers customizados |
| `OpenApiConfig.java` | **Configuração Swagger/OpenAPI**<br>- Define informações da API (título, descrição, versão)<br>- Configura autenticação JWT no Swagger UI<br>- Organiza endpoints por tags |
| `FlywayRepairConfig.java` | **Configuração Flyway**<br>- Repair automático em caso de falhas<br>- Validação de migrações |
| `JacksonStringSanitizerConfig.java` | **Sanitização de strings**<br>- Remove caracteres invisíveis<br>- Previne injeção de XSS<br>- Normaliza entradas JSON |

---

### 🛠️ Utilitários

**Localização:** `com.luigarah.util`

#### 📁 Arquivos e Funções

| Arquivo | Função |
|---------|--------|
| `GerarHashSenha.java` | **Utilitário para gerar hash BCrypt**<br>- Gera hash de senhas para testes<br>- Usado para criar senha do admin padrão<br>- Executável via main() |
| `JsonStringCleaner.java` | **Limpeza de strings JSON**<br>- Remove caracteres invisíveis<br>- Remove espaços extras<br>- Previne ataques de injeção |
| `UrlCleaner.java` | **Validação e limpeza de URLs**<br>- Valida formato de URLs<br>- Remove scripts maliciosos<br>- Sanitiza URLs de imagens |

---

### ⚠️ Tratamento de Exceções

**Localização:** `com.luigarah.exception`

#### 📁 Arquivos e Funções

| Arquivo | Função |
|---------|--------|
| `GlobalExceptionHandler.java` | **Tratamento global de exceções**<br>- Captura todas as exceções não tratadas<br>- Retorna respostas padronizadas (JSON)<br>- Log de erros<br>- Previne exposição de stack traces |
| `RecursoNaoEncontradoException.java` | **Exceção para recursos não encontrados**<br>- HTTP 404<br>- Usado quando produto, usuário, etc. não existe |
| `RegraDeNegocioException.java` | **Exceção de regras de negócio**<br>- HTTP 400<br>- Usado para validações de negócio<br>- Ex: "Produto sem estoque", "Senha incorreta" |
| `ProductNotFoundException.java` | **Exceção específica de produto**<br>- HTTP 404<br>- Usado em buscas de produtos |

---

### 📚 Documentação dos Controllers

**Localização:** `com.luigarah.controller.doc`

Todos os 9 controllers possuem interfaces de documentação Swagger:

| Arquivo | Controller Documentado |
|---------|------------------------|
| `AuthControllerDoc.java` | Autenticação e perfil |
| `CarrinhoControllerDoc.java` | Carrinho de compras |
| `EstoqueControllerDoc.java` | Gestão de estoque |
| `IdentidadeControllerDoc.java` | Identidades de produtos |
| `ListaDesejoControllerDoc.java` | Lista de desejos |
| `PadraoTamanhoControllerDoc.java` | Padrões de tamanhos |
| `ProdutoControllerDoc.java` | CRUD de produtos |
| `ProdutoIdentidadeControllerDoc.java` | Produtos com identidades |
| `TamanhoControllerDoc.java` | CRUD de tamanhos |

---

## 🔒 Segurança e Autenticação

### 🛡️ Spring Security

O projeto utiliza **Spring Security 6** com as seguintes configurações:

#### Endpoints Públicos (sem autenticação)
- ✅ GET `/api/produtos/**` - Listagem de produtos
- ✅ GET `/api/tamanhos/**` - Listagem de tamanhos
- ✅ GET `/api/identidades/**` - Listagem de identidades
- ✅ GET `/api/estoque/**` - Consulta de estoque
- ✅ POST `/api/auth/login` - Login
- ✅ POST `/api/auth/registrar` - Registro
- ✅ `/swagger-ui/**` - Documentação Swagger
- ✅ `/v3/api-docs/**` - OpenAPI JSON

#### Endpoints Autenticados (requer token JWT)
- 🔐 Todos os endpoints de escrita (POST, PUT, DELETE)
- 🔐 Carrinho de compras
- 🔐 Lista de desejos
- 🔐 Perfil de usuário

#### Endpoints Restritos (apenas ADMIN)
- 🔒 POST/PUT/DELETE `/api/produtos/**`
- 🔒 POST/PUT/DELETE `/api/tamanhos/**`
- 🔒 POST/PUT/DELETE `/api/identidades/**`
- 🔒 PATCH `/api/estoque/**`
- 🔒 **Todos os endpoints** `/api/admin/usuarios/**`

### 🛡️ Proteção de Dados (LGPD)

O sistema implementa **proteção total de dados sensíveis** conforme a Lei Geral de Proteção de Dados (LGPD):

#### Dados Nunca Expostos pela API:
- ❌ **Senhas** (armazenadas apenas como hash BCrypt)
- ❌ **Documentos** (CPF, RG, passaporte)
- ❌ **Dados bancários** (cartões de crédito, contas)
- ❌ **Informações médicas**
- ❌ **Dados biométricos**

#### Dados Visíveis apenas ao Próprio Usuário:
- ✅ Próprio perfil completo (via `/api/auth/perfil`)
- ✅ Próprio histórico de pedidos
- ✅ Próprios endereços de entrega

#### Dados Visíveis ao ADMIN (sem dados sensíveis):
- ✅ Nome, sobrenome, email
- ✅ Telefone, data de nascimento, gênero
- ✅ Role, status ativo/inativo
- ✅ Provedor de autenticação
- ❌ **NUNCA:** senha, documentos, dados sensíveis

#### Permissões do ADMIN:
**PODE:**
- ✅ Visualizar lista de usuários (sem dados sensíveis)
- ✅ Editar nome, sobrenome, email, telefone
- ✅ Alterar role (USER ↔ ADMIN)
- ✅ Desativar/ativar usuários
- ✅ Ver estatísticas gerais

**NÃO PODE:**
- ❌ Ver ou alterar senhas de usuários
- ❌ Acessar dados sensíveis (documentos, etc.)
- ❌ Fazer login como outro usuário
- ❌ Deletar usuários permanentemente (apenas desativar)

#### Logs e Auditoria:
- 📝 Todas as operações administrativas são logadas
- 📝 Registro de quem alterou o quê e quando
- 📝 Conformidade com Art. 48 da LGPD

### 🔑 Autenticação JWT

**Fluxo de Autenticação:**

```
1. Usuário faz login → POST /api/auth/login { email, senha }
2. Backend valida credenciais (BCrypt)
3. Backend gera token JWT assinado (HS256)
4. Token retornado para o cliente
5. Cliente inclui token em requisições: Authorization: Bearer {token}
6. JwtAuthenticationFilter valida token
7. Spring Security autentica usuário no contexto
8. Requisição processada com usuário autenticado
```

**Estrutura do Token JWT:**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "usuario@example.com",
    "role": "USER",
    "iat": 1697280000,
    "exp": 1697366400
  },
  "signature": "..."
}
```

**Configuração:**
- **Algoritmo:** HS256 (HMAC SHA-256)
- **Secret:** Mínimo 256 bits (configurável via variável de ambiente)
- **Expiração:** 24 horas (86400000ms) - configurável
- **Claims:** email (sub), role, issued at, expiration

### 🔐 Validação de Senhas Fortes

**Requisitos obrigatórios:**
- ✅ Mínimo 6 caracteres
- ✅ Máximo 100 caracteres
- ✅ Pelo menos 1 letra maiúscula (A-Z)
- ✅ Pelo menos 1 letra minúscula (a-z)
- ✅ Pelo menos 1 número (0-9)
- ✅ Pelo menos 1 caractere especial (@$!%*?&)

**Regex de validação:**
```regex
^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,100}$
```

**Exemplos:**
- ✅ `Admin@123` - Válida
- ✅ `MinhaSenh@Segura2025!` - Válida
- ❌ `admin123` - Sem maiúscula e sem especial
- ❌ `ADMIN@123` - Sem minúscula

### 🔒 Hash de Senhas - BCrypt

- **Algoritmo:** BCrypt com salt automático
- **Strength:** 10 rounds (padrão Spring Security)
- **Armazenamento:** Apenas hash no banco, nunca senha em texto plano

---

## 🗄️ Banco de Dados

### ☁️ Oracle Autonomous Database (ADB)

**Ambiente de Produção:**
- **Plataforma:** Oracle Cloud Infrastructure (OCI)
- **Tier:** Always Free (gratuito permanentemente)
- **Tipo:** Autonomous Transaction Processing (ATP)
- **Conexão:** Wallet seguro (mTLS)
- **Schema:** `APP_LUIGARAH`

### 📊 Estrutura de Tabelas

O banco possui **9 tabelas principais**:

#### 1. **USUARIOS** - Usuários do sistema
```sql
Campos principais:
- ID (PK, auto-increment via sequence)
- NOME, SOBRENOME, EMAIL (unique)
- SENHA (hash BCrypt)
- TELEFONE, DATA_NASCIMENTO, GENERO
- ROLE (USER/ADMIN)
- PROVIDER (LOCAL/GOOGLE/FACEBOOK)
- ATIVO, EMAIL_VERIFICADO
- DATA_CRIACAO, DATA_ATUALIZACAO, ULTIMO_ACESSO
```

#### 2. **ENDERECOS** - Endereços de entrega
```sql
Campos principais:
- ID (PK)
- USUARIO_ID (FK → USUARIOS)
- PAIS, ESTADO, CIDADE, CEP
- RUA, NUMERO, COMPLEMENTO, BAIRRO
- PRINCIPAL (boolean)
```

#### 3. **PRODUTOS** - Catálogo de produtos
```sql
Campos principais:
- ID (PK)
- NOME, DESCRICAO
- PRECO (NUMBER 10,2)
- CATEGORIA, TIPO_PRODUTO
- IMAGEM_URL, IMAGEM_URL_SECUNDARIA
- IDENTIDADE_ID (FK → IDENTIDADES)
- ATIVO, DATA_CRIACAO
```

#### 4. **IDENTIDADES** - Cores/variações de produtos
```sql
Campos principais:
- ID (PK)
- NOME (ex: "Azul Marinho")
- COR_HEX (ex: "#003366")
- IMAGEM_URL
- ATIVO
```

#### 5. **TAMANHOS** - Tamanhos disponíveis
```sql
Campos principais:
- ID (PK)
- NOME (ex: "Pequeno")
- SIGLA (ex: "P")
- ORDEM (para ordenação)
- ATIVO
```

#### 6. **PRODUTO_TAMANHO** - Relação produto-tamanho com estoque
```sql
Campos principais:
- PRODUTO_ID (PK, FK → PRODUTOS)
- TAMANHO_ID (PK, FK → TAMANHOS)
- QUANTIDADE_ESTOQUE
```

#### 7. **CARRINHO_ITENS** - Itens no carrinho
```sql
Campos principais:
- ID (PK)
- USUARIO_ID (FK → USUARIOS)
- PRODUTO_ID (FK → PRODUTOS)
- TAMANHO_ID (FK → TAMANHOS, nullable)
- QUANTIDADE
- DATA_CRIACAO
```

#### 8. **LISTA_DESEJO_ITENS** - Items na lista de desejos
```sql
Campos principais:
- ID (PK)
- USUARIO_ID (FK → USUARIOS)
- PRODUTO_ID (FK → PRODUTOS)
- DATA_CRIACAO
```

#### 9. **PADROES_TAMANHOS** - Padrões de tamanhos por produto
```sql
Campos principais:
- ID (PK)
- PRODUTO_ID (FK → PRODUTOS)
- PADRAO (PP/P/M/G/GG/XG/XXG/UNICO)
```

### 🔄 Migração com Flyway

**Scripts de Migração:** `src/main/resources/db/migration/`

| Script | Descrição |
|--------|-----------|
| `V1__schema.sql` | Criação de todas as tabelas, sequences, constraints e índices |
| `V2__seed_tamanhos.sql` | Dados iniciais de tamanhos padrão (PP, P, M, G, GG, XG, XXG, ÚNICO) |
| `V3__data.sql` | Dados de exemplo (produtos, identidades, etc.) |

**Funcionamento:**
- Flyway executa automaticamente na inicialização
- Versionamento de esquema (histórico na tabela FLYWAY_SCHEMA_HISTORY)
- Migrations aplicadas apenas uma vez
- Validação de checksums para integridade

### 🔗 Pool de Conexões - HikariCP

**Configuração de Produção:**
```properties
maximum-pool-size=5      # Máximo 5 conexões simultâneas
minimum-idle=2           # Mínimo 2 conexões ociosas
idle-timeout=600000      # 10 minutos
connection-timeout=30000 # 30 segundos
```

**Por que pool pequeno?**
- Oracle ADB Always Free tem limite de conexões
- Render Free Tier tem recursos limitados
- Pool otimizado para web services REST

---

## 📡 Endpoints da API

### 🔐 Autenticação (`/api/auth`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| POST | `/api/auth/login` | Login com email/senha | ❌ | - |
| POST | `/api/auth/registrar` | Criar nova conta | ❌ | - |
| GET | `/api/auth/perfil` | Visualizar perfil | ✅ | USER |
| PUT | `/api/auth/perfil` | Atualizar perfil | ✅ | USER |
| PUT | `/api/auth/alterar-senha` | Alterar senha | ✅ | USER |

### 📦 Produtos (`/api/produtos`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/produtos` | Listar todos os produtos | ❌ | - |
| GET | `/api/produtos/{id}` | Buscar produto por ID | ❌ | - |
| GET | `/api/produtos/categoria/{categoria}` | Filtrar por categoria | ❌ | - |
| POST | `/api/produtos` | Criar novo produto | ✅ | ADMIN |
| PUT | `/api/produtos/{id}` | Atualizar produto | ✅ | ADMIN |
| DELETE | `/api/produtos/{id}` | Deletar produto | ✅ | ADMIN |

### 🛒 Carrinho (`/api/carrinho`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/carrinho` | Listar itens do carrinho | ✅ | USER |
| POST | `/api/carrinho` | Adicionar item ao carrinho | ✅ | USER |
| PUT | `/api/carrinho/{id}` | Atualizar quantidade | ✅ | USER |
| DELETE | `/api/carrinho/{id}` | Remover item | ✅ | USER |
| DELETE | `/api/carrinho` | Limpar carrinho | ✅ | USER |
| GET | `/api/carrinho/count` | Contar itens | ✅ | USER |

### ❤️ Lista de Desejos (`/api/lista-desejos`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/lista-desejos` | Listar favoritos | ✅ | USER |
| POST | `/api/lista-desejos/{produtoId}` | Adicionar aos favoritos | ✅ | USER |
| DELETE | `/api/lista-desejos/{id}` | Remover item | ✅ | USER |
| DELETE | `/api/lista-desejos/produto/{produtoId}` | Remover por produto | ✅ | USER |
| GET | `/api/lista-desejos/verificar/{produtoId}` | Verificar se está nos favoritos | ✅ | USER |
| DELETE | `/api/lista-desejos` | Limpar lista | ✅ | USER |
| GET | `/api/lista-desejos/count` | Contar itens | ✅ | USER |

### 📏 Tamanhos (`/api/tamanhos`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/tamanhos` | Listar tamanhos | ❌ | - |
| GET | `/api/tamanhos/{id}` | Buscar por ID | ❌ | - |
| POST | `/api/tamanhos` | Criar tamanho | ✅ | ADMIN |
| PUT | `/api/tamanhos/{id}` | Atualizar tamanho | ✅ | ADMIN |
| DELETE | `/api/tamanhos/{id}` | Deletar tamanho | ✅ | ADMIN |

### 🏷️ Identidades (`/api/identidades`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/identidades` | Listar identidades | ❌ | - |
| GET | `/api/identidades/{id}` | Buscar por ID | ❌ | - |
| POST | `/api/identidades` | Criar identidade | ✅ | ADMIN |
| PUT | `/api/identidades/{id}` | Atualizar identidade | ✅ | ADMIN |
| DELETE | `/api/identidades/{id}` | Deletar identidade | ✅ | ADMIN |

### 📊 Estoque (`/api/estoque`)

| Método | Endpoint | Descrição | Auth | Role |
|--------|----------|-----------|------|------|
| GET | `/api/estoque/produto/{id}` | Consultar estoque | ❌ | - |
| PATCH | `/api/estoque/produto/{produtoId}/tamanho/{tamanhoId}` | Atualizar quantidade | ✅ | ADMIN |

---

## 🚀 Como Executar

### 📋 Pré-requisitos

- ✅ **Java 21** (Oracle JDK ou OpenJDK)
- ✅ **Maven 3.9+**
- ✅ **Oracle Wallet** configurado (para ADB)
- ✅ **IDE** (IntelliJ IDEA, VS Code, Eclipse)

### 🔧 Configuração Local

#### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/luigara-backend.git
cd luigara-backend
```

#### 2. Configure o Wallet Oracle

**Windows:**
- Extraia o wallet em: `C:/oracle/wallet/luigarah`
- Verifique o arquivo `application-local.properties`:
```properties
spring.datasource.url=jdbc:oracle:thin:@luigarah_tp?TNS_ADMIN=C:/oracle/wallet/luigarah
```

**Linux/Mac:**
- Extraia o wallet em: `/opt/app/wallet/luigarah`
- Ajuste o `application-local.properties`:
```properties
spring.datasource.url=jdbc:oracle:thin:@luigarah_tp?TNS_ADMIN=/opt/app/wallet/luigarah
```

#### 3. Configure as credenciais

Edite `src/main/resources/application-local.properties`:
```properties
spring.datasource.username=APP_LUIGARAH
spring.datasource.password=SuaSenhaDoOracle
```

#### 4. Execute o projeto

**Via Maven:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Via IDE:**
- Abra o projeto na IDE
- Execute a classe `AplicacaoLuigara.java`
- Adicione VM options: `-Dspring.profiles.active=local`

#### 5. Acesse a aplicação

- **API Base:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Health Check:** http://localhost:8080/actuator/health (se habilitado)

### 🧪 Executar Testes

```bash
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=PasswordTest
```

---

## 🌐 Deploy em Produção

### 🚀 Plataforma: Render

**URL de Produção:** https://luigarah-backend.onrender.com

#### Configuração no Render

**1. Variáveis de Ambiente Obrigatórias:**

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `ORACLE_USERNAME` | Usuário do banco Oracle | `APP_LUIGARAH` |
| `ORACLE_PASSWORD` | Senha do banco Oracle | `***` |
| `JWT_SECRET` | Chave secreta JWT (mín 32 chars) | `***` (gerar com openssl) |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h) |
| `PORT` | Porta da aplicação | `8080` (auto) |

**2. Como gerar JWT_SECRET segura:**
```bash
openssl rand -base64 64
```

**3. Build Command:**
```bash
mvn clean package -DskipTests
```

**4. Start Command:**
```bash
java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/luigarah-backend-1.0.0.jar
```

### 🐳 Docker

O projeto inclui `Dockerfile` e `entrypoint.sh` para containerização:

**Build da imagem:**
```bash
docker build -t luigarah-backend .
```

**Executar container:**
```bash
docker run -p 8080:8080 \
  -e ORACLE_USERNAME=APP_LUIGARAH \
  -e ORACLE_PASSWORD=*** \
  -e JWT_SECRET=*** \
  -e WALLET_BASE64=*** \
  luigarah-backend
```

### 📊 Monitoramento

**Logs no Render:**
- Acesse o dashboard do Render
- Vá em "Logs" para ver logs em tempo real
- Erros são automaticamente destacados

**Health Check:**
- Render faz ping automático na aplicação
- Reinicia automaticamente em caso de falha

---

## 📖 Documentação

### 📚 Arquivos de Documentação

| Arquivo | Conteúdo |
|---------|----------|
| `README.md` | **Este arquivo** - Visão geral completa |
| `README_AUTH.md` | Documentação detalhada do sistema de autenticação |
| `ESTRUTURA_PASTAS.md` | Organização completa do projeto por módulos |
| `CONFIGURACAO_RENDER_AUTH.md` | Guia de configuração de variáveis no Render |
| `GUIA_ALTERAR_SENHA_SWAGGER.md` | Tutorial de alteração de senha via Swagger |
| `COMO_ALTERAR_SENHA.md` | Instruções de alteração de senha |
| `ENDPOINTS_PUBLICOS_CORRIGIDO.md` | Configuração de endpoints públicos |

### 📜 Scripts SQL

| Arquivo | Conteúdo |
|---------|----------|
| `auth_setup.sql` | Setup completo de autenticação (usuários, endereços, etc.) |
| `CORRIGIR_SENHA_ADMIN_COMPLETO.sql` | Script para corrigir senha do admin |
| `ATUALIZAR_SENHA_ADMIN.sql` | Atualizar senha do admin |
| `verificar_admin.sql` | Verificar dados do admin |
| `corrigir_admin.sql` | Corrigir configurações do admin |
| `EXECUTAR_ESTE_SQL_PARA_CORRIGIR_LOGIN.sql` | Correção de problemas de login |

### 🌐 Swagger/OpenAPI

**Acesso Local:** http://localhost:8080/swagger-ui/index.html  
**Acesso Produção:** https://luigarah-backend.onrender.com/swagger-ui/index.html

**Recursos:**
- ✅ Documentação interativa de todos os endpoints
- ✅ Autenticação JWT integrada (botão 🔒 Authorize)
- ✅ Testar endpoints diretamente no navegador
- ✅ Exemplos de requisição e resposta
- ✅ Schemas de DTOs
- ✅ Códigos de status HTTP

**Como usar:**
1. Acesse o Swagger UI
2. Faça login via `/api/auth/login`
3. Copie o token JWT da resposta
4. Clique em **🔒 Authorize**
5. Cole o token (sem "Bearer")
6. Clique em **Authorize** e depois **Close**
7. Agora você pode testar endpoints autenticados!

---

## 🧪 Testes

### Testes Implementados

**Localização:** `src/test/java/com/luigarah`

| Classe de Teste | Descrição |
|----------------|-----------|
| `PasswordTest.java` | Testes de hash BCrypt e validação de senhas |

### Executar Testes

```bash
# Todos os testes
mvn test

# Com relatório de cobertura (se configurado)
mvn test jacoco:report

# Ignorar testes no build
mvn clean package -DskipTests
```

### Cobertura de Testes (Planejado)

- [ ] Testes unitários de services
- [ ] Testes de integração de controllers
- [ ] Testes de repositories
- [ ] Testes de segurança
- [ ] Testes end-to-end

---

## 👥 Contribuição

### Como Contribuir

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. Abra um **Pull Request**

### Padrões de Código

- ✅ Seguir convenções Java (camelCase, PascalCase)
- ✅ Comentários em português
- ✅ DTOs validados com Bean Validation
- ✅ Mappers para conversão entidade ↔DTO
- ✅ Exceções customizadas para erros de negócio
- ✅ Documentação Swagger em todos os endpoints

### Estrutura de Commits

```
tipo(escopo): descrição curta

Descrição detalhada (opcional)

Exemplos:
feat(auth): adiciona login com Google OAuth2
fix(carrinho): corrige cálculo de total
docs(readme): atualiza instruções de deploy
refactor(produto): reorganiza estrutura de pastas
```

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📧 Contato e Suporte

- **Email:** dev@luigarah.com
- **Website:** https://luigarah.vercel.app
- **API Docs:** https://luigarah-backend.onrender.com/swagger-ui/index.html

---

## 🎯 Status do Projeto

- ✅ **Fase 1:** Estrutura base e autenticação - **Completo**
- ✅ **Fase 2:** CRUD de produtos e estoque - **Completo**
- ✅ **Fase 3:** Carrinho e lista de desejos - **Completo**
- ✅ **Fase 4:** Deploy em produção - **Completo**
- ✅ **Fase 5:** Administração de usuários com LGPD - **Completo**
- 🚧 **Fase 6:** Sistema de pedidos - **Em desenvolvimento**
- 📋 **Fase 7:** Pagamentos e checkout - **Planejado**
- 📋 **Fase 8:** Painel administrativo avançado - **Planejado**

---

## 🏆 Agradecimentos

- **Spring Boot** - Framework incrível para Java
- **Oracle Cloud** - Banco de dados gratuito e confiável
- **Render** - Plataforma de deploy simples e eficiente
- **Swagger** - Documentação automática de APIs
- **Lombok** - Redução de boilerplate

---

<div align="center">

**Desenvolvido com ❤️ por Sarah Hernandes**

**Luigarah - Sistema Seguro, Modular e Escalável**

**Conformidade total com LGPD**

📅 **Última atualização:** 14 de Outubro de 2025

</div>
