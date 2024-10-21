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
                Float.parseFloat(parametros.get(2)), // total
                parametros.get(3).toUpperCase().charAt(0) // paymentType (convertido a mayúscula)
        );
        dtoPago.desconectar();
    }

// pago modificar [id, service_id, fecha, total, payment_type]
    public void modificar(List<String> parametros) throws SQLException {
        dtoPago.modificar(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)), // service_id
                parametros.get(2), // fecha
                Float.parseFloat(parametros.get(3)), // total
                parametros.get(4).charAt(0) // payment_type, tomamos solo el primer carácter
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
