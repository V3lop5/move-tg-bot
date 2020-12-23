package de.fhaachen.matse.movebot.model

enum class ChallengerPermission {
    MASTER {
        override fun parentPermission() = MASTER
    },

    // Admin can do nearly everthing
    ADMIN {
        override fun parentPermission() = MASTER
    },
    CHALLENGER {
        override fun parentPermission() = ADMIN
    };

    abstract fun parentPermission(): ChallengerPermission

}