package de.fhaachen.matse.movebot.telegram.model

data class DeleteableMessage(
        val messageId: Int,
        val chatId: Long,
        val messageType: MessageType
) {

    fun isCleanedBy(cleanupCause: MessageCleanupCause) = cleanupCause.isCleaning(messageType)

}