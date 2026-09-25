package com.luigarah.seguranca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.config.JwtTokenProvider;
import com.luigarah.exception.CredenciaisInvalidasException;
import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.autenticacao.VerificationToken;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.repository.autenticacao.VerificationTokenRepository;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.RepositoriosPostgresTest;
import com.luigarah.service.autenticacao.EmailService;
import com.luigarah.service.autenticacao.VerificadorTokenOAuth;
import com.luigarah.service.storage.ImageStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe a aplicação inteira (filtros, Spring Security, controllers e serviços) sobre o
 * PostgreSQL embarcado e confere, pela API, as falhas de segurança corrigidas.
 * O e-mail, o storage de imagens e a verificação no Google/Facebook são simulados.
 */
@SpringBootTest(properties = {
        "JWT_SECRET=segredo-de-teste-com-pelo-menos-32-bytes-para-hs256",
        "DB_URL=jdbc:postgresql://nao-usado", "DB_USERNAME=nao-usado", "DB_PASSWORD=nao-usado",
        "BREVO_API_KEY=teste", "BREVO_SMTP_USERNAME=teste", "BREVO_SMTP_PASSWORD=teste",
        "app.cache.catalogo.aquecimento=false",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
@Import(RepositoriosPostgresTest.PostgresEmbarcadoConfig.class)
class SegurancaApiTest {

    private static final long ROUPA_BR_ID = 15L;

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JwtTokenProvider tokens;
    @Autowired UsuarioRepository usuarios;
    @Autowired VerificationTokenRepository codigos;
    @Autowired RepositorioProduto produtos;
    @Autowired PasswordEncoder senhas;

    @MockBean EmailService emailService;
    @MockBean ImageStorageService imageStorageService;
    @MockBean VerificadorTokenOAuth verificadorTokenOAuth;

    private String tokenUsuario;
    private String tokenAdmin;

    private Usuario criarUsuario(String email, Role role, boolean ativo) {
        return usuarios.save(Usuario.builder()
                .nome("Teste").sobrenome("Silva")
                .email(email)
                .senha(senhas.encode("Senha@123"))
                .role(role).ativo(ativo).emailVerificado(true)
                .provider(AuthProvider.LOCAL)
                .build());
    }

    private String novoEmail() {
        return "u" + UUID.randomUUID().toString().substring(0, 8) + "@teste.com";
    }

    private MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder req, String token) {
        return req.header("Authorization", "Bearer " + token);
    }

    private String corpo(Map<String, ?> dados) throws Exception {
        return json.writeValueAsString(dados);
    }

    @BeforeEach
    void preparar() {
        tokenUsuario = tokens.generateTokenFromUsername(criarUsuario(novoEmail(), Role.USER, true).getEmail());
        tokenAdmin = tokens.generateTokenFromUsername(criarUsuario(novoEmail(), Role.ADMIN, true).getEmail());
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Catálogo: escrita só para ADMIN")
    class EscritaNoCatalogo {

        @Test
        @DisplayName("Usuário comum não altera estoque, tamanhos nem padrão (PATCH)")
        void usuarioComumBloqueado() throws Exception {
            mvc.perform(comToken(patch("/api/estoque/produtos/1/estoque?modo=set&valor=0"), tokenUsuario))
                    .andExpect(status().isForbidden());
            mvc.perform(comToken(patch("/api/estoque/produtos/" + ROUPA_BR_ID + "/estoque/PP?modo=set&valor=0"), tokenUsuario))
                    .andExpect(status().isForbidden());
            mvc.perform(comToken(patch("/api/tamanhos/produtos/" + ROUPA_BR_ID + "/tamanhos"), tokenUsuario)
                            .contentType(MediaType.APPLICATION_JSON).content("[\"GG\"]"))
                    .andExpect(status().isForbidden());
            mvc.perform(comToken(patch("/api/padroes-tamanho/produtos/" + ROUPA_BR_ID + "/padrao?padrao=usa"), tokenUsuario))
                    .andExpect(status().isForbidden());
            mvc.perform(comToken(delete("/api/padroes-tamanho/produtos/" + ROUPA_BR_ID + "/padrao"), tokenUsuario))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Sem login também não altera")
        void anonimoBloqueado() throws Exception {
            MvcResult r = mvc.perform(patch("/api/estoque/produtos/1/estoque?modo=set&valor=0")).andReturn();
            assertTrue(r.getResponse().getStatus() == 401 || r.getResponse().getStatus() == 403);
        }

        @Test
        @DisplayName("ADMIN altera normalmente")
        void adminPermitido() throws Exception {
            mvc.perform(comToken(patch("/api/estoque/produtos/1/estoque?modo=set&valor=7"), tokenAdmin))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Leitura do catálogo continua pública, mesmo com token inválido")
        void leituraPublica() throws Exception {
            mvc.perform(get("/api/produtos/1")).andExpect(status().isOk());
            mvc.perform(get("/api/produtos/1").header("Authorization", "Bearer token-invalido"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Trocar a categoria do produto remove os tamanhos da categoria antiga")
        void trocaDeCategoriaLimpaTamanhos() throws Exception {
            assertFalse(produtos.listarEtiquetasPorProduto(ROUPA_BR_ID).isEmpty());

            // o PUT recebe o produto completo: lê, troca só a categoria e envia de volta
            MvcResult atual = mvc.perform(get("/api/produtos/" + ROUPA_BR_ID)).andReturn();
            com.fasterxml.jackson.databind.node.ObjectNode produto =
                    (com.fasterxml.jackson.databind.node.ObjectNode) json.readTree(atual.getResponse().getContentAsString()).path("dados");
            produto.put("categoria", "sapatos");
            produto.remove("identidade");
            produto.remove("subtituloTraduzido");

            MvcResult r = mvc.perform(comToken(put("/api/produtos/" + ROUPA_BR_ID), tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(produto)))
                    .andReturn();
            assertEquals(200, r.getResponse().getStatus(), json.writeValueAsString(produto) + " -> " + r.getResponse().getContentAsString());
            assertTrue(produtos.listarEtiquetasPorProduto(ROUPA_BR_ID).isEmpty());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Contas e tokens")
    class Contas {

        @Test
        @DisplayName("Cadastro não devolve token antes de confirmar o e-mail")
        void cadastroSemToken() throws Exception {
            String email = novoEmail();
            mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("nome", "Nova", "sobrenome", "Conta",
                                    "email", email, "senha", "Senha@123"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").doesNotExist());

            // e o login continua bloqueado até a verificação
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("email", email, "senha", "Senha@123"))))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Conta desativada perde o acesso mesmo com token ainda válido")
        void contaDesativada() throws Exception {
            Usuario u = criarUsuario(novoEmail(), Role.USER, true);
            String token = tokens.generateTokenFromUsername(u.getEmail());
            mvc.perform(comToken(get("/api/auth/perfil"), token)).andExpect(status().isOk());

            mvc.perform(comToken(get("/api/carrinho"), token)).andExpect(status().isOk());

            u.setAtivo(false);
            usuarios.save(u);
            MvcResult carrinho = mvc.perform(comToken(get("/api/carrinho"), token)).andReturn();
            assertTrue(carrinho.getResponse().getStatus() == 401 || carrinho.getResponse().getStatus() == 403);
            MvcResult perfil = mvc.perform(comToken(get("/api/auth/perfil"), token)).andReturn();
            assertNotEquals(200, perfil.getResponse().getStatus());
        }

        @Test
        @DisplayName("Alterar senha exige as regras de senha forte")
        void alterarSenhaFraca() throws Exception {
            mvc.perform(comToken(put("/api/auth/alterar-senha"), tokenUsuario)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("senhaAtual", "Senha@123",
                                    "novaSenha", "fraca1", "confirmarNovaSenha", "fraca1"))))
                    .andExpect(status().isBadRequest());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Login social (Google/Facebook)")
    class LoginSocial {

        @Test
        @DisplayName("Usa o e-mail confirmado pelo provedor, não o enviado no corpo")
        void usaEmailDoProvedor() throws Exception {
            Usuario admin = criarUsuario(novoEmail(), Role.ADMIN, true);
            String emailDoProvedor = novoEmail();
            when(verificadorTokenOAuth.verificar(eq("google"), eq("token-do-google")))
                    .thenReturn(new VerificadorTokenOAuth.UsuarioOAuth(emailDoProvedor, "123"));

            // tenta se passar pelo admin: o corpo pede o e-mail dele
            MvcResult r = mvc.perform(post("/api/auth/oauth/sync").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("provider", "google", "token", "token-do-google",
                                    "email", admin.getEmail(), "nome", "Invasor"))))
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode resposta = json.readTree(r.getResponse().getContentAsString());
            assertEquals(emailDoProvedor, resposta.path("usuario").path("email").asText());
            assertEquals(emailDoProvedor, tokens.getUsernameFromToken(resposta.path("token").asText()));
        }

        @Test
        @DisplayName("Token recusado pelo provedor não gera JWT")
        void tokenInvalido() throws Exception {
            when(verificadorTokenOAuth.verificar(anyString(), anyString()))
                    .thenThrow(new CredenciaisInvalidasException("Login com Google expirado ou inválido. Entre novamente."));
            mvc.perform(post("/api/auth/oauth/sync").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("provider", "google", "token", "falso",
                                    "email", "admin@luigarah.com", "nome", "Invasor"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.token").doesNotExist());
        }

        @Test
        @DisplayName("Sem token do provedor a requisição é recusada")
        void semToken() throws Exception {
            mvc.perform(post("/api/auth/oauth/sync").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("provider", "google", "email", "admin@luigarah.com", "nome", "Invasor"))))
                    .andExpect(status().isBadRequest());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Códigos de 6 dígitos")
    class Codigos {

        private String pedirReset(String email) throws Exception {
            mvc.perform(post("/api/auth/solicitar-reset-senha").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("email", email))))
                    .andExpect(status().isOk());
            return codigos.findLatestByEmailAndTipo(email, VerificationToken.TipoToken.RESET_SENHA)
                    .orElseThrow().getCodigo();
        }

        private MvcResult redefinir(String email, String codigo) throws Exception {
            return mvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                    .content(corpo(Map.of("email", email, "codigo", codigo,
                            "novaSenha", "Nova@1234", "confirmarNovaSenha", "Nova@1234")))).andReturn();
        }

        private String codigoErrado(String certo, int i) {
            String errado = String.format("%06d", 100000 + i);
            return errado.equals(certo) ? String.format("%06d", 200000 + i) : errado;
        }

        @Test
        @DisplayName("Depois de 5 erros o código deixa de valer, mesmo o certo")
        void limiteDeTentativas() throws Exception {
            String email = criarUsuario(novoEmail(), Role.USER, true).getEmail();
            String certo = pedirReset(email);

            for (int i = 1; i <= VerificationToken.MAX_TENTATIVAS; i++) {
                MvcResult r = redefinir(email, codigoErrado(certo, i));
                assertEquals(400, r.getResponse().getStatus());
            }
            // a contagem ficou gravada no banco (não voltou atrás junto com o erro)
            assertEquals(VerificationToken.MAX_TENTATIVAS,
                    codigos.findLatestByEmailAndTipo(email, VerificationToken.TipoToken.RESET_SENHA)
                            .orElseThrow().getTentativas());

            MvcResult comCodigoCerto = redefinir(email, certo);
            assertEquals(400, comCodigoCerto.getResponse().getStatus());
            assertTrue(comCodigoCerto.getResponse().getContentAsString().contains("Muitas tentativas"));
        }

        @Test
        @DisplayName("Código certo dentro do limite redefine a senha")
        void codigoCerto() throws Exception {
            String email = criarUsuario(novoEmail(), Role.USER, true).getEmail();
            String certo = pedirReset(email);
            assertEquals(400, redefinir(email, codigoErrado(certo, 1)).getResponse().getStatus());
            assertEquals(200, redefinir(email, certo).getResponse().getStatus());
        }

        @Test
        @DisplayName("Não dá para pedir outro código antes de 1 minuto")
        void intervaloEntreCodigos() throws Exception {
            String email = criarUsuario(novoEmail(), Role.USER, true).getEmail();
            pedirReset(email);
            mvc.perform(post("/api/auth/solicitar-reset-senha").contentType(MediaType.APPLICATION_JSON)
                            .content(corpo(Map.of("email", email))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Aguarde")));
        }

        @Test
        @DisplayName("Código de redefinição de senha vale 1 hora")
        void validadeDoReset() throws Exception {
            String email = criarUsuario(novoEmail(), Role.USER, true).getEmail();
            pedirReset(email);
            VerificationToken t = codigos.findLatestByEmailAndTipo(email, VerificationToken.TipoToken.RESET_SENHA).orElseThrow();
            assertTrue(t.getExpiraEm().isBefore(LocalDateTime.now().plusMinutes(61)));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Erros com o status certo")
    class Erros {

        @Test
        @DisplayName("Rota inexistente: 404; id inválido: 400; identidade inexistente: 404; JSON quebrado: 400")
        void statusCorretos() throws Exception {
            mvc.perform(get("/api/rota-que-nao-existe")).andExpect(status().isNotFound());
            mvc.perform(get("/api/produtos/abc")).andExpect(status().isBadRequest());
            mvc.perform(get("/api/produtos/identidade/codigo/female")).andExpect(status().isNotFound());
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{quebrado"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Usuário comum em rota de admin: 403")
        void rotaDeAdmin() throws Exception {
            mvc.perform(comToken(get("/api/admin/usuarios"), tokenUsuario)).andExpect(status().isForbidden());
        }
    }
}
