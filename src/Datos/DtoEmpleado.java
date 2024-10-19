package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class DtoEmpleado {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoEmpleado() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo empleado
    public void agregar(String ci, String nombre, String password, int phone_number, String address, String occupation) throws SQLException {
        try {
            // Primero, insertamos el usuario en la tabla users
            String queryUsers = "INSERT INTO users(ci, name, password, phone_number, address, user_type) VALUES(?, ?, ?, ?, ?, 'E');";
            PreparedStatement psUsers = conexion.EstablecerConexion().prepareStatement(queryUsers);
            psUsers.setString(1, ci);
            psUsers.setString(2, nombre);
            psUsers.setString(3, password); // Asegúrate de encriptar el password con BCrypt
            psUsers.setInt(4, phone_number);
            psUsers.setString(5, address);

            if (psUsers.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en Users");
                throw new SQLException();
            }

            // Luego, obtenemos el ID del usuario recién insertado para relacionarlo con la tabla empleados
            ResultSet generatedKeys = psUsers.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);

                // Ahora, insertamos en la tabla empleados
                String queryEmpleados = "INSERT INTO employees(id, occupation) VALUES(?, ?);";
                PreparedStatement psEmpleados = conexion.EstablecerConexion().prepareStatement(queryEmpleados);
                psEmpleados.setInt(1, userId);
                psEmpleados.setString(2, occupation);

                if (psEmpleados.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Empleados");
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void modificar(int id, String ci, String name, String password, int phone, String address, String occupation) throws SQLException {
        String query = "UPDATE users SET ci=?, name=?, password=?, phone_number=?, address=? WHERE id=?;"
                + "UPDATE employees SET occupation=? WHERE id=?";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setString(1, ci);
        ps.setString(2, name);
        ps.setString(3, password);  // Asegúrate de encriptar la contraseña aquí si es necesario
        ps.setInt(4, phone);
        ps.setString(5, address);
        ps.setInt(6, id);
        ps.setString(7, occupation);
        ps.setInt(8, id);

        // Si el número de filas afectadas es 0, significa que el empleado no fue encontrado
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            System.err.println("Ocurrió un error al modificar en Empleados");
            throw new SQLException("Empleado no encontrado");
        }
    }

    // Método para eliminar un Empleado
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar el Empleado");
            throw new SQLException("Empleado no encontrado");
        }
    }

    // Método para listar los empleadoes y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> empleados = new ArrayList<>();
        String query = "SELECT u.id, u.ci, u.name, u.phone_number, u.address, e.occupation FROM users u INNER JOIN employees e ON u.id = e.id;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        empleados.add(new String[]{"ID", "CI", "Nombre", "Teléfono", "Dirección", "Ocupación"});

        while (set.next()) {
            empleados.add(new String[]{
                set.getString("id"),
                set.getString("ci"),
                set.getString("name"),
                set.getString("phone_number"),
                set.getString("address"),
                set.getString("occupation")
            });
        }

        String bodyHtml = generarHTMLTabla(empleados);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(empleados);
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

    // Método para obtener un empleado por su ID
    public String obtenerEmpleadoPorId(int id) throws SQLException {
        String query = "SELECT u.id, u.ci, u.name, u.phone_number, u.address, e.occupation FROM users u INNER JOIN employees e ON u.id = e.id WHERE u.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String empleado = String.format("ID: %d, CI: %s, Nombre: %s, Teléfono: %d, Dirección: %s, Ocupación: %d",
                    rs.getInt("id"),
                    rs.getString("ci"),
                    rs.getString("name"),
                    rs.getInt("phone_number"),
                    rs.getString("address"),
                    rs.getString("occupation")
            );
            return empleado;
        } else {
            throw new SQLException("Empleado no encontrado");
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
        return "COMANDOS PARA CU: EMPLEADO<br>"
                + "empleado listar<br>"
                + "empleado agregar [ci; nombre; password; teléfono; dirección; ocupacion]<br>"
                + "empleado modificar [id; ci; nombre; password; teléfono; dirección; ocupacion]<br>"
                + "empleado eliminar [id]";
    }
}
