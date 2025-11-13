package Negocio;

import Datos.DtoPropietario;
import java.sql.SQLException;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class NegPropietario {

    private DtoPropietario dtoPropietario;

    public NegPropietario() {
        this.dtoPropietario = new DtoPropietario();
    }

    // propietario agregar [nombre; telefono; email; password; razon_social]
    public void agregar(List<String> parametros) throws SQLException {
        if (parametros.size() != 5) {
            throw new IllegalArgumentException("Se esperaban 5 parámetros para agregar un propietario.");
        }

        String newPassword = encrypt(parametros.get(3)); // Encriptamos la contraseña

        dtoPropietario.agregar(
                parametros.get(0), // nombre
                parametros.get(1), // telefono
                parametros.get(2), // email
                newPassword,        // password
                parametros.get(4)  // razon_social
        );
        dtoPropietario.desconectar();
    }

    // propietario modificar [id_propietario; nombre; telefono; email; password; razon_social]
    public void modificar(List<String> parametros) throws SQLException {
        if (parametros.size() != 6) {
            throw new IllegalArgumentException("Se esperaban 6 parámetros para modificar un propietario.");
        }

        String newPassword = encrypt(parametros.get(4)); // Encriptamos la contraseña

        dtoPropietario.modificar(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // nombre
                parametros.get(2), // telefono
                parametros.get(3), // email
                newPassword,        // password
                parametros.get(5)  // razon_social
        );
        dtoPropietario.desconectar();
    }

    // propietario eliminar [id_propietario]
    public void eliminar(int id) throws SQLException {
        dtoPropietario.eliminar(id);
        dtoPropietario.desconectar();
    }

    // propietario listar
    public String listar(String emailFrom) throws SQLException {
        String propietarios = dtoPropietario.listar(emailFrom);
        dtoPropietario.desconectar();
        return propietarios;
    }

    // Comandos disponibles para la entidad Propietario
    public String getComandos() {
        return dtoPropietario.getComandos();
    }

    // Método para encriptar contraseñas
    private String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
