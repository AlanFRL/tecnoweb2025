/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;

import Datos.DtoReporte;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tengo
 */
public class NegReporte {
    private DtoReporte dtoReporte;

    public NegReporte() {
        this.dtoReporte = new DtoReporte();
    }

    public String pagos(String emailFrom,List<String> parametros) throws SQLException {
        String pagos = dtoReporte.pagos(emailFrom, parametros.get(0), parametros.get(1), parametros.get(2));
        dtoReporte.desconectar();
        return pagos;
    }
    
    /*
    public String ventas(String emailFrom) throws SQLException {
        String paquetes = dtoReporte.ventas(emailFrom);
        dtoReporte.desconectar();
        return paquetes;
    }
    */
    
    
    public String getComandos() {
        return dtoReporte.getComandos();
    }
}
