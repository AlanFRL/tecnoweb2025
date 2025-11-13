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
public class DtoDoctor {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoDoctor() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo doctor
    public void agregar(String ci, String nombre, String password, int phone_number, String address, int number_ss) throws SQLException {
        try {
            // Primero, insertamos el usuario en la tabla users
            String queryUsers = "INSERT INTO users(ci, name, password, phone_number, address, user_type) VALUES(?, ?, ?, ?, ?, 'D');";
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

            // Luego, obtenemos el ID del usuario recién insertado para relacionarlo con la tabla doctors
            ResultSet generatedKeys = psUsers.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);

                // Ahora, insertamos en la tabla doctors
                String queryDoctors = "INSERT INTO doctors(id, number_ss) VALUES(?, ?);";
                PreparedStatement psDoctors = conexion.EstablecerConexion().prepareStatement(queryDoctors);
                psDoctors.setInt(1, userId);
                psDoctors.setInt(2, number_ss);

                if (psDoctors.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Doctors");
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void modificar(int id, String ci, String name, String password, int phone, String address, int numberSS) throws SQLException {
        String query = "UPDATE users SET ci=?, name=?, password=?, phone_number=?, address=? WHERE id=?;"
                + "UPDATE doctors SET number_ss=? WHERE id=?";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setString(1, ci);
        ps.setString(2, name);
        ps.setString(3, password);  // Asegúrate de encriptar la contraseña aquí si es necesario
        ps.setInt(4, phone);
        ps.setString(5, address);
        ps.setInt(6, id);
        ps.setInt(7, numberSS);
        ps.setInt(8, id);

        // Si el número de filas afectadas es 0, significa que el doctor no fue encontrado
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            System.err.println("Ocurrió un error al modificar en Doctors");
            throw new SQLException("Doctor no encontrado");
        }
    }

    // Método para eliminar un doctor
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar el Doctor");
            throw new SQLException("Doctor no encontrado");
        }
    }

    // Método para listar los doctores y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> doctors = new ArrayList<>();
        String query = "SELECT u.id, u.ci, u.name, u.phone_number, u.address, d.number_ss FROM users u INNER JOIN doctors d ON u.id = d.id;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        doctors.add(new String[]{"ID", "CI", "Nombre", "Teléfono", "Dirección", "Número SS"});

        while (set.next()) {
            doctors.add(new String[]{
                set.getString("id"),
                set.getString("ci"),
                set.getString("name"),
                set.getString("phone_number"),
                set.getString("address"),
                set.getString("number_ss")
            });
        }

        String bodyHtml = generarHTMLTabla(doctors);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(doctors);
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

    // Método para obtener un doctor por su ID
    public String obtenerDoctorPorId(int id) throws SQLException {
        String query = "SELECT u.id, u.ci, u.name, u.phone_number, u.address, d.number_ss FROM users u INNER JOIN doctors d ON u.id = d.id WHERE u.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String doctor = String.format("ID: %d, CI: %s, Nombre: %s, Teléfono: %d, Dirección: %s, Número SS: %d",
                    rs.getInt("id"),
                    rs.getString("ci"),
                    rs.getString("name"),
                    rs.getInt("phone_number"),
                    rs.getString("address"),
                    rs.getInt("number_ss")
            );
            return doctor;
        } else {
            throw new SQLException("Doctor no encontrado");
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
        return "COMANDOS PARA CU: DOCTOR<br>"
                + "doctor listar<br>"
                + "doctor agregar [ci;nombre;password;teléfono; dirección; número_ss]<br>"
                + "doctor modificar [id; ci; nombre; password; teléfono; dirección; número_ss]<br>"
                + "doctor eliminar [id]";
    }
}
