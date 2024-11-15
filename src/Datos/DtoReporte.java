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
