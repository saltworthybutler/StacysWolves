package au.lyrael.stacywolves.entity.wolf;

import au.lyrael.stacywolves.annotation.WolfMetadata;
import au.lyrael.stacywolves.annotation.WolfSpawn;
import au.lyrael.stacywolves.client.render.IRenderableWolf;
import au.lyrael.stacywolves.registry.ItemRegistry;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static au.lyrael.stacywolves.utility.WorldHelper.canSeeTheSky;
import static net.minecraftforge.common.BiomeDictionary.Type.*;

@WolfMetadata(name = "EntityIronWolf", primaryColour = 0x7F7F7F, secondaryColour = 0xD8AF93,
        spawns = {
                @WolfSpawn(biomeTypes = PLAINS, probability = 5, min = 1, max = 4),
                @WolfSpawn(biomeTypes = FOREST, probability = 5, min = 1, max = 4),
                @WolfSpawn(biomeTypes = HILLS, probability = 5, min = 1, max = 4),
                @WolfSpawn(biomeTypes = SANDY, probability = 5, min = 1, max = 4),
        })
public class EntityIronWolf extends EntityWolfBase implements IRenderableWolf {

    public EntityIronWolf(World worldObj) {
        super(worldObj);
        addLikedItem(ItemRegistry.getWolfFood("iron_bone"));
        this.addEdibleItem(new ItemStack(Items.beef));
        this.addEdibleItem(new ItemStack(Items.chicken));
    }

    @Override
    public EntityWolfBase createChild(EntityAgeable parent) {
        EntityWolfBase child = new EntityIronWolf(this.worldObj);
        return createChild(parent, child);
    }

    @Override
    public String getTextureFolderName() {
        return "iron";
    }

    @Override
    public boolean getCanSpawnHere() {
        return !canSeeTheSky(getWorldObj(), posX, posY, posZ) && creatureCanSpawnHere();
    }
}