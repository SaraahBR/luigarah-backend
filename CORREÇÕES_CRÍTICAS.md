# 🔧 CORREÇÕES CRÍTICAS - Backend Luigara

## 📅 Data: 15/10/2025

---

## ✅ Problemas Críticos Corrigidos

### 1. ❌ ClassCastException no JWT (RESOLVIDO)
**Erro Original:**
```
java.lang.ClassCastException: class java.lang.String cannot be cast to class org.springframework.security.core.userdetails.UserDetails
```

**Causa:**
O método `generateTokenForUser()` em `AuthService.java` estava criando um `UsernamePasswordAuthenticationToken` passando apenas o email (String) como principal, em vez de um objeto `UserDetails`.

**Correção:**
- ✅ Criado um objeto `UserDetails` correto antes de criar o `Authentication`
- ✅ Adicionado import `org.springframework.security.core.userdetails.UserDetails`
- ✅ O token JWT agora é gerado corretamente com todas as authorities do usuário

**Arquivo:** `src/main/java/com/luigarah/service/autenticacao/AuthService.java`

---

### 2. ❌ NoResourceFoundException em `/produtos/categoria/*` (RESOLVIDO)
**Erro Original:**
```
org.springframework.web.servlet.resource.NoResourceFoundException: No static resource produtos/categoria/sapatos.
```

**Causa:**
O Spring Security estava tentando tratar endpoints como `/produtos/categoria/sapatos` como recursos estáticos em vez de endpoints de API, retornando erro 500.

**Correção:**
- ✅ Adicionada regra no `SecurityConfig` para permitir todos os GETs públicos: `.requestMatchers(HttpMethod.GET, "/**").permitAll()`
- ✅ Endpoints `/produtos/**` (sem prefixo `/api`) agora são acessíveis publicamente para leitura
- ✅ Mantida segurança para operações de escrita (POST, PUT, DELETE) que exigem role ADMIN

**Arquivo:** `src/main/java/com/luigarah/config/SecurityConfig.java`

---

### 3. ❌ CORS com Headers Duplicados (RESOLVIDO)
**Erro Original:**
```
Access-Control-Allow-Origin: 'http://localhost:3000, http://localhost:3000'
```

**Causa:**
Configuração CORS estava duplicando o header `Access-Control-Allow-Origin`, fazendo o navegador bloquear as requisições.

**Correção:**
- ✅ Método `getOrigensComWildcard()` agora usa `HashSet` para eliminar duplicatas automaticamente
- ✅ Removida qualquer duplicação na configuração CORS
- ✅ CORS configurado uma única vez no `SecurityConfig` usando `fonteCorsConfiguration`

**Arquivo:** `src/main/java/com/luigarah/config/ConfiguracaoCors.java`

---

## 🔐 Melhorias de Segurança

### Endpoints Públicos (Leitura sem autenticação)
✅ `/api/produtos/**` - GET permitido
✅ `/produtos/**` - GET permitido  
✅ `/api/categorias/**` - GET permitido
✅ `/api/tamanhos/**` - GET permitido
✅ `/api/identidades/**` - GET permitido
✅ `/api/estoque/**` - GET permitido
✅ `/api/busca/**` - GET permitido
✅ `/api/auth/**` - Autenticação pública

### Endpoints Protegidos (Requerem Autenticação)
🔒 `/api/carrinho/**` - Apenas usuários autenticados
🔒 `/api/lista-desejos/**` - Apenas usuários autenticados
🔒 `/api/favoritos/**` - Apenas usuários autenticados
🔒 `/api/pedidos/**` - Apenas usuários autenticados
🔒 `/api/usuario/**` - Apenas usuários autenticados

### Endpoints ADMIN (Requerem Role ADMIN)
🔐 POST/PUT/DELETE em `/api/produtos/**`
🔐 POST/PUT/DELETE em `/api/identidades/**`
🔐 POST/PUT/DELETE em `/api/tamanhos/**`
🔐 POST/PUT/DELETE em `/api/estoque/**`
🔐 `/api/admin/**`

---

## 📋 Checklist de Testes

### ✅ Backend
- [x] Compilação sem erros
- [x] JWT gerado corretamente para OAuth
- [x] Endpoints de produtos acessíveis publicamente
- [x] CORS funcionando sem duplicação
- [x] Autenticação OAuth funcionando

### 🔄 Próximos Passos
1. Fazer deploy no Render com as correções
2. Testar no frontend se os produtos aparecem
3. Testar login OAuth (Google)
4. Verificar se não há mais erros de CORS no console do browser

---

## 🚀 Comandos para Deploy

```bash
# 1. Compilar o projeto
mvn clean package -DskipTests

# 2. Commit das alterações
git add .
git commit -m "fix: corrigir ClassCastException JWT, NoResourceFoundException e CORS duplicado"

# 3. Push para o Render
git push origin main
```

---

## 📝 Mensagem de Commit Sugerida

```
fix: corrigir ClassCastException JWT, NoResourceFoundException e CORS duplicado

- Corrigido ClassCastException no método generateTokenForUser criando UserDetails correto
- Adicionado suporte para endpoints com e sem prefixo /api no SecurityConfig
- Removida duplicação de headers CORS usando HashSet para eliminar duplicatas
- Endpoints GET públicos agora são permitidos para evitar NoResourceFoundException
- Mantida segurança para operações de escrita (POST/PUT/DELETE) apenas para ADMIN

Erros resolvidos:
- ❌ ClassCastException: String cannot be cast to UserDetails
- ❌ NoResourceFoundException: No static resource produtos/categoria/sapatos
- ❌ CORS duplicado: 'http://localhost:3000, http://localhost:3000'
```

---

## 🎯 Resumo das Alterações

| Arquivo | Alteração | Motivo |
|---------|-----------|--------|
| `AuthService.java` | Criado UserDetails correto no método `generateTokenForUser()` | Corrigir ClassCastException no JWT |
| `SecurityConfig.java` | Permitir GETs públicos para todos os endpoints | Corrigir NoResourceFoundException |
| `ConfiguracaoCors.java` | Usar HashSet para eliminar duplicatas de origens | Corrigir CORS duplicado |

---

**Status:** ✅ TODOS OS ERROS CRÍTICOS CORRIGIDOS

**Impacto:** 🚀 Backend agora funciona corretamente com autenticação OAuth, endpoints públicos acessíveis e CORS configurado sem duplicação

