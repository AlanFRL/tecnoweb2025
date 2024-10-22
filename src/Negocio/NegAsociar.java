package Negocio;

import Datos.DtoAsociar;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class NegAsociar {

    private DtoAsociar dtoAsociar;

    public NegAsociar() {
        this.dtoAsociar = new DtoAsociar();
    }

    // asociar agregar[id_comida; id_tratamiento; cantidad]
    public void agregar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int mealId = Integer.parseInt(parametros.get(0));
            int treatmentId = Integer.parseInt(parametros.get(1));
            int quantity = Integer.parseInt(parametros.get(2));

            // Llamar al método agregar del DtoAsociar
            dtoAsociar.agregar(mealId, treatmentId, quantity);
            dtoAsociar.desconectar();
            System.out.println("Asociación agregada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al agregar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // asociar modificar[id_comida; id_tratamiento; nueva_cantidad]
    public void modificar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int mealId = Integer.parseInt(parametros.get(0));
            int treatmentId = Integer.parseInt(parametros.get(1));
            int newQuantity = Integer.parseInt(parametros.get(2));

            // Llamar al método modificar del DtoAsociar
            dtoAsociar.modificar(mealId, treatmentId, newQuantity);
            dtoAsociar.desconectar();
            System.out.println("Asociación modificada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al modificar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // asociar eliminar[id_comida; id_tratamiento]
    public void eliminar(List<String> parametros) throws SQLException {
        try {
            // Convertir los parámetros
            int mealId = Integer.parseInt(parametros.get(0));
            int treatmentId = Integer.parseInt(parametros.get(1));

            // Llamar al método eliminar del DtoAsociar
            dtoAsociar.eliminar(mealId, treatmentId);
            dtoAsociar.desconectar();
            System.out.println("Asociación eliminada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar la asociación: " + e.getMessage());
            throw e;
        }
    }

    // asociar listar
    public String listar(String emailFrom) throws SQLException {
        try {
            // Llamar al método listar del DtoAsociar
            String asociaciones = dtoAsociar.listar(emailFrom);
            dtoAsociar.desconectar();
            return asociaciones;
        } catch (SQLException e) {
            System.out.println("Error al listar las asociaciones: " + e.getMessage());
            throw e;
        }
    }

    // Comandos disponibles para la entidad Asociar
    public String getComandos() {
        return dtoAsociar.getComandos();
    }
}
