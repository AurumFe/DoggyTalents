package doggytalents.item;

import doggytalents.api.inferface.IDogEntity;
import doggytalents.api.inferface.IDogItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 **/
public class ItemTreat extends Item implements IDogItem {

    private int maxLevel;
    public ItemTreat(int maxLevel, Properties properties) {
        super(properties);
        this.maxLevel = maxLevel;
    }

    @Override
    public ActionResultType onInteractWithDog(IDogEntity dogIn, World worldIn, PlayerEntity playerIn, Hand handIn) {
        int level = dogIn.getLevelFeature().getLevel();

        if (dogIn.getGrowingAge() < 0) {
            worldIn.setEntityState(dogIn, (byte)6);
            if(!worldIn.isRemote) {
                 playerIn.sendMessage(new TranslationTextComponent("treat.normal_treat.too_young"));
            }

            return ActionResultType.FAIL;
        }
        if(level < this.maxLevel) {
            if(!playerIn.abilities.isCreativeMode)
                playerIn.getHeldItem(handIn).shrink(1);

            if(!playerIn.world.isRemote) {
                dogIn.getLevelFeature().increaseLevel();
                dogIn.setHealth(dogIn.getMaxHealth());
                dogIn.getAISit().setSitting(true);
                worldIn.setEntityState(dogIn, (byte)7);
                playerIn.sendMessage(new TranslationTextComponent("treat.normal_treat.level_up"));
            }

            return ActionResultType.SUCCESS;
        }
        else {
            worldIn.setEntityState(dogIn, (byte)6);
            if(!worldIn.isRemote) {
                playerIn.sendMessage(new TranslationTextComponent("treat.normal_treat.max_level"));
            }

            return ActionResultType.FAIL;
        }
    }
}
