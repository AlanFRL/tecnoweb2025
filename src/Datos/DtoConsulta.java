package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DtoConsulta {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoConsulta() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar una nueva consulta
    public void agregar(int patientId, int employeeId, String date, String reason) throws SQLException {
        try {
            // Insertar en la tabla services con precio fijo de 50
            String queryService = "INSERT INTO services(patient_id, employee_id, service_type, price) VALUES(?, ?, 'C', 50) RETURNING id;";
            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);

            ResultSet rs = psService.executeQuery();
            if (rs.next()) {
                int serviceId = rs.getInt("id"); // Obtener el ID generado de services

                // Convertir la fecha de String a java.sql.Date
                java.sql.Date sqlDate = convertirFecha(date);

                // Insertar en la tabla consults
                String queryConsult = "INSERT INTO consults(id, date, reason) VALUES(?, ?, ?);";
                PreparedStatement psConsult = conexion.EstablecerConexion().prepareStatement(queryConsult);
                psConsult.setInt(1, serviceId);
                psConsult.setDate(2, sqlDate);
                psConsult.setString(3, reason);

                if (psConsult.executeUpdate() == 0) {
                    throw new SQLException("Error al insertar en Consults");
                }
            } else {
                throw new SQLException("Error al obtener el ID generado para la consulta en Services");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // Método para convertir String a java.sql.Date con formato dd/MM/yyyy
    private java.sql.Date convertirFecha(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaUtil = formatoFecha.parse(fechaStr);
            return new java.sql.Date(fechaUtil.getTime()); // Convertir a java.sql.Date
        } catch (ParseException e) {
            System.err.println("Error de formato en la fecha: " + fechaStr);
            throw new SQLException("Formato de fecha inválido. Use 'dd/MM/yyyy'.");
        }
    }

    // Método para modificar una consulta
    public void modificar(int id, int patientId, int employeeId, float price, String date, String reason) throws SQLException {
        try {
            String queryService = "UPDATE services SET patient_id=?, employee_id=?, price=? WHERE id=?;";
            String queryConsult = "UPDATE consults SET date=?, reason=? WHERE id=?;";
            
            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);
            psService.setFloat(3, price);
            psService.setInt(4, id);

            // Convertir la fecha de String a java.sql.Date
            java.sql.Date sqlDate = convertirFecha(date);

            PreparedStatement psConsult = conexion.EstablecerConexion().prepareStatement(queryConsult);
            psConsult.setDate(1, sqlDate);
            psConsult.setString(2, reason);
            psConsult.setInt(3, id);

            if (psService.executeUpdate() == 0 || psConsult.executeUpdate() == 0) {
                throw new SQLException("Error al modificar la consulta");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // Método para eliminar una consulta
    public void eliminar(int id) throws SQLException {
        try {
            String query = "DELETE FROM services WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Consulta no encontrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // Método para listar las consultas y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> consultas = new ArrayList<>();
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, c.date, c.reason FROM services s "
                     + "INNER JOIN consults c ON s.id = c.id WHERE s.service_type = 'C';";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        consultas.add(new String[]{"ID", "Paciente", "Empleado", "Precio", "Fecha", "Motivo"});
        while (rs.next()) {
            consultas.add(new String[]{
                String.valueOf(rs.getInt("id")),
                String.valueOf(rs.getInt("patient_id")),
                String.valueOf(rs.getInt("employee_id")),
                String.valueOf(rs.getFloat("price")),
                rs.getString("date"),
                rs.getString("reason")
            });
        }

        String bodyHtml = generarHTMLTabla(consultas);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(consultas);
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

    // Método para obtener una consulta por su ID
    public String obtenerConsultaPorId(int id) throws SQLException {
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, c.date, c.reason FROM services s "
                     + "INNER JOIN consults c ON s.id = c.id WHERE s.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String consulta = String.format("ID: %d, Paciente: %d, Empleado: %d, Precio: %.2f, Fecha: %s, Motivo: %s",
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("employee_id"),
                    rs.getFloat("price"),
                    rs.getString("date"),
                    rs.getString("reason")
            );
            return consulta;
        } else {
            throw new SQLException("Consulta no encontrada");
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
        return "COMANDOS PARA CU: CONSULTA<br>"
                + "consulta listar<br>"
                + "consulta agregar [id_paciente; id_empleado; fecha(dd/MM/YYYY); motivo]<br>"
                + "consulta modificar [id; id_paciente; id_empleado; precio; fecha(dd/MM/YYYY); motivo]<br>"
                + "consulta eliminar [id]<br>";
    }
}
