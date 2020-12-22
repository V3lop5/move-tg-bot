package de.fhaachen.matse.movebot.telegram.model

enum class MessageCleanupCause {
    COMMAND_COMPLETE {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.COMMAND_PROCESS || messageType == MessageType.REQUEST_MESSAGE || messageType == MessageType.REQUESTED_VALUE
        }
    },
    COMMAND_CANCELED {
        override fun isCleaning(messageType: MessageType): Boolean {
            return COMMAND_COMPLETE.isCleaning(messageType)
        }
    },
    COMMAND_PARAMETER_REQUEST_FINISHED {
        override fun isCleaning(messageType: MessageType): Boolean {
            return false
        }
    },
    TUTORIAL_COMMAND {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.TUTORIAL
        }
    },
    CONFIRM_COMPLETE {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.CONFIRM_REQUEST
        }
    },
    REMINDER_CLICKED {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.REMINDER
        }
    },
    NEW_REMINDER {
        override fun isCleaning(messageType: MessageType): Boolean {
            return REMINDER_CLICKED.isCleaning(messageType)
        }
    },
    CONFIRM_CANCELED {
        override fun isCleaning(messageType: MessageType): Boolean {
            return CONFIRM_COMPLETE.isCleaning(messageType)
        }
    },
    WEEKLY_STAT {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.WEEKLY_REMINDER
        }
    },
    SHUTDOWN {
        override fun isCleaning(messageType: MessageType): Boolean {
            return COMMAND_CANCELED.isCleaning(messageType) || CONFIRM_CANCELED.isCleaning(messageType) || REMINDER_CLICKED.isCleaning(messageType) || WEEKLY_STAT.isCleaning(messageType)
        }
    },
    LIVE_LOCATION_START {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.LOCATION_RECIVED
        }
    },
    LIVE_LOCATION_END {
        override fun isCleaning(messageType: MessageType): Boolean {
            return messageType == MessageType.LIVE_LOCATION || LIVE_LOCATION_START.isCleaning(messageType)
        }
    };

    abstract fun isCleaning(messageType: MessageType): Boolean
}