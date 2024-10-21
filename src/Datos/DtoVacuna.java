package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoVacuna {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoVacuna() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar una nueva vacuna
    public void agregar(int patientId, int employeeId, String name) throws SQLException {
        try {
            // Insertar en la tabla services con precio fijo de 10
            String queryService = "INSERT INTO services(patient_id, employee_id, service_type, price) VALUES(?, ?, 'V', 10) RETURNING id;";
            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);

            ResultSet rs = psService.executeQuery();
            if (rs.next()) {
                int serviceId = rs.getInt("id"); // Obtener el ID generado de services

                // Insertar en la tabla vaccines
                String queryVaccine = "INSERT INTO vaccines(id, name) VALUES(?, ?);";
                PreparedStatement psVaccine = conexion.EstablecerConexion().prepareStatement(queryVaccine);
                psVaccine.setInt(1, serviceId);
                psVaccine.setString(2, name);

                if (psVaccine.executeUpdate() == 0) {
                    throw new SQLException("Error al insertar en Vaccines");
                }
            } else {
                throw new SQLException("Error al obtener el ID generado para la vacuna en Services");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // Método para modificar una vacuna
    public void modificar(int id, int patientId, int employeeId, float price, String name) throws SQLException {
        try {
            String queryService = "UPDATE services SET patient_id=?, employee_id=?, price=? WHERE id=?;";
            String queryVaccine = "UPDATE vaccines SET name=? WHERE id=?;";

            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);
            psService.setFloat(3, price);
            psService.setInt(4, id);

            PreparedStatement psVaccine = conexion.EstablecerConexion().prepareStatement(queryVaccine);
            psVaccine.setString(1, name);
            psVaccine.setInt(2, id);

            if (psService.executeUpdate() == 0 || psVaccine.executeUpdate() == 0) {
                throw new SQLException("Error al modificar la vacuna");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // Método para eliminar una vacuna
    public void eliminar(int id) throws SQLException {
        try {
            String query = "DELETE FROM services WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Vacuna no encontrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // Método para listar las vacunas y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> vacunas = new ArrayList<>();
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, v.name FROM services s "
                + "INNER JOIN vaccines v ON s.id = v.id WHERE s.service_type = 'V';";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        vacunas.add(new String[]{"ID", "Paciente", "Empleado", "Precio", "Nombre"});
        while (rs.next()) {
            vacunas.add(new String[]{
                String.valueOf(rs.getInt("id")),
                String.valueOf(rs.getInt("patient_id")),
                String.valueOf(rs.getInt("employee_id")),
                String.valueOf(rs.getFloat("price")),
                rs.getString("name")
            });
        }

        String bodyHtml = generarHTMLTabla(vacunas);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(vacunas);
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

    // Método para obtener una vacuna por su ID
    public String obtenerVacunaPorId(int id) throws SQLException {
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, v.name FROM services s "
                + "INNER JOIN vaccines v ON s.id = v.id WHERE s.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String vacuna = String.format("ID: %d, Paciente: %d, Empleado: %d, Precio: %.2f, Nombre: %s",
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("employee_id"),
                    rs.getFloat("price"),
                    rs.getString("name")
            );
            return vacuna;
        } else {
            throw new SQLException("Vacuna no encontrada");
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
        return "COMANDOS PARA CU: VACUNA<br>"
                + "vacuna listar<br>"
                + "vacuna agregar [id_paciente; id_empleado; nombre]<br>"
                + "vacuna modificar [id; id_paciente; id_empleado; precio; nombre]<br>"
                + "vacuna eliminar [id]<br>"
                + "vacuna obtener [id]";
    }
}
