package Negocio;

import Datos.DtoResponsable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class NegResponsable {

    private DtoResponsable dtoResponsable;

    public NegResponsable() {
        this.dtoResponsable = new DtoResponsable();
    }

    // responsable agregar[ci; nombre; direccion; genero; fechaNacimiento; telefono; ocupacion]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            dtoResponsable.agregar(
                parametros.get(0), // ci
                parametros.get(1), // nombre
                parametros.get(2), // direccion
                parametros.get(3).charAt(0), // genero
                parametros.get(4), // fechaNacimiento
                Integer.parseInt(parametros.get(5)), // telefono
                parametros.get(6) // ocupacion
            );
            dtoResponsable.desconectar();
        } catch (SQLException e) {
            System.err.println("Error al agregar Responsable: " + e.getMessage());
            throw e;
        }
    }

    // responsable modificar[id; ci; nombre; direccion; genero; fechaNacimiento; telefono; ocupacion]
    public void modificar(List<String> parametros) throws SQLException, ParseException {
        try {
            dtoResponsable.modificar(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // ci
                parametros.get(2), // nombre
                parametros.get(3), // direccion
                parametros.get(4).charAt(0), // genero
                parametros.get(5), // fechaNacimiento
                Integer.parseInt(parametros.get(6)), // telefono
                parametros.get(7) // ocupacion
            );
            System.out.println("SE MODIFICÓ LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
        } catch (SQLException e) {
            System.err.println("Error al modificar Responsable: " + e.getMessage());
            throw e;
        }
    }

    // responsable eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoResponsable.eliminar(id);
        dtoResponsable.desconectar();
    }

    // responsable listar
    public String listar(String emailFrom) throws SQLException {
        String responsables = dtoResponsable.listar(emailFrom);
        dtoResponsable.desconectar();
        return responsables;
    }

    // Comandos disponibles para la entidad Responsable
    public String getComandos() {
        return dtoResponsable.getComandos();
    }
}
