package _2no2name.worldthreader.mixin.threadsafe_scoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ScoreboardScore.class)
public abstract class ScoreboardPlayerScoreMixin implements _2no2name.worldthreader.common.scoreboard.ScoreboardScoreAccess {
    private final AtomicInteger atomicScore = new AtomicInteger();
    private final AtomicBoolean atomicLocked = new AtomicBoolean();
    private final AtomicBoolean atomicForceUpdate = new AtomicBoolean();
    @Shadow
    private boolean locked;
    @Shadow
    private boolean forceUpdate;
    @Shadow
    private int score;

    @Shadow
    public abstract Scoreboard getScoreboard();


    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void init(CallbackInfo ci) {
        this.atomicScore.set(this.score);
        this.atomicLocked.set(this.locked);
        this.atomicForceUpdate.set(this.forceUpdate);
    }

    /**
     * @author 2No2Name
     * @reason access atomic fields
     */
    @Overwrite
    public int getScore() {
        return this.atomicScore.get();
    }

    /**
     * @author 2No2Name
     * @reason access atomic fields
     */
    @Overwrite
    public void setScore(int score) {
        this.score = score;
        int oldScore = this.atomicScore.getAndSet(score);
        if (oldScore != score || this.forceUpdate) {
            this.forceUpdate = false;
            this.atomicForceUpdate.set(false);
            //TODO: give something other than null
            this.getScoreboard().updateScore(null,null, (ScoreboardScore) (Object) this);
        }
    }

    /**
     * @author 2No2Name
     * @reason access atomic fields
     */
    @Overwrite
    public boolean isLocked() {
        return this.atomicLocked.get();
    }

    /**
     * @author 2No2Name
     * @reason access atomic fields
     */
    @Overwrite
    public void setLocked(boolean locked) {
        this.locked = locked;
        this.atomicLocked.set(locked);
    }

    /**
     * @author 2No2Name
     * @reason access atomic fields
     */
    /* TODO: Fix: there is no `this.getObjective()`
//    @Overwrite
    public void incrementScore(int amount) {
        if (Objects.requireNonNull(this.getObjective()).getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }

        this.worldthreader$forceAddScore(amount);
    }
    */

    @Override
    public void worldthreader$forceAddScore(int amount) {
        this.score = this.atomicScore.addAndGet(amount);
        if (amount != 0 || this.forceUpdate) {
            this.forceUpdate = false;
            this.atomicForceUpdate.set(false);
            //TODO: don't pass null
            this.getScoreboard().updateScore(null, null, (ScoreboardScore) (Object) this);
        }
    }
}
