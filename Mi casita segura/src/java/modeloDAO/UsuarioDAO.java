/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mi.casita.segura;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sazo
 */

public class UsuarioDAO {
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAO.class.getName());

    // Crear nuevo usuario
    public boolean crearUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (dpi, nombre, apellidos, correo, contrasena, id_rol, lote, numero_casa, codigo_qr, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getDpi());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidos());
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, usuario.getContrasena());
            stmt.setInt(6, usuario.getIdRol());
            
            // Solo asignar casa si no es guardia (rol 2)
            if (usuario.getIdRol() != 2 && usuario.getLote() != null && usuario.getNumeroCasa() != null) {
                stmt.setString(7, usuario.getLote());
                stmt.setInt(8, usuario.getNumeroCasa());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.INTEGER);
            }
            
            // Generar código QR simple (en producción usar librería QR)
            String codigoQr = "QR_" + usuario.getDpi() + "_" + System.currentTimeMillis();
            stmt.setString(9, codigoQr);
            usuario.setCodigoQr(codigoQr);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setIdUsuario(generatedKeys.getInt(1));
                        LOGGER.info("Usuario creado exitosamente con ID: " + usuario.getIdUsuario());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear usuario", e);
        }
        
        return false;
    }

    // Obtener todos los usuarios activos
    public List<Usuario> obtenerUsuariosActivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "LEFT JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.activo = 1 ORDER BY u.nombre, u.apellidos";
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario usuario = mapearResultSetAUsuario(rs);
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios activos", e);
        }
        
        return usuarios;
    }

    // Verificar si DPI ya existe
    public boolean existeDPI(String dpi) {
        return existeDPI(dpi, 0);
    }

    public boolean existeDPI(String dpi, int excluirUsuarioId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE dpi = ? AND activo = 1";
        if (excluirUsuarioId > 0) {
            sql += " AND id_usuario != ?";
        }
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dpi);
            if (excluirUsuarioId > 0) {
                stmt.setInt(2, excluirUsuarioId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al verificar DPI existente", e);
        }
        
        return false;
    }

    // Verificar si correo ya existe
    public boolean existeCorreo(String correo) {
        return existeCorreo(correo, 0);
    }

    public boolean existeCorreo(String correo, int excluirUsuarioId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ? AND activo = 1";
        if (excluirUsuarioId > 0) {
            sql += " AND id_usuario != ?";
        }
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            if (excluirUsuarioId > 0) {
                stmt.setInt(2, excluirUsuarioId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al verificar correo existente", e);
        }
        
        return false;
    }

    // Método combinado para verificar DPI y correo
    public boolean existeUsuario(String dpi, String correo) {
        return existeDPI(dpi) || existeCorreo(correo);
    }

    // Eliminar usuario (soft delete)
    public boolean eliminarUsuario(int id) {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id_usuario = ?";
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.info("Usuario eliminado (soft delete) con ID: " + id);
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario con ID: " + id, e);
        }
        
        return false;
    }

    // Obtener todos los roles
    public List<Rol> obtenerRoles() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol, descripcion FROM roles WHERE activo = 1 ORDER BY nombre_rol";
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setActivo(true);
                roles.add(rol);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener roles", e);
            // Si no hay roles en BD, crear roles por defecto
            roles = crearRolesPorDefecto();
        }
        
        return roles;
    }

    // Crear roles por defecto si no existen en BD
    private List<Rol> crearRolesPorDefecto() {
        List<Rol> roles = new ArrayList<>();
        roles.add(new Rol(1, "Administrador"));
        roles.add(new Rol(2, "Guardia"));
        roles.add(new Rol(3, "Residente"));
        return roles;
    }

    // Obtener usuario por ID - método adicional útil
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "LEFT JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.id_usuario = ?";
        
        try (Connection conn = BDConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por ID: " + id, e);
        }
        
        return null;
    }

    // Mapear ResultSet a Usuario
    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setDpi(rs.getString("dpi"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setNombreRol(rs.getString("nombre_rol"));
        usuario.setLote(rs.getString("lote"));
        usuario.setCodigoQr(rs.getString("codigo_qr"));
        
        Object numeroCasa = rs.getObject("numero_casa");
        if (numeroCasa != null) {
            usuario.setNumeroCasa(rs.getInt("numero_casa"));
        }
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }
        
        usuario.setActivo(rs.getBoolean("activo"));
        
        return usuario;
    }
}