# 📸 Guia Completo: Sistema de Upload de Imagens com Cloudflare R2

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura do Sistema](#arquitetura-do-sistema)
- [Configuração do Cloudflare R2](#configuração-do-cloudflare-r2)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Endpoints da API](#endpoints-da-api)
- [Troubleshooting](#troubleshooting)

---

## 🚀 Visão Geral

O sistema de upload de imagens do Luigarah Backend suporta **dois ambientes**:

### 🏠 Desenvolvimento (Local)
- **Storage:** Armazenamento em disco local (`uploads/`)
- **Acesso:** Servido via Spring MVC (`/uploads/**`)
- **Profile:** `local`

### ☁️ Produção (Render)
- **Storage:** Cloudflare R2 (compatível com S3)
- **Acesso:** URLs públicas via CDN do R2
- **Profile:** `prod`

---

## 🏗️ Arquitetura do Sistema

```
┌─────────────────────────────────────────────────┐
│           Frontend (React/Next.js)              │
│                                                 │
│  FormData → POST /api/imagens/upload            │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      ImageUploadController.java                 │
│                                                 │
│  - Validação de arquivo (tipo, tamanho)        │
│  - Sanitização de nome                          │
│  - Escolha da pasta (produtos/usuarios/outros) │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│       ImageStorageService (Interface)           │
└────────────────────┬────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         ▼                       ▼
┌──────────────────┐   ┌──────────────────────┐
│ LocalStorage     │   │ S3ImageStorage       │
│ (Desenvolvimento)│   │ (Produção)           │
│                  │   │                      │
│ Salva em:        │   │ Salva em:            │
│ /uploads/...     │   │ Cloudflare R2        │
│                  │   │ (S3-compatible)      │
│ Retorna:         │   │                      │
│ /uploads/...     │   │ Retorna:             │
│                  │   │ https://pub-xxx...   │
└──────────────────┘   └──────────────────────┘
```

---

## ☁️ Configuração do Cloudflare R2

### 1. Criar Bucket no Cloudflare

1. Acesse [Cloudflare Dashboard](https://dash.cloudflare.com/)
2. Navegue até **R2 Object Storage**
3. Clique em **Create bucket**
4. Nome do bucket: `luigarah-prod`
5. Localização: **Automatic** (escolhida pelo Cloudflare)
6. Clique em **Create bucket**

### 2. Configurar Acesso Público

1. No painel do bucket, vá em **Settings**
2. Em **Public access**, clique em **Allow Access**
3. Será gerado um domínio público, por exemplo:
   ```
   https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev
   ```
4. **Copie este domínio** - será usado na variável `STORAGE_PUBLIC_BASE_URL`

### 3. Gerar Credenciais de API (S3)

1. No dashboard R2, vá em **Manage R2 API Tokens**
2. Clique em **Create API token**
3. **Nome:** `luigarah-backend-prod`
4. **Permissions:** 
   - ✅ Object Read & Write
   - ✅ Admin Read & Write (se precisar gerenciar buckets)
5. **TTL:** Não especificar (token permanente)
6. Clique em **Create API token**

**Importante:** Copie as credenciais geradas:
```
Access Key ID: d563b6d... (exemplo)
Secret Access Key: 3769ef6b... (exemplo)
```

⚠️ **Guarde essas credenciais em local seguro - não serão exibidas novamente!**

### 4. Obter Account ID

1. No dashboard R2, clique no nome do bucket
2. Na URL, você verá algo como:
   ```
   https://dash.cloudflare.com/aef01bde77cd4e5689cde7c9784a36ee/r2/...
                                ↑ Account ID (32 caracteres)
   ```
3. **Copie o Account ID** - será usado na variável `R2_ACCOUNT_ID`

---

## 🔐 Variáveis de Ambiente

### No Render (Produção)

Configure as seguintes variáveis em **Environment Variables**:

```bash
# Cloudflare R2 - Credenciais
AWS_ACCESS_KEY_ID=d563b6d... (sua chave)
AWS_SECRET_ACCESS_KEY=3769ef6b... (sua chave secreta)

# Cloudflare R2 - Configuração
R2_ACCOUNT_ID=aef01bde... (seu account ID)
STORAGE_BUCKET=luigarah-prod
STORAGE_PUBLIC_BASE_URL=https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev

# Profile Spring
SPRING_PROFILES_ACTIVE=prod
```

### Local (.env)

Para desenvolvimento local, **não é necessário** configurar R2. O sistema usa armazenamento em disco automaticamente.

```bash
# .env (desenvolvimento)
SPRING_PROFILES_ACTIVE=local
```

---

## 📁 Estrutura de Pastas

### Desenvolvimento (Local)

```
uploads/
├── produtos/          # Imagens de produtos
│   ├── 1234567890-produto-nome.jpg
│   └── ...
├── usuarios/          # Fotos de perfil
│   ├── 1234567890-admin-luigarah-com.jpg
│   └── ...
└── outros/            # Outras imagens
    └── ...
```

### Produção (Cloudflare R2)

```
luigarah-prod/         # Bucket
├── produtos/
│   ├── 1234567890-produto-nome.jpg
│   └── ...
├── usuarios/
│   ├── 1760654676753-admin-luigarah-com.jpg
│   └── ...
└── outros/
    └── ...
```

**URLs públicas geradas:**
```
https://pub-xxx...r2.dev/produtos/1234567890-produto-nome.jpg
https://pub-xxx...r2.dev/usuarios/1760654676753-admin-luigarah-com.jpg
```

---

## 📡 Endpoints da API

### 1. Upload Múltiplo de Imagens

**Endpoint:** `POST /api/imagens/upload`

**Autenticação:** ✅ Requerida (JWT)

**Content-Type:** `multipart/form-data`

**Parâmetros:**
- `files` (MultipartFile[]) - Array de arquivos (máx 10)
- `pasta` (String, opcional) - `produtos`, `usuarios` ou `outros` (padrão: `outros`)

**Formatos aceitos:** JPG, JPEG, PNG, WEBP, GIF

**Tamanho máximo:** 5MB por arquivo

**Exemplo (JavaScript):**
```javascript
const formData = new FormData();
formData.append('files', file1);
formData.append('files', file2);
formData.append('pasta', 'produtos');

const response = await fetch('/api/imagens/upload', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});

const data = await response.json();
console.log(data);
// {
//   "sucesso": true,
//   "mensagem": "2 arquivo(s) enviado(s) com sucesso",
//   "urls": [
//     "https://pub-xxx.r2.dev/produtos/1234567890-imagem.jpg",
//     "https://pub-xxx.r2.dev/produtos/1234567891-imagem2.jpg"
//   ]
// }
```

### 2. Upload de Foto de Perfil

**Endpoint:** `POST /api/auth/perfil/foto/upload`

**Autenticação:** ✅ Requerida (JWT)

**Content-Type:** `multipart/form-data`

**Parâmetros:**
- `file` (MultipartFile) - Arquivo de imagem

**Exemplo (JavaScript):**
```javascript
const formData = new FormData();
formData.append('file', avatarFile);

const response = await fetch('/api/auth/perfil/foto/upload', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});

const data = await response.json();
console.log(data);
// {
//   "sucesso": true,
//   "mensagem": "Foto enviada com sucesso",
//   "fotoPerfil": "https://pub-xxx.r2.dev/usuarios/admin-luigarah-com.jpg"
// }
```

### 3. Atualizar Foto de Perfil (por URL)

**Endpoint:** `PUT /api/auth/perfil/foto`

**Autenticação:** ✅ Requerida (JWT)

**Content-Type:** `application/json`

**Body:**
```json
{
  "fotoUrl": "https://exemplo.com/foto.jpg"
}
```

**Resposta:**
```json
{
  "sucesso": true,
  "mensagem": "Foto atualizada com sucesso",
  "fotoUrl": "https://exemplo.com/foto.jpg"
}
```

---

## 🔧 Troubleshooting

### ❌ Erro: "The region name 'auto-r2' is not valid"

**Causa:** Configuração incorreta da região AWS.

**Solução:** A região deve ser **exatamente** `auto`, não `auto-r2`.

Verifique em `application-prod.properties`:
```properties
aws.region=auto
```

---

### ❌ Erro: "Authorization" (XML) ao acessar URL pública

**Causa:** Domínio público do R2 não configurado ou URL incorreta.

**Problema:** A URL está usando o endpoint interno:
```
https://aef01bde...r2.cloudflarestorage.com/luigarah-prod/usuarios/foto.jpg
❌ Acesso privado - requer autenticação
```

**Solução:** Use o domínio público gerado pelo Cloudflare:
```
https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev/usuarios/foto.jpg
✅ Acesso público - sem autenticação
```

**Configuração correta no Render:**
```bash
STORAGE_PUBLIC_BASE_URL=https://pub-0307a72d067843b4bb500a3fd7669eca.r2.dev
```

---

### ❌ Erro: "MalformedInputException" no Maven build

**Causa:** Caracteres inválidos no arquivo `application.properties`.

**Solução:** Certifique-se de que o arquivo está codificado em **UTF-8** e não contém caracteres especiais problemáticos.

---

### ❌ Erro: "Arquivo muito grande (máx 5MB)"

**Causa:** Arquivo enviado excede o limite de 5MB.

**Solução:** Redimensione a imagem antes do upload ou aumente o limite em:
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

### ❌ Erro: "Tipo de arquivo inválido"

**Causa:** Formato de arquivo não suportado.

**Solução:** Use apenas: JPG, JPEG, PNG, WEBP ou GIF.

---

## 📊 Monitoramento e Logs

### Logs de Upload

O sistema registra todos os uploads com detalhes:

```log
📤 Upload de foto de perfil - Usuário: admin@luigarah.com
📦 Arquivo: avatar.jpg (152340 bytes)
✅ Upload OK para key='usuarios/1760654676753-admin-luigarah-com.jpg'
✅ Foto salva no storage: https://pub-xxx.r2.dev/usuarios/1760654676753-admin-luigarah-com.jpg
```

### Verificar Configuração

Ao iniciar a aplicação, verifique os logs:

```log
🔧 Inicializando S3ImageStorageService...
   storage.bucket=luigarah-prod
   storage.publicBaseUrl=https://pub-xxx.r2.dev
   aws.region=auto
   aws.s3.endpoint=https://aef01bde...r2.cloudflarestorage.com
   aws.credentials.accessKey=***configurado***
   aws.credentials.secretKey=***configurado***
✅ S3ImageStorageService inicializado com sucesso!
```

---

## 🎯 Melhores Práticas

### 1. Nomear Arquivos
- ✅ Use timestamp para evitar colisões: `1760654676753-nome.jpg`
- ✅ Sanitize nomes: `produto-nome.jpg` (sem espaços ou caracteres especiais)
- ❌ Evite: `Produto Nome (Novo).jpg`

### 2. Organização
- ✅ Separe por pastas: `produtos/`, `usuarios/`, `outros/`
- ✅ Use estrutura consistente

### 3. Segurança
- ✅ Valide tipo MIME (não apenas extensão)
- ✅ Limite tamanho de arquivo
- ✅ Nunca exponha credenciais no frontend
- ✅ Use HTTPS sempre

### 4. Performance
- ✅ Configure cache no CDN do R2
- ✅ Use imagens otimizadas (compressão)
- ✅ Implemente lazy loading no frontend

---

## 📚 Referências

- [Cloudflare R2 Documentation](https://developers.cloudflare.com/r2/)
- [AWS S3 SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
- [Spring Boot Multipart Upload](https://spring.io/guides/gs/uploading-files/)

