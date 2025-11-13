package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoPropietario {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoPropietario() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo propietario
    public void agregar(String nombre, String telefono, String email, String password, String razon_social) throws SQLException {
        try {
            // Primero, insertamos el usuario en la tabla usuario
            String queryUsuario = "INSERT INTO usuario(nombre, telefono, email, password, tipo_usuario, estado) VALUES(?, ?, ?, ?, 'propietario', TRUE) RETURNING id;";
            PreparedStatement psUsuario = conexion.EstablecerConexion().prepareStatement(queryUsuario);
            psUsuario.setString(1, nombre);
            psUsuario.setString(2, telefono);
            psUsuario.setString(3, email);
            psUsuario.setString(4, password);

            ResultSet rs = psUsuario.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");

                // Ahora, insertamos en la tabla propietario
                String queryPropietario = "INSERT INTO propietario(id, razon_social) VALUES(?, ?);";
                PreparedStatement psPropietario = conexion.EstablecerConexion().prepareStatement(queryPropietario);
                psPropietario.setInt(1, userId);
                psPropietario.setString(2, razon_social);

                if (psPropietario.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Propietario");
                    throw new SQLException();
                }
            } else {
                System.err.println("Ocurrió un error al insertar en Usuario");
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar un propietario
    public void modificar(int id, String nombre, String telefono, String email, String password, String razon_social) throws SQLException {
        try {
            // Actualizamos la tabla usuario
            String queryUsuario = "UPDATE usuario SET nombre=?, telefono=?, email=?, password=? WHERE id=?;";
            PreparedStatement psUsuario = conexion.EstablecerConexion().prepareStatement(queryUsuario);
            psUsuario.setString(1, nombre);
            psUsuario.setString(2, telefono);
            psUsuario.setString(3, email);
            psUsuario.setString(4, password);
            psUsuario.setInt(5, id);

            if (psUsuario.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al modificar en Usuario");
                throw new SQLException("Propietario no encontrado");
            }

            // Actualizamos la tabla propietario
            String queryPropietario = "UPDATE propietario SET razon_social=? WHERE id=?;";
            PreparedStatement psPropietario = conexion.EstablecerConexion().prepareStatement(queryPropietario);
            psPropietario.setString(1, razon_social);
            psPropietario.setInt(2, id);

            if (psPropietario.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al modificar en Propietario");
                throw new SQLException("Propietario no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un propietario
    public void eliminar(int id) throws SQLException {
        try {
            // Eliminamos de la tabla usuario (CASCADE eliminará de propietario automáticamente)
            String query = "DELETE FROM usuario WHERE id=? AND tipo_usuario='propietario';";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);
            
            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al eliminar el Propietario");
                throw new SQLException("Propietario no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para listar los propietarios y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> propietarios = new ArrayList<>();
        String query = "SELECT u.id, u.nombre, u.telefono, u.email, u.estado, p.razon_social " +
                       "FROM usuario u INNER JOIN propietario p ON u.id = p.id " +
                       "WHERE u.tipo_usuario='propietario';";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        
        propietarios.add(new String[]{"ID", "Nombre", "Teléfono", "Email", "Estado", "Razón Social"});

        while (set.next()) {
            propietarios.add(new String[]{
                set.getString("id"),
                set.getString("nombre"),
                set.getString("telefono"),
                set.getString("email"),
                set.getBoolean("estado") ? "Activo" : "Inactivo",
                set.getString("razon_social")
            });
        }

        String bodyHtml = generarHTMLTabla(propietarios);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(propietarios);
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

    // Método para desconectar de la base de datos
    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    // Comandos disponibles
    public String getComandos() {
        return "COMANDOS PARA CU: PROPIETARIO<br>"
                + "propietario help<br>"
                + "propietario listar<br>"
                + "propietario agregar [nombre; telefono; email; password; razon_social]<br>"
                + "propietario modificar [id_propietario; nombre; telefono; email; password; razon_social]<br>"
                + "propietario eliminar [id_propietario]<br><br>"
                + "EJEMPLOS:<br>"
                + "propietario listar<br>"
                + "propietario agregar [Juan Pérez; 70123456; juan@lavanderia.com; pass123; Lavandería Belén S.R.L.]<br>"
                + "propietario modificar [1; Juan Pérez; 70123456; juan@lavanderia.com; newpass; Lavandería Belén S.R.L.]<br>"
                + "propietario eliminar [1]";
    }
}
