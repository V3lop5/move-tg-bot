package de.fhaachen.matse.movebot.control

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.fhaachen.matse.movebot.LocalDateTimeAdapter
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Team
import de.fhaachen.matse.movebot.model.TeamFight
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime

val gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
    .registerTypeHierarchyAdapter(LocalDateTime::class.java, LocalDateTimeAdapter)
    .create()

val challengerFile = File("challengers.json")
val teamsFile = File("teams.json")
val fightsFile = File("fights.json")


fun loadChallengers() = (if (challengerFile.exists()) FileReader(challengerFile).use {
    gson.fromJson<MutableSet<Challenger>>(it, object : TypeToken<MutableSet<Challenger>>() {}.type)
} else null) ?: mutableSetOf()

fun loadTeams() = (if (teamsFile.exists()) FileReader(teamsFile).use {
    gson.fromJson<MutableSet<Team>>(it, object : TypeToken<MutableSet<Team>>() {}.type)
} else null) ?: mutableSetOf()

fun loadFights() = (if (fightsFile.exists()) FileReader(fightsFile).use {
    gson.fromJson<MutableSet<TeamFight>>(it, object : TypeToken<MutableSet<TeamFight>>() {}.type)
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

fun saveTeams(teams: Set<Team>) {
    backup(teamsFile)
    try {
        FileWriter(teamsFile).use { it.write(gson.toJson(teams)) }
    } catch (e: Exception) {
        // TODO Über Events regeln. Quick and dirty...
        ChallengerManager.sendError("Speichern fehlgeschlagen", e)
        restore(teamsFile)
    }
}

fun saveFights(teams: Set<TeamFight>) {
    backup(fightsFile)
    try {
        FileWriter(fightsFile).use { it.write(gson.toJson(teams)) }
    } catch (e: Exception) {
        // TODO Über Events regeln. Quick and dirty...
        ChallengerManager.sendError("Speichern fehlgeschlagen", e)
        restore(fightsFile)
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