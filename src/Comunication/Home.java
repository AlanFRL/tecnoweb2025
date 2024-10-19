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

        Interpreter interpreter = new Interpreter(email.getSubject().toLowerCase(), email.getFrom());
        String emailFrom = interpreter.getSender();
        interpreter.setListener(new ITokenEventListener() {

            @Override
            public void usuario(TokenEvent event) {
                System.out.println("CU: USUARIO");
                System.out.println(event);
                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = usuario.listar(emailFrom);
                            System.out.println(lista);
                            System.out.println("listar ok");
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                        } else {
                            System.out.println("demasiado parametros");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN  LA CANTIDAD DE PARAMETROS");
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), "ERROR EN  LA CANTIDAD DE PARAMETROS");
                        }
                    } else {
                        if (event.getAction() == Token.AGREGAR) {
                            if (event.getParams().size() == 7) {
                                usuario.agregar(event.getParams());
                                System.out.println("agregacion ok");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGO LOS DATOS CORRECTAMENTE");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "SE AGREGO LOS DATOS CORRECTAMENTE");
                            } else {
                                System.out.println("demasiado parametros");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARAMETROS");
                            }

                        } else {
                            if (event.getAction() == Token.MODIFICAR) {
                                if (event.getParams().size() == 8) {
                                    usuario.modificar(event.getParams());
                                    System.out.println("modif ok");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICO LOS DATOS CORRECTAMENTE");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICO LOS DATOS CORRECTAMENTE");
                                } else {
                                    System.out.println("demasiado parametros");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARAMETROS");
                                }
                            } else {
                                if (event.getAction() == Token.ELIMINAR) {
                                    if (event.getParams().size() == 1) {
                                        usuario.eliminar(Integer.parseInt(event.getParams().get(0)));
                                        System.out.println("delete ok");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINO LOS DATOS CORRECTAMENTE");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINO LOS DATOS CORRECTAMENTE");
                                    } else {
                                        System.out.println("demasiado parametros");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARAMETROS");
                                    }
                                } else {
                                    if (event.getAction() == Token.HELP) {
                                        if (event.getParams().size() == 0) {
                                            System.out.println(usuario.getComandos());
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(usuario.getComandos()));
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(usuario.getComandos()));
                                        } else {
                                            System.out.println("help No necesita parametros");
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                        }
                                    } else {
                                        System.out.println("ACCION DESCONOCIDA DE CU: USUARIO");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL : " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                }
            }

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
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                        } else {
                            System.out.println("demasiados parámetros");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
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
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                            } else {
                                System.out.println("demasiados parámetros");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            }

                        } else {
                            if (event.getAction() == Token.MODIFICAR) {
                                if (event.getParams().size() == 7) { // id, ci, nombre, password, teléfono, dirección, ocupacion
                                    empleado.modificar(event.getParams());
                                    System.out.println("modificación ok");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                } else {
                                    System.out.println("demasiados parámetros");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                }
                            } else {
                                if (event.getAction() == Token.ELIMINAR) {
                                    if (event.getParams().size() == 1) {
                                        empleado.eliminar(Integer.parseInt(event.getParams().get(0)));
                                        System.out.println("eliminación ok");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                    } else {
                                        System.out.println("demasiados parámetros");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    }
                                } else {
                                    if (event.getAction() == Token.HELP) {
                                        if (event.getParams().size() == 0) {
                                            System.out.println(empleado.getComandos());
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(empleado.getComandos()));
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(empleado.getComandos()));
                                        } else {
                                            System.out.println("help no necesita parámetros");
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                        }
                                    } else {
                                        System.out.println("ACCIÓN DESCONOCIDA DE CU: EMPLEADO");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
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
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                        } else {
                            System.out.println("demasiados parámetros");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                return;
                            }
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                        }
                    } else {
                        if (event.getAction() == Token.AGREGAR) {
                            if (event.getParams().size() == 6) { // ci, nombre, password, teléfono, dirección, número_ss
                                doctor.agregar(event.getParams());
                                System.out.println("agregación ok");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS CORRECTAMENTE");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS CORRECTAMENTE");
                            } else {
                                System.out.println("demasiados parámetros");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                            }

                        } else {
                            if (event.getAction() == Token.MODIFICAR) {
                                if (event.getParams().size() == 7) { // id, ci, nombre, password, teléfono, dirección, número_ss
                                    doctor.modificar(event.getParams());
                                    System.out.println("modificación ok");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS CORRECTAMENTE");
                                } else {
                                    System.out.println("demasiados parámetros");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                        return;
                                    }
                                    respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                }
                            } else {
                                if (event.getAction() == Token.ELIMINAR) {
                                    if (event.getParams().size() == 1) {
                                        doctor.eliminar(Integer.parseInt(event.getParams().get(0)));
                                        System.out.println("eliminación ok");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS CORRECTAMENTE");
                                    } else {
                                        System.out.println("demasiados parámetros");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARÁMETROS");
                                    }
                                } else {
                                    if (event.getAction() == Token.HELP) {
                                        if (event.getParams().size() == 0) {
                                            System.out.println(doctor.getComandos());
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(doctor.getComandos()));
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(doctor.getComandos()));
                                        } else {
                                            System.out.println("help no necesita parámetros");
                                            if (true) {
                                                JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                                return;
                                            }
                                            respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                        }
                                    } else {
                                        System.out.println("ACCIÓN DESCONOCIDA DE CU: DOCTOR");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                            return;
                                        }
                                        respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        return;
                    }
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                }
            }

            @Override
            public void paciente(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void responsable(TokenEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
            public void servicio(TokenEvent event) {
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

        }
        );

        Thread thread = new Thread(interpreter);
        thread.setName("Interpreter Thread");
        thread.start();
    }

}
