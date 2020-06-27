package doggytalents.common.entity.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import doggytalents.DoggyTalents2;
import doggytalents.common.util.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FindWaterGoal2 extends Goal {

    private final CreatureEntity creature;
    private final PathNavigator navigator;
    private final World world;

    private final int waterSearchRange = 12;
    private final int safeSearchRange = 6;

    @Nullable
    private BlockPos waterPos;
    private int timeToRecalcPath;


    public FindWaterGoal2(CreatureEntity creatureIn) {
        this.creature = creatureIn;
        this.navigator = creatureIn.getNavigator();
        this.world = creatureIn.world;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.creature.onGround || (this.creature.ticksExisted % 5) != 0) {
            return false;
        }

        boolean isInFire = this.isInDangerSpot(this.creature);
        boolean isOnFire = this.creature.isBurning();

        if (!isInFire && !isOnFire) {
            return false;
        }

        BlockPos entityPos = new BlockPos(this.creature);

        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(entityPos.add(-this.waterSearchRange, -4, -this.waterSearchRange), entityPos.add(this.waterSearchRange, 4, this.waterSearchRange))) {
            if (this.getBlockType(blockpos1) == BlockType.WATER) {
                this.waterPos = blockpos1;
                break;
            }
        }
        boolean waterBlockNearBy = this.waterPos != null;

        return waterBlockNearBy || isInFire;

    }

    @Override
    public boolean shouldContinueExecuting() {
        // If there is some water nearby
        if (this.waterPos != null) {
            // Check it is still a water block
            BlockType safety = this.getBlockType(this.waterPos);
            if (safety != BlockType.WATER) {
                return false;
            }

            // If entity is burning continue
            if (this.creature.isBurning()) {
                return true;
            }
        }

        //
        return this.isInDangerSpot(this.creature);
    }


    @Override
    public void startExecuting() {
        DoggyTalents2.LOGGER.debug("Start executing: {}", waterPos);
        this.timeToRecalcPath = 0;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            BlockPos targetPos = null;

            if (this.waterPos != null) {
                targetPos = this.waterPos;
            } else if (!this.creature.hasPath()) {
                BlockPos.Mutable mutablePos = new BlockPos.Mutable();
                for(int i = 0; i < 10; ++i) {
                    int j = EntityUtil.getRandomNumber(this.creature, -this.safeSearchRange, this.safeSearchRange);
                    int k = EntityUtil.getRandomNumber(this.creature, -4, 4);
                    int l = EntityUtil.getRandomNumber(this.creature, -this.safeSearchRange, this.safeSearchRange);

                    mutablePos.setPos(this.creature.getPosX() + j, this.creature.getPosY() + k, this.creature.getPosZ() + l);
                    boolean flag = this.getBlockType(mutablePos).isSafe();
                    if (flag) {
                        targetPos = mutablePos;
                        break;
                    }
                }
            }

            DoggyTalents2.LOGGER.debug(" {}", targetPos);

            if (targetPos != null) {
                this.navigator.tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.2D);
            }
        }
    }

    @Override
    public void resetTask() {
        this.navigator.clearPath();
        this.waterPos = null;
    }

    /**
     * Returning {@link BlockType#FIRE} indicates the entity is actively in danger
     * Returning {@link BlockType#WATER} indicates the entity is on fire but not in source
     * Returning {@link BlockType#SAFE} indicates the entity is fine
     *
     * @param bb
     * @return
     */
    public boolean isInDangerSpot(Entity entityIn) {
        AxisAlignedBB bb = entityIn.getBoundingBox();
        int minX = MathHelper.floor(bb.minX);
        int minY = MathHelper.floor(bb.minY);
        int minZ = MathHelper.floor(bb.minZ);

        int maxX = MathHelper.ceil(bb.maxX);
        int maxY = MathHelper.ceil(bb.maxY);
        int maxZ = MathHelper.ceil(bb.maxZ);

        for (BlockPos pos : BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockType safety = getBlockType(pos);

            if (safety == BlockType.FIRE) {
                return true;
            }
        }

        return false;
    }

    public BlockType getBlockType(BlockPos posIn) {
        // If the block is fire or lava
        Material material = this.world.getBlockState(posIn).getMaterial();
        if (material == Material.FIRE || material == Material.LAVA) {
            return BlockType.FIRE;
        }

        // If it is water
        if (this.world.getFluidState(posIn).isTagged(FluidTags.WATER)) {
            return BlockType.WATER;
        }

        // If it is raining and is not fire or lava
        if (this.world.isRainingAt(posIn)) {
            return BlockType.WATER;
        }

        // Otherwise the block is ok to go to
        return BlockType.SAFE;
    }

    public static enum BlockType {
        SAFE,
        FIRE,
        WATER;

        public boolean isSafe() {
            return this == SAFE || this == WATER;
        }
    }
}