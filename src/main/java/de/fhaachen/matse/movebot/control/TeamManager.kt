package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.handler.TeamHandler
import de.fhaachen.matse.movebot.model.*
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.KeyboardRequestHandler
import java.time.LocalDateTime

object TeamManager {

    val teams = loadTeams()
    val fights = loadFights()

    fun createTeam(name: String, challenger: Challenger): Team {
        val team = Team(teams.size + 1, name)
        teams += team
        TeamHandler.onTeamCreation(challenger, team)
        addTeamMember(team, challenger)
        return team
    }

    fun addTeamMember(team: Team, challenger: Challenger) {
        team.members.forEach {
            ChallengeBot.sendMessage(
                it.challengerId,
                "Neues Teammitglied: ${challenger.nickname} ist dem Team _${team.name}_ beigetreten."
            )
        }
        team.members += TeamMember(challenger.telegramUser.id, LocalDateTime.now())
        TeamHandler.onTeamJoin(challenger, team)
    }

    fun onJoinRequest(challenger: Challenger, teamname: String) {
        val team = teams.find { it.name.equals(teamname, true) }
            ?: throw IllegalStateException("Team $teamname nicht gefunden.")

        if (!canJoin(challenger, team)) {
            ChallengeBot.sendMessage(
                challenger.telegramUser.id,
                "Anfrage wurde verworfen, da du diesem Team nicht beitreten kannst. Bist du eventuell schon in einem Team?"
            )
            return
        }

        KeyboardRequestHandler.addRequest(
            team.members.map { it.challengerId },
            listOf("Annehmen", "Ablehnen"),
            { chatId, keyboard ->
                ChallengeBot.sendMessage(
                    chatId,
                    "${challenger.nickname} möchte dem Team _${team.name}_ beitreten.\nNimmst du die Anfrage an?",
                    keyboard
                )
            },
            { user, answer -> onJoinRequestAnswer(challenger, team, answer == "Annehmen", user) }
        )

        ChallengeBot.sendMessage(
            challenger.telegramUser.id,
            "Deine Anfrage an das Team $teamname wurde verschickt. Bitte warte, bis dich ein Teammitglied annimmt."
        )
    }

    fun onJoinRequestAnswer(challenger: Challenger, team: Team, accepted: Boolean, answeredBy: Int) {
        if (!accepted) {
            ChallengeBot.sendMessage(
                challenger.telegramUser.id,
                "Deine Anfrage wurde abgelehnt. Du wurdest nicht zum Team _${team.name}_ hinzugefügt."
            )
            return
        }

        if (!canJoin(challenger, team)) {
            ChallengeBot.sendMessage(
                challenger.telegramUser.id,
                "Deine Anfrage wurde angenommen. Allerdings kannst du dem Team _${team.name}_ nicht mehr beitreten.\nBist du vielleicht in einem anderen Team?"
            )
            ChallengeBot.sendMessage(
                answeredBy,
                "Du hast die Anfrage von ${challenger.nickname} angenommen. Allerdings darf dieser Teilnehmer nicht mehr dem Team ${team.name} beitreten."
            )
            return
        }

        addTeamMember(team, challenger)
        ChallengeBot.sendMessage(
            challenger.telegramUser.id,
            "Deine Anfrage wurde angenommen. Du bist nun Mitglied des Teams _${team.name}_."
        )
    }

    fun canJoin(challenger: Challenger, team: Team): Boolean {
        if (isInTeam(team, challenger))
            return false

        // TODO Team-Challenges überprüfen. Das neue Mitglied dürfte nicht in zwei konkurrierenden Teams sein.

        // Das könnte dann wieder raus.
        if (teams.any { t -> t.members.any { it.challengerId == challenger.telegramUser.id } })
            return false

        return true
    }

    fun canFight(teamA: Team, teamB: Team): Boolean {
        // TODO Vorbereitung, falls irgendwann einmal Mitglieder in mehreren Teams sind
        return true
    }

    fun save() {
        saveTeams(teams)
        saveFights(fights)
    }

    fun hasFight(own: Team, other: Team, movementType: MovementType): Boolean {
        return fights.any { it.movementType == movementType && (it.teamA == own.teamId || it.teamB == own.teamId) && (it.teamA == other.teamId || it.teamB == other.teamId) }
    }

    fun onFightRequest(own: Team, other: Team, movementType: MovementType) {
        KeyboardRequestHandler.addRequest(
            other.members.map { it.challengerId },
            listOf("Annehmen", "Ablehnen"),
            { chatId, keyboard ->
                ChallengeBot.sendMessage(
                    chatId,
                    "Das Team _${own.name}_ fordert dein Team _${other.name}_ zum Wettkampf in der Sportart ${movementType.title} heraus. Nimmst du die Herausforderung an?",
                    keyboard
                )
            },
            { user, answer -> onFightRequestAnswer(own, other, movementType, answer == "Annehmen", user) }
        )
    }

    fun onFightRequestAnswer(own: Team, other: Team, movementType: MovementType, accepted: Boolean, answeredBy: Int) {
        if (!accepted) {
            own.members.forEach { ChallengeBot.sendMessage(it.challengerId, "Upps! Das Team _${other.name}_ hat die Anfrage abgelehnt.") }
            return
        }

        if (hasFight(own, other, movementType)) {
            ChallengeBot.sendMessage(answeredBy, "Die Anfrage wurde anscheinend zwischenzeitlich bereits angenommen... (?)")
            return
        }

        fights += TeamFight(own.teamId, other.teamId, movementType)

        own.members.forEach {
            ChallengeBot.sendMessage(
                it.challengerId,
                "Dein Team (_${own.name}_) ist in einem Wettkampf mit Team _${other.name}_. Erziele als Team mehr ${movementType.unit} in der Sportart ${movementType.title} als das andere Team!"
            )
        }
        other.members.forEach {
            ChallengeBot.sendMessage(
                it.challengerId,
                "Dein Team (_${other.name}_) ist in einem Wettkampf mit Team _${own.name}_. Erziele als Team mehr ${movementType.unit} in der Sportart ${movementType.title} als das andere Team!"
            )
        }

    }

    fun getActiveFights(challenger: Challenger) = getTeams(challenger).flatMap { getActiveFights(it) }

    fun getActiveFights(team: Team) =
        fights.filter { (it.teamA == team.teamId || it.teamB == team.teamId) }
            .map { getFightStatistic(it, team) }.sortedBy { it.movementType }.sortedBy { it.other.teamId }


    private fun getFightStatistic(fight: TeamFight, own: Team): TeamFightStatistic {
        // Das andere Team finden. Das erste Team, welches nicht das eigene ist & im Fight dabei ist
        val other = teams.find { it.teamId != own.teamId && (fight.teamA == it.teamId || fight.teamB == it.teamId) }
            ?: throw IllegalStateException("Das Team wurde nicht gefunden.")
        return TeamFightStatistic(own, other, fight.movementType, fight.startTime)
    }


    private fun isInTeam(team: Team, challenger: Challenger) =
        team.members.any { it.challengerId == challenger.telegramUser.id }

    fun getTeams(challenger: Challenger) = teams.filter { isInTeam(it, challenger) }
}