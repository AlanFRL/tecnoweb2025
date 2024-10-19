package Negocio;

import Datos.DtoDoctor;
import Datos.DtoEmpleado;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class NegEmpleado {

    private DtoEmpleado dtoEmpleado;

    public NegEmpleado() {
        this.dtoEmpleado = new DtoEmpleado();
    }

    // empleado agregar[ci; nombre; password; teléfono; dirección; ocupacion]
    public void agregar(List<String> parametros) throws SQLException {
        // Verifica que se reciban exactamente 6 parámetros
        if (parametros.size() != 6) {
            throw new IllegalArgumentException("Se esperaban 6 parámetros para agregar un empleado.");
        }

        String newPassword = encrypt(parametros.get(2)); // Encriptamos la contraseña

        // Llama a la capa de datos para insertar el empleado
        dtoEmpleado.agregar(parametros.get(0), parametros.get(1), newPassword,
                Integer.parseInt(parametros.get(3)), parametros.get(4),
                parametros.get(5));
        dtoEmpleado.desconectar();
    }

// empleado modificar[id; ci; nombre; password; teléfono; dirección; número_ss]
    public void modificar(List<String> parametros) throws SQLException, ParseException {
        try {
            String newPassword = encrypt(parametros.get(3));  // Encripta si es necesario
            dtoEmpleado.modificar(
                    Integer.parseInt(parametros.get(0)), // id
                    parametros.get(1), // ci
                    parametros.get(2), // name
                    newPassword, // password
                    Integer.parseInt(parametros.get(4)), // phone
                    parametros.get(5), // address
                    parametros.get(6) // occupation
            );
            System.out.println("SE MODIFICÓ LOS DATOS CORRECTAMENTE");
        } catch (SQLException e) {
            System.out.println("MENSAJE SQL: " + e.getMessage());
            throw e; // Para asegurarnos de que el error siga su curso normal
        }
    }

    // empleado eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoEmpleado.eliminar(id);
        dtoEmpleado.desconectar();
    }

    // empleado listar
    public String listar(String emailFrom) throws SQLException {
        String empleados = dtoEmpleado.listar(emailFrom);
        dtoEmpleado.desconectar();
        return empleados;
    }

    // Método para encriptar la contraseña
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Comandos disponibles para la entidad Empleado
    public String getComandos() {
        return dtoEmpleado.getComandos();
    }
}
