package gay.skitbet.jackiro.utils

data class JackiroConfig(
    var clientToken: String? = null,
    var clientID: String? = null,
    var clientSecret: String? = null,
    var ownerId: String? = null,
    var shardCount: Int = 0,
    var currentlyPlaying: MutableList<String> = mutableListOf()
)
