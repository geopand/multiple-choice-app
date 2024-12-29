import android.content.Context
import java.io.FileOutputStream
import java.io.InputStream

object DatabaseUtils {

    fun copyDatabase(context: Context, dbName: String) {
        val dbPath = context.getDatabasePath(dbName)

        // Check if the database already exists
        if (dbPath.exists()) {
            return // Database already exists
        }

        // Ensure the databases directory exists
        dbPath.parentFile?.mkdirs()

        // Copy the database from assets
        context.assets.open(dbName).use { inputStream ->
            FileOutputStream(dbPath).use { outputStream ->
                copyStream(inputStream, outputStream)
            }
        }
    }

    private fun copyStream(inputStream: InputStream, outputStream: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
    }
}
