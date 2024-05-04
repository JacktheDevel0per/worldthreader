package _2no2name.worldthreader.mixin.dimension_change;

import _2no2name.worldthreader.common.dimension_change.TeleportedEntityInfo;
import _2no2name.worldthreader.common.mixin_support.interfaces.EntityExtended;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity implements EntityExtended {

    @Shadow
    @Final
    private DefaultedList<ItemStack> armorItems;
    @Shadow
    @Final
    private DefaultedList<ItemStack> handItems;

    public MobEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract void readLeashNbt();


    @Shadow private @Nullable Either<UUID, BlockPos> leashNbt;

    //TODO: fix this, fix the .fromNbt to actually use the new stuff.
    @Override
    public void worldthreader$restoreEntity(TeleportedEntityInfo teleportedEntity) {
        if (this.getRemovalReason() == Entity.RemovalReason.CHANGED_DIMENSION) {
            //Undo the effects of removeFromDimension()
            this.unsetRemoved();

            //For MobEntities this includes restoring Leash and Armor/Hand items
            NbtCompound nbt = teleportedEntity.nbtCompound();

            /*
            if (nbt.contains(MobEntity.LEASH_KEY, NbtElement.COMPOUND_TYPE)) {
                //TODO: set leash data.
//                this.leashNbt = nbt.getCompound(MobEntity.LEASH_KEY);
                this.readLeashNbt();
            }

            if (nbt.contains("ArmorItems", NbtElement.LIST_TYPE)) {
                NbtList nbtList = nbt.getList("ArmorItems", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < this.armorItems.size(); ++i) {
                    this.armorItems.set(i, ItemStack.fromNbt(null, nbtList.getCompound(i)).orElseThrow());
                }
            }
            if (nbt.contains("HandItems", NbtElement.LIST_TYPE)) {
                NbtList nbtList = nbt.getList("HandItems", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < this.handItems.size(); ++i) {
                    this.handItems.set(i, ItemStack.fromNbt(null ,nbtList.getCompound(i)).orElseThrow());
                }
            }
            */
        }
    }
}
