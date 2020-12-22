package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.botName
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.AllowedValuesParameter
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.Parameter
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object HelpCommand : Command("help", "Was kann der *$botName* überhaupt? Finde es mit diesem Befehl heraus!") {

    init {
        parameters.add(Parameter("Command", "Bitte gebe den *Command* ein.", optional = true))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>) {
        if (params.isEmpty()) {
            val helpMessage = ChallengeBot.registeredCommands
                    .asSequence()
                    .sortedBy { it.commandIdentifier }
                    .mapNotNull { it as? Command }
                    .filter { command -> command.permissions.filterNot { it.check(user, chat) }.none() }
                    .map { helpMessageFromCommand(it) }
                    .fold("*$botName*") { a, b -> "$a\n\n$b" }

            sendComplete(chat, helpMessage)
        } else {
            val command = ChallengeBot.registeredCommands.find { it.commandIdentifier == params[0].toLowerCase() }
            if (command == null) {
                sendMessage(chat, "Der Command ${params[0]} existiert nicht.")
                return
            }

            sendComplete(chat, helpMessageFromCommand(command as Command))
        }

    }

    private fun helpMessageFromCommand(command: Command): String {
        var commandTemplate = "/${command.command}"
        if (command.parameters.isNotEmpty()) {
            commandTemplate += " *${command.parameters.joinToString(" ") { "[${if (it is AllowedValuesParameter) it.values.joinToString("|") else it.name}]" }}*"
        }
        return "$commandTemplate\n" +
                command.description
    }
}