package com.luigarah.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de endereço do usuário")
public class EnderecoDTO {

    @Schema(description = "ID do endereço", example = "1")
    private Long id;

    @NotBlank(message = "País é obrigatório")
    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    @Schema(description = "País", example = "Brasil", required = true)
    private String pais;

    @NotBlank(message = "Estado é obrigatório")
    @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
    @Schema(description = "Estado", example = "São Paulo", required = true)
    private String estado;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Schema(description = "Cidade", example = "São Paulo", required = true)
    private String cidade;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato 00000-000")
    @Schema(description = "CEP", example = "01310-100", required = true)
    private String cep;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Schema(description = "Bairro", example = "Bela Vista", required = true)
    private String bairro;

    @NotBlank(message = "Rua é obrigatória")
    @Size(max = 200, message = "Rua deve ter no máximo 200 caracteres")
    @Schema(description = "Rua/Logradouro", example = "Avenida Paulista", required = true)
    private String rua;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Schema(description = "Número", example = "1578", required = true)
    private String numero;

    @Size(max = 200, message = "Complemento deve ter no máximo 200 caracteres")
    @Schema(description = "Complemento (opcional)", example = "Apto 101, Bloco A")
    private String complemento;

    @Schema(description = "Indica se é o endereço principal", example = "true")
    private Boolean principal;
}

