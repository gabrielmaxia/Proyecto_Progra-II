/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servicio;

import Util.EmailUtil;

/**
 *
 * @author sazo
 */

public class NotificacionServicio {
    
    public static void procesarNotificacion(String destinatario, String asunto, String cuerpo) {
        // RN01: Procesamiento en segundo plano sin interrumpir operaciones
        Thread notificationThread = new Thread(() -> {
            try {
                EmailUtil.sendSimpleEmail(destinatario, asunto, cuerpo);
                System.out.println("Notificación enviada exitosamente a: " + destinatario);
            } catch (Exception e) {
                // RN01: Si falla el envío, no debe afectar la operación principal
                System.err.println("Error enviando notificación (no crítico): " + e.getMessage());
            }
        });
        
        notificationThread.setDaemon(true);
        notificationThread.start();
    }
    
    /**
     * Versión con adjunto (para casos como envío de QR)
     */
    public static void procesarNotificacionConAdjunto(String destinatario, String asunto, String cuerpo, 
                                                      byte[] adjunto, String nombreArchivo) {
        Thread notificationThread = new Thread(() -> {
            try {
                EmailUtil.sendEmailWithAttachment(destinatario, asunto, cuerpo, adjunto, nombreArchivo);
                System.out.println("Notificación con adjunto enviada exitosamente a: " + destinatario);
            } catch (Exception e) {
                System.err.println("Error enviando notificación con adjunto (no crítico): " + e.getMessage());
            }
        });
        
        notificationThread.setDaemon(true);
        notificationThread.start();
    }
}