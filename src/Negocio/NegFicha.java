package Negocio;

import Datos.DtoFicha;
import java.sql.SQLException;
import java.util.List;

public class NegFicha {

    private DtoFicha dtoFicha;

    public NegFicha() {
        this.dtoFicha = new DtoFicha();
    }

    // ficha agregar[doctor_id; consult_id; symptoms; diagnosis; notes]
    public void agregar(List<String> parametros) throws SQLException {
        dtoFicha.agregar(
                Integer.parseInt(parametros.get(0)), // doctor_id
                Integer.parseInt(parametros.get(1)), // consult_id
                parametros.get(2), // symptoms
                parametros.get(3), // diagnosis
                parametros.get(4)  // notes
        );
        dtoFicha.desconectar();
    }

    // ficha modificar[id; doctor_id; consult_id; symptoms; diagnosis; notes]
    public void modificar(List<String> parametros) throws SQLException {
        dtoFicha.modificar(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)), // doctor_id
                Integer.parseInt(parametros.get(2)), // consult_id
                parametros.get(3), // symptoms
                parametros.get(4), // diagnosis
                parametros.get(5)  // notes
        );
        dtoFicha.desconectar();
    }

    // ficha eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoFicha.eliminar(id);
        dtoFicha.desconectar();
    }

    // ficha listar
    public String listar(String emailFrom) throws SQLException {
        String fichas = dtoFicha.listar(emailFrom);
        dtoFicha.desconectar();
        return fichas;
    }

    // Comandos disponibles para la entidad Ficha
    public String getComandos() {
        return dtoFicha.getComandos();
    }
}