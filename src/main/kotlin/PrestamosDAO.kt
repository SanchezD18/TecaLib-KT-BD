import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class Prestamo(
    val idPrestamo: Int? = null,    //ID autoincremental
    val fechaPrestamo: String,
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

    // Crear prestamo completo
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
                        throw SQLException("El libro con ID ${prestamo.idLibro} no existe")
                    }
                }
                conn.commit()
                println("Préstamo creado con éxito y libro marcado como no disponible.")

            } catch (e: SQLException) {
                conn.rollback()
                println("Error al crear el préstamo: ${e.message}")
                throw e
            } finally {
                conn.autoCommit = true
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Actualizar prestamo por ID
    fun actualizarPrestamo(prestamo: Prestamo) {
        if (prestamo.idPrestamo == null) {
            println("No se puede actualizar un prestamo sin id.")
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
                    println("Prestamos con ID = ${prestamo.idPrestamo} actualizado con éxito.")
                } else {
                    println("No se encontró ningún prestamo con ID = ${prestamo.idPrestamo}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Eliminar prestamo por ID
    fun eliminarPrestamo(idPrestamo: Int) {
        getConnection()?.use { conn ->
            conn.prepareStatement("DELETE FROM Prestamo WHERE id_prestamo = ?").use { pstmt ->
                pstmt.setInt(1, idPrestamo)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Prestamo con ID = $idPrestamo eliminado correctamente.")
                } else {
                    println("No se encontró ningún prestamo con ID = $idPrestamo.")
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
        println("3. Añadir prestamo")
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
                    print("Introduce el ID: ")
                    val idPrestamo = scanner.nextInt()

                    val prestamo = PrestamosDAO.consultarPrestamoPorID(idPrestamo)
                    if (prestamo != null) {
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
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

                    val libro = LibrosDAO.obtenerLibroPorId(idLibro)
                    if (libro == null) {
                        println("Error: El libro con ID $idLibro no existe.")
                    } else if (!libro.disponible) {
                        println("Error: El libro '${libro.titulo}' no está disponible para préstamo.")
                    } else {
                        print("Introduce tu DNI: ")
                        val dni = scanner.nextLine()
                        print("Introduce la fecha de hoy: (Formato - DD/MM/AAAA)")
                        val fechaPrestamo = scanner.nextLine()
                        print("Introduce la fecha de devolucion: (Formato - DD/MM/AAAA)")
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
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
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
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                }
                5 -> {
                    println()
                    println("--- Eliminar cliente ---")
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
                    }
                    print("ID del libro a eliminar: ")
                    val idPrestamo = scanner.nextInt()
                    scanner.nextLine()
                    PrestamosDAO.eliminarPrestamo(idPrestamo)
                    PrestamosDAO.listarPrestamos().forEach { prestamo ->
                        println("Prestamo encontrado - Prestamo : ${prestamo.idPrestamo}, Cliente: ${prestamo.dni} Libro: ${prestamo.idLibro}, Fecha prestamo: ${prestamo.fechaPrestamo}, Fecha Devolución ${prestamo.fechaDevolucion}")
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
    }
}

