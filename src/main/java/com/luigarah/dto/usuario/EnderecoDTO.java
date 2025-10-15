package com.luigarah.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Endereço de entrega do usuário")
public class EnderecoDTO {

    @Schema(description = "ID do endereço", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "País é obrigatório")
    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    @Schema(description = "País", example = "Brazil", required = true, maxLength = 100)
    private String pais;

    @NotBlank(message = "Estado é obrigatório")
    @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
    @Schema(description = "Estado", example = "Paraná", required = true, maxLength = 100)
    private String estado;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Schema(description = "Cidade", example = "Bela Vista do Paraíso", required = true, maxLength = 100)
    private String cidade;

    @NotBlank(message = "CEP é obrigatório")
    @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres")
    @Schema(description = "CEP", example = "86130-000", required = true, maxLength = 20)
    private String cep;

    @Size(max = 200, message = "Bairro deve ter no máximo 200 caracteres")
    @Schema(description = "Bairro", maxLength = 200)
    private String bairro;

    @Size(max = 300, message = "Rua deve ter no máximo 300 caracteres")
    @Schema(description = "Rua", maxLength = 300)
    private String rua;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Schema(description = "Número", example = "189", maxLength = 20)
    private String numero;

    @Size(max = 200, message = "Complemento deve ter no máximo 200 caracteres")
    @Schema(description = "Complemento", maxLength = 200)
    private String complemento;

    @Schema(description = "Indica se é o endereço principal", example = "true")
    private Boolean principal;

    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAtualizacao;
}
