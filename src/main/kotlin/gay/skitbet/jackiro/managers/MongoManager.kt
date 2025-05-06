package gay.skitbet.jackiro.managers

import gay.skitbet.jackiro.database.ServerConfigRepository
import gay.skitbet.mongoy.Mongoy

object MongoManager {

    val serverConfigRepository: ServerConfigRepository by lazy { ServerConfigRepository() }

    fun connect(dbName: String) {
        Mongoy.init(dbName)
        initRepositories()
    }

    private fun initRepositories() {
        // Repositories are initialized lazily via the serverConfigRepository property
    }
}
