import java.io.File
import java.sql.DriverManager

fun main() {
    // Ruta al archivo de BD SQLite
    val dbPath = "src/main/resources/TecaLib.sqlite"

    println("Ruta de la BD: ${dbPath}")
    val url = "jdbc:sqlite:${dbPath}"

    // Conexión y prueba
    DriverManager.getConnection(url).use { conn ->
        println("Conexión establecida correctamente con SQLite.")
    }
}