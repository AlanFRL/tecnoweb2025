package Comunication;

import Comunication.MailVerificationThread;
import Comunication.SendEmail;
import Interfaces.IEmailEventListener;
import Negocio.Interpreter;
import Interfaces.ITokenEventListener;
import Utils.Token;
import Comunication.TokenEvent;
import Negocio.NegDoctor;
import Negocio.NegEmpleado;
import Negocio.NegPaciente;
import Negocio.NegResponsable;
import Negocio.NegUsuario;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import Utils.Email;

public class Home {

    private static String mensaje;

    public static String getMensaje() {
        return mensaje;
    }

    public static void setMensaje(String mensaje) {
        Home.mensaje = mensaje;
    }

    public static void Iniciar() {
        MailVerificationThread mail = new MailVerificationThread();
        mail.setEmailEventListener(new IEmailEventListener() {
            @Override
            public void onReceiveEmailEvent(List<Email> emails) {
                for (Email email : emails) {

                    interprete(email);
                }
            }
        });
        Thread thread = new Thread(mail);
        thread.setName("Mail Verification Thread");
        thread.start();
    }

    public static void interprete(Email email) {
        SendEmail respuesta = new SendEmail();

        NegUsuario usuario = new NegUsuario();
        NegDoctor doctor = new NegDoctor();
        NegEmpleado empleado = new NegEmpleado();
        NegPaciente paciente = new NegPaciente();
        NegResponsable responsable = new NegResponsable();

        Interpreter interpreter = new Interpreter(email.getSubject().toLowerCase(), email.getFrom());
        String emailFrom = interpreter.getSender();
        interpreter.setListener(new ITokenEventListener() {

            @Override
            public void empleado(TokenEvent event) {
                System.out.println("CU: EMPLEADO");
                System.out.println(event);
                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = empleado.listar(emailFrom);
                            System.out.println(lista);
                            System.out.println("listar ok");
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else {
                        if (event.getAction() == Token.AGREGAR) {
                            if (event.getParams().size() == 6) { // ci, nombre, password, teléfono, dirección, ocupacion
                                System.out.println("Cantidad de parámetros recibidos: " + event.getParams().size());
                                for (String param : event.getParams()) {
                                    System.out.println("Parámetro: " + param);
                                }
                                empleado.agregar(event.getParams());
                                System.out.println("agregación ok");
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                    return;
                                }
                            } else {
                                System.out.println("demasiados parámetros");
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    return;
                                }
                            }

                        } else {
                            if (event.getAction() == Token.MODIFICAR) {
                                if (event.getParams().size() == 7) { // id, ci, nombre, password, teléfono, dirección, ocupacion
                                    empleado.modificar(event.getParams());
                                    System.out.println("modificación ok");
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                        return;
                                    }
                                } else {
                                    System.out.println("demasiados parámetros");
                                    respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        return;
                                    }
                                }
                            } else {
                                if (event.getAction() == Token.ELIMINAR) {
                                    if (event.getParams().size() == 1) {
                                        empleado.eliminar(Integer.parseInt(event.getParams().get(0)));
                                        System.out.println("eliminación ok");
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                            return;
                                        }
                                    } else {
                                        System.out.println("demasiados parámetros");
                                        respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                            return;
                                        }
                                    }
                                } else {
                                    if (event.getAction() == Token.HELP) {
                                        if (event.getParams().size() == 0) {
                                            System.out.println(empleado.getComandos());
                                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(empleado.getComandos()));
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(empleado.getComandos()));
                                                return;
                                            }
                                        } else {
                                            System.out.println("help no necesita parámetros");
                                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                                return;
                                            }
                                        }
                                    } else {
                                        System.out.println("ACCIÓN DESCONOCIDA DE CU: EMPLEADO");
                                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                }
            }

            @Override
            public void doctor(TokenEvent event) {
                System.out.println("CU: DOCTOR");
                System.out.println(event);
                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = doctor.listar(emailFrom);
                            System.out.println(lista);
                            System.out.println("listar ok");
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else {
                        if (event.getAction() == Token.AGREGAR) {
                            if (event.getParams().size() == 6) { // ci, nombre, password, teléfono, dirección, número_ss
                                doctor.agregar(event.getParams());
                                System.out.println("agregación ok");
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                    return;
                                }
                            } else {
                                System.out.println("demasiados parámetros");
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    return;
                                }
                            }

                        } else {
                            if (event.getAction() == Token.MODIFICAR) {
                                if (event.getParams().size() == 7) { // id, ci, nombre, password, teléfono, dirección, número_ss
                                    doctor.modificar(event.getParams());
                                    System.out.println("modificación ok");
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                        return;
                                    }
                                } else {
                                    System.out.println("demasiados parámetros");
                                    respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        return;
                                    }
                                }
                            } else {
                                if (event.getAction() == Token.ELIMINAR) {
                                    if (event.getParams().size() == 1) {
                                        doctor.eliminar(Integer.parseInt(event.getParams().get(0)));
                                        System.out.println("eliminación ok");
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                            return;
                                        }
                                    } else {
                                        System.out.println("demasiados parámetros");
                                        respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                            return;
                                        }
                                    }
                                } else {
                                    if (event.getAction() == Token.HELP) {
                                        if (event.getParams().size() == 0) {
                                            System.out.println(doctor.getComandos());
                                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(doctor.getComandos()));
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(doctor.getComandos()));
                                                return;
                                            }
                                        } else {
                                            System.out.println("help no necesita parámetros");
                                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                                return;
                                            }
                                        }
                                    } else {
                                        System.out.println("ACCIÓN DESCONOCIDA DE CU: DOCTOR");
                                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                }
            }

            @Override
            public void paciente(TokenEvent event) {
                System.out.println("CU: PACIENTE");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = paciente.listar(emailFrom);
                            System.out.println(lista);
                            System.out.println("listar ok");
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.AGREGAR) {
                        if (event.getParams().size() == 8) { // ci, nombre, direccion, genero, fechaNacimiento, tipoSangre, factorRH, proxyId
                            paciente.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.MODIFICAR) {
                        if (event.getParams().size() == 9) { // id, ci, nombre, direccion, genero, fechaNacimiento, tipoSangre, factorRH, proxyId
                            paciente.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.ELIMINAR) {
                        if (event.getParams().size() == 1) {
                            paciente.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.HELP) {
                        if (event.getParams().size() == 0) {
                            System.out.println(paciente.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(paciente.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(paciente.getComandos()));
                                return;
                            }
                        } else {
                            System.out.println("help no necesita parámetros");
                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                return;
                            }
                        }
                    } else {
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: PACIENTE");
                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                        if (true) {
                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                            return;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                }
            }

            @Override
            public void responsable(TokenEvent event) {
                System.out.println("CU: RESPONSABLE");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = responsable.listar(emailFrom);
                            System.out.println(lista);
                            System.out.println("listar ok");
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.AGREGAR) {
                        if (event.getParams().size() == 7) { // ci, nombre, direccion, genero, fechaNacimiento, telefono, ocupacion
                            responsable.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.MODIFICAR) {
                        if (event.getParams().size() == 8) { // id, ci, nombre, direccion, genero, fechaNacimiento, telefono, ocupacion
                            responsable.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.ELIMINAR) {
                        if (event.getParams().size() == 1) {
                            responsable.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                return;
                            }
                        } else {
                            System.out.println("demasiados parámetros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                        }
                    } else if (event.getAction() == Token.HELP) {
                        if (event.getParams().size() == 0) {
                            System.out.println(responsable.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(responsable.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(responsable.getComandos()));
                                return;
                            }
                        } else {
                            System.out.println("help no necesita parámetros");
                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                return;
                            }
                        }
                    } else {
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: RESPONSABLE");
                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                        if (true) {
                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                            return;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                }
            }

            @Override
            public void producto(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void sala(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void asignacion(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void seguimiento(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void pago(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void reporte(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            // CONTROL DE ERRORES
            @Override
            public void error(TokenEvent event) {
                System.out.println("COMANDO DESCONOCIDO");
                System.out.println(event);
                if (true) {
                    JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                    return;
                }
                respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");

            }

            @Override
            public void posibleserrores(TokenEvent event) {
                String sms = "";
                switch (event.getAction()) {
                    case Token.LISTAR:
                        sms = "COMANDO DESCONOCIDO";
                        break;
                    case Token.AGREGAR:
                        sms = "COMANDO DESCONOCIDO";
                        break;
                    case Token.MODIFICAR:
                        sms = "COMANDO DESCONOCIDO";
                        break;
                    case Token.ELIMINAR:
                        sms = "COMANDO DESCONOCIDO";
                        break;
                    case Token.MOSTRAR:
                        sms = "COMANDO DESCONOCIDO";
                        break;
                    case Token.HELP:
                        if (true) {
                            sms = respuesta.mensajeTexto(emailFrom, "\r\n");
                            break;
                        }
                        sms = respuesta.mensajeTexto(emailFrom, "\n");
                        break;
                    default:
                        sms = "COMANDO DESCONOCIDO";
                }

                if (sms != " ") {
                    if (true) {
                        JOptionPane.showMessageDialog(null, sms);
                        return;
                    }
                    respuesta.responseUser(email.getFrom(), sms);
                    return;
                }
                System.out.println("no envio nada");
            }

            @Override
            public void tratamiento(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void consulta(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void vacuna(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void asociar(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        }
        );

        Thread thread = new Thread(interpreter);
        thread.setName("Interpreter Thread");
        thread.start();
    }

}
