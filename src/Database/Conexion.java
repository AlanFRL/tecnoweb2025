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

    private final String DRIVER = "org.postgresql.Driver";
    private final String DB = "db_grupo04sa";
    private final String USER = "grupo04sa";
    private final String PASSWORD  = "grup004grup004*";
    private final String URL = "jdbc:postgresql://mail.tecnoweb.org.bo/";
        
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

        try {
            Class.forName(DRIVER);
            this.con = DriverManager.getConnection(this.URL + this.DB, this.USER, this.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error DB: " + e);
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
