/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mi.casita.segura;

/**
 *
 * @author sazo
 */
public class Rol {
    private int idRol;
    private String nombreRol;
    private String descripcion;
    private boolean activo;

    public Rol() {}

    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    // Getters y Setters
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreRol;
    }
}