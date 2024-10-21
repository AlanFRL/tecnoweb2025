package Negocio;

import Interfaces.ITokenEventListener;
import Datos.TokenCommand;
import Utils.Token;
import Comunication.TokenEvent;

public class Interpreter implements Runnable {

    private ITokenEventListener listener;
    private Analex analex;

    private String command;
    private String sender;

    public Interpreter(String command, String sender) {
        this.command = command;
        this.sender = sender;
    }

    public ITokenEventListener getListener() {
        return listener;
    }

    public void setListener(ITokenEventListener listener) {
        this.listener = listener;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private void filterEvent(TokenCommand token_command) {
        TokenEvent token_event = new TokenEvent(this, sender);
        token_event.setAction(token_command.getAction());

        int count_params = token_command.countParams();
        for (int i = 0; i < count_params; i++) {
            int pos = token_command.getParams(i);
            token_event.addParams(analex.getParam(pos));
        }
        boolean sw = false;

        switch (token_command.getName()) {
            case Token.EMPLEADO:
                listener.empleado(token_event);
                sw = true;
                break;
            case Token.DOCTOR:
                listener.doctor(token_event);
                sw = true;
                break;
            case Token.PACIENTE:
                listener.paciente(token_event);
                sw = true;
                break;
            case Token.RESPONSABLE:
                listener.responsable(token_event);
                sw = true;
                break;
            case Token.PRODUCTO:
                listener.producto(token_event);
                sw = true;
                break;
            case Token.SALA:
                listener.sala(token_event);
                sw = true;
                break;
            case Token.TRATAMIENTO:
                listener.tratamiento(token_event);
                sw = true;
                break;
            case Token.ASIGNACION:
                listener.asignacion(token_event);
                sw = true;
                break;
            case Token.SEGUIMIENTO:
                listener.seguimiento(token_event);
                sw = true;
                break;
            case Token.PAGO:
                listener.pago(token_event);
                sw = true;
                break;
            case Token.REPORTE:
                listener.reporte(token_event);
                sw = true;
                break;
            case Token.CONSULTA:
                listener.consulta(token_event);
                sw = true;
                break;
            case Token.VACUNA:
                listener.vacuna(token_event);
                sw = true;
                break;
            case Token.ASOCIAR:
                listener.asociar(token_event);
                sw = true;
                break;

        }
        if (!sw) {
            listener.posibleserrores(token_event);
        }

    }

    private void tokenError(Token token, String error) {
        TokenEvent token_event = new TokenEvent(this, sender);
        token_event.setAction(token.getAttribute());
        token_event.addParams(command);
        token_event.addParams(error);
        listener.error(token_event);
    }

    @Override
    public void run() {
        analex = new Analex(command);
        TokenCommand token_command = new TokenCommand();
        Token token;

        while ((token = analex.Preanalisis()).getName() != Token.END && token.getName() != Token.ERROR) {
            if (token.getName() == Token.CU) {
                token_command.setName(token.getAttribute());// id del CU
            } else if (token.getName() == Token.ACTION) {
                token_command.setAction(token.getAttribute());// id de la accion
            } else if (token.getName() == Token.PARAMS) {
                token_command.addParams(token.getAttribute());// la posicion del parametro en el tsp
            }
            analex.next();
        }

        if (token.getName() == Token.END) {
            filterEvent(token_command);// se analizo el comando con exito            
        } else if (token.getName() == Token.ERROR) {
            tokenError(token, analex.lexeme()); // se produjo un error en el analisis            
        }

    }

}
