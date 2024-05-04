package _2no2name.worldthreader.mixin.threadsafe_scoreboard;

import _2no2name.worldthreader.common.scoreboard.ScoreboardScoreAccess;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.server.command.ScoreboardCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScoreboardCommand.class)
public class ScoreboardCommandMixin {
    @Redirect(
            method = {
                    "executeAdd(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/scoreboard/ScoreboardObjective;I)I",
                    "executeRemove(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/scoreboard/ScoreboardObjective;I)I"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreAccess;getScore()I")
    )
    private static int score(ScoreAccess instance) {
        return 0;
    }

    @Redirect(
            method = {
                    "executeAdd(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/scoreboard/ScoreboardObjective;I)I",
                    "executeRemove(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/scoreboard/ScoreboardObjective;I)I"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreAccess;setScore(I)V")
    )
    private static void score(ScoreAccess instance, int score) {
        ((ScoreboardScoreAccess) instance).worldthreader$forceAddScore(score);
    }
}
