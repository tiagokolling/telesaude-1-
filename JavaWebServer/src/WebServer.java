import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;

public class WebServer {
    public static void main(String[] args) throws IOException {
        // Cria o servidor na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/conta", new ContaHandler());
        server.setExecutor(null); // Usa um executor padrão
        server.start();
        System.out.println("Servidor iniciado na porta 8080...");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html"; // Página padrão
            }
            
            File file = new File("src/public" + path); // Define a pasta base 'public'
            if (file.exists() && !file.isDirectory()) {
                String mimeType = Files.probeContentType(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", mimeType);
                exchange.sendResponseHeaders(200, file.length());
                
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(os);
                }
            } else {
                String response = "404 (Not Found)";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    static class ContaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String numeroConta = null;
                if (query != null && query.startsWith("numero=")) {
                    numeroConta = query.split("=")[1];
                }

                ContaDAO contaDAO = new ContaDAO();
                Conta conta = contaDAO.buscarConta(Integer.parseInt(numeroConta));

                String response = "{\"numero\": "+ conta.getNumero() +", \"saldo\": \""+ conta.getSaldo() +"\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = "405 (Method Not Allowed)";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}