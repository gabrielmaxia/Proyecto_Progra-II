import com.fazecast.jSerialComm.*;

public class ServoControl {
    
    public void activarServomotor() {
        System.out.println("⚙️ === INICIANDO SERVOCONTROL ===");
        
        // Listar todos los puertos disponibles para diagnóstico
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("🔍 Puertos seriales disponibles:");
        for (SerialPort port : ports) {
            System.out.println("   - " + port.getSystemPortName() + " : " + port.getDescriptivePortName());
        }
        
        // Intentar conectar al puerto COM5 (o el que uses)
        SerialPort arduinoPort = SerialPort.getCommPort("COM5");
        System.out.println("🔌 Puerto COM5 encontrado: " + (arduinoPort != null));
        
        if (arduinoPort != null) {
            // Configurar parámetros de comunicación
            arduinoPort.setBaudRate(9600);
            arduinoPort.setNumDataBits(8);
            arduinoPort.setNumStopBits(1);
            arduinoPort.setParity(SerialPort.NO_PARITY);
            
            System.out.println("🚪 Intentando abrir puerto COM5...");
            if (arduinoPort.openPort()) {
                System.out.println("✅ Puerto abierto exitosamente");
                
                try {
                    // Esperar para que Arduino se inicialice
                    System.out.println("⏳ Esperando inicialización de Arduino...");
                    Thread.sleep(2000);
                    
                    // Enviar comando
                    String comando = "SUBIR\n";
                    byte[] buffer = comando.getBytes();
                    int bytesWritten = arduinoPort.writeBytes(buffer, buffer.length);
                    System.out.println("📤 Bytes enviados: " + bytesWritten + ", Comando: " + comando.trim());
                    
                    // Leer respuesta de Arduino (opcional)
                    Thread.sleep(1000);
                    if (arduinoPort.bytesAvailable() > 0) {
                        byte[] readBuffer = new byte[arduinoPort.bytesAvailable()];
                        int bytesRead = arduinoPort.readBytes(readBuffer, readBuffer.length);
                        String respuesta = new String(readBuffer);
                        System.out.println("📥 Respuesta Arduino: " + respuesta.trim());
                    } else {
                        System.out.println("ℹ️  No hay respuesta del Arduino (puede ser normal)");
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ Error durante comunicación: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // Cerrar el puerto
                    arduinoPort.closePort();
                    System.out.println("🔒 Puerto cerrado");
                }
            } else {
                System.err.println("❌ ERROR: No se pudo abrir el puerto COM5");
            }
        } else {
            System.err.println("❌ ERROR: Puerto COM5 no encontrado");
            System.out.println("💡 Sugerencia: Verifica que el Arduino esté conectado");
            System.out.println("💡 Sugerencia: Revisa el puerto COM en el Administrador de dispositivos");
        }
        System.out.println("🏁 === FIN SERVOCONTROL ===");
    }
}