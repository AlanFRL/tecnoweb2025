# Análisis Completo del Sistema de Gestión por Correo Electrónico

## 📋 Resumen Ejecutivo

Este es un sistema de gestión de clínica de rehabilitación nutricional que funciona **completamente a través de correo electrónico**. Los usuarios envían comandos por email al sistema, el sistema los procesa, ejecuta operaciones CRUD en la base de datos y responde al remitente con los resultados.

---

## 🏗️ Arquitectura General

### Componentes Principales

```
┌─────────────────────────────────────────────────────────────┐
│                    USUARIO EXTERNO                          │
│         (Envía correos con comandos)                        │
└────────────────────┬────────────────────────────────────────┘
                     │ Correo electrónico
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              SERVIDOR DE CORREO TECNOWEB                    │
│         grupo14sc@tecnoweb.org.bo (POP3/SMTP)              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                APLICACIÓN JAVA (Principal.java)             │
│  ┌──────────────────────────────────────────────────┐      │
│  │  MailVerificationThread (Polling cada 5 seg)     │      │
│  └──────────────┬───────────────────────────────────┘      │
│                 │                                            │
│                 ▼                                            │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Home.interprete() - Procesa emails              │      │
│  └──────────────┬───────────────────────────────────┘      │
│                 │                                            │
│                 ▼                                            │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Interpreter (Hilo) + Analex (Analizador)        │      │
│  └──────────────┬───────────────────────────────────┘      │
│                 │                                            │
│                 ▼                                            │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Casos de Uso (Negocio Layer)                    │      │
│  │  - NegPaciente, NegDoctor, NegProducto, etc.     │      │
│  └──────────────┬───────────────────────────────────┘      │
│                 │                                            │
│                 ▼                                            │
│  ┌──────────────────────────────────────────────────┐      │
│  │  DTOs (Datos Layer)                              │      │
│  │  - DtoPaciente, DtoDoctor, DtoProducto, etc.     │      │
│  └──────────────┬───────────────────────────────────┘      │
│                 │                                            │
│                 ▼                                            │
│  ┌──────────────────────────────────────────────────┐      │
│  │  Base de Datos PostgreSQL                        │      │
│  │  (localhost:5432/tecnoweb22024)                  │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────┘
                     │
                     ▼ Respuesta por email
┌─────────────────────────────────────────────────────────────┐
│                    USUARIO EXTERNO                          │
│         (Recibe respuesta del sistema)                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de Funcionamiento Completo

### 1. **Inicio de la Aplicación (Principal.java)**

```java
// El usuario presiona el botón "INICIAR" en la interfaz gráfica
iniciar.Iniciar();
```

**Qué hace:**
- Crea una instancia de `MailVerificationThread`
- Inicia un hilo que ejecuta el polling de correos
- El hilo se mantiene activo constantemente

---

### 2. **Polling de Correos (MailVerificationThread.java)**

**Protocolo:** POP3
**Servidor:** `www.tecnoweb.org.bo:110`
**Credenciales:**
- Usuario: `grupo14sc`
- Password: `grup014grup014*`

**Ciclo infinito cada 5 segundos:**

```java
while (true) {
    // 1. Conectarse al servidor POP3
    socket = new Socket(HOST, PORT_POP);
    
    // 2. Autenticarse
    authUser(USER, PASSWORD);
    
    // 3. Obtener cantidad de correos
    int count = getEmailCount();  // Comando STAT
    
    // 4. Si hay correos nuevos
    if (count > 0) {
        // Leer todos los correos (comando RETR)
        emails = getEmails(count);
        
        // Eliminar los correos del servidor (comando DELE)
        deleteEmails(count);
        
        // Notificar al listener con los correos
        emailEventListener.onReceiveEmailEvent(emails);
    }
    
    // 5. Cerrar conexión (comando QUIT)
    output.writeBytes(Command.quit());
    
    // 6. Esperar 5 segundos
    Thread.sleep(5000);
}
```

**Comandos POP3 utilizados:**
- `USER grupo14sc` - Identificación
- `PASS grup014grup014*` - Autenticación
- `STAT` - Obtener cantidad de correos
- `RETR n` - Recuperar correo número n
- `DELE n` - Eliminar correo número n
- `QUIT` - Cerrar sesión

---

### 3. **Extracción de Datos del Email (Extractor.java)**

Cuando se recibe el texto plano del correo, se extrae:

```java
public static Email getEmail(String plain_text) {
    return new Email(
        getFrom(plain_text),    // Extrae el remitente
        getSubject(plain_text)  // Extrae el asunto
    );
}
```

**Ejemplo de correo recibido:**
```
Return-Path: <usuario@gmail.com>
From: Usuario <usuario@gmail.com>
Subject: producto listar
To: grupo14sc@tecnoweb.org.bo
...
```

**Email extraído:**
- `from`: "usuario@gmail.com"
- `subject`: "producto listar"

---

### 4. **Interpretación del Comando (Home.interprete())**

Por cada correo recibido, se llama a `Home.interprete(email)`:

```java
public static void interprete(Email email) {
    // Se crea el intérprete con el asunto del email (comando)
    Interpreter interpreter = new Interpreter(
        email.getSubject().toLowerCase(),  // "producto listar"
        email.getFrom()                     // "usuario@gmail.com"
    );
    
    // Se configura un listener para cada caso de uso
    interpreter.setListener(new ITokenEventListener() {
        @Override
        public void producto(TokenEvent event) {
            // Lógica para el caso de uso PRODUCTO
        }
        
        @Override
        public void paciente(TokenEvent event) {
            // Lógica para el caso de uso PACIENTE
        }
        
        // ... más casos de uso
    });
    
    // Se ejecuta el intérprete en un hilo separado
    Thread thread = new Thread(interpreter);
    thread.start();
}
```

---

### 5. **Análisis Léxico del Comando (Analex.java)**

El `Interpreter` utiliza `Analex` (Analizador Léxico) para parsear el comando:

**Ejemplo: `"producto listar"`**

```
┌──────────────────────────────────────────────────────┐
│  Entrada: "producto listar"                          │
└──────────────┬───────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────┐
│  ANALEX (Analizador Léxico)                          │
│  - Lee carácter por carácter                         │
│  - Identifica tokens                                 │
└──────────────┬───────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────┐
│  Token 1: < CU, PRODUCTO >                           │
│  Token 2: < ACTION, LISTAR >                         │
│  Token 3: < END >                                    │
└──────────────────────────────────────────────────────┘
```

**Ejemplo con parámetros: `"producto agregar [Pizza; 25.5; queso, tomate]"`**

```
┌──────────────────────────────────────────────────────┐
│  Token 1: < CU, PRODUCTO >                           │
│  Token 2: < ACTION, AGREGAR >                        │
│  Token 3: < PARAMS, 0 >  → TSP[0] = "Pizza"         │
│  Token 4: < PARAMS, 1 >  → TSP[1] = "25.5"          │
│  Token 5: < PARAMS, 2 >  → TSP[2] = "queso, tomate" │
│  Token 6: < END >                                    │
└──────────────────────────────────────────────────────┘
```

**TSP (Tabla de Símbolos de Parámetros):**
- Almacena los parámetros extraídos entre `[` y `]`
- Separados por `;`
- Puede contener espacios y caracteres especiales

---

### 6. **Interpretación Semántica (Interpreter.java)**

El `Interpreter` recorre los tokens y construye un `TokenCommand`:

```java
@Override
public void run() {
    analex = new Analex(command);
    TokenCommand token_command = new TokenCommand();
    Token token;

    // Recorrer todos los tokens
    while ((token = analex.Preanalisis()).getName() != Token.END 
           && token.getName() != Token.ERROR) {
        
        if (token.getName() == Token.CU) {
            // Identificar el caso de uso
            token_command.setName(token.getAttribute());  // PRODUCTO
        } 
        else if (token.getName() == Token.ACTION) {
            // Identificar la acción
            token_command.setAction(token.getAttribute());  // LISTAR
        } 
        else if (token.getName() == Token.PARAMS) {
            // Agregar posición del parámetro
            token_command.addParams(token.getAttribute());
        }
        
        analex.next();
    }

    // Si terminó exitosamente
    if (token.getName() == Token.END) {
        filterEvent(token_command);  // Ejecutar el caso de uso
    } 
    // Si hubo error
    else if (token.getName() == Token.ERROR) {
        tokenError(token, analex.lexeme());
    }
}
```

---

### 7. **Enrutamiento a Casos de Uso (Interpreter.filterEvent())**

Según el token `CU`, se dispara el listener correspondiente:

```java
private void filterEvent(TokenCommand token_command) {
    TokenEvent token_event = new TokenEvent(this, sender);
    token_event.setAction(token_command.getAction());

    // Extraer parámetros del TSP y agregarlos al evento
    int count_params = token_command.countParams();
    for (int i = 0; i < count_params; i++) {
        int pos = token_command.getParams(i);
        token_event.addParams(analex.getParam(pos));
    }

    // Enrutar según el caso de uso
    switch (token_command.getName()) {
        case Token.PRODUCTO:
            listener.producto(token_event);
            break;
        case Token.PACIENTE:
            listener.paciente(token_event);
            break;
        case Token.DOCTOR:
            listener.doctor(token_event);
            break;
        // ... más casos
    }
}
```

---

### 8. **Ejecución del Caso de Uso (Ejemplo: PRODUCTO)**

En `Home.interprete()`, el listener ejecuta la lógica:

```java
@Override
public void producto(TokenEvent event) {
    SendEmail respuesta = new SendEmail();
    
    try {
        // Determinar la acción
        if (event.getAction() == Token.LISTAR) {
            if (event.getParams().size() == 0) {
                // producto listar
                String lista = producto.listar(emailFrom);
                System.out.println(lista);
                System.out.println("listar ok");
            } else {
                // Error: listar no necesita parámetros
                respuesta.responseUser(email.getFrom(), 
                    "ERROR EN LA CANTIDAD DE PARAMETROS");
            }
        }
        else if (event.getAction() == Token.AGREGAR) {
            if (event.getParams().size() == 3) {
                // producto agregar [name; price; ingredients]
                producto.agregar(event.getParams());
                respuesta.responseUser(emailFrom, 
                    "Producto agregado correctamente");
            } else {
                // Error: faltan parámetros
                respuesta.responseUser(email.getFrom(), 
                    "ERROR EN LA CANTIDAD DE PARAMETROS");
            }
        }
        else if (event.getAction() == Token.MODIFICAR) {
            if (event.getParams().size() == 4) {
                // producto modificar [id; name; price; ingredients]
                producto.modificar(event.getParams());
                respuesta.responseUser(emailFrom, 
                    "Producto modificado correctamente");
            }
        }
        else if (event.getAction() == Token.ELIMINAR) {
            if (event.getParams().size() == 1) {
                // producto eliminar [id]
                int id = Integer.parseInt(event.getParams().get(0));
                producto.eliminar(id);
                respuesta.responseUser(emailFrom, 
                    "Producto eliminado correctamente");
            }
        }
        else if (event.getAction() == Token.HELP) {
            // producto help
            String comandos = producto.getComandos();
            respuesta.responseUser(emailFrom, comandos);
        }
        
    } catch (Exception ex) {
        // Error en la ejecución
        ex.printStackTrace();
        respuesta.responseUser(email.getFrom(), 
            "ERROR AL EJECUTAR EL COMANDO");
    }
}
```

---

### 9. **Capa de Negocio (NegProducto.java)**

La capa de negocio coordina las operaciones:

```java
public class NegProducto {
    private DtoProducto dtoProducto;

    public void agregar(List<String> parametros) throws SQLException {
        dtoProducto.agregar(
            parametros.get(0),  // name
            Float.parseFloat(parametros.get(1)),  // price
            parametros.get(2)   // ingredients
        );
        dtoProducto.desconectar();
    }

    public String listar(String emailFrom) throws SQLException {
        String productos = dtoProducto.listar(emailFrom);
        dtoProducto.desconectar();
        return productos;
    }
    
    // ... más métodos
}
```

---

### 10. **Capa de Datos (DtoProducto.java)**

Ejecuta las consultas SQL:

```java
public class DtoProducto {
    private Conexion conexion;

    public void agregar(String name, float price, String ingredients) 
            throws SQLException {
        String query = "INSERT INTO meals(name, price, ingredients) " +
                       "VALUES(?, ?, ?);";
        PreparedStatement ps = conexion.EstablecerConexion()
                                       .prepareStatement(query);
        ps.setString(1, name);
        ps.setFloat(2, price);
        ps.setString(3, ingredients);

        if (ps.executeUpdate() == 0) {
            throw new SQLException();
        }
    }

    public String listar(String emailFrom) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT id, name, price, ingredients FROM meals;";
        PreparedStatement ps = conexion.EstablecerConexion()
                                       .prepareStatement(query);
        ResultSet set = ps.executeQuery();
        
        productos.add(new String[]{"ID", "Nombre", "Precio", "Ingredientes"});
        
        while (set.next()) {
            productos.add(new String[]{
                set.getString("id"),
                set.getString("name"),
                set.getString("price"),
                set.getString("ingredients")
            });
        }

        // Generar HTML con los datos
        String bodyHtml = generarHTMLTabla(productos);
        
        // Enviar por email
        SendEmail sendEmail = new SendEmail();
        sendEmail.responseEmail(emailFrom, bodyHtml);
        
        return imprimirTabla.mostrarTabla(productos);
    }
}
```

---

### 11. **Envío de Respuesta por Email (SendEmail.java)**

Dos métodos principales de envío:

#### **A) responseEmail() - Para HTML (tablas, listas)**

```java
public void responseEmail(String emailReceptor, String codigoHTML) {
    try {
        // Usa SMTP manual con soporte HTML
        responseUserOriginal(emailReceptor, codigoHTML, true);
        System.out.println("mensaje enviado");
    } catch (Exception ex) {
        System.out.println("correo no enviado");
    }
}
```

**Uso:** Para enviar listados de datos en formato HTML

#### **B) responseUser() - Para texto plano (confirmaciones, errores)**

```java
public void responseUser(String emailReceptor, String data) {
    try {
        // Usa SMTP manual sin HTML
        responseUserOriginal(emailReceptor, data, false);
        System.out.println("Respuesta enviada al comando: " + data);
    } catch (Exception ex) {
        System.out.println("Correo no enviado.");
    }
}
```

**Uso:** Para enviar mensajes de confirmación o error

#### **C) responseUserOriginal() - Implementación SMTP manual**

```java
public void responseUserOriginal(String Receptor, String data, boolean isHtml) {
    socket = new Socket(SERVER, PORT_SMTP);  // mail.tecnoweb.org.bo:25
    
    // Handshake SMTP
    salida.writeBytes("HELO " + SERVER + "\r\n");
    salida.writeBytes("MAIL FROM: <grupo14sc@tecnoweb.org.bo> \r\n");
    salida.writeBytes("RCPT TO: <" + Receptor + "> \r\n");
    salida.writeBytes("DATA\r\n");
    
    // Construir el mensaje
    if (isHtml) {
        comando = "Subject: NOTIFICACION\r\n" + 
                 "MIME-Version: 1.0\r\n" + 
                 "Content-Type: text/html; charset=UTF-8\r\n\r\n" + 
                 data + "\r\n.\r\n";
    } else {
        comando = "Subject: NOTIFICACION\r\n\r\n" + data + "\r\n.\r\n";
    }
    
    salida.writeBytes(comando);
    salida.writeBytes("QUIT\r\n");
}
```

**Protocolo:** SMTP manual (puerto 25)
**Servidor:** `mail.tecnoweb.org.bo`
**Remitente:** `grupo14sc@tecnoweb.org.bo`

---

## 📊 Casos de Uso Implementados

El sistema maneja 14 casos de uso diferentes:

| # | Caso de Uso | Token | Acciones Disponibles |
|---|-------------|-------|---------------------|
| 1 | **EMPLEADO** | 100 | agregar, modificar, eliminar, listar, help |
| 2 | **DOCTOR** | 101 | agregar, modificar, eliminar, listar, help |
| 3 | **PACIENTE** | 102 | agregar, modificar, eliminar, listar, help |
| 4 | **RESPONSABLE** | 103 | agregar, modificar, eliminar, listar, help |
| 5 | **PRODUCTO** | 104 | agregar, modificar, eliminar, listar, help |
| 6 | **SALA** | 105 | agregar, modificar, eliminar, listar, help |
| 7 | **TRATAMIENTO** | 106 | agregar, modificar, eliminar, listar, help |
| 8 | **FICHA** | 107 | agregar, modificar, eliminar, listar, help |
| 9 | **SEGUIMIENTO** | 108 | agregar, modificar, eliminar, listar, help |
| 10 | **PAGO** | 109 | agregar, modificar, eliminar, listar, registrar, help |
| 11 | **REPORTE** | 110 | pagos, consultas, historial, help |
| 12 | **CONSULTA** | 111 | agregar, modificar, eliminar, listar, help |
| 13 | **VACUNA** | 112 | agregar, modificar, eliminar, listar, help |
| 14 | **ASOCIAR** | 113 | agregar (relacionar paciente-responsable) |

---

## 🎯 Formato de Comandos

### **Sintaxis General**

```
<caso_de_uso> <accion> [parametro1; parametro2; parametro3]
```

### **Ejemplos de Comandos**

#### **1. Listar (sin parámetros)**
```
producto listar
paciente listar
doctor listar
```

#### **2. Agregar (con parámetros)**
```
producto agregar [Pizza Margarita; 35.50; queso, tomate, albahaca]
paciente agregar [12345678; Juan Pérez; Calle Falsa 123; M; 1990-05-15; O; +; 1]
doctor agregar [87654321; Dra. María López; Av. Siempre Viva 456; F; maria@example.com; 555-1234; Nutrición]
```

#### **3. Modificar (con ID + parámetros)**
```
producto modificar [5; Pizza Napolitana; 40.00; queso, tomate, anchoas]
paciente modificar [3; 12345678; Juan Pérez; Nueva Dirección 789; M; 1990-05-15; O; +; 1]
```

#### **4. Eliminar (solo ID)**
```
producto eliminar [5]
paciente eliminar [3]
doctor eliminar [7]
```

#### **5. Help (sin parámetros)**
```
producto help
paciente help
help
```

**Respuesta del comando `help`:**
```
PARA ACCEDER A LOS COMANDOS DE CADA CASO DE USO
USAR LOS SIGUIENTES COMANDOS:
paciente help
responsable help
doctor help
empleado help
consulta help
pago help
...
```

**Respuesta del comando `producto help`:**
```
COMANDOS PARA CU: PRODUCTO
producto listar
producto agregar [name; price; ingredients]
producto modificar [id; name; price; ingredients]
producto eliminar [id]
```

---

## 🔧 Reglas del Analizador Léxico

### **Caracteres Válidos**

- **Letras:** `A-Z`, `a-z`
- **Dígitos:** `0-9`
- **Espacios:** ` `, `\t`, `\n`
- **Delimitadores de parámetros:** `[`, `]`
- **Separador de parámetros:** `;`
- **Caracteres especiales:** `#`, `,`, `/`, `-`, `:`, `@`, `.`, `_`

### **Estados del Autómata**

El analizador léxico usa una máquina de estados finitos:

```
Estado 0: Inicial
Estado 1: Leyendo palabra (caso de uso o acción)
Estado 2: Token reconocido (CU o ACTION)
Estado 3: Leyendo parámetro
Estado 4: Fin de parámetro (encontró ;)
Estado 5: Fin de parámetros (encontró ])
Estado 6: Fin del comando (EOF)
Estado 7: Error - comando desconocido
Estado 8: Error - carácter no válido
```

### **Ejemplos de Análisis**

**Input:** `"producto agregar [Pizza; 25.5]"`

```
Estado 0 → 'p' → Estado 1 (ac = "p")
Estado 1 → 'r' → Estado 1 (ac = "producto")
Estado 1 → ' ' → Estado 2 → Token: <CU, PRODUCTO>
Estado 0 → 'a' → Estado 1 (ac = "a")
Estado 1 → 'g' → Estado 1 (ac = "agregar")
Estado 1 → ' ' → Estado 2 → Token: <ACTION, AGREGAR>
Estado 0 → '[' → Estado 33 (ac = "")
Estado 33 → 'P' → Estado 3 (ac = "Pizza")
Estado 3 → ';' → Estado 4 → Token: <PARAMS, 0>, TSP[0] = "Pizza"
Estado 33 → '2' → Estado 34 (ac = "25.5")
Estado 34 → ']' → Estado 5 → Token: <PARAMS, 1>, TSP[1] = "25.5"
Estado 0 → EOF → Estado 6 → Token: <END>
```

---

## 💾 Conexión a Base de Datos

### **Configuración Actual**

```java
private final String DRIVER = "org.postgresql.Driver";
private final String DB = "tecnoweb22024";
private final String USER = "postgres";
private final String PASSWORD = "8554";
private final String URL = "jdbc:postgresql://localhost:5432/";
```

### **Estructura de Tablas (Ejemplo: Producto)**

```sql
-- Tabla meals (productos)
CREATE TABLE meals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    ingredients TEXT
);
```

### **Otras Configuraciones Comentadas**

El código tiene configuraciones alternativas comentadas para:
- Servidor remoto de tecnoweb
- Diferentes bases de datos locales
- Diferentes puertos (5432, 5433)

---

## 🧵 Gestión de Hilos

El sistema utiliza múltiples hilos para operaciones concurrentes:

### **Hilo 1: MailVerificationThread**
- **Función:** Polling constante de correos
- **Frecuencia:** Cada 5 segundos
- **Ciclo de vida:** Infinito (mientras la app esté corriendo)

### **Hilo 2: Interpreter Thread (múltiples instancias)**
- **Función:** Procesar cada comando recibido
- **Creación:** Por cada email que llega
- **Ciclo de vida:** Hasta que termina de procesar el comando

```java
// En Home.interprete()
Thread thread = new Thread(interpreter);
thread.setName("Interpreter Thread");
thread.start();
```

**Ventaja:** Permite procesar múltiples comandos simultáneamente sin bloquear el polling de correos.

---

## 🎨 Interfaz Gráfica (Principal.java)

### **Componentes**

- **Campo TO:** Correo destino (prellenado con `grupo14sc@tecnoweb.org.bo`)
- **Campo SUBJECT:** Comando a enviar
- **Botón INICIAR:** Inicia el polling de correos
- **Botón ENVIAR:** Envía un correo con el comando (para testing)

### **Uso del Botón ENVIAR**

```java
private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    String subject = toLowerCase(Jtf_subject.getText());
    enviar.sendmail(Jtxf_to.getText(), subject);
}
```

**Nota:** Este botón es para pruebas. En producción, los usuarios envían correos desde sus clientes de email normales.

---

## 🔐 Credenciales del Sistema

### **Correo del Sistema (POP3/SMTP)**
- **Email:** grupo14sc@tecnoweb.org.bo
- **Password:** grup014grup014*
- **Servidor POP3:** www.tecnoweb.org.bo:110
- **Servidor SMTP:** mail.tecnoweb.org.bo:25

### **Base de Datos**
- **DB:** tecnoweb22024
- **User:** postgres
- **Password:** 8554
- **Host:** localhost:5432

---

## 📝 Manejo de Errores

### **Tipos de Errores**

1. **ERROR_COMMAND (300):** Comando no reconocido
   - Ejemplo: `"productu listar"` (typo en "producto")
   
2. **ERROR_CHARACTER (301):** Carácter no válido en el comando
   - Ejemplo: `"producto$ listar"` ($ no es válido)

3. **Errores de Parámetros:** Cantidad incorrecta
   - Ejemplo: `"producto agregar [Pizza]"` (faltan parámetros)

4. **Errores de Base de Datos:** SQLException
   - Ejemplo: Violación de constraint, ID no encontrado

5. **Errores de Red:** IOException
   - Problemas de conexión POP3/SMTP

### **Respuestas de Error**

```java
// Error en cantidad de parámetros
respuesta.responseUser(email.getFrom(), 
    "ERROR EN LA CANTIDAD DE PARAMETROS");

// Error en ejecución
respuesta.responseUser(email.getFrom(), 
    "ERROR AL EJECUTAR EL COMANDO");

// Comando no reconocido
respuesta.responseUser(email.getFrom(), 
    "COMANDO NO RECONOCIDO: " + command);
```

---

## 🔍 Logging y Debug

El sistema utiliza `System.out.println()` extensivamente para debugging:

```java
System.out.println("CU: PRODUCTO");
System.out.println(event);
System.out.println("listar ok");
System.out.println("S : " + entrada.readLine());  // Respuestas SMTP/POP3
System.err.println("Ocurrió un error al insertar en Meals");
```

También usa `JOptionPane.showMessageDialog()` para mostrar mensajes en GUI:

```java
JOptionPane.showMessageDialog(null, "ERROR EN LA CANTIDAD DE PARAMETROS");
```

---

## 🔄 Flujo Completo Ejemplo: "producto listar"

```
1. Usuario envía email:
   From: usuario@gmail.com
   To: grupo14sc@tecnoweb.org.bo
   Subject: producto listar

2. MailVerificationThread detecta el correo (polling cada 5s)
   - Comando POP3: STAT → "1 mensaje"
   - Comando POP3: RETR 1 → obtiene el correo completo
   - Comando POP3: DELE 1 → elimina del servidor

3. Extractor.getEmail() procesa el texto plano
   → Email(from="usuario@gmail.com", subject="producto listar")

4. Home.interprete(email) recibe el email
   → Crea Interpreter("producto listar", "usuario@gmail.com")
   → Inicia hilo de interpretación

5. Analex analiza "producto listar"
   → Token 1: <CU, PRODUCTO>
   → Token 2: <ACTION, LISTAR>
   → Token 3: <END>

6. Interpreter.filterEvent() identifica el caso de uso
   → listener.producto(token_event)

7. Home ejecuta la lógica de producto.listar()
   → NegProducto.listar("usuario@gmail.com")
   → DtoProducto.listar("usuario@gmail.com")

8. DtoProducto consulta la base de datos
   → SELECT id, name, price, ingredients FROM meals;
   → ResultSet con todos los productos

9. DtoProducto genera HTML con tabla
   <table border='1'>
     <tr><td>ID</td><td>Nombre</td><td>Precio</td>...</tr>
     <tr><td>1</td><td>Pizza</td><td>35.5</td>...</tr>
     ...
   </table>

10. SendEmail.responseEmail() envía el HTML
    → SMTP manual a mail.tecnoweb.org.bo:25
    → HELO, MAIL FROM, RCPT TO, DATA
    → Subject: NOTIFICACION
    → Content-Type: text/html
    → Envía el HTML de la tabla

11. Usuario recibe email con tabla de productos
    From: grupo14sc@tecnoweb.org.bo
    To: usuario@gmail.com
    Subject: NOTIFICACION
    Body: [Tabla HTML con productos]
```

**Tiempo total:** ~5-10 segundos (depende del polling)

---

## 📦 Estructura de Paquetes

```
tecnoweb2025/
├── src/
│   ├── Comunication/
│   │   ├── Home.java                    # Coordinador principal
│   │   ├── MailVerificationThread.java  # Polling de correos (POP3)
│   │   ├── SendEmail.java               # Envío de respuestas (SMTP)
│   │   ├── TokenEvent.java              # Evento con datos del comando
│   │
│   ├── Database/
│   │   └── Conexion.java                # Conexión a PostgreSQL
│   │
│   ├── Datos/                           # DTOs (Acceso a BD)
│   │   ├── DtoProducto.java
│   │   ├── DtoPaciente.java
│   │   ├── DtoDoctor.java
│   │   └── ...
│   │
│   ├── Interfaces/
│   │   ├── IEmailEventListener.java     # Callback para emails recibidos
│   │   └── ITokenEventListener.java     # Callback para cada caso de uso
│   │
│   ├── Negocio/                         # Lógica de negocio
│   │   ├── Interpreter.java             # Intérprete de comandos
│   │   ├── Analex.java                  # Analizador léxico
│   │   ├── NegProducto.java
│   │   ├── NegPaciente.java
│   │   └── ...
│   │
│   ├── tecnoweb2025/
│   │   └── Principal.java               # Interfaz gráfica (Swing)
│   │
│   └── Utils/
│       ├── Token.java                   # Constantes de tokens
│       ├── Email.java                   # Modelo de email
│       ├── Command.java                 # Comandos POP3
│       ├── Extractor.java               # Extrae datos del email
│       ├── Cinta.java                   # Manejo de cadenas para Analex
│       └── TSParams.java                # Tabla de símbolos de parámetros
│
├── Librerias/                           # JARs externos
│   ├── AbsoluteLayout.jar
│   ├── javax.mail.jar
│   ├── postgresql-42.3.0.jar
│   └── ...
│
└── build.xml                            # Script de Ant
```

---

## 🎓 Conceptos Clave de Compiladores Aplicados

Este sistema implementa conceptos de **teoría de compiladores**:

### **1. Análisis Léxico (Analex.java)**
- Reconoce tokens válidos
- Maneja tabla de símbolos (TSParams)
- Implementa autómata finito determinista

### **2. Análisis Sintáctico (implícito en Token.java)**
- Define gramática del lenguaje de comandos
- Estructura: `CU ACTION [PARAMS]`

### **3. Análisis Semántico (Interpreter.java)**
- Valida cantidad de parámetros
- Ejecuta acciones según el contexto
- Maneja errores semánticos

### **4. Generación de Código (Home.interprete())**
- Traduce comandos a operaciones Java
- Ejecuta consultas SQL
- Genera respuestas

---

## 🚀 Ventajas del Sistema

1. **Acceso Remoto:** Los usuarios pueden gestionar desde cualquier cliente de email
2. **Auditoría:** Todos los comandos quedan registrados en correos
3. **Asíncrono:** No requiere conexión permanente
4. **Multiplataforma:** Funciona desde cualquier dispositivo con email
5. **Sin instalación cliente:** Solo se necesita el servidor corriendo

---

## ⚠️ Limitaciones y Consideraciones

1. **Latencia:** Polling cada 5 segundos → respuesta no inmediata
2. **Seguridad:** Sin autenticación robusta (solo email del remitente)
3. **Concurrencia:** Múltiples usuarios simultáneos pueden saturar
4. **Formato estricto:** Los comandos deben seguir sintaxis exacta
5. **Sin sesiones:** Cada comando es independiente
6. **Eliminación de correos:** Una vez procesados, se borran del servidor

---

## 🔮 Próximos Pasos: Migración a Lavandería

Para adaptar el sistema a una lavandería, se deberán:

1. **Redefinir casos de uso:**
   - CLIENTE (en lugar de PACIENTE)
   - PRENDA (en lugar de PRODUCTO)
   - SERVICIO (lavado, planchado, etc.)
   - PEDIDO (en lugar de FICHA)
   - ENTREGA

2. **Modificar tokens en Token.java:**
   - Cambiar constantes de CU
   - Actualizar lexemas

3. **Crear nuevos DTOs:**
   - DtoCliente
   - DtoPrenda
   - DtoServicio
   - DtoPedido

4. **Adaptar base de datos:**
   - Nuevas tablas
   - Relaciones actualizadas

5. **Actualizar Home.interprete():**
   - Nuevos listeners
   - Lógica específica de lavandería

---

## 📚 Conclusión

Este es un sistema robusto y bien estructurado que implementa:
- ✅ Comunicación por email (POP3/SMTP)
- ✅ Analizador léxico y sintáctico personalizado
- ✅ Arquitectura en capas (Presentación → Negocio → Datos)
- ✅ Patrón Observer (listeners)
- ✅ Multithreading
- ✅ CRUD completo
- ✅ Generación dinámica de HTML

**Es una excelente base para migrar a un sistema de gestión de lavandería.**
