/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mi.casita.segura;
import java.time.LocalDateTime;

/**
 *
 * @author sazo
 */
public class Usuario {
    private int idUsuario;
    private String dpi;
    private String nombre;
    private String apellidos;
    private String correo;
    private String contrasena;
    private int idRol;
    private String nombreRol;
    private String lote;
    private Integer numeroCasa;
    private String codigoQr;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private LocalDateTime ultimoAcceso;
    private boolean activo;

    // Constructores
    public Usuario() {}

    public Usuario(String dpi, String nombre, String apellidos, String correo, 
                   String contrasena, int idRol, String lote, Integer numeroCasa) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.lote = lote;
        this.numeroCasa = numeroCasa;
        this.activo = true;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreCompleto() { return nombre + " " + apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public Integer getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(Integer numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getCasaCompleta() {
        if (lote != null && numeroCasa != null) {
            return lote + "-" + numeroCasa;
        }
        return "N/A";
    }

    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getEstadoTexto() { return activo ? "Activo" : "Inactivo"; }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", correo='" + correo + '\'' +
                ", nombreRol='" + nombreRol + '\'' +
                ", casa='" + getCasaCompleta() + '\'' +
                ", activo=" + activo +
                '}';
    }
}
