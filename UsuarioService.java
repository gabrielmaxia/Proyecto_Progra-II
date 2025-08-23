/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mi.casita.segura;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author sazo
 */


public class UsuarioService {
    private UsuarioDAO usuarioDAO;
    private NotificationService notificationService;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.notificationService = new NotificationService();
    }

    public boolean crearUsuario(Usuario usuario) throws Exception {
        // Validaciones de negocio
        validarDatosUsuario(usuario);

        // Verificar si ya existe usuario con mismo DPI o correo
        if (usuarioDAO.existeUsuario(usuario.getDpi(), usuario.getCorreo())) {
            throw new Exception("El DPI o el correo ya están registrados");
        }

        // Crear usuario
        boolean creado = usuarioDAO.crearUsuario(usuario);
        
        if (creado) {
            try {
                // Enviar notificación con código QR
                notificationService.enviarNotificacionAccesoCreado(usuario);
            } catch (Exception e) {
                System.err.println("Error enviando notificación: " + e.getMessage());
                // No fallar la creación del usuario si falla la notificación
            }
        }

        return creado;
    }

    public List<Usuario> obtenerUsuariosActivos() throws SQLException {
        return usuarioDAO.obtenerUsuariosActivos();
    }

    public boolean eliminarUsuario(int idUsuario) throws SQLException {
        return usuarioDAO.eliminarUsuario(idUsuario);
    }

    public List<Rol> obtenerRoles() throws SQLException {
        return usuarioDAO.obtenerRoles();
    }

    // Método adicional útil para obtener un usuario específico
    public Usuario obtenerUsuarioPorId(int id) {
        return usuarioDAO.obtenerUsuarioPorId(id);
    }

    private void validarDatosUsuario(Usuario usuario) throws Exception {
        if (usuario.getDpi() == null || usuario.getDpi().trim().isEmpty()) {
            throw new Exception("El DPI es obligatorio");
        }

        // Validar formato DPI (13 dígitos)
        if (!usuario.getDpi().matches("\\d{13}")) {
            throw new Exception("El DPI debe tener exactamente 13 dígitos");
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            throw new Exception("Los apellidos son obligatorios");
        }

        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo es obligatorio");
        }

        if (!validarEmail(usuario.getCorreo())) {
            throw new Exception("El formato del correo no es válido");
        }

        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            throw new Exception("La contraseña es obligatoria");
        }

        if (usuario.getContrasena().length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }

        // RN1: Validar campos de lote y casa para residentes
        if (esResidente(usuario.getIdRol())) {
            if (usuario.getLote() == null || usuario.getNumeroCasa() == null) {
                throw new Exception("El lote y número de casa son obligatorios para residentes");
            }
            
            if (usuario.getLote().length() != 1 || 
                usuario.getLote().charAt(0) < 'A' || usuario.getLote().charAt(0) > 'Z') {
                throw new Exception("El lote debe ser una letra de A a Z");
            }
            
            if (usuario.getNumeroCasa() < 1 || usuario.getNumeroCasa() > 50) {
                throw new Exception("El número de casa debe estar entre 1 y 50");
            }

            // Verificar disponibilidad de casa
            if (!esCasaDisponible(usuario.getLote(), usuario.getNumeroCasa())) {
                throw new Exception("La casa " + usuario.getLote() + "-" + usuario.getNumeroCasa() + " ya está ocupada");
            }
        }
    }

    private boolean validarEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    private boolean esResidente(int idRol) {
        try {
            List<Rol> roles = usuarioDAO.obtenerRoles();
            for (Rol rol : roles) {
                if (rol.getIdRol() == idRol && "Residente".equals(rol.getNombreRol())) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error verificando rol: " + e.getMessage());
        }
        return false;
    }

    // Verificar si una casa está disponible
    private boolean esCasaDisponible(String lote, int numeroCasa) {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerUsuariosActivos();
            for (Usuario u : usuarios) {
                if (lote.equals(u.getLote()) && numeroCasa == u.getNumeroCasa()) {
                    return false; // Casa ocupada
                }
            }
            return true; // Casa disponible
        } catch (Exception e) {
            System.err.println("Error verificando disponibilidad de casa: " + e.getMessage());
            return false;
        }
    }
}