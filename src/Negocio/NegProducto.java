package Negocio;

import Datos.DtoProducto;
import java.sql.SQLException;
import java.util.List;

public class NegProducto {

    private DtoProducto dtoProducto;

    public NegProducto() {
        this.dtoProducto = new DtoProducto();
    }

    // Método para agregar un nuevo producto
    public void agregar(List<String> parametros) throws SQLException {
        dtoProducto.agregar(
                parametros.get(0), // name
                Float.parseFloat(parametros.get(1)), // price
                parametros.get(2) // ingredients
        );
        dtoProducto.desconectar();
    }

    // Método para modificar un producto
    public void modificar(List<String> parametros) throws SQLException {
        dtoProducto.modificar(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // name
                Float.parseFloat(parametros.get(2)), // price
                parametros.get(3) // ingredients
        );
        dtoProducto.desconectar();
    }

    // producto eliminar [id]
    public void eliminar(int id) throws SQLException {
        dtoProducto.eliminar(id);
        dtoProducto.desconectar();
    }

    // producto listar
    public String listar(String emailFrom) throws SQLException {
        String productos = dtoProducto.listar(emailFrom);
        dtoProducto.desconectar();
        return productos;
    }

    // Comandos disponibles para la entidad Producto
    public String getComandos() {
        return dtoProducto.getComandos();
    }
}
