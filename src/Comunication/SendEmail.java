/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comunication;

//import com.juanvladimir13.template.MaryTemplate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

    private final static int PORT_SMTP = 25;
    private final static String SERVER = "mail.tecnoweb.org.bo";
    private final static String user_emisor = "grupo04sa@tecnoweb.org.bo";
    private static String user_receptor;
    private static Socket socket;
    private static BufferedReader entrada;
    private static DataOutputStream salida;

    public SendEmail() {
        this.user_receptor = "";

    }

    public String getUser_receptor() {
        return user_receptor;
    }

    public void setUser_receptor(String user_receptor) {
        this.user_receptor = user_receptor;
    }

    public void responseUser(String Receptor, String data) {
        String comando = "";
        try {
            socket = new Socket(SERVER, PORT_SMTP);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new DataOutputStream(socket.getOutputStream());

            if (socket != null && entrada != null && salida != null) {
                System.out.println("S : " + entrada.readLine()); // Leer saludo del servidor

                comando = "HELO " + SERVER + "\r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "MAIL FROM: <" + user_emisor + "> \r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "RCPT TO: <" + Receptor + "> \r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "DATA\r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "Subject: NOTIFICACION\r\n" + data + "\r\n.\r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "QUIT\r\n";
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                // Mensaje para confirmar que se envió la respuesta
                System.out.println("Respuesta al comando enviada: " + data);
            }

            salida.close();
            entrada.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println(" S : No se pudo conectar con el servidor indicado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static protected String getMultiline(BufferedReader in) throws IOException {
        String lines = "";
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.charAt(3) == ' ') {
                lines = lines + "\n" + line;
                // No more lines in the server response
                break;
            }
            // Add read line to the list of lines
            lines = lines + "\n" + line;
        }
        return lines;
    }

    ///////////////////////////////////////////////////////////////
    public void responseEmail(String emailReceptor, String codigoHTML) {
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.put("mail.smtp.starttls.enable", "true");
            p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            p.put("mail.smtp.port", "587");
            p.put("mail.smtp.auth", "true");
            Session s = Session.getDefaultInstance(p);

            MimeMessage mensaje = new MimeMessage(s);
            mensaje.setFrom(new InternetAddress("grupo004sa@gmail.com"));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceptor));
            mensaje.setSubject("NOTIFICACION");
            //mensaje.setText("informacion");
            mensaje.setContent(codigoHTML, "text/html");//
            Transport t = s.getTransport("smtp");
            t.connect("grupo004sa@gmail.com", "cxlhqruueybqklkk");
            System.out.println("enviando...");
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            System.out.println("mensaje enviado");
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("correo no enviado");
        }
    }

    public void sendmail(String receptor, String subject) {
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.put("mail.smtp.starttls.enable", "true");
            p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            p.put("mail.smtp.port", "587");
            p.put("mail.smtp.auth", "true");
            Session s = Session.getDefaultInstance(p);

            MimeMessage mensaje = new MimeMessage(s);
            mensaje.setFrom(new InternetAddress("grupo004sa@gmail.com"));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(receptor));
            mensaje.setSubject(subject);
            mensaje.setText("enviado");
            Transport t = s.getTransport("smtp");
            t.connect("grupo004sa@gmail.com", "cxlhqruueybqklkk");
            System.out.println("enviando...");
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            System.out.println("mensaje enviado");
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("correo no enviado");
        }
    }
    ////////////////////////////////////////////////////////////

    public String mensajeHtml(String mensaje) {
        String html = "<h2>%s</h2>";
        return String.format(html, mensaje);
    }

    public String cargarHtml(String s) {
        String html = "<center>\n"
                + "  <br>\n" + s + "  <br>\n"
                + "</center>";
        return html;
    }

    public String mensajeTexto(String emailFrom, String salto) {
        String comando = "PARA ACCEDER A LOS COMANDOS DE CADA CASO DE USO" + salto
                + "USAR LOS SIGUIENTES COMANDOS:" + salto
                + "usuario help" + salto
                + "almacen help" + salto
                + "paquete help" + salto
                + "guia help" + salto
                + "seguimiento help" + salto
                + "tarifa help" + salto
                + "pago help" + salto
                + "reporte help" + salto
                + "venta help" + salto
                + "detalleventa help" + salto
                + "persona help"; // Agregado el comando persona help

        String bodyhtml = "<html>"
                + "<body>"
                + "<h2>Comandos</h2>"
                + "<p>Estimado/a,</p>"
                + "<p>" + comando.replace(salto, "<br>") + "</p>"
                + "</body>"
                + "</html>";

        System.out.println(emailFrom);
        responseEmail(emailFrom, bodyhtml);
        return comando;
    }

    public String mensajeComandos(String mensajeHtml) {
        String[] partes = mensajeHtml.split("<br>");
        String mensaje = "";
        for (int i = 0; i < partes.length; i++) {
            mensaje = mensaje + partes[i] + "\r\n";
        }
        return mensaje;
    }

}
