package doggytalents.common.storage;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import doggytalents.DoggyEntityTypes;
import doggytalents.api.feature.EnumMode;
import doggytalents.common.entity.DogEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DogRespawnData {

    private final DogRespawnStorage storage;
    private final UUID uuid;
    private CompoundNBT data;

    //TODO Make it list you can only add too
    private static final List<String> TAGS_TO_REMOVE = Lists.newArrayList(
            "Pos", "Health", "Motion", "Rotation", "FallDistance", "Fire", "Air", "OnGround",
            "Dimension", "PortalCooldown", "Passengers", "Leash", "InLove", "Leash", "HurtTime",
            "HurtByTimestamp", "DeathTime", "AbsorptionAmount", "FallFlying", "Brain", "Sitting"); // Remove dog mode

    protected DogRespawnData(DogRespawnStorage storageIn, UUID uuid) {
        this.storage = storageIn;
        this.uuid = uuid;
    }

    public UUID getId() {
        return this.uuid;
    }

    public void populate(DogEntity dogIn) {
        this.data = new CompoundNBT();
        dogIn.writeWithoutTypeId(this.data);

        // Remove tags that don't need to be saved
        for (String tag : TAGS_TO_REMOVE) {
            this.data.remove(tag);
        }

        this.data.removeUniqueId("UUID");
        this.data.removeUniqueId("LoveCause");
    }

    public DogEntity respawn(World worldIn, PlayerEntity playerIn, BlockPos pos) {
        DogEntity dog = (DogEntity) DoggyEntityTypes.DOG.get().spawn(worldIn, null, playerIn, pos, SpawnReason.TRIGGERED, false, false);

        CompoundNBT compoundnbt = dog.writeWithoutTypeId(new CompoundNBT());
        UUID uuid = dog.getUniqueID();
        compoundnbt.merge(this.data);
        dog.setUniqueId(uuid);
        dog.read(compoundnbt);

        dog.setMode(EnumMode.DOCILE);
        dog.getAISit().setSitting(false);

        return dog;
    }

    public void read(CompoundNBT compound) {
        this.data = compound.getCompound("data");
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.put("data", this.data);
        return compound;
    }
}
