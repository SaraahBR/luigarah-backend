import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 🔍 Script para testar se o CORS está configurado no backend Spring Boot
 *
 * Compile: javac TestCORS.java
 * Execute: java TestCORS
 */
public class TestCORS {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        System.out.println(CYAN + "\n🔍 TESTANDO CONFIGURAÇÃO CORS DO BACKEND SPRING BOOT\n" + RESET);
        System.out.println("Backend: https://luigarah-backend.onrender.com");
        System.out.println("Frontend Origin: http://localhost:3000\n");
        System.out.println("=".repeat(80) + "\n");

        String[] endpoints = {
                "https://luigarah-backend.onrender.com/produtos/categoria/sapatos?pagina=0&tamanho=15",
                "https://luigarah-backend.onrender.com/produtos/categoria/bolsas?pagina=0&tamanho=15",
                "https://luigarah-backend.onrender.com/produtos/categoria/roupas?pagina=0&tamanho=15",
                "https://luigarah-backend.onrender.com/api/auth/oauth/sync"
        };

        boolean todosOK = true;

        for (String endpoint : endpoints) {
            boolean ok = testarEndpoint(endpoint);
            if (!ok) {
                todosOK = false;
            }
        }

        System.out.println("\n" + "=".repeat(80) + "\n");

        if (todosOK) {
            System.out.println(GREEN + "✅ TODOS OS ENDPOINTS TÊM CORS CONFIGURADO CORRETAMENTE!" + RESET);
            System.out.println("\n📋 Próximos passos:");
            System.out.println("   1. Limpe o cache do navegador (Ctrl+Shift+Delete)");
            System.out.println("   2. Faça Hard Reload (Ctrl+Shift+R)");
            System.out.println("   3. Tente novamente em modo anônimo (Ctrl+Shift+N)\n");
        } else {
            System.out.println(RED + "❌ CORS NÃO ESTÁ CONFIGURADO NO BACKEND!" + RESET);
            System.out.println("\n📋 Próximos passos:");
            System.out.println("   1. Abra o arquivo: src/main/java/com/luigarah/config/SecurityConfig.java");
            System.out.println("   2. Adicione o método corsConfigurationSource() (veja SOLUCAO_ERRO_500.md)");
            System.out.println("   3. Adicione .cors() no filterChain()");
            System.out.println("   4. Faça: git add . && git commit -m \"fix: CORS\" && git push");
            System.out.println("   5. Aguarde deploy no Render.com (3-5 minutos)");
            System.out.println("   6. Execute este script novamente\n");
        }
    }

    private static boolean testarEndpoint(String urlString) {
        try {
            System.out.println(BLUE + "📡 Testando: " + RESET + urlString);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Simular requisição vinda do frontend
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Origin", "http://localhost:3000");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // Fazer a requisição
            int statusCode = conn.getResponseCode();
            String statusText = conn.getResponseMessage();

            System.out.println("   Status: " + statusCode + " " + statusText);

            // Verificar headers CORS
            Map<String, List<String>> headers = conn.getHeaderFields();

            String allowOrigin = conn.getHeaderField("Access-Control-Allow-Origin");
            String allowMethods = conn.getHeaderField("Access-Control-Allow-Methods");
            String allowCredentials = conn.getHeaderField("Access-Control-Allow-Credentials");
            String allowHeaders = conn.getHeaderField("Access-Control-Allow-Headers");

            System.out.println("\n   " + YELLOW + "CORS Headers:" + RESET);

            boolean corsOK = true;

            if (allowOrigin != null && !allowOrigin.isEmpty()) {
                System.out.println("   " + GREEN + "✅ Access-Control-Allow-Origin: " + allowOrigin + RESET);
            } else {
                System.out.println("   " + RED + "❌ Access-Control-Allow-Origin: AUSENTE" + RESET);
                corsOK = false;
            }

            if (allowMethods != null && !allowMethods.isEmpty()) {
                System.out.println("   " + GREEN + "✅ Access-Control-Allow-Methods: " + allowMethods + RESET);
            } else {
                System.out.println("   " + YELLOW + "⚠️  Access-Control-Allow-Methods: AUSENTE" + RESET);
            }

            if (allowCredentials != null && !allowCredentials.isEmpty()) {
                System.out.println("   " + GREEN + "✅ Access-Control-Allow-Credentials: " + allowCredentials + RESET);
            } else {
                System.out.println("   " + YELLOW + "⚠️  Access-Control-Allow-Credentials: AUSENTE" + RESET);
            }

            if (allowHeaders != null && !allowHeaders.isEmpty()) {
                System.out.println("   " + GREEN + "✅ Access-Control-Allow-Headers: " + allowHeaders + RESET);
            } else {
                System.out.println("   " + YELLOW + "⚠️  Access-Control-Allow-Headers: AUSENTE" + RESET);
            }

            System.out.println();

            if (!corsOK) {
                System.out.println("   " + RED + "❌ CORS NÃO CONFIGURADO PARA ESTE ENDPOINT!" + RESET);
                System.out.println("   " + RED + "   O navegador vai BLOQUEAR esta requisição!" + RESET);
            } else {
                System.out.println("   " + GREEN + "✅ CORS CONFIGURADO CORRETAMENTE!" + RESET);
            }

            System.out.println("\n" + "-".repeat(80) + "\n");

            conn.disconnect();
            return corsOK;

        } catch (Exception e) {
            System.out.println("   " + RED + "❌ Erro ao testar endpoint: " + e.getMessage() + RESET);
            System.out.println("\n" + "-".repeat(80) + "\n");
            return false;
        }
    }
}
