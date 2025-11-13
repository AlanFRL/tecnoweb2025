Esta es un script inicial de la base de datos:

-- ===============================
-- BASE DE DATOS: LAVANDERIA BELÉN
-- ===============================

-- ===============================
-- usuario / empleado / propietario (TPT)
-- ===============================
CREATE TABLE usuario (
  id            SERIAL PRIMARY KEY,
  nombre        VARCHAR(50) NOT NULL,
  telefono      VARCHAR(20),
  email         VARCHAR(100) UNIQUE,
  password      VARCHAR(100) NOT NULL,
  tipo_usuario  VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('propietario','empleado')),
  estado        BOOLEAN DEFAULT TRUE
);

CREATE TABLE empleado (
  id                   INT PRIMARY KEY,
  cargo                VARCHAR(50),
  fecha_contratacion   DATE,
  CONSTRAINT empleado_fk_usuario
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE propietario (
  id            INT PRIMARY KEY,
  razon_social  VARCHAR(120) NOT NULL,
  CONSTRAINT propietario_fk_usuario
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- ===============================
-- cliente / proveedor
-- ===============================
CREATE TABLE cliente (
  id         SERIAL PRIMARY KEY,
  nombre     VARCHAR(50) NOT NULL,
  direccion  TEXT,
  telefono   VARCHAR(20)
);

CREATE TABLE proveedor (
  id            SERIAL PRIMARY KEY,
  razon_social  VARCHAR(120) NOT NULL,
  telefono      VARCHAR(20),
  direccion     TEXT
);

-- ===============================
-- servicio / promocion / promocion_servicio
-- ===============================
CREATE TABLE servicio (
  id             SERIAL PRIMARY KEY,
  nombre         VARCHAR(100) NOT NULL UNIQUE,
  descripcion    TEXT,
  tipo_cobro     VARCHAR(10) NOT NULL CHECK (tipo_cobro IN ('KILO','PAQUETE')),
  precio_unitario NUMERIC(10,2) NOT NULL,
  estado         BOOLEAN DEFAULT TRUE
);

-- descuento en porcentaje (1..100)
CREATE TABLE promocion (
  id           SERIAL PRIMARY KEY,
  nombre       VARCHAR(120) NOT NULL,
  descripcion  TEXT,
  descuento    NUMERIC(5,2) NOT NULL CHECK (descuento > 0 AND descuento <= 100),
  estado       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE promocion_servicio (
  id_promocion  INT NOT NULL,
  id_servicio   INT NOT NULL,
  fecha_inicio  DATE NOT NULL,
  fecha_final   DATE NOT NULL,
  PRIMARY KEY (id_promocion, id_servicio),
  CONSTRAINT fk_ps_promocion
    FOREIGN KEY (id_promocion) REFERENCES promocion(id) ON DELETE CASCADE,
  CONSTRAINT fk_ps_servicio
    FOREIGN KEY (id_servicio)  REFERENCES servicio(id)  ON DELETE CASCADE,
  CONSTRAINT chk_ps_fechas CHECK (fecha_final >= fecha_inicio)
);

-- ===============================
-- orden / orden_detalle
-- ===============================
CREATE TABLE orden (
  nro               VARCHAR(20) PRIMARY KEY,
  fecha_recepcion   DATE NOT NULL,
  fecha_listo       DATE,
  fecha_entrega     DATE,
  estado            VARCHAR(20) NOT NULL
                       CHECK (estado IN ('PENDIENTE','LISTA','ENTREGADA'))
                       DEFAULT 'PENDIENTE',
  forma_pago        VARCHAR(10) NOT NULL
                       CHECK (forma_pago IN ('CONTADO','CREDITO'))
                       DEFAULT 'CONTADO',
  fecha_vencimiento DATE,                 -- opcional (para crédito)
  subtotal          NUMERIC(10,2) NOT NULL DEFAULT 0,
  descuento         NUMERIC(10,2) NOT NULL DEFAULT 0,
  total             NUMERIC(10,2) NOT NULL DEFAULT 0,
  cliente_id        INT NOT NULL REFERENCES cliente(id),
  empleado_id       INT NOT NULL REFERENCES empleado(id),
  observaciones     TEXT
);

CREATE TABLE orden_detalle (
  id               SERIAL PRIMARY KEY,
  orden_nro        VARCHAR(20) NOT NULL
                      REFERENCES orden(nro)
                      ON UPDATE CASCADE ON DELETE CASCADE,
  servicio_id      INT NOT NULL REFERENCES servicio(id),
  unidad           VARCHAR(10) NOT NULL CHECK (unidad IN ('KILO','PIEZA')),
  peso_kg          NUMERIC(10,2),          -- usar cuando unidad='KILO'
  cantidad         INT,                    -- usar cuando unidad='PIEZA'
  precio_unitario  NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
  descuento        NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (descuento >= 0), -- monto por línea
  fragancia        VARCHAR(50),
  notas            TEXT,
  subtotal         NUMERIC(10,2) NOT NULL DEFAULT 0,
  total_linea      NUMERIC(10,2) NOT NULL DEFAULT 0
);

-- ===============================
-- equipo / mantenimiento
-- ===============================
CREATE TABLE equipo (
  codigo                 VARCHAR(20) PRIMARY KEY,
  nombre                 VARCHAR(100) NOT NULL,
  tipo                   VARCHAR(30)  NOT NULL CHECK (tipo IN ('LAVADORA','SECADORA','PLANCHADORA','OTRO')),
  marca                  VARCHAR(50),
  modelo                 VARCHAR(50),
  fecha_compra           DATE,
  capacidad_kg           NUMERIC(10,2),
  consumo_electrico_kw   NUMERIC(12,2),
  consumo_agua_litros    NUMERIC(12,2),
  estado                 VARCHAR(30)  NOT NULL
                           CHECK (estado IN ('LIBRE','OCUPADO','MANTENIMIENTO','FUERA_SERVICIO'))
                           DEFAULT 'LIBRE'
);

CREATE TABLE mantenimiento (
  id             SERIAL PRIMARY KEY,
  equipo_codigo  VARCHAR(20) NOT NULL
                    REFERENCES equipo(codigo)
                    ON UPDATE CASCADE ON DELETE CASCADE,
  fecha          DATE NOT NULL,
  descripcion    TEXT,
  costo          NUMERIC(10,2) DEFAULT 0
);

-- ===============================
-- orden_proceso
-- ===============================
CREATE TABLE orden_proceso (
  id                      SERIAL PRIMARY KEY,
  orden_nro               VARCHAR(20) NOT NULL
                             REFERENCES orden(nro)
                             ON UPDATE CASCADE ON DELETE CASCADE,
  equipo_codigo           VARCHAR(20) NOT NULL
                             REFERENCES equipo(codigo)
                             ON UPDATE CASCADE,
  etapa                   VARCHAR(20) NOT NULL
                             CHECK (etapa IN ('LAVADO','SECADO','PLANCHADO')),
  ciclos                  INT NOT NULL DEFAULT 1 CHECK (ciclos > 0),
  duracion_ciclo          INT,                 -- minutos
  estado                  VARCHAR(20) NOT NULL
                             CHECK (estado IN ('PENDIENTE','EN PROCESO','FINALIZADO'))
                             DEFAULT 'PENDIENTE',
  observacion             TEXT,
  kwh_consumidos          NUMERIC(12,2),
  agua_litros_consumidos  NUMERIC(12,2)
);

-- ===============================
-- insumo / inventario / proceso_insumo
-- ===============================
CREATE TABLE insumo (
  codigo          VARCHAR(20) PRIMARY KEY,
  nombre          VARCHAR(100) NOT NULL,
  unidad_medida   VARCHAR(10) NOT NULL CHECK (unidad_medida IN ('GR','ML')),
  stock_actual    NUMERIC(10,2) NOT NULL DEFAULT 0,  -- (si decides llevar stock aquí)
  stock_min       NUMERIC(10,2) NOT NULL DEFAULT 0,
  stock_max       NUMERIC(10,2) NOT NULL DEFAULT 0,
  costo_promedio  NUMERIC(10,2),
  estado          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE inventario (
  id              SERIAL PRIMARY KEY,
  insumo_codigo   VARCHAR(20) NOT NULL
                    REFERENCES insumo(codigo)
                    ON UPDATE CASCADE,
  tipo            VARCHAR(20) CHECK (tipo IN ('INGRESO','AJUSTE','BAJA','RETORNO')),
  fecha           DATE DEFAULT CURRENT_DATE,
  cantidad        INT NOT NULL,               -- entero (unidades de insumo)
  costo_unitario  NUMERIC(10,2),
  referencia      TEXT,
  empleado_id     INT REFERENCES empleado(id),
  proveedor_id    INT REFERENCES proveedor(id)
);

CREATE TABLE proceso_insumo (
  id              SERIAL PRIMARY KEY,
  proceso_id      INT NOT NULL REFERENCES orden_proceso(id) ON DELETE CASCADE,
  insumo_codigo   VARCHAR(20) NOT NULL
                     REFERENCES insumo(codigo)
                     ON UPDATE CASCADE,
  cantidad        INT NOT NULL                -- entero (ml/gr)
);

-- ===============================
-- pago
-- ===============================
CREATE TABLE pago (
  id          SERIAL PRIMARY KEY,
  orden_nro   VARCHAR(20) NOT NULL
                 REFERENCES orden(nro) ON DELETE CASCADE,
  fecha       TIMESTAMP NOT NULL DEFAULT NOW(),
  monto       NUMERIC(10,2) NOT NULL CHECK (monto > 0),
  metodo      VARCHAR(20) NOT NULL CHECK (metodo IN ('EFECTIVO','TRANSFERENCIA')),
  referencia  VARCHAR(120)
);
