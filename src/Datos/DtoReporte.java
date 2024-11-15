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
public class DtoReporte {
    private Conexion conexion;
    private ImprimirTabla imprimirTabla;

    public DtoReporte() {
        this.conexion = new Conexion();
        this.imprimirTabla = new ImprimirTabla();
    }
    /*
    public String pagos(String emailFrom, String fecha_ini, String fecha_fin, String tipo_servicio) throws SQLException {
        System.out.println("[DtoReporte] Iniciando reporte de pagos");
        String fecha_inicial = convertirFechaFormatoCadena(fecha_ini);
        String fecha_final = convertirFechaFormatoCadena(fecha_fin);
        String tipoServicioMayuscula = tipo_servicio.toUpperCase();
        
        System.out.println("[DtoReporte] Fechas convertidas: inicio=" + fecha_inicial + ", fin=" + fecha_final);
        System.out.println("[DtoReporte] Tipo de servicio recibido: " + tipo_servicio);
        System.out.println("[DtoReporte] Tipo de servicio convertido a mayúscula: " + tipoServicioMayuscula);

        String query = "SELECT s.service_type, COUNT(p.id) AS total_pagos, SUM(p.total) AS total_ingresos " +
                       "FROM payments p " +
                       "JOIN services s ON p.service_id = s.id " +
                       "WHERE p.date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
                       "AND s.service_type = ? " +
                       "GROUP BY s.service_type;";

        PreparedStatement ps = null;
        ResultSet set = null;

        try {
            ps = conexion.EstablecerConexion().prepareStatement(query);
            ps.setString(1, fecha_inicial);
            ps.setString(2, fecha_final);
            ps.setString(3, tipoServicioMayuscula);
            System.out.println("[DtoReporte] Query generado: " + ps);

            set = ps.executeQuery();
            System.out.println("[DtoReporte] Query ejecutado con éxito");

            List<String[]> resultados = new ArrayList<>();
            resultados.add(new String[]{"Tipo Servicio", "Total Pagos", "Total Ingresos"});

            while (set.next()) {
                resultados.add(new String[]{
                    set.getString("service_type"),
                    String.valueOf(set.getInt("total_pagos")),
                    String.valueOf(set.getFloat("total_ingresos"))
                });
            }
            
            if (resultados.size() == 1) { // Si solo está el encabezado, no hay datos
                System.out.println("[DtoReporte] No se encontraron resultados para el rango especificado");
                resultados.add(new String[] { "Sin datos", "0", "0.00" });
            }

            String bodyHtml = generarHTMLTabla(resultados);
            SendEmail sendEmail = new SendEmail();
            sendEmail.responseEmail(emailFrom, bodyHtml);

            return imprimirTabla.mostrarTabla(resultados);
        } catch (SQLException e) {
            System.err.println("[DtoReporte] Error al ejecutar el query: " + e.getMessage());
            throw e;
        } finally {
            if (set != null) set.close();
            if (ps != null) ps.close();
            conexion.CerrarConexion();
            System.out.println("[DtoReporte] Conexión cerrada");
        }
    }
*/
    public String pagos(String emailFrom, String fecha_ini, String fecha_fin, int patientId) throws SQLException {
        System.out.println("[DtoReporte] Iniciando reporte de pagos por paciente");
        String fecha_inicial = convertirFechaFormatoCadena(fecha_ini);
        String fecha_final = convertirFechaFormatoCadena(fecha_fin);

        String query = "SELECT p.date, s.service_type, p.total " +
                       "FROM payments p " +
                       "JOIN services s ON p.service_id = s.id " +
                       "WHERE s.patient_id = ? " +
                       "AND p.date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
                       "ORDER BY p.date ASC;";

        try (PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query)) {
            ps.setInt(1, patientId);
            ps.setString(2, fecha_inicial);
            ps.setString(3, fecha_final);
            System.out.println("[DtoReporte] Query generado: " + ps);

            ResultSet set = ps.executeQuery();
            System.out.println("[DtoReporte] Query ejecutado con éxito");

            List<String[]> resultados = new ArrayList<>();
            resultados.add(new String[]{"Fecha", "Tipo Servicio", "Total Pagado"});

            while (set.next()) {
                resultados.add(new String[]{
                    set.getString("date"),
                    set.getString("service_type"),
                    String.format("%.2f", set.getDouble("total"))
                });
            }

            if (resultados.size() == 1) {
                resultados.add(new String[] { "Sin datos", "-", "0.00" });
            }

            String bodyHtml = generarHTMLTabla(resultados);
            new SendEmail().responseEmail(emailFrom, bodyHtml);

            return imprimirTabla.mostrarTabla(resultados);
        } finally {
            conexion.CerrarConexion();
            System.out.println("[DtoReporte] Conexión cerrada");
        }
    }
    
    
    public String consultas(String emailFrom, String fecha_ini, String fecha_fin, int doctorId) throws SQLException {
        System.out.println("[DtoReporte] Iniciando reporte de consultas realizadas por un doctor");
        String fecha_inicial = convertirFechaFormatoCadena(fecha_ini);
        String fecha_final = convertirFechaFormatoCadena(fecha_fin);

        String query = "SELECT c.date, c.reason, cs.symptoms, cs.diagnosis " +
                       "FROM consults c " +
                       "JOIN caresheets cs ON c.id = cs.consult_id " +
                       "WHERE cs.doctor_id = ? " +
                       "AND c.date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
                       "ORDER BY c.date ASC;";

        try (PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query)) {
            ps.setInt(1, doctorId);
            ps.setString(2, fecha_inicial);
            ps.setString(3, fecha_final);
            System.out.println("[DtoReporte] Query generado: " + ps);

            ResultSet set = ps.executeQuery();
            System.out.println("[DtoReporte] Query ejecutado con éxito");

            List<String[]> resultados = new ArrayList<>();
            resultados.add(new String[]{"Fecha", "Motivo", "Síntomas", "Diagnóstico"});

            while (set.next()) {
                resultados.add(new String[]{
                    set.getString("date"),
                    set.getString("reason"),
                    set.getString("symptoms"),
                    set.getString("diagnosis")
                });
            }

            if (resultados.size() == 1) {
                resultados.add(new String[] { "Sin datos", "-", "-", "-" });
            }

            String bodyHtml = generarHTMLTabla(resultados);
            new SendEmail().responseEmail(emailFrom, bodyHtml);

            return imprimirTabla.mostrarTabla(resultados);
        } finally {
            conexion.CerrarConexion();
            System.out.println("[DtoReporte] Conexión cerrada");
        }
    }


    public String historial(String emailFrom, int patientId) throws SQLException {
        System.out.println("[DtoReporte] Iniciando historial de tratamiento de un paciente");

        String query = "SELECT t.id, t.origin, t.status, o.date, o.weight, o.height, o.age, o.description " +
                       "FROM treatments t " +
                       "JOIN observations o ON t.id = o.treatment_id " +
                       "WHERE t.id IN (SELECT id FROM services WHERE patient_id = ?) " +
                       "ORDER BY o.date ASC;";

        try (PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query)) {
            ps.setInt(1, patientId);
            System.out.println("[DtoReporte] Query generado: " + ps);

            ResultSet set = ps.executeQuery();
            System.out.println("[DtoReporte] Query ejecutado con éxito");

            List<String[]> resultados = new ArrayList<>();
            resultados.add(new String[]{"ID Tratamiento", "Origen", "Estado", "Fecha Observación", "Peso", "Altura", "Edad", "Descripción"});

            while (set.next()) {
                resultados.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("origin"),
                    set.getString("status"),
                    set.getString("date"),
                    String.format("%.2f", set.getDouble("weight")),
                    String.format("%.2f", set.getDouble("height")),
                    String.valueOf(set.getInt("age")),
                    set.getString("description")
                });
            }

            if (resultados.size() == 1) {
                resultados.add(new String[] { "Sin datos", "-", "-", "-", "-", "-", "-", "-" });
            }

            String bodyHtml = generarHTMLTabla(resultados);
            new SendEmail().responseEmail(emailFrom, bodyHtml);

            return imprimirTabla.mostrarTabla(resultados);
        } finally {
            conexion.CerrarConexion();
            System.out.println("[DtoReporte] Conexión cerrada");
        }
    }


    // Generación de tabla HTML
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
    
    private String convertirFechaFormatoCadena(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatoSalida = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fechaUtil = formatoEntrada.parse(fechaStr);
            return formatoSalida.format(fechaUtil); // Retorna la fecha en formato yyyy-MM-dd como cadena
        } catch (ParseException e) {
            System.err.println("Error de formato en la fecha: " + fechaStr);
            throw new SQLException("Formato de fecha inválido. Use 'dd/MM/yyyy'.");
        }
    }
    

    public void desconectar() {
        if (conexion != null) {
            conexion.CerrarConexion();
        }
    }

    public String getComandos() {
        return "COMANDOS PARA CU: Reporte<br>" +
               "reporte pagos [fecha_ini(dd/MM/yyyy); fecha_fin(dd/MM/yyyy); tipo_servicio(Opciones: C, V, T, vacio)] <br>";
    }
}
