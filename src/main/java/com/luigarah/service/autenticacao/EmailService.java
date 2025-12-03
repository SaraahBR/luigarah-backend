package com.luigarah.service.autenticacao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para envio de emails usando Brevo API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${app.brevo.api-key}")
    private String brevoApiKey;

    @Value("${app.brevo.sender-email}")
    private String senderEmail;

    @Value("${app.brevo.sender-name}")
    private String senderName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    /**
     * Envia email de boas-vindas ao Luigarah
     */
    public void enviarBoasVindas(String destinatario, String nomeUsuario, boolean isOAuth) {
        try {
            log.info("📧 Enviando email de boas-vindas para: {}", destinatario);

            String assunto = "Bem-vindo(a) ao Luigarah! 🎉";
            String mensagemTipo = isOAuth ? "login com Google" : "verificação da sua conta";

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .button { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Bem-vindo(a) ao Luigarah!</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>É um prazer tê-lo(a) conosco! 🎊</p>
                            <p>Obrigado por %s no <strong>Luigarah</strong> - sua nova plataforma de moda e estilo!</p>
                            <p>Estamos animados para você explorar nossas coleções exclusivas e encontrar peças perfeitas para você.</p>
                            <p><strong>O que você pode fazer agora:</strong></p>
                            <ul>
                                <li>🛍️ Explorar nossos produtos</li>
                                <li>❤️ Adicionar itens à sua lista de desejos</li>
                                <li>🛒 Fazer seu primeiro pedido</li>
                                <li>👤 Personalizar seu perfil</li>
                            </ul>
                            <center>
                                <a href="%s" class="button">Começar a Comprar</a>
                            </center>
                            <p>Se precisar de ajuda, nossa equipe está sempre disponível para você!</p>
                            <p>Boas compras! ✨</p>
                            <p><strong>Equipe Luigarah</strong></p>
                        </div>
                        <div class="footer">
                            <p>© 2025 Luigarah. Todos os direitos reservados.</p>
                            <p>Este é um email automático, por favor não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, nomeUsuario, mensagemTipo, frontendUrl);

            enviarEmail(destinatario, assunto, htmlContent);
            log.info("✅ Email de boas-vindas enviado com sucesso!");

        } catch (Exception e) {
            log.error("❌ Erro ao enviar email de boas-vindas: {}", e.getMessage(), e);
            // Não lança exceção para não interromper o fluxo de cadastro
        }
    }

    /**
     * Envia código de verificação de conta
     */
    public void enviarCodigoVerificacao(String destinatario, String codigo, String nomeUsuario) {
        try {
            log.info("📧 Enviando código de verificação para: {}", destinatario);

            String assunto = "Código de Verificação - Luigarah";

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .codigo { background: white; border: 2px dashed #667eea; border-radius: 10px; padding: 20px; text-align: center; margin: 20px 0; }
                        .codigo-numero { font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px; }
                        .alert { background: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 Verificação de Conta</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Você solicitou a verificação da sua conta no <strong>Luigarah</strong>.</p>
                            <p>Use o código abaixo para confirmar sua conta:</p>
                            <div class="codigo">
                                <p style="margin: 0; color: #666; font-size: 14px;">Seu código de verificação é:</p>
                                <div class="codigo-numero">%s</div>
                            </div>
                            <div class="alert">
                                <strong>⏰ Atenção:</strong> Este código expira em <strong>12 horas</strong>.
                            </div>
                            <p>Se você não solicitou este código, por favor ignore este email.</p>
                            <p>Estamos quase lá! Após a verificação, você terá acesso completo à plataforma. 🎉</p>
                            <p><strong>Equipe Luigarah</strong></p>
                        </div>
                        <div class="footer">
                            <p>© 2025 Luigarah. Todos os direitos reservados.</p>
                            <p>Este é um email automático, por favor não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, nomeUsuario, codigo);

            enviarEmail(destinatario, assunto, htmlContent);
            log.info("✅ Código de verificação enviado com sucesso!");

        } catch (Exception e) {
            log.error("❌ Erro ao enviar código de verificação: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar email de verificação");
        }
    }

    /**
     * Envia código de redefinição de senha
     */
    public void enviarCodigoResetSenha(String destinatario, String codigo, String nomeUsuario) {
        try {
            log.info("📧 Enviando código de reset de senha para: {}", destinatario);

            String assunto = "Redefinição de Senha - Luigarah";

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .codigo { background: white; border: 2px dashed #f5576c; border-radius: 10px; padding: 20px; text-align: center; margin: 20px 0; }
                        .codigo-numero { font-size: 36px; font-weight: bold; color: #f5576c; letter-spacing: 8px; }
                        .alert { background: #f8d7da; border-left: 4px solid #dc3545; padding: 12px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔑 Redefinição de Senha</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Você solicitou a redefinição de senha da sua conta no <strong>Luigarah</strong>.</p>
                            <p>Use o código abaixo para criar uma nova senha:</p>
                            <div class="codigo">
                                <p style="margin: 0; color: #666; font-size: 14px;">Seu código de redefinição é:</p>
                                <div class="codigo-numero">%s</div>
                            </div>
                            <div class="alert">
                                <strong>⏰ Atenção:</strong> Este código expira em <strong>12 horas</strong>.
                            </div>
                            <div class="alert">
                                <strong>🔒 Segurança:</strong> Se você não solicitou a redefinição de senha, <strong>ignore este email</strong> e sua senha permanecerá inalterada.
                            </div>
                            <p>Por questões de segurança, recomendamos que você escolha uma senha forte.</p>
                            <p><strong>Equipe Luigarah</strong></p>
                        </div>
                        <div class="footer">
                            <p>© 2025 Luigarah. Todos os direitos reservados.</p>
                            <p>Este é um email automático, por favor não responda.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, nomeUsuario, codigo);

            enviarEmail(destinatario, assunto, htmlContent);
            log.info("✅ Código de reset de senha enviado com sucesso!");

        } catch (Exception e) {
            log.error("❌ Erro ao enviar código de reset de senha: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar email de redefinição de senha");
        }
    }

    /**
     * Método genérico para enviar email via Brevo API
     */
    private void enviarEmail(String destinatario, String assunto, String htmlContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> sender = new HashMap<>();
        sender.put("email", senderEmail);
        sender.put("name", senderName);

        Map<String, Object> to = new HashMap<>();
        to.put("email", destinatario);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sender", sender);
        requestBody.put("to", List.of(to));
        requestBody.put("subject", assunto);
        requestBody.put("htmlContent", htmlContent);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email enviado com sucesso via Brevo API");
            } else {
                log.error("❌ Erro ao enviar email via Brevo: {}", response.getStatusCode());
                throw new RuntimeException("Falha ao enviar email");
            }

        } catch (Exception e) {
            log.error("❌ Exceção ao chamar Brevo API: {}", e.getMessage(), e);
            throw new RuntimeException("Erro na comunicação com serviço de email", e);
        }
    }
}

