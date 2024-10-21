package Negocio;

import Datos.DtoPago;
import java.sql.SQLException;
import java.util.List;

public class NegPago {

    private DtoPago dtoPago;

    public NegPago() {
        this.dtoPago = new DtoPago();
    }

    // Método para agregar un nuevo pago
    public void agregar(List<String> parametros) throws SQLException {
        dtoPago.agregar(
                Integer.parseInt(parametros.get(0)), // serviceId
                parametros.get(1), // fecha
                parametros.get(2).toUpperCase().charAt(0) // paymentType (convertido a mayúscula)
        );
        dtoPago.desconectar();
    }

// Método para modificar un pago existente
    public void modificar(List<String> parametros) throws SQLException {
        dtoPago.modificar(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)), // service_id
                parametros.get(2), // fecha
                parametros.get(3).toUpperCase().charAt(0) // paymentType (convertido a mayúscula)
        );
        dtoPago.desconectar();
    }

    // pago eliminar [id]
    public void eliminar(int id) throws SQLException {
        dtoPago.eliminar(id);
        dtoPago.desconectar();
    }

    // pago listar
    public String listar(String emailFrom) throws SQLException {
        String pagos = dtoPago.listar(emailFrom);
        dtoPago.desconectar();
        return pagos;
    }

    // Comandos disponibles para la entidad Pago
    public String getComandos() {
        return dtoPago.getComandos();
    }
}