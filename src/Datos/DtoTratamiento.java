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
            // Verificar si hay habitaciones disponibles
            if (!verificarHabitacionDisponible(roomId)) {
                throw new SQLException("No hay habitaciones disponibles en la sala seleccionada.");
            }

            // Insertar en la tabla services con precio inicial de 0
            String queryService = "INSERT INTO services(patient_id, employee_id, service_type, price) VALUES(?, ?, 'T', 0) RETURNING id;";
            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);

            ResultSet rs = psService.executeQuery();
            if (rs.next()) {
                int serviceId = rs.getInt("id"); // Obtener el ID generado de services

                // Insertar en la tabla treatments con status 'activo'
                String queryTreatment = "INSERT INTO treatments(id, origin, room_id, status) VALUES(?, ?, ?, 'activo');";
                PreparedStatement psTreatment = conexion.EstablecerConexion().prepareStatement(queryTreatment);
                psTreatment.setInt(1, serviceId);
                psTreatment.setString(2, origin);
                psTreatment.setInt(3, roomId);

                if (psTreatment.executeUpdate() == 0) {
                    throw new SQLException("Error al insertar en Treatments");
                }

                // Reducir la cantidad de habitaciones disponibles en la tabla rooms
                actualizarHabitaciones(roomId, -1);
            } else {
                throw new SQLException("Error al obtener el ID generado para el tratamiento en Services");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // Método para modificar un tratamiento
    public void modificar(int id, int patientId, int employeeId, float price, String origin, int newRoomId) throws SQLException {
        try {
            // Obtener el ID de la habitación actual del tratamiento
            int currentRoomId = obtenerRoomIdActual(id);

            // Si la nueva habitación es diferente de la actual, verificar si hay disponibilidad en la nueva habitación
            if (newRoomId != currentRoomId) {
                if (!verificarHabitacionDisponible(newRoomId)) {
                    throw new SQLException("No hay habitaciones disponibles en la sala seleccionada.");
                }
            }

            String queryService = "UPDATE services SET patient_id=?, employee_id=?, price=? WHERE id=?;";
            String queryTreatment = "UPDATE treatments SET origin=?, room_id=? WHERE id=?;";

            PreparedStatement psService = conexion.EstablecerConexion().prepareStatement(queryService);
            psService.setInt(1, patientId);
            psService.setInt(2, employeeId);
            psService.setFloat(3, price);
            psService.setInt(4, id);

            PreparedStatement psTreatment = conexion.EstablecerConexion().prepareStatement(queryTreatment);
            psTreatment.setString(1, origin);
            psTreatment.setInt(2, newRoomId);
            psTreatment.setInt(3, id);

            if (psService.executeUpdate() == 0 || psTreatment.executeUpdate() == 0) {
                throw new SQLException("Error al modificar el tratamiento");
            }

            // Si la habitación ha cambiado, actualizar la cantidad de habitaciones disponibles
            if (newRoomId != currentRoomId) {
                // Reducir la cantidad de habitaciones disponibles en la nueva habitación
                actualizarHabitaciones(newRoomId, -1);

                // Aumentar la cantidad de habitaciones disponibles en la habitación anterior
                actualizarHabitaciones(currentRoomId, 1);
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

// Método para obtener el ID de la habitación actual de un tratamiento
    private int obtenerRoomIdActual(int treatmentId) throws SQLException {
        String query = "SELECT room_id FROM treatments WHERE id = ?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, treatmentId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("room_id");
        } else {
            throw new SQLException("Tratamiento no encontrado");
        }
    }

    // Método para eliminar un tratamiento (establecer status 'alta' y devolver habitación)
    public void alta(int id) throws SQLException {
        try {
            // Obtener el room_id asociado al tratamiento antes de establecer el status en 'alta'
            String queryGetRoom = "SELECT room_id FROM treatments WHERE id=?";
            PreparedStatement psGetRoom = conexion.EstablecerConexion().prepareStatement(queryGetRoom);
            psGetRoom.setInt(1, id);
            ResultSet rsRoom = psGetRoom.executeQuery();

            int roomId = -1;
            if (rsRoom.next()) {
                roomId = rsRoom.getInt("room_id");
            } else {
                throw new SQLException("Tratamiento no encontrado");
            }

            // Establecer el status en 'alta'
            String queryAlta = "UPDATE treatments SET status='alta' WHERE id=?;";
            PreparedStatement psAlta = conexion.EstablecerConexion().prepareStatement(queryAlta);
            psAlta.setInt(1, id);

            if (psAlta.executeUpdate() == 0) {
                throw new SQLException("Error al marcar el tratamiento como 'alta'");
            }

            // Devolver la habitación incrementando el available_rooms
            actualizarHabitaciones(roomId, 1);
        } catch (SQLException e) {
            System.err.println("Error al marcar el tratamiento como 'alta': " + e.getMessage());
            throw e;
        }
    }

    // Método para verificar si hay habitaciones disponibles en una sala
    private boolean verificarHabitacionDisponible(int roomId) throws SQLException {
        String query = "SELECT available_rooms FROM rooms WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("available_rooms") > 0;
        }
        return false;
    }

    // Método para actualizar la cantidad de habitaciones disponibles
    private void actualizarHabitaciones(int roomId, int cambio) throws SQLException {
        String query = "UPDATE rooms SET available_rooms = available_rooms + ? WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, cambio);
        ps.setInt(2, roomId);

        if (ps.executeUpdate() == 0) {
            throw new SQLException("Error al actualizar las habitaciones disponibles");
        }
    }

    // Método para eliminar un tratamiento
    public void eliminar(int id) throws SQLException {
        try {
            // Verificar el estado del tratamiento y obtener el room_id
            String queryCheckStatus = "SELECT room_id, status FROM treatments WHERE id=?;";
            PreparedStatement psCheckStatus = conexion.EstablecerConexion().prepareStatement(queryCheckStatus);
            psCheckStatus.setInt(1, id);
            ResultSet rsCheck = psCheckStatus.executeQuery();

            int roomId = -1;
            String status = "";

            if (rsCheck.next()) {
                roomId = rsCheck.getInt("room_id");
                status = rsCheck.getString("status");
            } else {
                throw new SQLException("Tratamiento no encontrado");
            }

            // Si el estado es 'activo', devolver la habitación
            if (status.equalsIgnoreCase("activo")) {
                actualizarHabitaciones(roomId, 1); // Incrementar la cantidad de habitaciones disponibles
            }

            // Eliminar el tratamiento de la tabla services
            String queryDelete = "DELETE FROM services WHERE id=?;";
            PreparedStatement psDelete = conexion.EstablecerConexion().prepareStatement(queryDelete);
            psDelete.setInt(1, id);

            if (psDelete.executeUpdate() == 0) {
                throw new SQLException("Error al eliminar el tratamiento");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // Método para listar los tratamientos y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> tratamientos = new ArrayList<>();
        String query = "SELECT s.id, s.patient_id, s.employee_id, s.price, t.origin, t.room_id, t.status FROM services s "
                + "INNER JOIN treatments t ON s.id = t.id WHERE s.service_type = 'T';";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        tratamientos.add(new String[]{"ID", "Paciente", "Empleado", "Precio", "Origen", "ID Habitación", "Status"});
        while (rs.next()) {
            tratamientos.add(new String[]{
                String.valueOf(rs.getInt("id")),
                String.valueOf(rs.getInt("patient_id")),
                String.valueOf(rs.getInt("employee_id")),
                String.valueOf(rs.getFloat("price")),
                rs.getString("origin"),
                String.valueOf(rs.getInt("room_id")),
                rs.getString("status")
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
                + "tratamiento alta [id]<br>";
    }
}
