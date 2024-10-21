package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoFicha {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoFicha() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar una nueva ficha sin requerir id
    public void agregar(int doctorId, int consultId, String symptoms, String diagnosis, String notes) throws SQLException {
        try {
            String query = "INSERT INTO caresheets(doctor_id, consult_id, symptoms, diagnosis, notes) VALUES(?, ?, ?, ?, ?);";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, doctorId);
            ps.setInt(2, consultId);
            ps.setString(3, symptoms);
            ps.setString(4, diagnosis);
            ps.setString(5, notes);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en Caresheets");
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar una ficha existente
    public void modificar(int id, int doctorId, int consultId, String symptoms, String diagnosis, String notes) throws SQLException {
        String query = "UPDATE caresheets SET doctor_id=?, consult_id=?, symptoms=?, diagnosis=?, notes=? WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, doctorId);
        ps.setInt(2, consultId);
        ps.setString(3, symptoms);
        ps.setString(4, diagnosis);
        ps.setString(5, notes);
        ps.setInt(6, id);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            System.err.println("Ocurrió un error al modificar en Caresheets");
            throw new SQLException("Ficha no encontrada");
        }
    }

    // Método para eliminar una ficha
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM caresheets WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar la Ficha");
            throw new SQLException("Ficha no encontrada");
        }
    }

    // Método para listar las fichas y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> fichas = new ArrayList<>();
        String query = "SELECT id, doctor_id, consult_id, symptoms, diagnosis, notes FROM caresheets;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        fichas.add(new String[]{"ID", "Doctor ID", "Consult ID", "Symptoms", "Diagnosis", "Notes"});

        while (set.next()) {
            fichas.add(new String[]{
                set.getString("id"),
                set.getString("doctor_id"),
                set.getString("consult_id"),
                set.getString("symptoms"),
                set.getString("diagnosis"),
                set.getString("notes")
            });
        }

        String bodyHtml = generarHTMLTabla(fichas);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(fichas);
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
    public String getComandos() {
        return "COMANDOS PARA CU: FICHA<br>"
                + "ficha listar<br>"
                + "ficha agregar [doctor_id; consult_id; symptoms; diagnosis; notes]<br>"
                + "ficha modificar [id; doctor_id; consult_id; symptoms; diagnosis; notes]<br>"
                + "ficha eliminar [id]";
    }
}