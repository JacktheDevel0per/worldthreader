package _2no2name.worldthreader.mixin.threadsafe_scoreboard;

import _2no2name.worldthreader.common.scoreboard.ThreadsafeScoreboard;
import _2no2name.worldthreader.common.scoreboard.ThreadsafeScoreboardObjective;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {



    @Shadow @Final private Reference2ObjectMap<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion;

    @Shadow @Final private Object2ObjectMap<String, ScoreboardObjective> objectives;

    @Shadow @Final private Object2ObjectMap<String, Team> teams;

    @Shadow @Final private Object2ObjectMap<String, Team> teamsByScoreHolder;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void createThreadsafeCollections(CallbackInfo ci) {
        if (this instanceof ThreadsafeScoreboard) {
            //TODO: this is not thread safe.
            /*
            this.objectivesByCriterion = new ConcurrentHashMap<>(this.objectivesByCriterion);
            this.objectives = new ConcurrentHashMap<>(this.objectives);
            this.teams = new ConcurrentHashMap<>(this.teams);
            this.teamsByScoreHolder = new ConcurrentHashMap<>(this.teamsByScoreHolder);

             */
        }
    }

    @Redirect(
            method = "addObjective",
            at = @At(value = "NEW", target = "net/minecraft/scoreboard/ScoreboardObjective")
    )
    private ScoreboardObjective createThreadsafeObjective(Scoreboard scoreboard, String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, NumberFormat numberFormat) {
        return new ThreadsafeScoreboardObjective(scoreboard, name, criterion, displayName, renderType);
    }

    @Redirect(
            method = "addObjective",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;computeIfAbsent(Ljava/lang/Object;Lit/unimi/dsi/fastutil/objects/Reference2ObjectFunction;)Ljava/lang/Object;")
    )
    private <K, V> Object useCopyOnWriteArrayList(Reference2ObjectMap map, K key, Reference2ObjectFunction<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(key, (K) -> new CopyOnWriteArrayList<>());
    }


    //TODO: Fix / add back..
 /*
    @Redirect(
            method = "getScore",

            at = @At(value = "INVOKE", target = "DON'T KNOW WHAT TO TARGET", ordinal = 0)
    )
    private <K, X, W> Object useConcurrentHashMap(Map<K, Map<X, W>> map, K key, Function<? super K, ? extends Map<X, W>> mappingFunction) {
        return map.computeIfAbsent(key, (K a) -> new ConcurrentHashMap<X, W>());
    }
  */
}
