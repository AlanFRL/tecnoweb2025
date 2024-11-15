/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author tengo
 */
public class DtoSeguimiento {
    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoSeguimiento() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }

    // Método para agregar un nuevo seguimiento sin requerir id
    public void agregar(int treatmentId, int doctorId, String date, float weight, float height, int age, String description) throws SQLException {
        try {
            // Conversión de String a Date en formato dd/MM/yyyy
            Date fecha = convertirFecha(date);
            
            String query = "INSERT INTO observations(treatment_id, doctor_id, date, weight, height, age, description) VALUES(?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, treatmentId);
            ps.setInt(2, doctorId);
            ps.setDate(3, (java.sql.Date)fecha);
            ps.setFloat(4, weight);
            ps.setFloat(5, height);
            ps.setInt(6, age);
            ps.setString(7, description);

            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al insertar un registro de observations en DtoSeguimiento");
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para modificar un seguimiento existente
    public void modificar(int id, int treatmentId, int doctorId, String date, float weight, float height, int age, String description) throws SQLException {
        try {
            // Conversión de String a Date en formato dd/MM/yyyy
            Date fecha = convertirFecha(date);
            
            String query = "UPDATE observations SET treatment_id=?, doctor_id=?, date=?, weight=?, height=?, age=?, description=? WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, treatmentId);
            ps.setInt(2, doctorId);
            ps.setDate(3, (java.sql.Date)fecha);
            ps.setFloat(4, weight);
            ps.setFloat(5, height);
            ps.setInt(6, age);
            ps.setString(7, description);
            ps.setInt(8, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Ocurrió un error al modificar un registro de observations en DtoSeguimiento");
                throw new SQLException("Seguimiento no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Método para eliminar un seguimiento
    public void eliminar(int id) throws SQLException {
        try {
            String query = "DELETE FROM observations WHERE id=?;";
            PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                System.err.println("Ocurrió un error al eliminar el registro de observations en DtoSeguimiento");
                throw new SQLException("Seguimiento no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }  
    }

    // Método para listar los seguimientos y enviar el resultado por email
    public String listar(String emailFrom) throws SQLException {
        List<String[]> seguimientos = new ArrayList<>();
        String query = "SELECT * FROM observations;";
        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        seguimientos.add(new String[]{"ID", "Tratamiento ID", "Doctor ID", "Fecha", "Peso", "Estatura", "Edad", "Descripcion"});

        while (set.next()) {
            seguimientos.add(new String[]{
                set.getString("id"),
                set.getString("treatment_id"),
                set.getString("doctor_id"),
                set.getString("date"),
                set.getString("weight"),
                set.getString("height"),
                set.getString("age"),
                set.getString("description")
            });
        }

        String bodyHtml = generarHTMLTabla(seguimientos);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        return imprimirTabla.mostrarTabla(seguimientos);
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

    // Método para desconectar de la base de datos
    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    // Comandos disponibles
    public String getComandos() {
        return "COMANDOS PARA CU: SEGUIMIENTO<br>"
                + "seguimiento listar<br>"
                + "seguimiento agregar [tratamiento_id(int);doctor_id(int);fecha(string);peso(float);estatura(float);edad(int);descripcion(string)]<br>"
                + "seguimiento modificar [id(int);tratamiento_id(int);doctor_id(int);fecha(dd/MM/yyyy);peso(float);estatura(float);edad(int);descripcion(string)]<br>"
                + "seguimiento eliminar [id]";
    }
}
