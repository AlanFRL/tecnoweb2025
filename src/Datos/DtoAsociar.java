package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoAsociar {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoAsociar() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar una nueva asociación entre meal y treatment
    public void agregar(int mealId, int treatmentId, int quantity) throws SQLException {
        try {
            // Obtener el precio de la comida
            String queryMeal = "SELECT price FROM meals WHERE id = ?;";
            PreparedStatement psMeal = conexion.EstablecerConexion().prepareStatement(queryMeal);
            psMeal.setInt(1, mealId);
            ResultSet rsMeal = psMeal.executeQuery();

            if (rsMeal.next()) {
                float mealPrice = rsMeal.getFloat("price");

                // Calcular el costo total de la comida en el tratamiento
                float totalMealCost = mealPrice * quantity;

                // Insertar la nueva relación en meal_treatment
                String queryInsert = "INSERT INTO meal_treatment(meal_id, treatment_id, quantity) VALUES(?, ?, ?);";
                PreparedStatement psInsert = conexion.EstablecerConexion().prepareStatement(queryInsert);
                psInsert.setInt(1, mealId);
                psInsert.setInt(2, treatmentId);
                psInsert.setInt(3, quantity);

                if (psInsert.executeUpdate() == 0) {
                    throw new SQLException("Error al asociar la comida con el tratamiento");
                }

                // Actualizar el precio total del tratamiento en la tabla services
                String queryUpdateService = "UPDATE services SET price = price + ? WHERE id = ?;";
                PreparedStatement psUpdateService = conexion.EstablecerConexion().prepareStatement(queryUpdateService);
                psUpdateService.setFloat(1, totalMealCost);
                psUpdateService.setInt(2, treatmentId);

                if (psUpdateService.executeUpdate() == 0) {
                    throw new SQLException("Error al actualizar el precio del tratamiento en services");
                }
            } else {
                throw new SQLException("Comida no encontrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // Método para modificar una asociación existente entre meal y treatment
    public void modificar(int mealId, int treatmentId, int newQuantity) throws SQLException {
        try {
            // Obtener la cantidad actual de la asociación
            String querySelect = "SELECT quantity FROM meal_treatment WHERE meal_id = ? AND treatment_id = ?;";
            PreparedStatement psSelect = conexion.EstablecerConexion().prepareStatement(querySelect);
            psSelect.setInt(1, mealId);
            psSelect.setInt(2, treatmentId);
            ResultSet rsSelect = psSelect.executeQuery();

            if (rsSelect.next()) {
                int oldQuantity = rsSelect.getInt("quantity");

                // Obtener el precio de la comida
                String queryMeal = "SELECT price FROM meals WHERE id = ?;";
                PreparedStatement psMeal = conexion.EstablecerConexion().prepareStatement(queryMeal);
                psMeal.setInt(1, mealId);
                ResultSet rsMeal = psMeal.executeQuery();

                if (rsMeal.next()) {
                    float mealPrice = rsMeal.getFloat("price");

                    // Calcular la diferencia en el costo total
                    float costDifference = (newQuantity - oldQuantity) * mealPrice;

                    // Actualizar la cantidad en meal_treatment
                    String queryUpdate = "UPDATE meal_treatment SET quantity = ? WHERE meal_id = ? AND treatment_id = ?;";
                    PreparedStatement psUpdate = conexion.EstablecerConexion().prepareStatement(queryUpdate);
                    psUpdate.setInt(1, newQuantity);
                    psUpdate.setInt(2, mealId);
                    psUpdate.setInt(3, treatmentId);

                    if (psUpdate.executeUpdate() == 0) {
                        throw new SQLException("Error al modificar la asociación en meal_treatment");
                    }

                    // Actualizar el precio total del tratamiento en la tabla services
                    String queryUpdateService = "UPDATE services SET price = price + ? WHERE id = ?;";
                    PreparedStatement psUpdateService = conexion.EstablecerConexion().prepareStatement(queryUpdateService);
                    psUpdateService.setFloat(1, costDifference);
                    psUpdateService.setInt(2, treatmentId);

                    if (psUpdateService.executeUpdate() == 0) {
                        throw new SQLException("Error al actualizar el precio del tratamiento en services");
                    }
                } else {
                    throw new SQLException("Comida no encontrada");
                }
            } else {
                throw new SQLException("Asociación no encontrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // Método para eliminar una asociación entre meal y treatment
    public void eliminar(int mealId, int treatmentId) throws SQLException {
        try {
            // Obtener la cantidad y el precio de la comida en la asociación
            String querySelect = "SELECT quantity, price FROM meal_treatment mt "
                               + "JOIN meals m ON mt.meal_id = m.id WHERE mt.meal_id = ? AND mt.treatment_id = ?;";
            PreparedStatement psSelect = conexion.EstablecerConexion().prepareStatement(querySelect);
            psSelect.setInt(1, mealId);
            psSelect.setInt(2, treatmentId);
            ResultSet rsSelect = psSelect.executeQuery();

            if (rsSelect.next()) {
                int quantity = rsSelect.getInt("quantity");
                float mealPrice = rsSelect.getFloat("price");

                // Calcular el costo total de la comida a eliminar
                float totalMealCost = quantity * mealPrice;

                // Eliminar la asociación de meal_treatment
                String queryDelete = "DELETE FROM meal_treatment WHERE meal_id = ? AND treatment_id = ?;";
                PreparedStatement psDelete = conexion.EstablecerConexion().prepareStatement(queryDelete);
                psDelete.setInt(1, mealId);
                psDelete.setInt(2, treatmentId);

                if (psDelete.executeUpdate() == 0) {
                    throw new SQLException("Error al eliminar la asociación en meal_treatment");
                }

                // Actualizar el precio total del tratamiento en la tabla services
                String queryUpdateService = "UPDATE services SET price = price - ? WHERE id = ?;";
                PreparedStatement psUpdateService = conexion.EstablecerConexion().prepareStatement(queryUpdateService);
                psUpdateService.setFloat(1, totalMealCost);
                psUpdateService.setInt(2, treatmentId);

                if (psUpdateService.executeUpdate() == 0) {
                    throw new SQLException("Error al actualizar el precio del tratamiento en services");
                }
            } else {
                throw new SQLException("Asociación no encontrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // Método para listar todas las asociaciones entre meal y treatment
    public String listar(String emailFrom) throws SQLException {
        List<String[]> asociaciones = new ArrayList<>();
        String query = "SELECT mt.meal_id, m.name AS meal_name, mt.treatment_id, s.patient_id, mt.quantity, m.price "
                     + "FROM meal_treatment mt "
                     + "JOIN meals m ON mt.meal_id = m.id "
                     + "JOIN treatments t ON mt.treatment_id = t.id "
                     + "JOIN services s ON t.id = s.id;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        asociaciones.add(new String[]{"ID Comida", "Nombre Comida", "ID Tratamiento", "ID Paciente", "Cantidad", "Precio Unitario"});
        while (rs.next()) {
            asociaciones.add(new String[]{
                String.valueOf(rs.getInt("meal_id")),
                rs.getString("meal_name"),
                String.valueOf(rs.getInt("treatment_id")),
                String.valueOf(rs.getInt("patient_id")),
                String.valueOf(rs.getInt("quantity")),
                String.valueOf(rs.getFloat("price"))
            });
        }

        String bodyHtml = generarHTMLTabla(asociaciones);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(asociaciones);
    }

    // Método para generar una tabla HTML para enviar por email
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
        return "COMANDOS PARA CU: ASOCIAR<br>"
                + "asociar listar<br>"
                + "asociar agregar [id_papilla; id_tratamiento; cantidad]<br>"
                + "asociar modificar [id_papilla; id_tratamiento; nueva_cantidad]<br>"
                + "asociar eliminar [id_papilla; id_tratamiento]";
    }

}
