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

    // Reporte de pagos por servicio en un rango de fechas, opcionalmente filtrado por tipo de servicio
    public String pagos(String emailFrom, String fecha_ini, String fecha_fin, String tipo_servicio) throws SQLException {
        // Conversión de String a Date en formato dd/MM/yyyy
        java.sql.Date fecha_inicial = convertirFecha(fecha_ini);
        java.sql.Date fecha_final = convertirFecha(fecha_fin);
        
         // Query sin concatenación condicional
        String query = "SELECT s.service_type, COUNT(p.id) AS total_pagos, SUM(p.total) AS total_ingresos " +
                       "FROM payments p " +
                       "JOIN services s ON p.service_id = s.id " +
                       "WHERE p.date BETWEEN ? AND ? AND s.service_type = ? " +
                       "GROUP BY s.service_type;";

        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
        ps.setDate(1, fecha_inicial);
        ps.setDate(2, fecha_final);
        ps.setString(3, tipo_servicio); // Parámetro de tipo de servicio siempre presente

    System.out.println("Query ejecutado: " + ps);
        
        /*
        // Construimos el query dinámicamente según si se filtra por tipo de servicio o no
        String query = "SELECT s.service_type, COUNT(p.id) AS total_pagos, SUM(p.total) AS total_ingresos " +
                       "FROM payments p " +
                       "JOIN services s ON p.service_id = s.id " +
                       "WHERE p.date BETWEEN ? AND ? ";

        if (tipo_servicio != null && !tipo_servicio.isEmpty()) {
            query += "AND s.service_type = ? "; // Filtrar por tipo de servicio si se proporciona
        }
        
        query += "GROUP BY s.service_type;";

        PreparedStatement ps = conexion.EstablecerConexion().prepareStatement(query);
*/
        /*
        ps.setDate(1, fecha_inicial);
        ps.setDate(2, fecha_final);

        if (tipo_servicio != null && !tipo_servicio.isEmpty()) {
            ps.setString(3, tipo_servicio); // Se añade el parámetro del tipo de servicio
        }
        
        System.out.println("Query ejecutado: " + ps);
*/
        ResultSet set = ps.executeQuery();
        List<String[]> resultados = new ArrayList<>();
        resultados.add(new String[]{"Tipo Servicio", "Total Pagos", "Total Ingresos"});

        while (set.next()) {
            resultados.add(new String[]{
                set.getString("service_type"),
                String.valueOf(set.getInt("total_pagos")),
                String.valueOf(set.getFloat("total_ingresos"))
            });
        }

        String bodyHtml = generarHTMLTabla(resultados);
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);

        return imprimirTabla.mostrarTabla(resultados);
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
    
    // Método para convertir String a java.sql.Date con formato dd/MM/yyyy
    private java.sql.Date convertirFecha(String fechaStr) throws SQLException {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaUtil = formatoFecha.parse(fechaStr);
            return new java.sql.Date(fechaUtil.getTime()); // Convertir a java.sql.Date
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
               "reporte pagos [fecha_ini; fecha_fin; tipo_servicio (opcional)] (yyyy-mm-dd)<br>";
    }
}
