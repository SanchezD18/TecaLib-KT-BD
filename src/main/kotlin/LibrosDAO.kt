import java.util.*

data class Libro(
    val id: Int? = null, // Lo genera SQLite automáticamente
    val titulo: String,
    val autor: String,
    val editorial: String,
    val precio: Double,
    val disponible: Boolean
)

object LibrosDAO {

    fun listarLibros(): List<Libro> {
        val lista = mutableListOf<Libro>()
        getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT * FROM Libros")
                while (rs.next()) {
                    lista.add(
                        Libro(
                            id = rs.getInt("id_libro"),
                            titulo = rs.getString("titulo"),
                            autor = rs.getString("autor"),
                            editorial = rs.getString("editorial"),
                            precio = rs.getDouble("precio"),
                            disponible = rs.getInt("disponible") == 1
                        )
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return lista
    }

    // Consultar libro por ID
    fun consultarLibroPorID(id: Int): Libro? {
        var libro: Libro? = null
        getConnection()?.use { conn ->
            conn.prepareStatement("SELECT * FROM Libros WHERE id_libro = ?").use { pstmt ->
                pstmt.setInt(1, id)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    libro = Libro(
                        id = rs.getInt("id_libro"),
                        titulo = rs.getString("titulo"),
                        autor = rs.getString("autor"),
                        editorial = rs.getString("editorial"),
                        precio = rs.getDouble("precio"),
                        disponible = rs.getInt("disponible") == 1
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return libro
    }

    fun insertarLibro(libro: Libro) {
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO Libros(titulo, autor, editorial, precio, disponible) VALUES (?, ?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setString(1, libro.titulo)
                pstmt.setString(2, libro.autor)
                pstmt.setString(3, libro.editorial)
                pstmt.setDouble(4, libro.precio)
                pstmt.setInt(5, 1)
                pstmt.executeUpdate()
                println("Libro '${libro.titulo}' insertada con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun actualizarLibro(libro: Libro) {
        if (libro.id == null) {
            println("No se puede actualizar un libro sin id.")
            return
        }
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "UPDATE Libros SET titulo = ?, autor = ?, editorial = ?, precio = ?, disponible = ? WHERE id_libro = ?"
            ).use { pstmt ->
                val disponible: Int = if(libro.disponible) {
                    1
                } else{
                    0
                }
                pstmt.setString(1, libro.titulo)
                pstmt.setString(2, libro.autor)
                pstmt.setString(3, libro.editorial)
                pstmt.setDouble(4, libro.precio)
                pstmt.setInt(5, disponible)
                pstmt.setInt(6, libro.id)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Libro con id=${libro.id} actualizado con éxito.")
                } else {
                    println("No se encontró ningun libro con id=${libro.id}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun eliminarLibro(id: Int) {
        getConnection()?.use { conn ->
            conn.prepareStatement("DELETE FROM Libros WHERE id_libro = ?").use { pstmt ->
                pstmt.setInt(1, id)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Libro con id=$id eliminado correctamente.")
                } else {
                    println("No se encontró ningún libro con id=$id.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}


fun menuLibros(){
    val scanner = Scanner(System.`in`)
    var seguir = true
    while (seguir) {
        println()
        println("---------------------------------")
        println(" - Menú Libros")
        println("1. Mostrar libros")
        println("2. Mostrar libro (por ID)")
        println("3. Añadir libro")
        println("4. Modificar libro (por ID)")
        println("5. Eliminar libro (por ID)")
        println("6. Mostrar precio total del inventario")
        println("7. Mostrar libros disponibles en linea")
        println("0. Salir")
        println("---------------------------------")
        print("Selecciona una opción: ")
        try {
            val opcion = scanner.nextInt()
            scanner.nextLine()
            when (opcion) {
                1 -> {
                    println()
                    println("--- Mostrar libros ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                }
                2 -> {
                    println()
                    println("--- Mostrar libro por ID ---")
                    print("Introduce el ID: ")
                    val idlibro = scanner.nextInt()
                    scanner.nextLine()

                    val libro = LibrosDAO.consultarLibroPorID(idlibro)
                    if (libro != null) {
                        println("Libro encontrado: - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    } else {
                        println("No se encontró ningun libro con ese ID.")
                    }
                }
                3 -> {
                    println("--- Añadir libro ---")
                    print("Título: ")
                    val titulo = scanner.nextLine()
                    print("Autor: ")
                    val autor = scanner.nextLine()
                    print("Editorial: ")
                    val editorial = scanner.nextLine()
                    print("Precio: ")
                    val precio = scanner.nextDouble()
                    scanner.nextLine()
                    LibrosDAO.insertarLibro(
                        Libro(
                            titulo = titulo,
                            autor = autor,
                            editorial = editorial,
                            precio = precio,
                            disponible = true
                        )
                    )
                }
                4 -> {
                    println()
                    println("--- Modificar precio ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                    print("ID del libro a modificar: ")
                    val idLibro = scanner.nextInt()
                    scanner.nextLine()
                    print("Nuevo título: ")
                    val nuevoTitulo = scanner.nextLine()
                    print("Nuevo autor: ")
                    val nuevoAutor = scanner.nextLine()
                    print("Nueva editorial: ")
                    val nuevaEditorial = scanner.nextLine()
                    print("Nuevo precio: ")
                    val nuevoPrecio = scanner.nextDouble()
                    scanner.nextLine()
                    print("Disponible (1 Sí - 0 No)")
                    val nuevoDisponible = scanner.nextInt()
                    scanner.nextLine()

                    LibrosDAO.actualizarLibro(Libro(
                        id = idLibro,
                        titulo = nuevoTitulo,
                        autor = nuevoAutor,
                        editorial = nuevaEditorial,
                        precio = nuevoPrecio,
                        disponible = nuevoDisponible == 1
                    ))

                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                }
                5 -> {
                    println()
                    println("--- Eliminar libro ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                    print("ID del libro a eliminar: ")
                    val id = scanner.nextInt()
                    scanner.nextLine()
                    LibrosDAO.eliminarLibro(id)
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€, Disponible: ${libro.disponible}")
                    }
                }
                6 ->{
                    println()
                    println("--- Precio total del inventario ---")
                    llamar_fn_total_valor_inventario()
                }
                7 ->{
                    println()
                    llamar_fn_titulos_disponibles_concatenados()
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




//Funciones MySQL Libros
fun llamar_fn_total_valor_inventario(){
    getConnection()?.use { conn ->
        val sql = "SELECT fn_total_valor_libros()"
        conn.prepareStatement(sql).use { stmt ->
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    val resultado = rs.getDouble(1)
                    println("El precio total de los libros es de: $resultado€")
                }
            }
        }
    }
}

fun llamar_fn_titulos_disponibles_concatenados(){
    getConnection()?.use { conn ->
        val sql = "SELECT fn_titulos_disponibles_concatenados()"
        conn.prepareStatement(sql).use { stmt ->
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    val resultado = rs.getString(1)
                    println("--- Listado de Libros ---")
                    println(resultado)
                }
            }
        }
    }
}

