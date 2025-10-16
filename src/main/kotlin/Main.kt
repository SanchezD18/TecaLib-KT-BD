import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

// Ruta al archivo de base de datos SQLite
const val URL_BD = "jdbc:sqlite:src/main/resources/TecaLib.sqlite"

// Obtener conexión
fun getConnection(): Connection? {
    return try {
        DriverManager.getConnection(URL_BD)
    } catch (e: SQLException) {
        e.printStackTrace()
        null
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    var seguir = true
    while (seguir) {
        menu()
        print("Selecciona una opción: ")
        try {
            val opcion = scanner.nextInt()
            scanner.nextLine()
            when (opcion) {
                1 -> {
                    println()
                    println("--- Mostrar libros ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                            println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
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
                        println("Libro encontrado: - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
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
                            precio = precio
                        )
                    )
                }
                4 -> {
                    println()
                    println("--- Modificar precio ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
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

                    LibrosDAO.actualizarLibro(Libro(
                        id = idLibro,
                        titulo = nuevoTitulo,
                        autor = nuevoAutor,
                        editorial = nuevaEditorial,
                        precio = nuevoPrecio
                    ))

                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
                    }
                }
                5 -> {
                    println()
                    println("--- Eliminar libro ---")
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
                    }
                    print("ID del libro a eliminar: ")
                    val id = scanner.nextInt()
                    scanner.nextLine()
                    LibrosDAO.eliminarLibro(id)
                    LibrosDAO.listarLibros().forEach { libro ->
                        println("  - ID: ${libro.id}, Título: ${libro.titulo}, Autor: ${libro.autor}, Editorial: ${libro.editorial}, Precio: ${libro.precio}€")
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
    }
    scanner.close()
}

fun menu() {
    println()
    println("---------------------------------")
    println(" - Menú DaviTeca")
    println("1. Mostrar libros")
    println("2. Mostrar libro (por ID)")
    println("3. Añadir libro")
    println("4. Modificar libro (por ID)")
    println("5. Eliminar libro (por ID)")
    println("0. Salir")
    println("---------------------------------")
}







