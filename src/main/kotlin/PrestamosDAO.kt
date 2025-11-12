import java.sql.SQLException
import java.util.*

data class Prestamo(
    val idPrestamo: Int? = null,    //ID autoincremental
    val fechaPrestamo: String,      // He optado por poner las fechas en string, no manejo una expresión regular todavía, pero es que con el tipo Date era imposible de gestionar.
    val fechaDevolucion: String,
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
                            fechaPrestamo = rs.getString("fecha_prestamo"),
                            fechaDevolucion = rs.getString("fecha_devolucion"),
                            dni = rs.getString("dni"),
                            idLibro = rs.getInt("id_libro"),
                        )
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return lista
    }

    // Consultar Préstamo por ID
    fun consultarPrestamoPorID(idPrestamo: Int): Prestamo? {
        val prestamo: Prestamo? = null
        getConnection()?.use { conn ->
            conn.prepareStatement("SELECT * FROM Prestamos WHERE id_prestamo = ?").use { pstmt ->
                pstmt.setInt(1, idPrestamo)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    Prestamo(
                        idPrestamo = rs.getInt("id_prestamo"),
                        fechaPrestamo = rs.getString("fecha_prestamo"),
                        fechaDevolucion = rs.getString("fecha_devolucion"),
                        dni = rs.getString("dni"),
                        idLibro = rs.getInt("id_libro"),
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return prestamo
    }

    // Crear préstamo completo
    fun crearPrestamo(prestamo: Prestamo) {
        getConnection()?.use { conn ->
            try {
                conn.autoCommit = false

                conn.prepareStatement(
                    "INSERT INTO Prestamos(fecha_prestamo, fecha_devolucion, dni, id_libro) VALUES (?, ?, ?, ?)"
                ).use { pstmt ->
                    pstmt.setString(1, prestamo.fechaPrestamo)
                    pstmt.setString(2, prestamo.fechaDevolucion)
                    pstmt.setString(3, prestamo.dni)
                    pstmt.setInt(4, prestamo.idLibro)
                    pstmt.executeUpdate()
                }

                conn.prepareStatement(
                    "UPDATE Libros SET disponible = 0 WHERE id_libro = ?"
                ).use { pstmt ->
                    pstmt.setInt(1, prestamo.idLibro)
                    val filasActualizadas = pstmt.executeUpdate()

                    if (filasActualizadas == 0) {
                        throw SQLException("El libro con ID ${prestamo.idLibro} no existe.")
                    }
                }
                conn.commit()
                println("Préstamo creado con éxito.")

            } catch (e: SQLException) {
                conn.rollback()
                println("Error al crear el préstamo: ${e.message}")
                throw e
            } finally {
                conn.autoCommit = true
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Actualizar préstamo por ID
    fun actualizarPrestamo(prestamo: Prestamo) {
        if (prestamo.idPrestamo == null) {
            println("No se puede actualizar un préstamo sin id.")
            return
        }
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "UPDATE Prestamo SET fecha_prestamo = ?, fecha_devolucion = ?, dni = ?, id_libro = ? WHERE id_prestamo = ?"
            ).use { pstmt ->
                pstmt.setString(1,prestamo.fechaPrestamo)
                pstmt.setString(2, prestamo.fechaDevolucion)
                pstmt.setString(3, prestamo.dni)
                pstmt.setInt(4, prestamo.idLibro)
                pstmt.setInt(5, prestamo.idPrestamo)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Préstamo con ID = ${prestamo.idPrestamo} actualizado con éxito.")
                } else {
                    println("No se encontró ningún prestamo con ID = ${prestamo.idPrestamo}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Eliminar préstamo por ID
    fun eliminarPrestamo(idPrestamo: Int) {
        getConnection()?.use { conn ->
            conn.prepareStatement("DELETE FROM Prestamos WHERE id_prestamo = ?").use { pstmt ->
                pstmt.setInt(1, idPrestamo)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Préstamo con ID = $idPrestamo eliminado correctamente.")
                } else {
                    println("No se encontró ningún préstamo con ID = $idPrestamo.")
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
        println("2. Mostrar préstamo (por ID)")
        println("3. Añadir préstamo")
        println("4. Modificar préstamo (por ID)")
        println("5. Eliminar préstamo (por ID)")
        println("6. Consultar información completa de prestamos.")
        println("7. Devolver libro")
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
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println("  - Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                }
                2 -> {
                    println()
                    println("--- Mostrar prestamo por ID ---")
                    print("Introduce el ID: ")
                    val idPrestamo = scanner.nextInt()

                    val prestamo = PrestamosDAO.consultarPrestamoPorID(idPrestamo)
                    if (prestamo != null) {
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println("Préstamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    } else {
                        println("No se encontró ningún prestamo con ese ID.")
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

                    val libro = LibrosDAO.consultarLibroPorID(idLibro)
                    if (libro == null) {
                        println("Error: El libro con ID $idLibro no existe.")
                    } else if (!libro.disponible) {
                        println("Error: El libro '${libro.titulo}' no está disponible para préstamo.")
                    } else {
                        print("Introduce tu DNI: ")
                        val dni = scanner.nextLine()
                        print("Introduce la fecha de hoy (Formato - DD-MM-AAAA): ")
                        val fechaPrestamo = scanner.nextLine()
                        print("Introduce la fecha de devolucion (Formato - DD-MM-AAAA): ")
                        val fechaDevolucion = scanner.nextLine()

                        PrestamosDAO.crearPrestamo(
                            Prestamo(
                                fechaPrestamo = fechaPrestamo,
                                fechaDevolucion = fechaDevolucion,
                                dni = dni,
                                idLibro = idLibro
                            )
                        )
                    }
                }

                4 -> {
                    println()
                    println("--- Modificar prestamo ---")
                    PrestamosDAO.listarPrestamos().forEach { prestamo  ->
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println("- Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                    print("ID del prestamo a modificar: ")
                    val idPrestamo = scanner.nextInt()
                    print("Fecha prestamo:  ")
                    val nuevaFechaPrestamo = scanner.nextLine()
                    print("Fecha devolución: ")
                    val nuevaFechaDevolucion = scanner.nextLine()
                    print("Nuevo DNI: ")
                    val nuevoDNI = scanner.nextLine()
                    print("Nuevo ID de libro: ")
                    val nuevoIDLibro = scanner.nextInt()
                    scanner.nextLine()


                    PrestamosDAO.actualizarPrestamo(Prestamo(
                        idPrestamo = idPrestamo,
                        fechaPrestamo = nuevaFechaPrestamo,
                        fechaDevolucion = nuevaFechaDevolucion,
                        dni = nuevoDNI,
                        idLibro = nuevoIDLibro
                    ))

                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println("- Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                }
                5 -> {
                    println()
                    println("--- Eliminar prestamo ---")
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println("- Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                    print("ID del prestamo a eliminar: ")
                    val idPrestamo = scanner.nextInt()
                    scanner.nextLine()
                    PrestamosDAO.eliminarPrestamo(idPrestamo)
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println(" - Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                }
                6 ->{
                    println()
                    llamar_sp_obtener_informacion_completa_prestamos()
                }
                7 -> {
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        val tituloLibro = LibrosDAO.consultarLibroPorID(prestamo.idLibro)?.titulo ?: "Desconocido"
                        val nombreCliente = ClientesDAO.consultarClientePorDNI(prestamo.dni)?.nombre ?: "Desconocido"
                        println(" - Préstamo : ${prestamo.idPrestamo}, Cliente: $nombreCliente, Libro: $tituloLibro, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                    println()
                    print("Introduce el id del prestamo que quieres devolver: ")
                    val id_Prestamo = scanner.nextInt()
                    scanner.nextLine()
                    llamar_sp_devolver_libro(id_Prestamo)
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
    }
}



//PROCEDIMIENTOS DE PRESTAMOS
fun llamar_sp_obtener_informacion_completa_prestamos() {
    getConnection()?.use { conn ->
        val sql = "CALL sp_obtener_informacion_completa_prestamos()"
        conn.prepareStatement(sql).use { stmt ->
            stmt.executeQuery().use { rs ->
                println("=== INFORMACIÓN COMPLETA DE PRÉSTAMOS ===")
                println()

                while (rs.next()) {
                    println("ID Préstamo: ${rs.getInt("ID Préstamo")}")
                    println("Fecha Préstamo: ${rs.getString("Fecha Préstamo")}")
                    println("Fecha Devolución: ${rs.getString("Fecha Devolución") ?: "Pendiente"}")
                    println("Cliente: ${rs.getString("Cliente")} (DNI: ${rs.getString("DNI")})")
                    println("Contacto: ${rs.getString("Teléfono")} | ${rs.getString("Email")}")
                    println("Libro: ${rs.getString("Título Libro")}")
                    println("Autor: ${rs.getString("Autor")}")
                    println("Editorial: ${rs.getString("Editorial")}")
                    println("Precio: ${rs.getDouble("Precio")}€")
                    println("Estado: ${rs.getString("Estado")}")
                    println("${"-".repeat(50)}")
                }
            }
        }
    }
}


fun llamar_sp_devolver_libro(idPrestamo: Int) {
    getConnection()?.use { conn ->
        val sql = "CALL sp_devolver_libro(?)"
        conn.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, idPrestamo)

            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    val mensaje = rs.getString("mensaje")
                    println("✓ $mensaje")
                }
            }
        }
    }
}