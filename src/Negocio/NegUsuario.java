/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Negocio;

import Datos.DtoUsuario;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class NegUsuario {

    private DtoUsuario dtoUsuario;

    public NegUsuario() {
        this.dtoUsuario = new DtoUsuario();
    }
//usuario agregar[8230432; Jose Mario; j.mario18@hotmail.es; jose; 75540850; av/ pentaguazu; true]
    //usuario agregar[milton ; milton@gmail.com ; 123123123 ; 1]
    public void agregar(List<String> parametros) throws SQLException {
        String newPassword = encrypt(parametros.get(3));
        dtoUsuario.agregar(Integer.parseInt(parametros.get(0)), parametros.get(1), parametros.get(2),newPassword, Integer.parseInt(parametros.get(4)), parametros.get(5),Boolean.parseBoolean(parametros.get(6)));
        dtoUsuario.desconectar();
    }

    public void modificar(List<String> parametros) throws SQLException, ParseException {
        String newPassword = encrypt(parametros.get(3));
        dtoUsuario.modificar(Integer.parseInt(parametros.get(0)), Integer.parseInt(parametros.get(1)), parametros.get(2), parametros.get(3), newPassword, Integer.parseInt(parametros.get(5)), parametros.get(6), Boolean.parseBoolean(parametros.get(7)));
    }

    public void eliminar(int id) throws SQLException {
        dtoUsuario.eliminar(id);
        dtoUsuario.desconectar();
    }

    public String listar(String emailFrom) throws SQLException {
        String usuarios = dtoUsuario.listar(emailFrom);
        dtoUsuario.desconectar();
        return usuarios;
    }

    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public String getComandos() {
        return dtoUsuario.getComandos();
    }
}
