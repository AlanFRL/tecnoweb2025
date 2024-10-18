package Comunication;

import Comunication.MailVerificationThread;
import Comunication.SendEmail;
import Interfaces.IEmailEventListener;
import Negocio.Interpreter;
import Interfaces.ITokenEventListener;
import Utils.Token;
import Comunication.TokenEvent;
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
