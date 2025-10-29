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
        println()
        println("---------------------------------")
        println(" ¿Con que tabla quieres trabajar? ")
        println("1. Libros")
        println("2. Clientes")
        println("3. Prestamos")
        print("Selecciona una opción: ")
        try {
            val opcion = scanner.nextInt()
            scanner.nextLine()
            when (opcion) {
                1 -> {
                    menuLibros()
                }
                2 -> {
                   menuClientes()
                }
                3 -> {
                    menuPrestamos()
                }
                0 -> {
                    println("¡Buenas noches!")
                    seguir = false
                }
                else -> {
                    println("Opción no válida. Por favor, selecciona una opción del 0 al 3.")
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








