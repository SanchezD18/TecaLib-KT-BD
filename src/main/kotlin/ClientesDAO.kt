import java.util.*

data class Cliente(
    val dni: String,
    val nombre: String,
    val apellidos: String,
    val telefono: Int,
    val email: String,
)

object ClientesDAO {

    // Listar todos los clientes
    fun listarClientes(): List<Cliente> {
        val lista = mutableListOf<Cliente>()
        getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM Clientes")
                while (rs.next()) {
                    lista.add(
                        Cliente(
                            dni = rs.getString("dni"),
                            nombre = rs.getString("nombre"),
                            apellidos = rs.getString("apellidos"),
                            telefono = rs.getInt("telefono"),
                            email = rs.getString("email"),
                        )
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return lista
    }

    // Consultar cliente por DNI
    fun consultarClientePorDNI(dni: String): Cliente? {
        var cliente: Cliente? = null
        getConnection()?.use { conn ->
            conn.prepareStatement("SELECT * FROM Clientes WHERE dni = ?").use { pstmt ->
                pstmt.setString(1, dni)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    cliente = Cliente(
                        dni = rs.getString("dni"),
                        nombre = rs.getString("nombre"),
                        apellidos = rs.getString("apellidos"),
                        telefono = rs.getInt("telefono"),
                        email = rs.getString("email"),
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return cliente
    }

    // Insertar cliente completo
    fun insertarCliente(cliente: Cliente) {
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO Clientes(dni, nombre, apellidos, telefono, email) VALUES (?, ?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setString(1, cliente.dni)
                pstmt.setString(2, cliente.nombre)
                pstmt.setString(3, cliente.apellidos)
                pstmt.setInt(4, cliente.telefono)
                pstmt.setString(5, cliente.email)
                pstmt.executeUpdate()
                println("Cliente '${cliente.nombre}' insertado con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Actualizar cliente por DNI
    fun actualizarCliente(cliente: Cliente) {
        if (cliente.dni == null) {
            println("No se puede actualizar un cliente sin dni.")
            return
        }
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "UPDATE Clientes SET dni = ?, nombre = ?, apellidos = ?, telefono = ?, email = ? WHERE id_cliente = ?"
            ).use { pstmt ->
                pstmt.setString(1, cliente.dni)
                pstmt.setString(2, cliente.nombre)
                pstmt.setString(3, cliente.apellidos)
                pstmt.setInt(4, cliente.telefono)
                pstmt.setString(5, cliente.email)
                pstmt.setString(6, cliente.dni)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Cliente con DNI = ${cliente.dni} actualizado con éxito.")
                } else {
                    println("No se encontró ningún cliente con DNI = ${cliente.dni}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Eliminar cliente por DNI
    fun eliminarCliente(dni: String) {
        getConnection()?.use { conn ->
            conn.prepareStatement("DELETE FROM Clientes WHERE dni = ?").use { pstmt ->
                pstmt.setString(1, dni)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Cliente con DNI = $dni eliminado correctamente.")
                } else {
                    println("No se encontró ningún cliente con DNI = $dni.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}



fun menuClientes(){
    val scanner = Scanner(System.`in`)
    var seguir = true
    while (seguir) {
        println()
        println("---------------------------------")
        println(" - Menú Clientes")
        println("1. Mostrar clientes")
        println("2. Mostrar cliente (por DNI)")
        println("3. Añadir cliente")
        println("4. Modificar cliente (por DNI)")
        println("5. Eliminar cliente (por DNI)")
        println("0. Atrás")
        println("---------------------------------")
        print("Selecciona una opción: ")
        try {
            val opcion = scanner.nextInt()
            scanner.nextLine()
            when (opcion) {
                1 -> {
                    println()
                    println("--- Mostrar clientes ---")
                    ClientesDAO.listarClientes().forEach { cliente ->
                        println("  - DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    }
                }
                2 -> {
                    println()
                    println("--- Mostrar libro por DNI ---")
                    print("Introduce el DNI: ")
                    val dniCliente = scanner.nextLine()

                    val cliente = ClientesDAO.consultarClientePorDNI(dniCliente)
                    if (cliente != null) {
                        println("Cliente encontrado:- DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    } else {
                        println("No se encontró ningún cliente con ese DNI.")
                    }
                }
                3 -> {
                    println("--- Añadir cliente ---")
                    print("DNI: ")
                    val dni = scanner.nextLine()
                    print("Nombre: ")
                    val nombre = scanner.nextLine()
                    print("Apellidos: ")
                    val apellidos = scanner.nextLine()
                    print("Teléfono: ")
                    val telefono = scanner.nextInt()
                    scanner.nextLine()
                    print("Email: ")
                    val email = scanner.nextLine()
                    ClientesDAO.insertarCliente(
                        Cliente(
                            dni = dni,
                            nombre = nombre,
                            apellidos = apellidos,
                            telefono = telefono,
                            email = email
                        )
                    )
                }

                4 -> {
                    println()
                    println("--- Modificar precio ---")
                    ClientesDAO.listarClientes().forEach { cliente ->
                        println("  - DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    }
                    print("DNI del cliente a modificar: ")
                    val DNICliente = scanner.nextLine()
                    print("Nuevo Nombre: ")
                    val nuevoNombre = scanner.nextLine()
                    print("Nuevos Apellidos: ")
                    val nuevosApellidos = scanner.nextLine()
                    print("Nuevo teléfono: ")
                    val nuevoTelefono = scanner.nextInt()
                    scanner.nextLine()
                    print("Nuevo email: ")
                    val nuevoEmail = scanner.nextLine()

                    ClientesDAO.actualizarCliente(Cliente(
                        dni = DNICliente,
                        nombre = nuevoNombre,
                        apellidos = nuevosApellidos,
                        telefono = nuevoTelefono,
                        email = nuevoEmail
                    ))

                    ClientesDAO.listarClientes().forEach { cliente ->
                        println("  - DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    }
                }
                5 -> {
                    println()
                    println("--- Eliminar cliente ---")
                    ClientesDAO.listarClientes().forEach { cliente ->
                        println("  - DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    }
                    print("ID del libro a eliminar: ")
                    val dni = scanner.nextLine()
                    ClientesDAO.eliminarCliente(dni)
                    ClientesDAO.listarClientes().forEach { cliente ->
                        println("  - DNI: ${cliente.dni}, Nombre completo: ${cliente.nombre} ${cliente.apellidos}  Teléfono: ${cliente.telefono}, Email: ${cliente.email}€")
                    }
                }
                0 -> {
                    println("¡Atrás!")
                    seguir = false
                }
                else -> {
                    println("Opción no válida. Por favor, selecciona una opción del 0 al 6.")
                }
            }

        } catch (e: InputMismatchException) {
            println("Error: Debes introducir un número válido.")
            scanner.nextLine()
        } catch (e: Exception) {
            println("Error: ${e.message}")
            scanner.nextLine()
        }
    }}