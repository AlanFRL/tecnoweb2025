package Negocio;

import Datos.DtoTratamiento;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class NegTratamiento {

    private DtoTratamiento dtoTratamiento;

    public NegTratamiento() {
        this.dtoTratamiento = new DtoTratamiento();
    }

    // tratamiento agregar[id_paciente; id_empleado; origen; id_habitacion]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int patientId = Integer.parseInt(parametros.get(0));
            int employeeId = Integer.parseInt(parametros.get(1));
            String origin = parametros.get(2);
            int roomId = Integer.parseInt(parametros.get(3));

            // Llamar al método agregar del DtoTratamiento
            dtoTratamiento.agregar(patientId, employeeId, origin, roomId);
            dtoTratamiento.desconectar();
            System.out.println("Tratamiento agregado exitosamente con estado 'activo'.");
        } catch (SQLException e) {
            System.out.println("Error al agregar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // tratamiento modificar[id; id_paciente; id_empleado; precio; origen; id_habitacion]
    public void modificar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int id = Integer.parseInt(parametros.get(0));
            int patientId = Integer.parseInt(parametros.get(1));
            int employeeId = Integer.parseInt(parametros.get(2));
            float price = Float.parseFloat(parametros.get(3));
            String origin = parametros.get(4);
            int roomId = Integer.parseInt(parametros.get(5));

            // Llamar al método modificar del DtoTratamiento
            dtoTratamiento.modificar(id, patientId, employeeId, price, origin, roomId);
            dtoTratamiento.desconectar();
            System.out.println("Tratamiento modificado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al modificar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // tratamiento eliminar[id]
    public void eliminar(int id) throws SQLException {
        try {
            // Llamar al método eliminar del DtoTratamiento
            dtoTratamiento.eliminar(id);
            dtoTratamiento.desconectar();
            System.out.println("Tratamiento eliminado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // tratamiento alta[id]
    public void alta(int id) throws SQLException {
        try {
            // Llamar al método alta del DtoTratamiento
            dtoTratamiento.alta(id);
            dtoTratamiento.desconectar();
            System.out.println("El estado del tratamiento se cambió a 'alta' y se liberó la habitación.");
        } catch (SQLException e) {
            System.out.println("Error al dar de alta el tratamiento: " + e.getMessage());
            throw e;
        }
    }

    // tratamiento listar
    public String listar(String emailFrom) throws SQLException {
        try {
            // Llamar al método listar del DtoTratamiento
            String tratamientos = dtoTratamiento.listar(emailFrom);
            dtoTratamiento.desconectar();
            return tratamientos;
        } catch (SQLException e) {
            System.out.println("Error al listar los tratamientos: " + e.getMessage());
            throw e;
        }
    }

    // Comandos disponibles para la entidad Tratamiento
    public String getComandos() {
        return dtoTratamiento.getComandos();
    }
}
