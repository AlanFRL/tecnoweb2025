
package Interfaces;

import Comunication.TokenEvent;


public interface ITokenEventListener {
    // CASOS DE USO\
    // Usuarios
    void empleado(TokenEvent event);
    void doctor(TokenEvent event);
    // Personas
    void paciente(TokenEvent event);
    void responsable(TokenEvent event);
    
    void producto(TokenEvent event);
    void sala(TokenEvent event);    
    void tratamiento(TokenEvent event);
    void ficha(TokenEvent event);
    void seguimiento(TokenEvent event);
    void pago(TokenEvent event);
    void reporte(TokenEvent event);
    void consulta(TokenEvent event);
    void vacuna(TokenEvent event);
    void asociar(TokenEvent event);
    
    // LAVANDERÍA - CU1: Gestión de Usuarios
    void propietario(TokenEvent event);
    void cliente(TokenEvent event);
    void proveedor(TokenEvent event);

    
    void error(TokenEvent event);
    void posibleserrores(TokenEvent event);

}
