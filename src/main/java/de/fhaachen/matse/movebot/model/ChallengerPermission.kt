package de.fhaachen.matse.movebot.model

enum class ChallengerPermission {
    MASTER {
        override fun parentPermission() = MASTER
    },

    // Admin can do nearly everthing
    ADMIN {
        override fun parentPermission() = MASTER
    },

    MODERATOR {
        override fun parentPermission() = ADMIN
    },

    CHALLENGER {
        override fun parentPermission() = ADMIN
    };

    abstract fun parentPermission(): ChallengerPermission

}