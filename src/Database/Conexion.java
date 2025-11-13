/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class Conexion {
/*
    private final String DRIVER = "org.postgresql.Driver";
    private final String DB = "db_grupo04sa";
    private final String USER = "grupo04sa";
    private final String PASSWORD  = "grup004grup004*";
    private final String URL = "jdbc:postgresql://mail.tecnoweb.org.bo/";
  */  
    private final String DRIVER = "org.postgresql.Driver";
    private final String DB = "lavanderia";
    private final String USER = "postgres";
    private final String PASSWORD  = "8554";
    private final String URL = "jdbc:postgresql://localhost:5432/";
        
//    private final String DRIVER = "org.postgresql.Driver";
//    private final String DB = "tecnoweb";
//    private final String USER = "postgres";
//    private final String PASSWORD = "1234";
//    private final String URL = "jdbc:postgresql://localhost:5432/";

//    private final String DRIVER = "org.postgresql.Driver";
//    private final String DB = "grupo04sa";
//    private final String USER = "postgres";
//    private final String PASSWORD = "Neji021019";
//    private final String URL = "jdbc:postgresql://localhost:5433/";
    
    
    private static Conexion instancia;
    private Connection con;

    public Conexion() {
        this.con = null;
    }

    public Connection EstablecerConexion() {
        System.out.println("=== INTENTANDO CONECTAR A LA BASE DE DATOS ===");
        System.out.println("Driver: " + DRIVER);
        System.out.println("URL Completa: " + (this.URL + this.DB));
        System.out.println("Usuario: " + USER);
        System.out.println("Password: " + PASSWORD);
        System.out.println("===============================================");

        try {
            System.out.println("Cargando driver PostgreSQL...");
            Class.forName(DRIVER);
            System.out.println("Driver cargado exitosamente!");
            
            System.out.println("Intentando establecer conexión...");
            this.con = DriverManager.getConnection(this.URL + this.DB, this.USER, this.PASSWORD);
            
            if (this.con != null) {
                System.out.println("✓ CONEXIÓN EXITOSA A LA BASE DE DATOS!");
            } else {
                System.err.println("✗ CONEXIÓN RETORNÓ NULL");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERROR: No se encontró el driver de PostgreSQL");
            System.err.println("Detalle: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ ERROR SQL al conectar a la base de datos");
            System.err.println("Detalle: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        }
        return this.con;
    }

    public void CerrarConexion() {
        try {
            this.con.close();
//            System.out.println("desconectado :"+(this.con==null));
        } catch (SQLException e) {
            System.out.println("Error DB: " + e);
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public static void main(String[] args) {
        Conexion con = getInstancia();
        if (con.EstablecerConexion() != null) {
            System.out.println("siu");
        }
        con.CerrarConexion();
    }
}
