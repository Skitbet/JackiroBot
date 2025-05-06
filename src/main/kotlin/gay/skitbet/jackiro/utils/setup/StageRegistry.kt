package gay.skitbet.jackiro.utils.setup

import gay.skitbet.jackiro.utils.setup.stages.SetupNicknameStage

object StageRegistry {
    val STAGES: List<SetupStage> = listOf(
        SetupNicknameStage()
    )

    fun nextStep(currentStage: SetupStage): SetupStage? {
        val index = getStepIndex(currentStage)
        return if (index + 1 >= STAGES.size) {
            null
        } else {
            STAGES[index + 1]
        }
    }

    fun getStepIndex(stage: SetupStage): Int {
        return STAGES.indexOf(stage).takeIf { it >= 0 } ?: STAGES.size
    }
}
