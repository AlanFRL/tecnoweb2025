package Utils;

public class Token {

    private int name;// si es CU, ACTION o ERROR
    private int attribute; // que tipo ya sea CU, ACTION o ERROR

    public static final int CU = 0;
    public static final int ACTION = 1;
    public static final int PARAMS = 2;
    public static final int END = 3;
    public static final int ERROR = 4;
    //CU    
    public static final int EMPLEADO = 100;
    public static final int DOCTOR = 101;
    public static final int PACIENTE = 102;
    public static final int RESPONSABLE = 103;
    public static final int PRODUCTO = 104;
    public static final int SALA = 105;
    public static final int TRATAMIENTO = 106;
    public static final int FICHA = 107;
    public static final int SEGUIMIENTO = 108;
    public static final int PAGO = 109;
    public static final int REPORTE = 110;
    public static final int CONSULTA = 111;
    public static final int VACUNA = 112;
    // PAPILLA
    public static final int ASOCIAR = 113;

    //ACCIONES        
    public static final int ELIMINAR = 200;
    public static final int MODIFICAR = 201;
    public static final int LISTAR = 202;
    public static final int AGREGAR = 203;
    public static final int MOSTRAR = 204;
    public static final int GENERAR = 205;
    public static final int HELP = 206;
    public static final int REGISTRAR = 209;

    public static final int ERROR_COMMAND = 300;
    public static final int ERROR_CHARACTER = 301;

    //constantes literales para realizar un efecto de impresión
    public static final String LEXEME_CU = "caso de uso";
    public static final String LEXEME_ACTION = "action";
    public static final String LEXEME_PARAMS = "params";
    public static final String LEXEME_END = "end";
    public static final String LEXEME_ERROR = "error";

    //Titulos de casos de uso con string    
    public static final String LEXEME_EMPLEADO = "empleado";
    public static final String LEXEME_DOCTOR = "doctor";
    public static final String LEXEME_PACIENTE = "paciente";
    public static final String LEXEME_RESPONSABLE = "responsable";
    public static final String LEXEME_PRODUCTO = "producto";
    public static final String LEXEME_SALA = "sala";
    public static final String LEXEME_TRATAMIENTO = "tratamiento";
    public static final String LEXEME_FICHA = "ficha";
    public static final String LEXEME_SEGUIMIENTO = "seguimiento";
    public static final String LEXEME_PAGO = "pago";
    public static final String LEXEME_REPORTE = "reporte";
    public static final String LEXEME_CONSULTA = "consulta";
    public static final String LEXEME_VACUNA = "vacuna";
    public static final String LEXEME_ASOCIAR = "asociar";

    //Titulos de las acciones generales en string
    public static final String LEXEME_ELIMINAR = "eliminar";
    public static final String LEXEME_MODIFICAR = "modificar";
    public static final String LEXEME_LISTAR = "listar";
    public static final String LEXEME_AGREGAR = "agregar";
    public static final String LEXEME_MOSTRAR = "mostrar";
    public static final String LEXEME_GENERAR = "generar";
    public static final String LEXEME_REGISTRAR = "registrar";
    public static final String LEXEME_HELP = "help";

    public static final String LEXEME_ERROR_COMMAND = "UNKNOWN COMMAND";
    public static final String LEXEME_ERROR_CHARACTER = "UNKNOWN CHARACTER";

    public Token() {
    }

    public Token(String token) {
        int id = findByLexeme(token);
        if (id != -1) {
            if (100 <= id && id < 200) {
                this.name = CU;
                this.attribute = id;
            } else if (200 <= id && id < 300) {
                this.name = ACTION;
                this.attribute = id;
            }
        } else {
            this.name = ERROR;
            this.attribute = ERROR_COMMAND;
            System.err.println("Class Token.Constructor dice: \n "
                    + " El lexema enviado al constructor no es reconocido como un token \n"
                    + "Lexema: " + token);
        }
    }

    public Token(int name) {
        this.name = name;
    }

    public Token(int name, int attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    // Setters y Getters
    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        if (0 <= name && name <= 1) {
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        } else if (name == 2) {
            return "< " + getStringToken(name) + " , " + attribute + ">";
        } else if (3 == name) {
            return "< " + getStringToken(name) + " , " + "_______ >";
        } else if (name == 4) {
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        }
        return "< TOKEN , DESCONOCIDO>";
    }

    /**
     * Devuelve el valor literal del token enviado Si no lo encuentra retorna N:
     * token.
     *
     * @param token
     * @return
     */
    public String getStringToken(int token) {
        switch (token) {
            case CU:
                return LEXEME_CU;
            case ACTION:
                return LEXEME_ACTION;
            case PARAMS:
                return LEXEME_PARAMS;
            case END:
                return LEXEME_END;
            case ERROR:
                return LEXEME_ERROR;

            //CU            
            case EMPLEADO:
                return LEXEME_EMPLEADO;
            case DOCTOR:
                return LEXEME_DOCTOR;
            case PACIENTE:
                return LEXEME_PACIENTE;
            case RESPONSABLE:
                return LEXEME_RESPONSABLE;
            case PRODUCTO:
                return LEXEME_PRODUCTO;
            case SALA:
                return LEXEME_SALA;
            case TRATAMIENTO:
                return LEXEME_TRATAMIENTO;
            case FICHA:
                return LEXEME_FICHA;
            case SEGUIMIENTO:
                return LEXEME_SEGUIMIENTO;
            case PAGO:
                return LEXEME_PAGO;
            case REPORTE:
                return LEXEME_REPORTE;
            case CONSULTA:
                return LEXEME_CONSULTA;
            case VACUNA:
                return LEXEME_VACUNA;
            case ASOCIAR:
                return LEXEME_ASOCIAR;

            //ACCION
            case AGREGAR:
                return LEXEME_AGREGAR;
            case ELIMINAR:
                return LEXEME_ELIMINAR;
            case MODIFICAR:
                return LEXEME_MODIFICAR;
            case LISTAR:
                return LEXEME_LISTAR;
            case MOSTRAR:
                return LEXEME_MOSTRAR;
            case GENERAR:
                return LEXEME_GENERAR;
            case HELP:
                return LEXEME_HELP;
            case REGISTRAR:
                return LEXEME_REGISTRAR;

            case ERROR_COMMAND:
                return LEXEME_ERROR_COMMAND;
            case ERROR_CHARACTER:
                return LEXEME_ERROR_CHARACTER;
            default:
                return "N: " + token;
        }
    }

    /**
     * Devuelve el valor numerico del lexema enviado Si no lo encuentra retorna
     * -1.
     *
     * @param lexeme
     * @return
     */
    private int findByLexeme(String lexeme) {
        switch (lexeme) {
            case LEXEME_CU:
                return CU;
            case LEXEME_ACTION:
                return ACTION;
            case LEXEME_PARAMS:
                return PARAMS;
            case LEXEME_END:
                return END;
            case LEXEME_ERROR:
                return ERROR;

            //CU             
            case LEXEME_EMPLEADO:
                return EMPLEADO;
            case LEXEME_DOCTOR:
                return DOCTOR;
            case LEXEME_PACIENTE:
                return PACIENTE;
            case LEXEME_RESPONSABLE:
                return RESPONSABLE;
            case LEXEME_PRODUCTO:
                return PRODUCTO;
            case LEXEME_SALA:
                return SALA;
            case LEXEME_TRATAMIENTO:
                return TRATAMIENTO;
            case LEXEME_FICHA:
                return FICHA;
            case LEXEME_SEGUIMIENTO:
                return SEGUIMIENTO;
            case LEXEME_PAGO:
                return PAGO;
            case LEXEME_REPORTE:
                return REPORTE;
            case LEXEME_CONSULTA:
                return CONSULTA;
            case LEXEME_VACUNA:
                return VACUNA;
            case LEXEME_ASOCIAR:
                return ASOCIAR;

            //ACTION                
            case LEXEME_ELIMINAR:
                return ELIMINAR;
            case LEXEME_MODIFICAR:
                return MODIFICAR;
            case LEXEME_LISTAR:
                return LISTAR;
            case LEXEME_AGREGAR:
                return AGREGAR;
            case LEXEME_MOSTRAR:
                return MOSTRAR;
            case LEXEME_GENERAR:
                return GENERAR;
            case LEXEME_HELP:
                return HELP;
            case LEXEME_REGISTRAR:
                return REGISTRAR;
            case LEXEME_ERROR_COMMAND:
                return ERROR_COMMAND;
            case LEXEME_ERROR_CHARACTER:
                return ERROR_CHARACTER;
            default:
                return -1;
        }
    }
}
