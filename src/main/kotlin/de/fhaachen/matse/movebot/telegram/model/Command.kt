package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender

abstract class Command(val command: String,
                       desc: String) : BotCommand(command, desc) {

    val permissions = mutableListOf<CommandPermission>()
    val requirements = mutableListOf<Requirement>()
    val parameters = mutableListOf<Parameter>()

    fun onlyUserChat() = requirements.add(userchatRequirement)

    override fun execute(sender: AbsSender, user: User, chat: Chat, params: Array<out String>) {
        if (!checkPermissions(user, chat))
            return

        if (!checkRequirements(user, chat, params))
            return

        parseParametersAndExecute(sender, user, chat, params.toMutableList())

    }

    /**
     * Parse the given Parameters until it fits.
     *
     * If any Parameter is missing it will return
     */
    fun parseParametersAndExecute(sender: AbsSender, user: User, chat: Chat, params: MutableList<String>) {
        val parsedValues = mutableListOf<String>()

        parameters.forEach { p ->
            if (params.isEmpty() || params.first() == "<REQUEST>") {
                if (p.optional || (params.isNotEmpty() && params.first() != "<REQUEST>")) return@forEach
                else return p.request(chat, this, parsedValues, params.drop(1))
            }

            val value = if (p.singleWord) params.removeAt(0) else params.joinToString(" ").trim()

            if (!p.isValueAllowed(value))
                return p.request(chat, this, parsedValues, params, value)

            parsedValues.add(value)
        }

        handle(sender, user, chat, parsedValues)
    }

    private fun checkPermissions(user: User, chat: Chat): Boolean {
        permissions.forEach {
            if (!it.check(user, chat)) {
                sendMessage(chat, "Du hast nicht die nötigen Rechte für diese Funktion!\n${it.message}")
                return false
            }
        }
        return true
    }

    private fun checkRequirements(user: User, chat: Chat, params: Array<out String>): Boolean {
        requirements.forEach {
            if (!it.check(user, chat, params)) {
                sendMessage(chat, "Du erfüllst nicht alle Vorraussetzungen für diese Funktion!\n${it.message}")
                return false
            }
        }
        return true
    }

    abstract fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>)


    fun sendMessage(chat: Chat, message: String, keyboard: ReplyKeyboard? = null): Message {
        return ChallengeBot.sendMessage(chat.id, message, keyboard).also { MessageHandler.addDeleteableMessage(it, MessageType.COMMAND_PROCESS) }
    }

    fun sendComplete(chat: Chat, message: String, keyboard: ReplyKeyboard? = null): Message {
        return ChallengeBot.sendMessage(chat.id, message, keyboard).also {
            MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.COMMAND_COMPLETE)
        }//.also { MessageHandler.addDeleteableMessage(it, MessageType.COMMAND_COMPLETE) }
    }
}