/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class DtoUsuario {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoUsuario() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    public void agregar(int cedula, String nombre, String correo, String password, int celular, String direccion, boolean rol) throws SQLException {
        String query = "INSERT INTO usuario(cedula, nombre, correo, password, celular, direccion, rol) values(?, ?, ?, ?,?,?,?);";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, cedula);
        ps.setString(2, nombre);
        ps.setString(3, correo);
        ps.setString(4, password);
        ps.setInt(5, celular);
        ps.setString(6, direccion);
        ps.setBoolean(7, rol);

        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrio un error al insertar en Usuario");
            throw new SQLException();
        }
    }

    public void modificar(int id, int cedula, String nombre, String correo, String password, int celular, String direccion, boolean rol) throws SQLException, ParseException {
        String query = "UPDATE usuario SET cedula=?, nombre=?, correo=?, password=?, celular=?, direccion=?, rol=? WHERE id=" + id + " ;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, cedula);
        ps.setString(2, nombre);
        ps.setString(3, correo);
        ps.setString(4, password);  // Encriptar el password
        ps.setInt(5, celular);
        ps.setString(6, direccion);
        ps.setBoolean(7, rol);

        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrio un error al modificar en Usuario");
            throw new SQLException("Usuario no encontrado");
        }
    }

    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM usuario WHERE id=" + id + "";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrio un error al eliminar el Usuario");
            throw new SQLException("Usuario no encontrado");
        }
    }

    public String listar(String emailFrom) throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuario;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        usuarios.add(new String[]{"ID","cedula", "nombre", "correo", "password", "celular", "direccion", "rol"});

        while (set.next()) {
            usuarios.add(new String[]{
                set.getString("id"),
                set.getString("cedula"),
                set.getString("nombre"),
                set.getString("correo"),
                set.getString("password"),
                set.getString("celular"),
                set.getString("direccion"),
                set.getString("rol")
            });
        }
        System.out.println(emailFrom);
        String bodyhtml=generarHTMLTabla(usuarios);
        System.out.println(bodyhtml);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyhtml);
        return imprimirTabla.mostrarTabla(usuarios);
    }
    private String generarHTMLTabla(List<String[]> datos) {
    StringBuilder html = new StringBuilder();
    html.append("<table border='1'>");

    // Añadimos las filas a la tabla
    for (String[] fila : datos) {
        html.append("<tr>");
        for (String celda : fila) {
            html.append("<td>").append(celda).append("</td>");
        }
        html.append("</tr>");
    }

    html.append("</table>");
    return html.toString();
}
    public String obtenerEmailPorId(int idUsuario) throws SQLException {
        String query = "SELECT correo FROM usuario WHERE id=?";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("correo");
        } else {
            throw new SQLException("Usuario no encontrado");
        }
    }

    public String obtenerUsuarioPorId(int id) throws SQLException {
        String query = "SELECT * FROM usuario WHERE id=?";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String usuario = String.format("ID: %d, Cedula: %d, Nombre: %s, Correo: %s, Password: %s, Celular: %d, Direccion: %s, Rol: %b",
                rs.getInt("id"),
                rs.getInt("cedula"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("password"),
                rs.getInt("celular"),
                rs.getString("direccion"),
                rs.getBoolean("rol")
            );
            return usuario;
        } else {
            throw new SQLException("Usuario no encontrado");
        }
    }

    public boolean verificarPassword(String passwordProporcionado, String passwordAlmacenado) {
        return BCrypt.checkpw(passwordProporcionado, passwordAlmacenado);
    }

    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    public String getComandos() {
        return "COMANDOS PARA CU: USUARIO<br> usuario listar<br> usuario agregar [cedula; nombre; correo; password; celular; direccion; rol;]<br> usuario modificar [id ; cedula; nombre; correo; password; celular; direccion; rol;]<br> usuario eliminar [id]";
    }
}
