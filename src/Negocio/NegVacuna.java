package Negocio;

import Datos.DtoVacuna;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class NegVacuna {

    private DtoVacuna dtoVacuna;

    public NegVacuna() {
        this.dtoVacuna = new DtoVacuna();
    }

    // vacuna agregar[id_paciente; id_empleado; nombre]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int patientId = Integer.parseInt(parametros.get(0));
            int employeeId = Integer.parseInt(parametros.get(1));
            String name = parametros.get(2);

            // Llamar al método agregar del DtoVacuna
            dtoVacuna.agregar(patientId, employeeId, name);
            dtoVacuna.desconectar();
            System.out.println("Vacuna agregada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // vacuna modificar[id; id_paciente; id_empleado; precio; nombre]
    public void modificar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int id = Integer.parseInt(parametros.get(0));
            int patientId = Integer.parseInt(parametros.get(1));
            int employeeId = Integer.parseInt(parametros.get(2));
            float price = Float.parseFloat(parametros.get(3));
            String name = parametros.get(4);

            // Llamar al método modificar del DtoVacuna
            dtoVacuna.modificar(id, patientId, employeeId, price, name);
            dtoVacuna.desconectar();
            System.out.println("Vacuna modificada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al modificar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // vacuna eliminar[id]
    public void eliminar(int id) throws SQLException {
        try {
            // Llamar al método eliminar del DtoVacuna
            dtoVacuna.eliminar(id);
            dtoVacuna.desconectar();
            System.out.println("Vacuna eliminada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // vacuna listar
    public String listar(String emailFrom) throws SQLException {
        try {
            // Llamar al método listar del DtoVacuna
            String vacunas = dtoVacuna.listar(emailFrom);
            dtoVacuna.desconectar();
            return vacunas;
        } catch (SQLException e) {
            System.out.println("Error al listar las vacunas: " + e.getMessage());
            throw e;
        }
    }

    // vacuna obtener[id]
    public String obtenerPorId(int id) throws SQLException {
        try {
            // Llamar al método obtenerVacunaPorId del DtoVacuna
            String vacuna = dtoVacuna.obtenerVacunaPorId(id);
            dtoVacuna.desconectar();
            return vacuna;
        } catch (SQLException e) {
            System.out.println("Error al obtener la vacuna: " + e.getMessage());
            throw e;
        }
    }

    // Comandos disponibles para la entidad Vacuna
    public String getComandos() {
        return dtoVacuna.getComandos();
    }
}
