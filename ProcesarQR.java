import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProcesarQR")
public class ProcesarQR extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        System.out.println("=== SERVLET ProcesarQR INICIALIZADO ===");
        super.init();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🎯 === PETICIÓN POST RECIBIDA ===");
        
        // Habilitar CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Obtener el parámetro
        String codigoQR = request.getParameter("codigo");
        System.out.println("📋 QR recibido: " + codigoQR);
        
        // Verificar si el parámetro viene vacío
        if (codigoQR == null || codigoQR.trim().isEmpty()) {
            System.out.println("❌ ERROR: Parámetro 'codigo' vacío o nulo");
            response.setContentType("text/plain");
            response.getWriter().write("ERROR: Código QR vacío");
            return;
        }

        try {
            // Llama a tu clase de control del servo
            ServoControl servo = new ServoControl();
            servo.activarServomotor();
            System.out.println("✅ Servomotor activado correctamente");
            
        } catch (Exception e) {
            System.out.println("❌ Error con servomotor: " + e.getMessage());
            e.printStackTrace();
        }

        // Respuesta al navegador
        response.setContentType("text/plain");
        response.getWriter().write("Servidor recibió: " + codigoQR);
        
        System.out.println("✅ === PETICIÓN COMPLETADA ===");
    }
    
    // Manejar solicitudes OPTIONS para CORS
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}