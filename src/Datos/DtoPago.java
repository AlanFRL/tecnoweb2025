 package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoPago {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoPago() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo pago sin especificar el ID y sin ingresar el total manualmente
    public void agregar(int serviceId, String fecha, char paymentType) throws SQLException {
        try {
            // Consultar el precio del servicio correspondiente
            String priceQuery = "SELECT price FROM services WHERE id = ?;";
            PreparedStatement pricePs = conexion.EstablecerConexion().prepareStatement(priceQuery);
            pricePs.setInt(1, serviceId);
            ResultSet priceRs = pricePs.executeQuery();

            if (priceRs.next()) {
                float total = priceRs.getFloat("price");

                // Insertar el pago en la tabla payments
                String query = "INSERT INTO payments(service_id, date, total, payment_type) VALUES(?, ?, ?, ?);";
                PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
                ps.setInt(1, serviceId);
                java.sql.Date sqlDate = java.sql.Date.valueOf(fecha);
                ps.setDate(2, sqlDate);
                ps.setFloat(3, total);
                ps.setString(4, String.valueOf(paymentType).toUpperCase());  // Convertir el char a mayúscula y luego a String

                if (ps.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Payments");
                    throw new SQLException();
                }
            } else {
                System.err.println("No se encontró el servicio con el ID especificado");
                throw new SQLException("Service not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

// Método para modificar un pago existente
    public void modificar(int id, int serviceId, String fecha, char paymentType) throws SQLException {
        try {
            // Consultar el precio del servicio correspondiente
            String priceQuery = "SELECT price FROM services WHERE id = ?;";
            PreparedStatement pricePs = conexion.EstablecerConexion().prepareStatement(priceQuery);
            pricePs.setInt(1, serviceId);
            ResultSet priceRs = pricePs.executeQuery();

            if (priceRs.next()) {
                float total = priceRs.getFloat("price");

                // Actualizar el pago en la tabla payments
                String query = "UPDATE payments SET service_id=?, date=?, total=?, payment_type=? WHERE id=?;";
                PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
                ps.setInt(1, serviceId);
                java.sql.Date sqlDate = java.sql.Date.valueOf(fecha);
                ps.setDate(2, sqlDate);
                ps.setFloat(3, total);
                ps.setString(4, String.valueOf(paymentType).toUpperCase()); // Asegurarse de que el tipo de pago sea mayúscula
                ps.setInt(5, id); // Este es el ID del pago a modificar

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    System.err.println("Ocurrió un error al modificar en Payments");
                    throw new SQLException("Pago no encontrado");
                }
            } else {
                System.err.println("No se encontró el servicio con el ID especificado");
                throw new SQLException("Service not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un pago
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM payments WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar el Pago");
            throw new SQLException("Pago no encontrado");
        }
    }

    // Método para listar los pagos y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT id, service_id, payment_type, date, total FROM payments;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        pagos.add(new String[]{"ID", "Service ID", "Payment Type", "Fecha", "Total"});

        while (set.next()) {
            pagos.add(new String[]{
                set.getString("id"),
                set.getString("service_id"),
                set.getString("payment_type"),
                set.getString("date"),
                set.getString("total")
            });
        }

        String bodyHtml = generarHTMLTabla(pagos);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(pagos);
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
        return "COMANDOS PARA CU: PAGO<br>"
                + "pago listar<br>"
                + "pago agregar [service_id; fecha; payment_type]<br>"
                + "pago modificar [id; service_id; fecha; payment_type]<br>"
                + "pago eliminar [id]";
    }
}