package au.lyrael.stacywolves.entity.wolf;

import au.lyrael.stacywolves.annotation.WolfMetadata;
import au.lyrael.stacywolves.annotation.WolfSpawn;
import au.lyrael.stacywolves.client.render.IRenderableWolf;
import au.lyrael.stacywolves.registry.ItemRegistry;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import static au.lyrael.stacywolves.utility.WorldHelper.getFullBlockLightValue;
import static net.minecraftforge.common.BiomeDictionary.Type.SAVANNA;

@WolfMetadata(name = "EntitySavannahWolf", primaryColour = 0xE4D7B0, secondaryColour = 0xB0B252,
        spawns = {
                @WolfSpawn(biomeTypes = {SAVANNA}, probability = 10, min = 1, max = 4),
        })
public class EntitySavannahWolf extends EntityWolfBase implements IRenderableWolf {

    public EntitySavannahWolf(World worldObj) {
        super(worldObj);
        addLikedItem(ItemRegistry.getWolfFood("savannah_bone"));
        this.addEdibleItem(new ItemStack(Items.beef));
        this.addEdibleItem(new ItemStack(Items.chicken));
    }

    @Override
    public EntityWolfBase createChild(EntityAgeable parent) {
        EntityWolfBase child = new EntitySavannahWolf(this.worldObj);
        return createChild(parent, child);
    }

    @Override
    public String getTextureFolderName() {
        return "savannah";
    }
}