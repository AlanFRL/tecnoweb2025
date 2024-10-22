package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoProducto {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoProducto() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo producto
    public void agregar(String name, float price, String ingredients) throws SQLException {
        try {
            String query = "INSERT INTO meals(name, price, ingredients) VALUES(?, ?, ?);";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setString(1, name);
            ps.setFloat(2, price);
            ps.setString(3, ingredients);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en Meals");
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar un producto
    public void modificar(int id, String name, float price, String ingredients) throws SQLException {
        try {
            String query = "UPDATE meals SET name=?, price=?, ingredients=? WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setString(1, name);
            ps.setFloat(2, price);
            ps.setString(3, ingredients);
            ps.setInt(4, id);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al modificar en Meals");
                throw new SQLException("Producto no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un producto
    public void eliminar(int id) throws SQLException {
        try {
            String query = "DELETE FROM meals WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al eliminar en Meals");
                throw new SQLException("Producto no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para listar los productos y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT id, name, price, ingredients FROM meals;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        productos.add(new String[]{"ID", "Nombre", "Precio", "Ingredientes"});

        while (set.next()) {
            productos.add(new String[]{
                set.getString("id"),
                set.getString("name"),
                set.getString("price"),
                set.getString("ingredients")
            });
        }

        String bodyHtml = generarHTMLTabla(productos);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(productos);
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
        return "COMANDOS PARA CU: PRODUCTO<br>"
                + "producto listar<br>"
                + "producto agregar [name; price; ingredients]<br>"
                + "producto modificar [id; name; price; ingredients]<br>"
                + "producto eliminar [id]";
    }
}
