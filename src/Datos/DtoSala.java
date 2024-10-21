package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoSala {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoSala() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar una nueva sala sin requerir id
    public void agregar(int capacity, int availableRooms) throws SQLException {
        try {
            String query = "INSERT INTO rooms(capacity, available_rooms) VALUES(?, ?);";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, capacity);
            ps.setInt(2, availableRooms);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en Rooms");
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar una sala existente
    public void modificar(int id, int capacity, int availableRooms) throws SQLException {
        String query = "UPDATE rooms SET capacity=?, available_rooms=? WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, capacity);
        ps.setInt(2, availableRooms);
        ps.setInt(3, id);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            System.err.println("Ocurrió un error al modificar en Rooms");
            throw new SQLException("Sala no encontrada");
        }
    }

    // Método para eliminar una sala
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM rooms WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar la Sala");
            throw new SQLException("Sala no encontrada");
        }
    }

    // Método para listar las salas y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> salas = new ArrayList<>();
        String query = "SELECT id, capacity, available_rooms FROM rooms;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        salas.add(new String[]{"ID", "Capacidad", "Habitaciones Disponibles"});

        while (set.next()) {
            salas.add(new String[]{
                set.getString("id"),
                set.getString("capacity"),
                set.getString("available_rooms")
            });
        }

        String bodyHtml = generarHTMLTabla(salas);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(salas);
    }

    private String generarHTMLTabla(List<String[]> datos) {
        StringBuilder html = new StringBuilder();
        html.append("<table border='1'>");

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
    // Comandos disponibles
    public String getComandos() {
        return "COMANDOS PARA CU: SALA<br>"
                + "sala listar<br>"
                + "sala agregar [capacidad; habitaciones disponibles]<br>"
                + "sala modificar [id; capacidad; habitaciones disponibles]<br>"
                + "sala eliminar [id]";
    }

}
