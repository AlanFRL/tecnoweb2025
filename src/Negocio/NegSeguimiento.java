/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Negocio;

import Datos.DtoSeguimiento;
import java.sql.SQLException;
import java.util.List;


/**
 *
 * @author tengo
 */
public class NegSeguimiento {
    private DtoSeguimiento dtoSeguimiento;

    public NegSeguimiento() {
        this.dtoSeguimiento = new DtoSeguimiento();
    }

    // seguimiento agregar [tratamiento_id(int);doctor_id(int);fecha(string);peso(float);estatura(float);edad(int);descripcion(string)]
    public void agregar(List<String> parametros) throws SQLException {
        dtoSeguimiento.agregar(
                Integer.parseInt(parametros.get(0)), // tratamiento_id
                Integer.parseInt(parametros.get(1)), // doctor_id
                parametros.get(2), // fecha
                Float.parseFloat(parametros.get(3)), //peso
                Float.parseFloat(parametros.get(4)), //estatura
                Integer.parseInt(parametros.get(5)), // edad
                parametros.get(6) // descripcion
        );
        dtoSeguimiento.desconectar();
    }

    // seguimiento modificar [id(int);tratamiento_id(int);doctor_id(int);fecha(string);peso(float);estatura(float);edad(int);descripcion(string)]
    public void modificar(List<String> parametros) throws SQLException {
        dtoSeguimiento.modificar(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)), // tratamiento_id
                Integer.parseInt(parametros.get(2)), // doctor_id
                parametros.get(3), // fecha
                Float.parseFloat(parametros.get(4)), //peso
                Float.parseFloat(parametros.get(5)), //estatura
                Integer.parseInt(parametros.get(6)), // edad
                parametros.get(7) // descripcion
        );
        dtoSeguimiento.desconectar();
    }

    // seguimiento eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoSeguimiento.eliminar(id);
        dtoSeguimiento.desconectar();
    }

    // seguimiento listar
    public String listar(String emailFrom) throws SQLException {
        String fichas = dtoSeguimiento.listar(emailFrom);
        dtoSeguimiento.desconectar();
        return fichas;
    }

    // Comandos disponibles para la entidad Ficha
    public String getComandos() {
        return dtoSeguimiento.getComandos();
    }
    
}
