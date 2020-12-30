package de.fhaachen.matse.movebot.telegram.model

enum class MessageType {
    COMMAND_PROCESS,
    COMMAND_COMPLETE,
    REQUEST_MESSAGE,
    REQUESTED_VALUE,
    CONFIRM_REQUEST,
    REMINDER,
    WEEKLY_REMINDER,
    LOCATION_RECIVED,
    LIVE_LOCATION,
    REQUEST_KEYBOARD
}