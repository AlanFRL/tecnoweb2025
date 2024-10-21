package Negocio;

import Datos.DtoConsulta;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class NegConsulta {

    private DtoConsulta dtoConsulta;

    public NegConsulta() {
        this.dtoConsulta = new DtoConsulta();
    }

    // consulta agregar[id_paciente; id_empleado; fecha; motivo]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int patientId = Integer.parseInt(parametros.get(0));
            int employeeId = Integer.parseInt(parametros.get(1));
            String date = parametros.get(2);
            String reason = parametros.get(3);

            // Llamar al método agregar del DtoConsulta
            dtoConsulta.agregar(patientId, employeeId, date, reason);
            dtoConsulta.desconectar();
            System.out.println("Consulta agregada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // consulta modificar[id; id_paciente; id_empleado; precio; fecha; motivo]
    public void modificar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int id = Integer.parseInt(parametros.get(0));
            int patientId = Integer.parseInt(parametros.get(1));
            int employeeId = Integer.parseInt(parametros.get(2));
            float price = Float.parseFloat(parametros.get(3));
            String date = parametros.get(4);
            String reason = parametros.get(5);

            // Llamar al método modificar del DtoConsulta
            dtoConsulta.modificar(id, patientId, employeeId, price, date, reason);
            dtoConsulta.desconectar();
            System.out.println("Consulta modificada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al modificar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // consulta eliminar[id]
    public void eliminar(int id) throws SQLException {
        try {
            // Llamar al método eliminar del DtoConsulta
            dtoConsulta.eliminar(id);
            dtoConsulta.desconectar();
            System.out.println("Consulta eliminada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar la consulta: " + e.getMessage());
            throw e;
        }
    }

    // consulta listar
    public String listar(String emailFrom) throws SQLException {
        try {
            // Llamar al método listar del DtoConsulta
            String consultas = dtoConsulta.listar(emailFrom);
            dtoConsulta.desconectar();
            return consultas;
        } catch (SQLException e) {
            System.out.println("Error al listar las consultas: " + e.getMessage());
            throw e;
        }
    }

    // consulta obtener[id]
    public String obtenerPorId(int id) throws SQLException {
        try {
            // Llamar al método obtenerConsultaPorId del DtoConsulta
            String consulta = dtoConsulta.obtenerConsultaPorId(id);
            dtoConsulta.desconectar();
            return consulta;
        } catch (SQLException e) {
            System.out.println("Error al obtener la consulta: " + e.getMessage());
            throw e;
        }
    }

    // Comandos disponibles para la entidad Consulta
    public String getComandos() {
        return dtoConsulta.getComandos();
    }
}
