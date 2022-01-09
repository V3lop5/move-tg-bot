package de.fhaachen.matse.movebot.telegram.model

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

fun inlineKeyboard(buttons: List<InlineKeyboardButton>, maxColumns: Int = 2): InlineKeyboardMarkup {
    val inlineKeyboard = InlineKeyboardMarkup()
    inlineKeyboard.keyboard = getButtonsOrderd(buttons, maxColumns)
    return inlineKeyboard
}

fun inlineKeyboard(vararg buttons: InlineKeyboardButton, maxColumns: Int = 2) = inlineKeyboard(buttons.toList(), maxColumns)

fun inlineKeyboardFromPair(vararg pairs: Pair<String, String>, maxColumns: Int = 2) = inlineKeyboardFromPair(pairs.toList(), maxColumns)

fun inlineKeyboardFromPair(pairs: List<Pair<String, String>>, maxColumns: Int = 2) = inlineKeyboard(pairs.map {
    val button = InlineKeyboardButton(it.first)
    button.callbackData = it.second
    return@map button
}, maxColumns)


fun getButtonsOrderd(btns: List<InlineKeyboardButton>, maxColumns: Int = 2): List<List<InlineKeyboardButton>> {
    if (maxColumns == 1) return btns.map { listOf(it) }
    val buttons = btns.toMutableList()
    val result = mutableListOf<List<InlineKeyboardButton>>()
    while (buttons.isNotEmpty()) {
        val temp = mutableListOf(buttons.removeAt(0))
        for (i in (0 until buttons.size)) {
            if (isEnoughSpace(temp, buttons[i])) {
                temp.add(buttons.removeAt(i))
            }
            if (temp.size == maxColumns) break
        }
        result.add(temp)
    }
    return result
}

fun isEnoughSpace(list: List<InlineKeyboardButton>, btn: InlineKeyboardButton): Boolean {
    return list.sumOf { it.text.length + 10 } + btn.text.length + 10 < 50
}

fun replyKeyboard(buttons: List<KeyboardButton>, maxColumns: Int = 2): ReplyKeyboardMarkup {
    val replyKeyboard = ReplyKeyboardMarkup()
    replyKeyboard.keyboard = getReplyButtonsOrderd(buttons, maxColumns)
    return replyKeyboard
}

fun replyKeyboard(vararg buttons: KeyboardButton, maxColumns: Int = 2) = replyKeyboard(buttons.toList(), maxColumns)

fun replyKeyboardFromPair(vararg replies: String, maxColumns: Int = 2) = replyKeyboardFromPair(replies.toList(), maxColumns)

fun replyKeyboardFromPair(replies: List<String>, maxColumns: Int = 2) = replyKeyboard(replies.map { KeyboardButton(it) }, maxColumns)


fun getReplyButtonsOrderd(btns: List<KeyboardButton>, maxColumns: Int = 2): List<KeyboardRow> {
    if (maxColumns == 1) return btns.map { btn -> KeyboardRow().also { it.add(btn) } }
    val buttons = btns.toMutableList()
    val result = mutableListOf<KeyboardRow>()
    while (buttons.isNotEmpty()) {
        val temp = KeyboardRow().also { it.add(buttons.removeAt(0)) }
        for (i in (0 until buttons.size)) {
            if (isEnoughSpace(temp, buttons[i])) {
                temp.add(buttons.removeAt(i))
            }
            if (temp.size == maxColumns) break
        }
        result.add(temp)
    }
    return result
}

fun isEnoughSpace(list: List<KeyboardButton>, btn: KeyboardButton): Boolean {
    return list.sumOf { it.text.length + 10 } + btn.text.length + 10 < 50
}
