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
import java.util.Date;
import java.util.List;

public class DtoPaciente {

    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoPaciente() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo paciente
    public void agregar(String ci, String nombre, String direccion, char genero, String fechaNacimiento, String tipoSangre, char factorRH, int proxyId) throws SQLException {
        try {
            // Conversión de String a Date en formato dd/MM/yyyy
            Date fechaNacimientoDate = convertirFecha(fechaNacimiento);

            // Convertir género a mayúscula
            char generoUpper = Character.toUpperCase(genero);

            // Convertir tipo de sangre y factor RH a mayúscula
            String tipoSangreUpper = tipoSangre.toUpperCase();
            char factorRHUpper = Character.toUpperCase(factorRH);

            // Insertar en la tabla people
            String queryPeople = "INSERT INTO people(ci, name, address, gender, birth_date, people_type) VALUES(?, ?, ?, ?, ?, 'A');";
            PreparedStatement psPeople = conexion.EstablecerConexion().prepareStatement(queryPeople, PreparedStatement.RETURN_GENERATED_KEYS);
            psPeople.setString(1, ci);
            psPeople.setString(2, nombre);
            psPeople.setString(3, direccion);
            psPeople.setString(4, String.valueOf(generoUpper)); // Usar el género en mayúscula
            psPeople.setDate(5, (java.sql.Date) fechaNacimientoDate);

            if (psPeople.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar en People");
                throw new SQLException();
            }

            // Continuar con la inserción en la tabla patients
            ResultSet generatedKeys = psPeople.getGeneratedKeys();
            if (generatedKeys.next()) {
                int peopleId = generatedKeys.getInt(1);
                String queryPatients = "INSERT INTO patients(id, blood_type, rh_factor, proxy_id) VALUES(?, ?, ?, ?);";
                PreparedStatement psPatients = conexion.EstablecerConexion().prepareStatement(queryPatients);
                psPatients.setInt(1, peopleId);
                psPatients.setString(2, tipoSangreUpper); // Usar tipo de sangre en mayúscula
                psPatients.setString(3, String.valueOf(factorRHUpper)); // Usar factor RH en mayúscula
                psPatients.setInt(4, proxyId);

                if (psPatients.executeUpdate() == 0) {
                    System.err.println("Ocurrió un error al insertar en Patients");
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para convertir String a java.sql.Date con formato dd/MM/yyyy
    private Date convertirFecha(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaUtil = formatoFecha.parse(fechaStr);
            return new java.sql.Date(fechaUtil.getTime()); // Convertir a java.sql.Date
        } catch (ParseException e) {
            System.err.println("Error de formato en la fecha: " + fechaStr);
            throw new SQLException("Formato de fecha inválido. Use 'dd/MM/yyyy'.");
        }
    }

    // Método para modificar un paciente
    public void modificar(int id, String ci, String nombre, String direccion, char genero, String fechaNacimiento, String tipoSangre, char factorRH, int proxyId) throws SQLException {
        try {
            // Convertir género, tipo de sangre y factor RH a mayúscula
            char generoUpper = Character.toUpperCase(genero);
            String tipoSangreUpper = tipoSangre.toUpperCase();
            char factorRHUpper = Character.toUpperCase(factorRH);

            // Convertir fecha de nacimiento a java.sql.Date
            Date fechaNacimientoDate = convertirFecha(fechaNacimiento);

            // Query para modificar la tabla people y patients
            String query = "UPDATE people SET ci=?, name=?, address=?, gender=?, birth_date=? WHERE id=?;"
                    + "UPDATE patients SET blood_type=?, rh_factor=?, proxy_id=? WHERE id=?";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);

            // Setear parámetros para la tabla people
            ps.setString(1, ci);
            ps.setString(2, nombre);
            ps.setString(3, direccion);
            ps.setString(4, String.valueOf(generoUpper)); // Usar el género en mayúscula
            ps.setDate(5, (java.sql.Date) fechaNacimientoDate); // Convertir a java.sql.Date
            ps.setInt(6, id);

            // Setear parámetros para la tabla patients
            ps.setString(7, tipoSangreUpper); // Usar tipo de sangre en mayúscula
            ps.setString(8, String.valueOf(factorRHUpper)); // Usar factor RH en mayúscula
            ps.setInt(9, proxyId);
            ps.setInt(10, id);

            // Ejecutar el update
            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al modificar el Paciente");
                throw new SQLException("Paciente no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un paciente
    public void eliminar(int id) throws SQLException {
        String query = "DELETE FROM people WHERE id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);

        if (ps.executeUpdate() == 0) {
            System.err.println("Ocurrió un error al eliminar el Paciente");
            throw new SQLException("Paciente no encontrado");
        }
    }

    // Método para listar pacientes y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> pacientes = new ArrayList<>();
        String query = "SELECT p.id, p.ci, p.name, p.address, p.gender, p.birth_date, pa.blood_type, pa.rh_factor, pa.proxy_id "
                + "FROM people p INNER JOIN patients pa ON p.id = pa.id;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        pacientes.add(new String[]{"ID", "CI", "Nombre", "Dirección", "Género", "Fecha de Nacimiento", "Tipo de Sangre", "Factor RH", "Responsable ID"});

        while (set.next()) {
            pacientes.add(new String[]{
                set.getString("id"),
                set.getString("ci"),
                set.getString("name"),
                set.getString("address"),
                set.getString("gender"),
                set.getString("birth_date"),
                set.getString("blood_type"),
                set.getString("rh_factor"),
                set.getString("proxy_id")
            });
        }

        String bodyHtml = generarHTMLTabla(pacientes);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(pacientes);
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

    // Método para obtener un paciente por su ID
    public String obtenerPacientePorId(int id) throws SQLException {
        String query = "SELECT p.id, p.ci, p.name, p.address, p.gender, p.birth_date, pa.blood_type, pa.rh_factor, pa.proxy_id "
                + "FROM people p INNER JOIN patients pa ON p.id = pa.id WHERE p.id=?;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String paciente = String.format("ID: %d, CI: %s, Nombre: %s, Dirección: %s, Género: %s, Fecha de Nacimiento: %s, Tipo de Sangre: %s, Factor RH: %s, Proxy ID: %d",
                    rs.getInt("id"),
                    rs.getString("ci"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    rs.getString("birth_date"),
                    rs.getString("blood_type"),
                    rs.getString("rh_factor"),
                    rs.getInt("proxy_id")
            );
            return paciente;
        } else {
            throw new SQLException("Paciente no encontrado");
        }
    }

    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    public String getComandos() {
        return "COMANDOS PARA CU: PACIENTE<br>"
                + "paciente listar<br>"
                + "paciente agregar [ci; nombre; direccion; genero; fechaNacimiento; tipoSangre; factorRH; responsableId]<br>"
                + "paciente modificar [id; ci; nombre; direccion; genero; fechaNacimiento; tipoSangre; factorRH; responsableId]<br>"
                + "paciente eliminar [id]";
    }
}
