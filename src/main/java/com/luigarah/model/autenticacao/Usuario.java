package com.luigarah.model.autenticacao;

import com.luigarah.model.carrinho.CarrinhoItem;
import com.luigarah.model.usuario.Endereco;
import com.luigarah.model.listadesejos.ListaDesejoItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidade de Usuário com autenticação JWT e suporte a login social.
 * Implementa UserDetails para integração com Spring Security.
 */
@Entity
@Table(name = "USUARIOS", schema = "APP_LUIGARAH")
@SequenceGenerator(
        name = "usuarios_seq_gen",
        sequenceName = "APP_LUIGARAH.USUARIOS_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarios_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Size(max = 100, message = "Sobrenome deve ter no máximo 100 caracteres")
    @Column(name = "SOBRENOME", length = 100)
    private String sobrenome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres")
    @Column(name = "SENHA", length = 255)
    private String senha;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Size(max = 20, message = "Gênero deve ter no máximo 20 caracteres")
    @Column(name = "GENERO", length = 20)
    private String genero;

    @Size(max = 500, message = "Foto URL deve ter no máximo 500 caracteres")
    @Column(name = "FOTO_URL", length = 500)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "ATIVO", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "EMAIL_VERIFICADO", nullable = false)
    @Builder.Default
    private Boolean emailVerificado = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", length = 20)
    private AuthProvider provider;

    @Column(name = "PROVIDER_ID", length = 255)
    private String providerId;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @Column(name = "ULTIMO_ACESSO")
    private LocalDateTime ultimoAcesso;

    // Relacionamentos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CarrinhoItem> carrinhoItens = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ListaDesejoItem> listaDesejoItens = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Endereco> enderecos = new ArrayList<>();

    // Métodos UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ativo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public String getNomeCompleto() {
        if (sobrenome != null && !sobrenome.isBlank()) {
            return nome + " " + sobrenome;
        }
        return nome;
    }
}
