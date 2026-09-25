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
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                            line-height: 1.4;
                            color: #1a1a1a;
                            background-color: #f5f5f5;
                            padding: 8px;
                        }
                        .email-wrapper { max-width: 480px; margin: 0 auto; background-color: #ffffff; }
                        .logo-container { 
                            background-color: #ffffff;
                            padding: 15px 10px;
                            text-align: center;
                            border-bottom: 1px solid #e5e5e5;
                        }
                        .logo { max-width: 100px; height: auto; }
                        .header { 
                            background-color: #1a1a1a;
                            color: #ffffff;
                            padding: 15px 15px;
                            text-align: center;
                        }
                        .header h1 { 
                            font-size: 16px;
                            font-weight: 600;
                            margin: 0;
                            letter-spacing: -0.5px;
                        }
                        .content { 
                            background-color: #ffffff;
                            padding: 20px 15px;
                            color: #1a1a1a;
                        }
                        .content h2 {
                            font-size: 14px;
                            font-weight: 600;
                            margin-bottom: 10px;
                            color: #1a1a1a;
                        }
                        .content p {
                            margin-bottom: 10px;
                            font-size: 12px;
                            color: #4a4a4a;
                        }
                        .content ul {
                            list-style: none;
                            padding: 10px 0;
                            margin: 10px 0;
                        }
                        .content ul li {
                            padding: 6px 0;
                            font-size: 12px;
                            color: #4a4a4a;
                            border-bottom: 1px solid #f0f0f0;
                        }
                        .content ul li:last-child { border-bottom: none; }
                        .button-container { text-align: center; margin: 15px 0; }
                        .button { 
                            display: inline-block;
                            background-color: #1a1a1a;
                            color: #ffffff !important;
                            padding: 10px 25px;
                            text-decoration: none;
                            border-radius: 4px;
                            font-size: 12px;
                            font-weight: 600;
                            transition: background-color 0.3s;
                        }
                        .button:hover { background-color: #333333; }
                        .footer { 
                            background-color: #f8f8f8;
                            padding: 15px;
                            text-align: center;
                            border-top: 1px solid #e5e5e5;
                        }
                        .footer p {
                            font-size: 10px;
                            color: #6a6a6a;
                            margin: 4px 0;
                        }
                        .divider {
                            height: 1px;
                            background-color: #e5e5e5;
                            margin: 15px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-wrapper">
                        <div class="logo-container">
                            <img src="https://github.com/SaraahBR/Luigarah/blob/main/public/logos/LUIGARA-LOGO.png?raw=true" alt="Luigarah" class="logo" style="max-width: 100px; width: 100%%; height: auto;">
                        </div>
                        <div class="header">
                            <h1 style="font-family: 'Playfair Display', 'Times New Roman', serif; font-size: 16px; font-weight: bold; letter-spacing: 0.15em; margin: 0;">LUIGARAH</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>É um prazer tê-lo(a) conosco! ♡</p>
                            <p>Obrigado por %s no <strong>Luigarah</strong> - sua nova plataforma de moda e estilo!</p>
                            <p>Estamos animados para você explorar nossas coleções exclusivas e encontrar peças perfeitas para você.</p>
                            
                            <div class="divider"></div>
                            
                            <p><strong>O que você pode fazer agora:</strong></p>
                            <ul>
                                <li>▪ Explorar nossos produtos</li>
                                <li>▪ Adicionar itens à sua lista de desejos</li>
                                <li>▪ Fazer seu primeiro pedido</li>
                                <li>▪ Personalizar seu perfil</li>
                            </ul>
                            
                            <div class="button-container">
                                <a href="https://luigarah.vercel.app/" class="button">Começar a Comprar</a>
                            </div>
                            
                            <div class="divider"></div>
                            
                            <p>Se precisar de ajuda, nossa equipe está sempre disponível para você!</p>
                            <p style="margin-top: 10px; color: #1a1a1a;"><strong>Equipe Luigarah</strong></p>
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
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                            line-height: 1.4;
                            color: #1a1a1a;
                            background-color: #f5f5f5;
                            padding: 8px;
                        }
                        .email-wrapper { max-width: 480px; margin: 0 auto; background-color: #ffffff; }
                        .logo-container { 
                            background-color: #ffffff;
                            padding: 15px 10px;
                            text-align: center;
                            border-bottom: 1px solid #e5e5e5;
                        }
                        .logo { max-width: 100px; height: auto; }
                        .header { 
                            background-color: #1a1a1a;
                            color: #ffffff;
                            padding: 15px 15px;
                            text-align: center;
                        }
                        .header h1 { 
                            font-size: 16px;
                            font-weight: 600;
                            margin: 0;
                            letter-spacing: -0.5px;
                        }
                        .content { 
                            background-color: #ffffff;
                            padding: 20px 15px;
                            color: #1a1a1a;
                        }
                        .content h2 {
                            font-size: 14px;
                            font-weight: 600;
                            margin-bottom: 10px;
                            color: #1a1a1a;
                        }
                        .content p {
                            margin-bottom: 10px;
                            font-size: 12px;
                            color: #4a4a4a;
                        }
                        .codigo { 
                            background: linear-gradient(135deg, #f8f8f8 0%%, #ffffff 100%%);
                            border: 2px solid #1a1a1a;
                            border-radius: 4px;
                            padding: 12px 10px;
                            text-align: center;
                            margin: 12px 0;
                        }
                        .codigo-label {
                            font-size: 9px;
                            color: #6a6a6a;
                            margin-bottom: 6px;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            font-weight: 600;
                        }
                        .codigo-numero { 
                            font-size: 24px;
                            font-weight: 700;
                            color: #1a1a1a;
                            letter-spacing: 5px;
                            font-family: 'Courier New', monospace;
                            margin: 5px 0;
                        }
                        .alert { 
                            background-color: #f8f8f8;
                            border-left: 3px solid #1a1a1a;
                            padding: 10px 12px;
                            margin: 12px 0;
                            border-radius: 3px;
                            font-size: 11px;
                        }
                        .alert strong {
                            color: #1a1a1a;
                        }
                        .footer { 
                            background-color: #f8f8f8;
                            padding: 15px;
                            text-align: center;
                            border-top: 1px solid #e5e5e5;
                        }
                        .footer p {
                            font-size: 10px;
                            color: #6a6a6a;
                            margin: 4px 0;
                        }
                        .divider {
                            height: 1px;
                            background-color: #e5e5e5;
                            margin: 15px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-wrapper">
                        <div class="logo-container">
                            <img src="https://github.com/SaraahBR/Luigarah/blob/main/public/logos/LUIGARA-LOGO.png?raw=true" alt="Luigarah" class="logo" style="max-width: 100px; width: 100%%; height: auto;">
                        </div>
                        <div class="header">
                            <h1 style="font-family: 'Playfair Display', 'Times New Roman', serif; font-size: 16px; font-weight: bold; letter-spacing: 0.15em; margin: 0;">LUIGARAH</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Você solicitou a verificação da sua conta no <strong>Luigarah</strong>.</p>
                            <p>Use o código abaixo para confirmar sua conta:</p>
                            
                            <div class="codigo">
                                <div class="codigo-label">Seu código de verificação é:</div>
                                <div class="codigo-numero">%s</div>
                            </div>
                            
                            <div class="alert">
                                <strong>⏰ Atenção:</strong> Este código expira em <strong>12 horas</strong>.
                            </div>
                            
                            <div class="divider"></div>
                            
                            <p>Se você não solicitou este código, por favor ignore este email.</p>
                            <p>Estamos quase lá! Após a verificação, você terá acesso completo à plataforma.</p>
                            <p style="margin-top: 10px; color: #1a1a1a;"><strong>Equipe Luigarah</strong></p>
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
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { 
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                            line-height: 1.4;
                            color: #1a1a1a;
                            background-color: #f5f5f5;
                            padding: 8px;
                        }
                        .email-wrapper { max-width: 480px; margin: 0 auto; background-color: #ffffff; }
                        .logo-container { 
                            background-color: #ffffff;
                            padding: 15px 10px;
                            text-align: center;
                            border-bottom: 1px solid #e5e5e5;
                        }
                        .logo { max-width: 100px; height: auto; }
                        .header { 
                            background-color: #1a1a1a;
                            color: #ffffff;
                            padding: 15px 15px;
                            text-align: center;
                        }
                        .header h1 { 
                            font-size: 16px;
                            font-weight: 600;
                            margin: 0;
                            letter-spacing: -0.5px;
                        }
                        .content { 
                            background-color: #ffffff;
                            padding: 20px 15px;
                            color: #1a1a1a;
                        }
                        .content h2 {
                            font-size: 14px;
                            font-weight: 600;
                            margin-bottom: 10px;
                            color: #1a1a1a;
                        }
                        .content p {
                            margin-bottom: 10px;
                            font-size: 12px;
                            color: #4a4a4a;
                        }
                        .codigo { 
                            background: linear-gradient(135deg, #f8f8f8 0%%, #ffffff 100%%);
                            border: 2px solid #1a1a1a;
                            border-radius: 4px;
                            padding: 12px 10px;
                            text-align: center;
                            margin: 12px 0;
                        }
                        .codigo-label {
                            font-size: 9px;
                            color: #6a6a6a;
                            margin-bottom: 6px;
                            text-transform: uppercase;
                            letter-spacing: 0.5px;
                            font-weight: 600;
                        }
                        .codigo-numero { 
                            font-size: 24px;
                            font-weight: 700;
                            color: #1a1a1a;
                            letter-spacing: 5px;
                            font-family: 'Courier New', monospace;
                            margin: 5px 0;
                        }
                        .alert { 
                            background-color: #f8f8f8;
                            border-left: 3px solid #1a1a1a;
                            padding: 10px 12px;
                            margin: 12px 0;
                            border-radius: 3px;
                            font-size: 11px;
                        }
                        .alert strong {
                            color: #1a1a1a;
                            display: block;
                            margin-bottom: 3px;
                        }
                        .security-alert {
                            background-color: #1a1a1a;
                            color: #ffffff;
                            border-left: 3px solid #666666;
                            padding: 10px 12px;
                            margin: 12px 0;
                            border-radius: 3px;
                            font-size: 11px;
                        }
                        .security-alert strong {
                            color: #ffffff;
                            display: block;
                            margin-bottom: 3px;
                        }
                        .footer { 
                            background-color: #f8f8f8;
                            padding: 15px;
                            text-align: center;
                            border-top: 1px solid #e5e5e5;
                        }
                        .footer p {
                            font-size: 10px;
                            color: #6a6a6a;
                            margin: 4px 0;
                        }
                        .divider {
                            height: 1px;
                            background-color: #e5e5e5;
                            margin: 15px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-wrapper">
                        <div class="logo-container">
                            <img src="https://github.com/SaraahBR/Luigarah/blob/main/public/logos/LUIGARA-LOGO.png?raw=true" alt="Luigarah" class="logo" style="max-width: 100px; width: 100%%; height: auto;">
                        </div>
                        <div class="header">
                            <h1 style="font-family: 'Playfair Display', 'Times New Roman', serif; font-size: 16px; font-weight: bold; letter-spacing: 0.15em; margin: 0;">LUIGARAH</h1>
                        </div>
                        <div class="content">
                            <h2>Olá, %s!</h2>
                            <p>Você solicitou a redefinição de senha da sua conta no <strong>Luigarah</strong>.</p>
                            <p>Use o código abaixo para criar uma nova senha:</p>
                            
                            <div class="codigo">
                                <div class="codigo-label">Seu código de redefinição é:</div>
                                <div class="codigo-numero">%s</div>
                            </div>
                            
                            <div class="alert">
                                <strong>⏰ Atenção:</strong>
                                Este código expira em <strong>1 hora</strong>.
                            </div>
                            
                            <div class="security-alert">
                                <strong>🔒 Segurança:</strong> Se você não solicitou a redefinição de senha, <strong>ignore este email</strong> e sua senha permanecerá inalterada.
                            </div>
                            
                            <div class="divider"></div>
                            
                            <p>Por questões de segurança, recomendamos que você escolha uma senha forte.</p>
                            <p style="margin-top: 10px; color: #1a1a1a;"><strong>Equipe Luigarah</strong></p>
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

