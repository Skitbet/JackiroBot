package gay.skitbet.jackiro.utils.setup

import gay.skitbet.jackiro.utils.setup.stages.DisabledCommandsStage
import gay.skitbet.jackiro.utils.setup.stages.SetupNicknameStage

object StageRegistry {

    val STAGES: List<SetupStage> = listOf(
        SetupNicknameStage(),
        DisabledCommandsStage()
    )

    fun nextStep(currentStage: SetupStage?): SetupStage? =
        getStepIndex(currentStage).let { STAGES.getOrNull(it + 1) }

    fun getStepIndex(stage: SetupStage?): Int =
        STAGES.indexOf(stage).takeIf { it >= 0 } ?: STAGES.size
}
