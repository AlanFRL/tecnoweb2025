Casos de Uso Aprobados

CU1: Gestión de Usuarios (Propietario, Empleado, Cliente, Proveedor)
CU2: Gestión de Equipos
CU3: Gestión de Insumos
CU4: Gestión de Inventario
CU5: Gestión de Órdenes
CU6: Gestión de Promociones
CU7: Gestión de Pagos (Contado, Crédito)
CU8: Reportes y Estadísticas


COMANDOS PARA EL CU1 GESTIÓN DE USUARIOS:
	propietario help
	propietario listar
	propietario agregar [nombre, telefono, email, password, razon_social]
	propietario modificar [id_propietario, nombre, telefono, email, password, razon_social]
	propietario eliminar [id_propietario]

	empleado help
	empleado listar
	empleado agregar [nombre, telefono, email, password, cargo, fecha_contratacion]
	empleado modificar [id_empleado, nombre, telefono, email, password, cargo, fecha_contratacion]
	empleado eliminar [id_empleado]
	
	cliente help
	cliente listar
	cliente agregar [nombre, direccion, telefono]
	cliente modificar [id_cliente, nombre, direccion, telefono]
	cliente eliminar [id_cliente]

	proveedor help
	proveedor listar
	proveedor agregar [razon_social, empresa, telefono]
	proveedor modificar [id_proveedor, razon_social, empresa, telefono]
	proveedor eliminar [id_proveedor]