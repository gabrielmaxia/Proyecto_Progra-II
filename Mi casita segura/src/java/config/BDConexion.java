/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mi.casita.segura;

import java.sql.*;
import java.util.Properties;

/**
 *
 * @author sazo
 */


public class BDConexion {
    private static Connection con = null;
    
    // Método estático para obtener la conexión
    public static Connection getConnection() {
        if (con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MiCasitaSegura","root","");            
            } catch (Exception e) {
                System.err.println("Error conectando a la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return con;
    }
    
    // Constructor para mantener compatibilidad
    public BDConexion(){
        getConnection();
    }
}