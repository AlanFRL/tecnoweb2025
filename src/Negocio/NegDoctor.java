package Negocio;

import Datos.DtoDoctor;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Usuario
 */
public class NegDoctor {

    private DtoDoctor dtoDoctor;

    public NegDoctor() {
        this.dtoDoctor = new DtoDoctor();
    }

    // doctor agregar[ci; nombre; password; teléfono; dirección; número_ss]
    public void agregar(List<String> parametros) throws SQLException {
        String newPassword = encrypt(parametros.get(2)); // Encriptamos la contraseña
        dtoDoctor.agregar(parametros.get(0), parametros.get(1), newPassword,
                Integer.parseInt(parametros.get(3)), parametros.get(4),
                Integer.parseInt(parametros.get(5)));
        dtoDoctor.desconectar();
    }

// doctor modificar[id; ci; nombre; password; teléfono; dirección; número_ss]
    public void modificar(List<String> parametros) throws SQLException, ParseException {
        try {
            String newPassword = encrypt(parametros.get(3));  // Encripta si es necesario
            dtoDoctor.modificar(
                    Integer.parseInt(parametros.get(0)), // id
                    parametros.get(1), // ci
                    parametros.get(2), // name
                    newPassword, // password
                    Integer.parseInt(parametros.get(4)), // phone
                    parametros.get(5), // address
                    Integer.parseInt(parametros.get(6)) // numberSS
            );
            System.out.println("SE MODIFICÓ LOS DATOS CORRECTAMENTE");
        } catch (SQLException e) {
            System.out.println("MENSAJE SQL: " + e.getMessage());
            throw e; // Para asegurarnos de que el error siga su curso normal
        }
    }

    // doctor eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoDoctor.eliminar(id);
        dtoDoctor.desconectar();
    }

    // doctor listar
    public String listar(String emailFrom) throws SQLException {
        String doctores = dtoDoctor.listar(emailFrom);
        dtoDoctor.desconectar();
        return doctores;
    }

    // Método para encriptar la contraseña
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Comandos disponibles para la entidad Doctor
    public String getComandos() {
        return dtoDoctor.getComandos();
    }
}
