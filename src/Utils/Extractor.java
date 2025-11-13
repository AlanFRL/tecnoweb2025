/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
package Utils;
 
public class Extractor {
    private static String GMAIL = "d=gmail";
    private static String HOTMAILCOM = "d=hotmail";
    private static String YAHOO = "d=yahoo";
    private static String OUTLOOK = "d=outlook";
    private static String HOTMAILES = "d=HOTMAIL";
    private static String FICCT = "d=ficct";
    private static String UAGRM = "d=uagrm";
    
    public static Email getEmail(String plain_text){
        System.out.println("\n");
        System.out.println("######################################################");
        System.out.println("##  EXTRACTOR: PROCESANDO CORREO ELECTRÓNICO       ##");
        System.out.println("######################################################");
        return new Email(getFrom(plain_text),getSubject(plain_text));
    }
    
    private static String getFrom(String plain_text){
        String search = "Return-Path: <";
        int index_begin = plain_text.indexOf(search) + search.length();
        int index_end = plain_text.indexOf(">");
        return plain_text.substring(index_begin, index_end);
    }
    
    private static String getTo(String plain_text){
        String to = "";
        if(plain_text.contains(GMAIL)){
            to = getToFromGmail(plain_text);
        } else if(plain_text.contains(HOTMAILCOM)){
            to = getToFromHotmail(plain_text);
        } else if(plain_text.contains(YAHOO)){
            to = getToFromYahoo(plain_text);
        }
        return to;
    }
    
    private static String getSubject(String plain_text){
        System.out.println("========== EXTRACTOR: ANALIZANDO SUBJECT ==========");
        
        // Imprimir las primeras 500 caracteres del correo para ver el formato
        System.out.println(">>> PRIMEROS 500 CARACTERES DEL CORREO:");
        System.out.println(plain_text.substring(0, Math.min(500, plain_text.length())));
        System.out.println(">>> FIN PRIMEROS 500 CARACTERES");
        System.out.println();
        
        // Detectar el proveedor
        System.out.println(">>> DETECTANDO PROVEEDOR:");
        System.out.println("  ¿Contiene GMAIL (d=gmail)? " + plain_text.contains(GMAIL));
        System.out.println("  ¿Contiene HOTMAILCOM (d=hotmail)? " + plain_text.contains(HOTMAILCOM));
        System.out.println("  ¿Contiene OUTLOOK (d=outlook)? " + plain_text.contains(OUTLOOK));
        System.out.println("  ¿Contiene HOTMAILES (d=HOTMAIL)? " + plain_text.contains(HOTMAILES));
        System.out.println("  ¿Contiene YAHOO (d=yahoo)? " + plain_text.contains(YAHOO));
        System.out.println("  ¿Contiene FICCT (d=ficct)? " + plain_text.contains(FICCT));
        System.out.println("  ¿Contiene UAGRM (d=uagrm)? " + plain_text.contains(UAGRM));
        System.out.println();
        
        String search = "Subject: ";
        int i = plain_text.indexOf(search) + search.length();
        
        System.out.println(">>> POSICIÓN DE 'Subject: ': " + (i - search.length()));
        System.out.println(">>> INICIO DEL SUBJECT (después de 'Subject: '): posición " + i);
        
        String end_string = "";
        if(plain_text.contains(GMAIL) || plain_text.contains(UAGRM)){
            end_string = "To:";
            System.out.println(">>> PROVEEDOR DETECTADO: Gmail/UAGRM");
        } else if(plain_text.contains(HOTMAILCOM) || plain_text.contains(OUTLOOK) || plain_text.contains(HOTMAILES)){
            end_string = "Thread-Topic";
            System.out.println(">>> PROVEEDOR DETECTADO: Hotmail/Outlook");
        } else if(plain_text.contains(YAHOO)){
            end_string = "MIME-Version:";
            System.out.println(">>> PROVEEDOR DETECTADO: Yahoo");
        } else if(plain_text.contains(FICCT)){
            end_string = "Content-Type:";
            System.out.println(">>> PROVEEDOR DETECTADO: FICCT");
        } else {
            System.out.println(">>> PROVEEDOR NO DETECTADO - Usando lógica genérica");
        }
        
        System.out.println(">>> MARCADOR DE FIN SELECCIONADO: '" + end_string + "'");
        
        int e = plain_text.indexOf(end_string, i);
        System.out.println(">>> POSICIÓN DEL MARCADOR DE FIN: " + e);
        
        if(e==-1){
            System.out.println(">>> MARCADOR DE FIN NO ENCONTRADO, buscando alternativas...");
            int position=plain_text.indexOf("MIME",i);
            System.out.println(">>> Posición de 'MIME': " + position);
            if(position==-1){
                int posicion=plain_text.indexOf("mime",i);
                System.out.println(">>> Posición de 'mime': " + posicion);
                if(posicion != -1) {
                    String subject = plain_text.substring(i, posicion);
                    System.out.println(">>> SUBJECT EXTRAÍDO (con 'mime'): '" + subject + "'");
                    System.out.println("===================================================");
                    return subject;
                } else {
                    System.out.println(">>> ERROR: NO SE ENCONTRÓ NINGÚN MARCADOR DE FIN");
                    System.out.println("===================================================");
                    return "";
                }
            }
            String subject = plain_text.substring(i, position);
            System.out.println(">>> SUBJECT EXTRAÍDO (con 'MIME'): '" + subject + "'");
            System.out.println("===================================================");
            return subject;   
        }
        
        String subject = plain_text.substring(i, e);
        System.out.println(">>> SUBJECT EXTRAÍDO: '" + subject + "'");
        System.out.println("===================================================");
        return subject;     
    }
    
    private static String getToFromGmail(String plain_text){
        return getToCommon(plain_text);
    }
    
    private static String getToFromHotmail(String plain_text){
        String aux = getToCommon(plain_text);
        return aux.substring(1, aux.length() - 1);
    }
    
    private static String getToFromYahoo(String plain_text){
        int index = plain_text.indexOf("To: ");
        int i = plain_text.indexOf("<", index);
        int e = plain_text.indexOf(">", i);
        return plain_text.substring(i + 1, e);
    }
    
    private static String getToCommon(String plain_text){
        String aux = "To: ";
        int index_begin = plain_text.indexOf(aux) + aux.length();
        int index_end = plain_text.indexOf("\n", index_begin);
        return plain_text.substring(index_begin, index_end);
    }
}
