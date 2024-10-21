package Negocio;

import Datos.DtoSala;
import java.sql.SQLException;
import java.util.List;

public class NegSala {

    private DtoSala dtoSala;

    public NegSala() {
        this.dtoSala = new DtoSala();
    }

    // sala agregar[capacidad; habitaciones disponibles]
    public void agregar(List<String> parametros) throws SQLException {
        dtoSala.agregar(
                Integer.parseInt(parametros.get(0)), // capacity
                Integer.parseInt(parametros.get(1)) // availableRooms
        );
        dtoSala.desconectar();
    }

    // sala modificar[id; capacidad; habitaciones disponibles]
    public void modificar(List<String> parametros) throws SQLException {
        dtoSala.modificar(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)), // capacity
                Integer.parseInt(parametros.get(2)) // availableRooms
        );
        dtoSala.desconectar();
    }

    // sala eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoSala.eliminar(id);
        dtoSala.desconectar();
    }

    // sala listar
    public String listar(String emailFrom) throws SQLException {
        String salas = dtoSala.listar(emailFrom);
        dtoSala.desconectar();
        return salas;
    }

    // Comandos disponibles para la entidad Sala
    public String getComandos() {
        return dtoSala.getComandos();
    }
}
