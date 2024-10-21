package Datos;

import Comunication.SendEmail;
import Database.Conexion;
import Utils.ImprimirTabla;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DtoResponsable {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoResponsable() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo responsable (proxy)
    public void agregar(String ci, String nombre, String direccion, char genero, String fechaNacimiento, int telefono, String ocupacion) throws SQLException {
        try {
            // Convertir género a mayúscula
            char generoUpper = Character.toUpperCase(genero);

            // Insertar en la tabla people
            String queryPeople = "INSERT INTO people(ci, name, address, gender, birth_date, people_type) VALUES(?, ?, ?, ?, ?, 'P');";
            PreparedStatement psPeople = conexion.EstablecerConexion().prepareStatement(queryPeople, PreparedStatement.RETURN_GENERATED_KEYS);
            psPeople.setString(1, ci);
            psPeople.setString(2, nombre);
            psPeople.setString(3, direccion);
            psPeople.setString(4, String.valueOf(generoUpper)); // Usar el género en mayúscula
            psPeople.setString(5, fechaNacimiento);

            if (psPeople.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en People");
                throw new SQLException();
            }

            // Obtener el ID generado
            ResultSet generatedKeys = psPeople.getGeneratedKeys();
            if (generatedKeys.next()) {
                int peopleId = generatedKeys.getInt(1);

                // Insertar en la tabla proxies
                String queryProxies = "INSERT INTO proxies(id, phone_number, occupation) VALUES(?, ?, ?);";
                PreparedStatement psProxies = conexion.EstablecerConexion().prepareStatement(queryProxies);
                psProxies.setInt(1, peopleId);
                psProxies.setInt(2, telefono);
                psProxies.setString(3, ocupacion);

                if (psProxies.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Proxies");
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar un responsable
    public void modificar(int id, String ci, String nombre, String direccion, char genero, String fechaNacimiento, int telefono, String ocupacion) throws SQLException {
        try {
            // Convertir género a mayúscula
            char generoUpper = Character.toUpperCase(genero);

            String query = "UPDATE people SET ci=?, name=?, address=?, gender=?, birth_date=? WHERE id=?;"
                    + "UPDATE proxies SET phone_number=?, occupation=? WHERE id=?";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);

            ps.setString(1, ci);
            ps.setString(2, nombre);
            ps.setString(3, direccion);
            ps.setString(4, String.valueOf(generoUpper)); // Usar el género en mayúscula
            ps.setString(5, fechaNacimiento);
            ps.setInt(6, id);
            ps.setInt(7, telefono);
            ps.setString(8, ocupacion);
            ps.setInt(9, id);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al modificar el Responsable");
                throw new SQLException("Responsable no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un responsable
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM people WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);

        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar el Responsable");
            throw new SQLException("Responsable no encontrado");
        }
    }

    // Método para listar responsables y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> responsables = new ArrayList<>();
        String query = "SELECT p.id, p.ci, p.name, p.address, p.gender, p.birth_date, pr.phone_number, pr.occupation "
                + "FROM people p INNER JOIN proxies pr ON p.id = pr.id;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        responsables.add(new String[]{"ID", "CI", "Nombre", "Dirección", "Género", "Fecha de Nacimiento", "Teléfono", "Ocupación"});

        while (set.next()) {
            responsables.add(new String[]{
                set.getString("id"),
                set.getString("ci"),
                set.getString("name"),
                set.getString("address"),
                set.getString("gender"),
                set.getString("birth_date"),
                set.getString("phone_number"),
                set.getString("occupation")
            });
        }

        String bodyHtml = generarHTMLTabla(responsables);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(responsables);
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

    // Método para obtener un responsable por su ID
    public String obtenerResponsablePorId(int id) throws SQLException {
        String query = "SELECT p.id, p.ci, p.name, p.address, p.gender, p.birth_date, pr.phone_number, pr.occupation "
                + "FROM people p INNER JOIN proxies pr ON p.id = pr.id WHERE p.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String responsable = String.format("ID: %d, CI: %s, Nombre: %s, Dirección: %s, Género: %s, Fecha de Nacimiento: %s, Teléfono: %d, Ocupación: %s",
                    rs.getInt("id"),
                    rs.getString("ci"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    rs.getString("birth_date"),
                    rs.getInt("phone_number"),
                    rs.getString("occupation")
            );
            return responsable;
        } else {
            throw new SQLException("Responsable no encontrado");
        }
    }

    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    public String getComandos() {
        return "COMANDOS PARA CU: RESPONSABLE<br>"
                + "responsable listar<br>"
                + "responsable agregar [ci; nombre; direccion; genero; fechaNacimiento; telefono; ocupacion]<br>"
                + "responsable modificar [id; ci; nombre; direccion; genero; fechaNacimiento; telefono; ocupacion]<br>"
                + "responsable eliminar [id]";
    }
}
