package de.fhaachen.matse.movebot.control

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.fhaachen.matse.movebot.model.Challenger
import java.io.File
import java.io.FileReader
import java.io.FileWriter

val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        .create()

val challengerFile = File("challengers.json")


fun loadChallengers() = (if (challengerFile.exists()) FileReader(challengerFile).use {
    gson.fromJson<MutableSet<Challenger>>(it, object : TypeToken<MutableSet<Challenger>>() {}.type)
} else null) ?: mutableSetOf()

fun saveChallengers(challengers: Set<Challenger>) {
    backup(challengerFile)
    try {
        FileWriter(challengerFile).use { it.write(gson.toJson(challengers)) }
    } catch (e: Exception) {
        // TODO Über Events regeln. Quick and dirty...
        ChallengerManager.sendError("Speichern fehlgeschlagen", e)
        restore(challengerFile)
    }
}


fun getBackupFile(file: File) = File(file.absolutePath + ".backup")

private fun backup(file: File) {
    if (!file.exists()) return

    val backupFile = getBackupFile(file)

    if (!backupFile.exists())
        backupFile.createNewFile()

    file.copyTo(backupFile, overwrite = true)
}


private fun restore(file: File) {
    val backupFile = getBackupFile(file)

    if (!backupFile.exists()) return

    if (!file.exists())
        file.createNewFile()

    backupFile.copyTo(file, overwrite = true)

}