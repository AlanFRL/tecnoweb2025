package Comunication;

import Comunication.MailVerificationThread;
import Comunication.SendEmail;
import Interfaces.IEmailEventListener;
import Negocio.Interpreter;
import Interfaces.ITokenEventListener;
import Utils.Token;
import Comunication.TokenEvent;
import Negocio.NegAsociar;
import Negocio.NegDoctor;
import Negocio.NegEmpleado;
import Negocio.NegFicha;
import Negocio.NegPaciente;
import Negocio.NegPago;
import Negocio.NegResponsable;
import Negocio.NegSala;
import Negocio.NegTratamiento;
import Negocio.NegUsuario;
import Negocio.NegConsulta;
import Negocio.NegProducto;
import Negocio.NegVacuna;
import Negocio.NegSeguimiento;
import Negocio.NegReporte;
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
        NegTratamiento tratamiento = new NegTratamiento();
        NegConsulta consulta = new NegConsulta();
        NegVacuna vacuna = new NegVacuna();
        NegAsociar asociar = new NegAsociar();
        NegProducto producto = new NegProducto();
        NegSeguimiento seguimiento = new NegSeguimiento();
        NegReporte reporte = new NegReporte();

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
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
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
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
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
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL EMPLEADO CORRECTAMENTE");
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
                                respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
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
                                    respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
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
                                        respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
                                        if (true) {
                                            JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL DOCTOR CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL PACIENTE CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
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
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL RESPONSABLE CORRECTAMENTE");
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
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = producto.listar(event.getSender());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 3) { // name, price, ingredients
                            producto.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
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
                        if (event.getParams().size() == 4) { // id, name, price, ingredients
                            producto.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
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
                        if (event.getParams().size() == 1) { // id
                            producto.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL PRODUCTO CORRECTAMENTE");
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
                            System.out.println(producto.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(producto.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(producto.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: PRODUCTO");
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
            public void sala(TokenEvent event) {
                NegSala sala = new NegSala();
                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = sala.listar(event.getSender());
                            System.out.println(lista);
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
                        if (event.getParams().size() == 2) { // id, capacidad, habitaciones disponibles
                            sala.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DE LA SALA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DE LA SALA CORRECTAMENTE");
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
                        if (event.getParams().size() == 3) { // id, capacidad, habitaciones disponibles
                            sala.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DE LA SALA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DE LA SALA CORRECTAMENTE");
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
                            sala.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DE LA SALA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DE LA SALA CORRECTAMENTE");
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
                            System.out.println(sala.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(sala.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(sala.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: SALA");
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
            public void ficha(TokenEvent event) {
                NegFicha ficha = new NegFicha();
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = ficha.listar(event.getSender());
                            System.out.println(lista);
//                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
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
                        if (event.getParams().size() == 5) { // doctor_id, consult_id, symptoms, diagnosis, notes
                            ficha.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
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
                        if (event.getParams().size() == 5) { // id, doctor_id, consult_id, symptoms, diagnosis, notes
                            ficha.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
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
                            ficha.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DE LA HOJA DE ATENCION/FICHA CORRECTAMENTE");
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
                            System.out.println(ficha.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(ficha.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(ficha.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: FICHA");
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
            public void seguimiento(TokenEvent event) {
                //NegSeguimiento seguimiento = new NegSeguimiento();
                SendEmail respuesta = new SendEmail();
                System.out.println("CU: SEGUIMIENTO");
                System.out.println(event);

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = seguimiento.listar(event.getSender());
                            System.out.println(lista);
//                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
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
                        if (event.getParams().size() == 7) { // treatment_id, doctor_id, date, weight, height, age, description
                            seguimiento.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
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
                        if (event.getParams().size() == 8) { // id, treatment_id, doctor_id, date, weight, height, age, description
                            seguimiento.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
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
                            seguimiento.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL SEGUIMIENTO CORRECTAMENTE");
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
                            System.out.println(seguimiento.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(seguimiento.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(seguimiento.getComandos()));
                                return;
                            }
                        } else {
                            System.out.println("help no necesita parámetros");
                            respuesta.responseUser(email.getFrom(), "COMANDO HELP NO NECESITA PARÁMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "COMANDO HELP NO NECESITA PARÁMETROS");
                                return;
                            }
                        }
                    } else {
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: SEGUIMIENTO");
                        respuesta.responseUser(email.getFrom(), "ACCIÓN DESCONOCIDA DE CU: SEGUIMIENTO");
                        if (true) {
                            JOptionPane.showMessageDialog(null, "ACCIÓN DESCONOCIDA DE CU: SEGUIMIENTO");
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
            public void pago(TokenEvent event) {
                NegPago pago = new NegPago();
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = pago.listar(event.getSender());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 3) { // id, service_id, fecha, total
                            pago.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL PAGO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL PAGO CORRECTAMENTE");
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
                        if (event.getParams().size() == 4) { // id, service_id, fecha, total
                            pago.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL PAGO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL PAGO CORRECTAMENTE");
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
                            pago.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL PAGO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL PAGO CORRECTAMENTE");
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
                            System.out.println(pago.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(pago.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(pago.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: PAGO");
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
                System.out.println("CU: TRATAMIENTO");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = tratamiento.listar(email.getFrom());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 4) { // id_paciente, id_empleado, origen, id_habitacion
                            tratamiento.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
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
                        if (event.getParams().size() == 6) { // id, id_paciente, id_empleado, precio, origen, id_habitacion
                            tratamiento.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
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
                        if (event.getParams().size() == 1) { // id
                            tratamiento.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL SERVICIO TRATAMIENTO CORRECTAMENTE");
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
                    } else if (event.getAction() == Token.ALTA) {
                        if (event.getParams().size() == 1) { // id
                            tratamiento.alta(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("alta ok");
                            respuesta.responseUser(email.getFrom(), "SE DIO DE ALTA EL TRATAMIENTO Y SE LIBERÓ LA HABITACIÓN CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE DIO DE ALTA EL TRATAMIENTO Y SE LIBERÓ LA HABITACIÓN CORRECTAMENTE");
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
                            System.out.println(tratamiento.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(tratamiento.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(tratamiento.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: TRATAMIENTO");
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
            public void consulta(TokenEvent event) {
                System.out.println("CU: CONSULTA");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = consulta.listar(email.getFrom());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 4) { // id_paciente, id_empleado, fecha, motivo
                            consulta.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
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
                        if (event.getParams().size() == 6) { // id, id_paciente, id_empleado, precio, fecha, motivo
                            consulta.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
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
                        if (event.getParams().size() == 1) { // id
                            consulta.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL SERVICIO CONSULTA CORRECTAMENTE");
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
                            System.out.println(consulta.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(consulta.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(consulta.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: CONSULTA");
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
            public void vacuna(TokenEvent event) {
                System.out.println("CU: VACUNA");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = vacuna.listar(email.getFrom());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 3) { // id_paciente, id_empleado, nombre
                            vacuna.agregar(event.getParams());
                            System.out.println("agregación ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
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
                        if (event.getParams().size() == 5) { // id, id_paciente, id_empleado, precio, nombre
                            vacuna.modificar(event.getParams());
                            System.out.println("modificación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
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
                        if (event.getParams().size() == 1) { // id
                            vacuna.eliminar(Integer.parseInt(event.getParams().get(0)));
                            System.out.println("eliminación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINARON LOS DATOS DEL SERVICIO VACUNA CORRECTAMENTE");
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
                            System.out.println(vacuna.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(vacuna.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(vacuna.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: VACUNA");
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
            public void asociar(TokenEvent event) {
                System.out.println("CU: ASOCIAR");
                System.out.println(event);
                SendEmail respuesta = new SendEmail();

                try {
                    if (event.getAction() == Token.LISTAR) {
                        if (event.getParams().size() == 0) {
                            String lista = asociar.listar(email.getFrom());
                            System.out.println(lista);
                            System.out.println("listar ok");
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
                        if (event.getParams().size() == 3) { // id_comida, id_tratamiento, cantidad
                            asociar.agregar(event.getParams());
                            System.out.println("asociación agregada ok");
                            respuesta.responseUser(email.getFrom(), "SE AGREGÓ LA ASOCIACIÓN CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE AGREGÓ LA ASOCIACIÓN CORRECTAMENTE");
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
                        if (event.getParams().size() == 3) { // id_comida, id_tratamiento, nueva_cantidad
                            asociar.modificar(event.getParams());
                            System.out.println("modificación de asociación ok");
                            respuesta.responseUser(email.getFrom(), "SE MODIFICÓ LA ASOCIACIÓN CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE MODIFICÓ LA ASOCIACIÓN CORRECTAMENTE");
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
                        if (event.getParams().size() == 2) { // id_comida, id_tratamiento
                            asociar.eliminar(event.getParams());
                            System.out.println("eliminación de asociación ok");
                            respuesta.responseUser(email.getFrom(), "SE ELIMINÓ LA ASOCIACIÓN CORRECTAMENTE");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "SE ELIMINÓ LA ASOCIACIÓN CORRECTAMENTE");
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
                            System.out.println(asociar.getComandos());
                            respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(asociar.getComandos()));
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(asociar.getComandos()));
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
                        System.out.println("ACCIÓN DESCONOCIDA DE CU: ASOCIAR");
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
            public void reporte(TokenEvent event) {
                System.out.println("CU: REPORTE");
                System.out.println(event);
                try {
                    if (event.getAction() == Token.PAGOS) {
                        if (event.getParams().size() == 3) {
                            System.out.println("antes de ejecutar reporte pagos");
                            String lista = reporte.pagos(emailFrom,event.getParams());
                            System.out.println("despues de ejecutar reporte pagos");
                            System.out.println(lista);
                            System.out.println("listar ok");
                            if (true) {
                                JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(lista));
                                return;
                            }
                            //respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(lista));
                        } else {
                            System.out.println("demasiado parametros");
                            respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARAMETROS");
                            if (true) {
                                JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
                                
                                return;
                            }
                        }
                    } /*else {
                        if (event.getAction() == Token.VENTAS) {
                            if (event.getParams().size() == 0) {
                                String lista = reporte.ventas(emailFrom);
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
                                    JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
                                    return;
                                }
                                respuesta.responseUser(email.getFrom(), "ERROR EN LA CANTIDAD DE PARAMETROS");
                            }
                        }*/ else {

                            if (event.getAction() == Token.HELP) {
                                if (event.getParams().size() == 0) {
                                    System.out.println(reporte.getComandos());
                                    respuesta.responseUser(email.getFrom(), respuesta.mensajeComandos(reporte.getComandos()));
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, respuesta.mensajeComandos(reporte.getComandos()));
                                        return;
                                    }
                                    
                                } else {
                                    System.out.println("help No necesita parametros");
                                    respuesta.responseUser(email.getFrom(), "COMANDO HELP NO NECESITA PARAMETROS");
                                    if (true) {
                                        JOptionPane.showMessageDialog(null, "COMANDO HELP NO NECESITA PARAMETROS");
                                        
                                        return;
                                    }
                                    
                                }
                            } else {
                                System.out.println("ACCION DESCONOCIDA de cu reporte");
                                respuesta.responseUser(email.getFrom(), "COMANDO DESCONOCIDO");
                                if (true) {
                                    JOptionPane.showMessageDialog(null, "COMANDO DESCONOCIDO");
                                    
                                    return;
                                }
                                
                            }
                        }
                    
                } catch (Exception ex) {
                    System.out.println("Mensaje SQL: " + ex.getMessage());
                    System.err.println("[Control] Error al ejecutar el reporte: " + ex.getMessage());
                    ex.printStackTrace();
                    respuesta.responseUser(email.getFrom(), "MENSAJE SQL: " + ex.getMessage());
                    if (true) {
                        JOptionPane.showMessageDialog(null, "MENSAJE SQL: " + ex.getMessage());
                        
                        return;
                    }
                    
                }
            }
        }  
        );
                

        Thread thread = new Thread(interpreter);
        thread.setName("Interpreter Thread");
        thread.start();
    }

}
