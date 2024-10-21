package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoTratamiento {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoTratamiento() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo tratamiento
    public void agregar(int patientId, int employeeId, String origin, int roomId) throws SQLException {
        try {
            // Insertar en la tabla services con precio inicial de 0
            String queryService = "INSERT INTO services(patient_id, employee_id, service_type, price) VALUES(?, ?, 'T', 0) RETURNING id;";
            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);

            ResultSet rs = psService.executeQuery();
            if (rs.next()) {
                int serviceId = rs.getInt("id"); // Obtener el ID generado de services

                // Insertar en la tabla treatments
                String queryTreatment = "INSERT INTO treatments(id, origin, room_id) VALUES(?, ?, ?);";
                PreparedStatement psTreatment = conexion.EstablecerConexion().prepareStatement(queryTreatment);
                psTreatment.setInt(1, serviceId);
                psTreatment.setString(2, origin);
                psTreatment.setInt(3, roomId);

                if (psTreatment.executeUpdate() == 0) {
                    throw new SQLException("Error al insertar en Treatments");
                }
            } else {
                throw new SQLException("Error al obtener el ID generado para el tratamiento en Services");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // Método para modificar un tratamiento
    public void modificar(int id, int patientId, int employeeId, float price, String origin, int roomId) throws SQLException {
        try {
            String queryService = "UPDATE services SET patient_id=?, employee_id=?, price=? WHERE id=?;";
            String queryTreatment = "UPDATE treatments SET origin=?, room_id=? WHERE id=?;";

            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);
            psService.setFloat(3, price);
            psService.setInt(4, id);

            PreparedStatement psTreatment = conexion.EstablecerConexion().prepareStatement(queryTreatment);
            psTreatment.setString(1, origin);
            psTreatment.setInt(2, roomId);
            psTreatment.setInt(3, id);

            if (psService.executeUpdate() == 0 || psTreatment.executeUpdate() == 0) {
                throw new SQLException("Error al modificar el tratamiento");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // Método para eliminar un tratamiento
    public void eliminar(int id) throws SQLException {
        try {
            String query = "DELETE FROM services WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Tratamiento no encontrado");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // Método para listar los tratamientos y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> tratamientos = new ArrayList<>();
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, t.origin, t.room_id FROM services s "
                + "INNER JOIN treatments t ON s.id = t.id WHERE s.service_type = 'T';";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        tratamientos.add(new String[]{"ID", "Paciente", "Empleado", "Precio", "Origen", "ID Habitación"});
        while (rs.next()) {
            tratamientos.add(new String[]{
                String.valueOf(rs.getInt("id")),
                String.valueOf(rs.getInt("patient_id")),
                String.valueOf(rs.getInt("employee_id")),
                String.valueOf(rs.getFloat("price")),
                rs.getString("origin"),
                String.valueOf(rs.getInt("room_id"))
            });
        }

        String bodyHtml = generarHTMLTabla(tratamientos);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(tratamientos);
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

    // Método para obtener un tratamiento por su ID
    public String obtenerTratamientoPorId(int id) throws SQLException {
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, t.origin, t.room_id FROM services s "
                + "INNER JOIN treatments t ON s.id = t.id WHERE s.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String tratamiento = String.format("ID: %d, Paciente: %d, Empleado: %d, Precio: %.2f, Origen: %s, ID Habitación: %d",
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("employee_id"),
                    rs.getFloat("price"),
                    rs.getString("origin"),
                    rs.getInt("room_id")
            );
            return tratamiento;
        } else {
            throw new SQLException("Tratamiento no encontrado");
        }
    }

    // Método para desconectar de la base de datos
    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    // Comandos disponibles
    public String getComandos() {
        return "COMANDOS PARA CU: TRATAMIENTO<br>"
                + "tratamiento listar<br>"
                + "tratamiento agregar [id_paciente; id_empleado; origen; id_habitacion]<br>"
                + "tratamiento modificar [id; id_paciente; id_empleado; precio; origen; id_habitacion]<br>"
                + "tratamiento eliminar [id]<br>"
                + "tratamiento obtener [id]";
    }
}
