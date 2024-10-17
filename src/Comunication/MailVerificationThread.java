package Comunication;

import Interfaces.IEmailEventListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Command;
import Utils.Email;
import Utils.Extractor;

public class MailVerificationThread implements Runnable {

    private final static int PORT_POP = 110;
    private final static String HOST = "www.tecnoweb.org.bo";
    private final static String USER = "grupo04sa";
    private final static String PASSWORD = "grup004grup004*";

    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    private IEmailEventListener emailEventListener;

    public IEmailEventListener getEmailEventListener() {
        return emailEventListener;
    }

    public void setEmailEventListener(IEmailEventListener emailEventListener) {
        this.emailEventListener = emailEventListener;
    }

    public MailVerificationThread() {
        socket = null;
        input = null;
        output = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Email> emails = null;
                socket = new Socket(HOST, PORT_POP);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());
                System.out.println("**************** Conexion establecida *************");

                authUser(USER, PASSWORD);

                int count = getEmailCount();
                if (count > 0) {
                    emails = getEmails(count);
                    System.out.println("Cantidad de correos: " + count);
                    System.out.println("Correos: " + emails);
                    deleteEmails(count);
                }

                output.writeBytes(Command.quit() + "\r\n");
                input.readLine();  // Espera la respuesta de QUIT
                System.out.println("************** Conexion cerrada ************");

                if (count > 0 && emailEventListener != null) {
                    emailEventListener.onReceiveEmailEvent(emails);
                }

                Thread.sleep(5000);
                System.out.println("ESTADO DEL HILO: " + Thread.currentThread().isAlive());

            } catch (IOException ex) {
                Logger.getLogger(MailVerificationThread.class.getName()).log(Level.SEVERE, "Error en IO", ex);
                break;  // Salir del bucle si hay un problema de conexión
            } catch (InterruptedException ex) {
                System.out.println("HILO BLOQUEADO - IMPOSIBLE SU INTERRUPCION");
                break;  // Salir del bucle si el hilo es interrumpido
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    Logger.getLogger(MailVerificationThread.class.getName()).log(Level.SEVERE, "Error al cerrar conexión", e);
                }
            }
        }
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            String initialResponse = input.readLine();
            System.out.println("Respuesta inicial del servidor: " + initialResponse);  // Mensaje de bienvenida

            // Enviar USER y verificar respuesta
            output.writeBytes("USER " + email + "\r\n");
            String userResponse = input.readLine();
            System.out.println("Respuesta después de USER: " + userResponse);
            if (userResponse == null || userResponse.contains("-ERR")) {
                throw new IOException("Error en USER: " + userResponse);
            }

            // Enviar PASS y verificar respuesta
            output.writeBytes("PASS " + password + "\r\n");
            String passResponse = input.readLine();
            System.out.println("Respuesta después de PASS: " + passResponse);
            if (passResponse == null || passResponse.contains("-ERR")) {
                throw new IOException("Error en PASS: " + passResponse);
            }
        }
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            output.writeBytes(Command.dele(i) + "\r\n");
            String response = input.readLine();  // Leer la respuesta de cada comando DELE
            System.out.println("Respuesta de DELE " + i + ": " + response);
        }
    }

    private int getEmailCount() throws IOException {
        output.writeBytes(Command.stat() + "\r\n");
        String line = input.readLine();
        System.out.println("Respuesta de STAT: " + line);
        String[] data = line.split(" ");
        return Integer.parseInt(data[1]);
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            output.writeBytes(Command.retr(i) + "\r\n");
            String text = readMultiline();
            emails.add(Extractor.getEmail(text));
        }
        return emails;
    }

    private String readMultiline() throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = input.readLine();
            if (line == null) {
                throw new IOException("Servidor no responde (ocurrio un error al abrir el correo)");
            }
            if (line.equals(".")) {
                break;
            }
            lines.append(line).append("\n");
        }
        return lines.toString();
    }

    // Método principal para probar el MailVerificationThread
    public static void main(String[] args) {
        MailVerificationThread mail = new MailVerificationThread();
        mail.setEmailEventListener(emails -> {
            System.out.println("Correos recibidos: " + emails.size());
            for (Email email : emails) {
                System.out.println("Email: " + email);
            }
        });

        Thread thread = new Thread(mail);
        thread.setName("Mail Verification Thread");
        thread.start();
    }
}
