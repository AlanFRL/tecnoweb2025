package Negocio;

import Datos.DtoPaciente;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class NegPaciente {

    private DtoPaciente dtoPaciente;

    public NegPaciente() {
        this.dtoPaciente = new DtoPaciente();
    }

    // paciente agregar[ci; nombre; direccion; genero; fechaNacimiento; tipoSangre; factorRH; proxyId]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            dtoPaciente.agregar(
                parametros.get(0), // ci
                parametros.get(1), // nombre
                parametros.get(2), // direccion
                parametros.get(3).charAt(0), // genero
                parametros.get(4), // fechaNacimiento
                parametros.get(5), // tipoSangre
                parametros.get(6).charAt(0), // factorRH
                Integer.parseInt(parametros.get(7)) // proxyId
            );
            dtoPaciente.desconectar();
        } catch (SQLException e) {
            System.err.println("Error al agregar Paciente: " + e.getMessage());
            throw e;
        }
    }

    // paciente modificar[id; ci; nombre; direccion; genero; fechaNacimiento; tipoSangre; factorRH; proxyId]
    public void modificar(List<String> parametros) throws SQLException, ParseException {
        try {
            dtoPaciente.modificar(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // ci
                parametros.get(2), // nombre
                parametros.get(3), // direccion
                parametros.get(4).charAt(0), // genero
                parametros.get(5), // fechaNacimiento
                parametros.get(6), // tipoSangre
                parametros.get(7).charAt(0), // factorRH
                Integer.parseInt(parametros.get(8)) // proxyId
            );
            System.out.println("SE MODIFICÓ LOS DATOS DEL PACIENTE CORRECTAMENTE");
        } catch (SQLException e) {
            System.err.println("Error al modificar Paciente: " + e.getMessage());
            throw e;
        }
    }

    // paciente eliminar[id]
    public void eliminar(int id) throws SQLException {
        dtoPaciente.eliminar(id);
        dtoPaciente.desconectar();
    }

    // paciente listar
    public String listar(String emailFrom) throws SQLException {
        String pacientes = dtoPaciente.listar(emailFrom);
        dtoPaciente.desconectar();
        return pacientes;
    }

    // Comandos disponibles para la entidad Paciente
    public String getComandos() {
        return dtoPaciente.getComandos();
    }
}
