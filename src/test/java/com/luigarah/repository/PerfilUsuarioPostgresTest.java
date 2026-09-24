package com.luigarah.repository;

import com.luigarah.config.JwtTokenProvider;
import com.luigarah.dto.usuario.AtualizarPerfilRequest;
import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.mapper.usuario.EnderecoMapper;
import com.luigarah.mapper.usuario.UsuarioMapper;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.service.autenticacao.AuthService;
import com.luigarah.service.autenticacao.EmailService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Salvamento do perfil ("Minha Conta") contra PostgreSQL real:
 * dados pessoais, endereço e preferências (V6).
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RepositoriosPostgresTest.PostgresEmbarcadoConfig.class,
        AuthService.class, UsuarioMapper.class, EnderecoMapper.class})
class PerfilUsuarioPostgresTest {

    @MockBean PasswordEncoder passwordEncoder;
    @MockBean JwtTokenProvider tokenProvider;
    @MockBean AuthenticationManager authenticationManager;
    @MockBean EmailService emailService;

    @Autowired AuthService authService;
    @Autowired UsuarioRepository repoUsuario;

    private static final String EMAIL = "cliente@exemplo.com";

    private void criarUsuario() {
        repoUsuario.saveAndFlush(Usuario.builder().nome("Cliente").email(EMAIL).senha("hash").build());
    }

    private static EnderecoDTO enderecoSaoPaulo() {
        EnderecoDTO e = new EnderecoDTO();
        e.setPais("Brazil");
        e.setEstado("São Paulo");
        e.setCidade("São Paulo");
        e.setCep("01310-100");
        e.setBairro("Bela Vista");
        e.setRua("Avenida Paulista");
        e.setNumero("1000");
        e.setPrincipal(true);
        return e;
    }

    @Test
    @DisplayName("Novo usuário começa com as preferências padrão")
    void deveTerPreferenciasPadrao() {
        criarUsuario();
        Usuario u = repoUsuario.findByEmail(EMAIL).orElseThrow();
        assertTrue(u.getReceberNovidades());
        assertFalse(u.getAlertasReposicao());
    }

    @Test
    @DisplayName("Deve salvar dados pessoais, endereço e preferências")
    void deveSalvarPerfilCompleto() {
        criarUsuario();

        AtualizarPerfilRequest req = new AtualizarPerfilRequest();
        req.setNome("Maria");
        req.setSobrenome("Silva");
        req.setTelefone("(11) 98888-7777");
        req.setDataNascimento(LocalDate.of(1995, 3, 10));
        req.setGenero("Feminino");
        req.setEnderecos(List.of(enderecoSaoPaulo()));
        req.setReceberNovidades(false);
        req.setAlertasReposicao(true);

        UsuarioDTO dto = authService.atualizarPerfilComJWT(req, EMAIL);

        assertEquals("Maria", dto.getNome());
        assertEquals("11988887777", dto.getTelefone());
        assertEquals(LocalDate.of(1995, 3, 10), dto.getDataNascimento());
        assertFalse(dto.getReceberNovidades());
        assertTrue(dto.getAlertasReposicao());
        assertEquals(1, dto.getEnderecos().size());
        assertEquals("Avenida Paulista", dto.getEnderecos().get(0).getRua());
        assertTrue(dto.getEnderecos().get(0).getPrincipal());
    }

    @Test
    @DisplayName("Salvar só as preferências não apaga dados pessoais nem endereço")
    void deveSalvarSoPreferencias() {
        criarUsuario();
        AtualizarPerfilRequest completo = new AtualizarPerfilRequest();
        completo.setNome("Maria");
        completo.setEnderecos(List.of(enderecoSaoPaulo()));
        authService.atualizarPerfilComJWT(completo, EMAIL);

        AtualizarPerfilRequest prefs = new AtualizarPerfilRequest();
        prefs.setAlertasReposicao(true);
        UsuarioDTO dto = authService.atualizarPerfilComJWT(prefs, EMAIL);

        assertEquals("Maria", dto.getNome());
        assertEquals(1, dto.getEnderecos().size());
        assertTrue(dto.getReceberNovidades()); // não enviado: continua o padrão
        assertTrue(dto.getAlertasReposicao());
    }

    @Test
    @DisplayName("Código postal aceita CEP brasileiro e formatos de outros países")
    void deveValidarCodigoPostal() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        EnderecoDTO e = enderecoSaoPaulo();

        for (String valido : List.of("01310-100", "01310100", "SW1A 1AA", "90210", "M5V 3L9")) {
            e.setCep(valido);
            assertTrue(validator.validateProperty(e, "cep").isEmpty(), valido);
        }
        for (String invalido : List.of("-", "0", "<script>")) {
            e.setCep(invalido);
            assertFalse(validator.validateProperty(e, "cep").isEmpty(), invalido);
        }
    }
}
