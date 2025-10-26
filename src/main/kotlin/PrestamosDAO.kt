import java.util.*

data class Prestamo(
    val idPrestamo: Int? = null,
    val fechaPrestamo: Date,
    val fechaDevolucion: Date,
    val dni: String,
    val idLibro: Int,
)

object PrestamosDAO {

    // Listar todos los Prestamos
    fun listarPrestamos(): List<Prestamo> {
        val lista = mutableListOf<Prestamo>()
        getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM Prestamos")
                while (rs.next()) {
                    lista.add(
                        Prestamo(
                            idPrestamo = rs.getInt("id_prestamo"),
                            fechaPrestamo = rs.getDate("fecha_prestamo"),
                            fechaDevolucion = rs.getDate("fecha_devolucion"),
                            dni = rs.getString("dni"),
                            idLibro = rs.getInt("id_libro"),
                        )
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return lista
    }

    // Consultar Prestamo por ID
    fun consultarPrestamoPorID(idPrestamo: Int): Prestamo? {
        val prestamo: Prestamo? = null
        getConnection()?.use { conn ->
            conn.prepareStatement("SELECT * FROM Prestamos WHERE id_prestamo = ?").use { pstmt ->
                pstmt.setInt(1, idPrestamo)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    Prestamo(
                        idPrestamo = rs.getInt("id_prestamo"),
                        fechaPrestamo = rs.getDate("fecha_prestamo"),
                        fechaDevolucion = rs.getDate("fecha_devolucion"),
                        dni = rs.getString("dni"),
                        idLibro = rs.getInt("id_libro"),
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return prestamo
    }

    // Crear prestamo completo
    fun crearPrestamo(prestamo: Prestamo) {
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO Prestamos(fecha_prestamo, fecha_devolucion, dni, id_libro) VALUES (?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setDate(1, prestamo.fechaPrestamo as java.sql.Date?)
                pstmt.setDate(2, prestamo.fechaDevolucion as java.sql.Date?)
                pstmt.setString(3, prestamo.dni)
                pstmt.setInt(4, prestamo.idLibro)
                pstmt.executeUpdate()
                println("Prestamo '${prestamo.idPrestamo}' creado con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    //SEGUIR POR AQUÍ

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



fun menuPrestamos(){
    val scanner = Scanner(System.`in`)
    var seguir = true
    while (seguir) {
        println()
        println("---------------------------------")
        println(" - Menú Prestamos")
        println("1. Mostrar prestamos")
        println("2. Mostrar prestamo (por ID)")
        println("3. Añadir prestamo`")
        println("4. Modificar prestamo (por ID)")
        println("5. Eliminar prestamo (por ID)")
        println("0. Atrás")
        println("---------------------------------")
        print("Selecciona una opción: ")
        try {
            val opcion = scanner.nextInt()
            scanner.nextLine()
            when (opcion) {
                1 -> {
                    println()
                    println("--- Mostrar Prestamos ---")
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        println("  - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                }
                2 -> {
                    println()
                    println("--- Mostrar prestamo por ID ---")
                    print("Introduce el DNI: ")
                    val idPrestamo = scanner.nextInt()

                    val prestamo = PrestamosDAO.consultarPrestamoPorID(idPrestamo)
                    if (prestamo != null) {
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    } else {
                        println("No se encontró ningún cliente con ese DNI.")
                    }
                }
                3 -> {
                    println("--- Tomar prestado libro ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                    print("Introduce el ID del libro: ")
                    val idLibro = scanner.nextInt()
                    scanner.nextLine()
                    print("Email: ")
                    val dni = scanner.nextLine()
                    PrestamosDAO.crearPrestamo(
                        Prestamo(
                            fechaPrestamo = Date(),
                            fechaDevolucion = Date(),
                            dni = dni,
                            idLibro = idLibro
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
                    println("¡Buenas noches!")
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