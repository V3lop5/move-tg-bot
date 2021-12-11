package de.fhaachen.matse.movebot.telegram.commands

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File
import java.time.LocalDateTime

object ExportCSVCommand : ChallengerCommand("exportcsv", "Exportiert die Daten als CSV.") {

    init {
        onlyUserChat()
        permissions.add(adminPermission)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {


        csvWriter().open("export.csv") {
            ChallengerManager.challengers.flatMap { challenger ->
                challenger.movements.map { movement ->
                    writeRow(
                        challenger.nickname,
                        movement.datetime.prettyString(),
                        movement.type,
                        movement.value,
                        movement.points
                    )
                }
            }
        }

        ChallengeBot.sendDocument(chat.id, File("export.csv"), "Datenexport von ${LocalDateTime.now().prettyString()}")
    }
}