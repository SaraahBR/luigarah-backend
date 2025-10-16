package com.luigarah.controller.storage;

import com.luigarah.dto.storage.ImageUploadResponse;
import com.luigarah.service.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller para upload de imagens
 */
@RestController
@RequestMapping("/api/imagens")
@Tag(name = "Upload de Imagens", description = "Endpoints para upload de imagens para produtos, perfis, etc.")
public class ImageUploadController {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private ImageStorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Upload de imagem",
            description = """
                    Faz upload de uma imagem para o storage (Cloudflare R2 em produção, disco local em desenvolvimento).
                    
                    **Formatos aceitos:** JPG, JPEG, PNG, WEBP, GIF
                    
                    **Tamanho máximo:** 5MB
                    
                    **Pastas disponíveis:**
                    - `produtos` - Imagens de produtos
                    - `usuarios` - Fotos de perfil
                    - `outros` - Outras imagens
                    
                    **Exemplo de uso:**
                    ```javascript
                    const formData = new FormData();
                    formData.append('file', imageFile);
                    formData.append('folder', 'produtos');
                    
                    const response = await fetch('/api/imagens/upload', {
                      method: 'POST',
                      headers: { 'Authorization': `Bearer ${token}` },
                      body: formData
                    });
                    ```
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Imagem enviada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou muito grande"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor ao processar upload")
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "Arquivo de imagem", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Pasta onde salvar a imagem", example = "produtos")
            @RequestParam(value = "folder", defaultValue = "produtos") String folder
    ) {
        try {
            // Validações
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Arquivo não pode estar vazio"));
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Arquivo muito grande. Tamanho máximo: 5MB"));
            }

            String contentType = file.getContentType();
            if (!storageService.isValidImageType(contentType)) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Tipo de arquivo inválido. Use: JPG, PNG, WEBP ou GIF"));
            }

            // Valida pasta
            if (!isValidFolder(folder)) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Pasta inválida. Use: produtos, usuarios ou outros"));
            }

            // Gera chave única para o arquivo
            String key = storageService.generateKey(folder, file.getOriginalFilename());

            // Faz upload
            String url = storageService.save(
                    key,
                    contentType,
                    file.getSize(),
                    file.getInputStream()
            );

            // Resposta de sucesso
            ImageUploadResponse response = ImageUploadResponse.builder()
                    .url(url)
                    .originalFilename(file.getOriginalFilename())
                    .contentType(contentType)
                    .size(file.getSize())
                    .key(key)
                    .message("Imagem enviada com sucesso")
                    .build();

            log.info("✅ Upload realizado: {} -> {}", file.getOriginalFilename(), url);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("❌ Erro de I/O no upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Erro ao processar arquivo: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erro no upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Erro no servidor: " + e.getMessage()));
        }
    }

    /**
     * Upload múltiplo de imagens
     */
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Upload múltiplo de imagens",
            description = "Faz upload de várias imagens de uma vez. Útil para galeria de produtos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagens enviadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos arquivos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> uploadMultipleImages(
            @Parameter(description = "Lista de arquivos de imagem", required = true)
            @RequestParam("files") MultipartFile[] files,

            @Parameter(description = "Pasta onde salvar as imagens", example = "produtos")
            @RequestParam(value = "folder", defaultValue = "produtos") String folder
    ) {
        try {
            if (files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Nenhum arquivo enviado"));
            }

            if (files.length > 10) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("Máximo de 10 imagens por vez"));
            }

            var responses = new java.util.ArrayList<ImageUploadResponse>();

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                if (file.getSize() > MAX_FILE_SIZE) {
                    return ResponseEntity.badRequest()
                            .body(buildErrorResponse("Arquivo " + file.getOriginalFilename() + " muito grande"));
                }

                String contentType = file.getContentType();
                if (!storageService.isValidImageType(contentType)) {
                    return ResponseEntity.badRequest()
                            .body(buildErrorResponse("Arquivo " + file.getOriginalFilename() + " não é uma imagem válida"));
                }

                String key = storageService.generateKey(folder, file.getOriginalFilename());
                String url = storageService.save(key, contentType, file.getSize(), file.getInputStream());

                responses.add(ImageUploadResponse.builder()
                        .url(url)
                        .originalFilename(file.getOriginalFilename())
                        .contentType(contentType)
                        .size(file.getSize())
                        .key(key)
                        .message("Imagem enviada com sucesso")
                        .build());
            }

            log.info("✅ Upload múltiplo realizado: {} imagens", responses.size());

            return ResponseEntity.ok(java.util.Map.of(
                    "message", responses.size() + " imagens enviadas com sucesso",
                    "images", responses
            ));

        } catch (Exception e) {
            log.error("❌ Erro no upload múltiplo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("Erro no servidor: " + e.getMessage()));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------------------------------

    private boolean isValidFolder(String folder) {
        return folder.equals("produtos") ||
               folder.equals("usuarios") ||
               folder.equals("outros");
    }

    private java.util.Map<String, Object> buildErrorResponse(String message) {
        return java.util.Map.of(
                "sucesso", false,
                "mensagem", message
        );
    }
}

