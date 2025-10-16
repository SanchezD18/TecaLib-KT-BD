data class Libro(
    val id: Int? = null, // lo genera SQLite automáticamente
    val titulo: String,
    val autor: String,
    val editorial: String,
    val precio: Double
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
                            id = rs.getInt("id"),
                            titulo = rs.getString("titulo"),
                            autor = rs.getString("autor"),
                            editorial = rs.getString("editorial"),
                            precio = rs.getDouble("precio")
                        )
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return lista
    }

    // Consultar libro por ID
    fun consultarLibroPorID(id: Int): Libro? {
        var planta: Libro? = null
        getConnection()?.use { conn ->
            conn.prepareStatement("SELECT * FROM Libros WHERE id = ?").use { pstmt ->
                pstmt.setInt(1, id)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    planta = Libro(
                        id = rs.getInt("id"),
                        titulo = rs.getString("titulo"),
                        autor = rs.getString("autor"),
                        editorial = rs.getString("editorial"),
                        precio = rs.getDouble("precio")
                    )
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return planta
    }

    fun insertarLibro(libro: Libro) {
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO Libros(titulo, autor, editorial, precio) VALUES (?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setString(1, libro.titulo)
                pstmt.setString(2, libro.autor)
                pstmt.setString(3, libro.editorial)
                pstmt.setDouble(4, libro.precio)
                pstmt.executeUpdate()
                println("Libro '${libro.titulo}' insertada con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun actualizarLibro(libro: Libro) {
        if (libro.id == null) {
            println("No se puede actualizar una planta sin id.")
            return
        }
        getConnection()?.use { conn ->
            conn.prepareStatement(
                "UPDATE Libros SET titulo = ?, autor = ?, editorial = ?, precio = ? WHERE id = ?"
            ).use { pstmt ->
                pstmt.setString(1, libro.titulo)
                pstmt.setString(2, libro.autor)
                pstmt.setString(3, libro.editorial)
                pstmt.setDouble(4, libro.precio)
                pstmt.setInt(5, libro.id)
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
            conn.prepareStatement("DELETE FROM Libros WHERE id = ?").use { pstmt ->
                pstmt.setInt(1, id)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Libro con id=$id eliminado correctamente.")
                } else {
                    println("No se encontró ningun libro con id=$id.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}