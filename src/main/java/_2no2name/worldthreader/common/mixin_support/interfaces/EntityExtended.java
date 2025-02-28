package _2no2name.worldthreader.common.mixin_support.interfaces;

import _2no2name.worldthreader.common.dimension_change.TeleportedEntityInfo;
import net.minecraft.nbt.NbtCompound;

public interface EntityExtended {

    default void onArrivedInWorld() {
    }

    void worldthreader$copyFromNBT(NbtCompound nbtCompound);

    void worldthreader$restoreEntity(TeleportedEntityInfo teleportedEntity);
}
