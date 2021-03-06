package au.lyrael.stacywolves.item;


import au.lyrael.stacywolves.utility.LanguageHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static au.lyrael.stacywolves.StacyWolves.CREATIVE_TAB;
import static au.lyrael.stacywolves.StacyWolves.MOD_ID;

public class ItemWolfPlacer extends ItemMonsterPlacer {
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private IIcon theIcon;
    protected int colorBase = 0x000000;
    protected int colorSpots = 0xFFFFFF;
    protected String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected EntityLiving entityToSpawn = null;

    public ItemWolfPlacer() {
        super();
    }

    public ItemWolfPlacer(String parEntityToSpawnName, int parPrimaryColor,
                          int parSecondaryColor) {
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CREATIVE_TAB);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
//      LOGGER.debug("Spawn egg constructor for [{}]", entityToSpawnName);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking,
     * he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer,
                             World par3World, int x, int y, int z, int side, float par8,
                             float par9, float par10) {
        if (par3World.isRemote) {
            return true;
        } else {
            Block block = par3World.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double d0 = 0.0D;

            if (side == 1 && block.getRenderType() == 11) {
                d0 = 0.5D;
            }

            Entity entity = spawnEntity(par3World, x + 0.5D, y + d0, z + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName()) {
                    ((EntityLiving) entity).setCustomNameTag(par1ItemStack.getDisplayName());
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode) {
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed.
     * Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
                                      EntityPlayer par3EntityPlayer) {
        if (par2World.isRemote) {
            return par1ItemStack;
        } else {
            MovingObjectPosition movingobjectposition =
                    getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

            if (movingobjectposition == null) {
                return par1ItemStack;
            } else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition
                        .MovingObjectType.BLOCK) {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!par2World.canMineBlock(par3EntityPlayer, i, j, k)) {
                        return par1ItemStack;
                    }

                    if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition
                            .sideHit, par1ItemStack)) {
                        return par1ItemStack;
                    }

                    if (par2World.getBlock(i, j, k) instanceof BlockLiquid) {
                        Entity entity = spawnEntity(par2World, i, j, k);

                        if (entity != null) {
                            if (entity instanceof EntityLivingBase && par1ItemStack
                                    .hasDisplayName()) {
                                ((EntityLiving) entity).setCustomNameTag(par1ItemStack
                                        .getDisplayName());
                            }

                            if (!par3EntityPlayer.capabilities.isCreativeMode) {
                                --par1ItemStack.stackSize;
                            }
                        }
                    }
                }

                return par1ItemStack;
            }
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by
     * the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public Entity spawnEntity(World parWorld, double parX, double parY, double parZ) {

        if (!parWorld.isRemote) // never spawn entity on client side
        {
            entityToSpawnNameFull = MOD_ID + "." + entityToSpawnName;
            if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull)) {
                entityToSpawn = (EntityLiving) EntityList
                        .createEntityByName(entityToSpawnNameFull, parWorld);
                entityToSpawn.setLocationAndAngles(parX, parY, parZ,
                        MathHelper.wrapAngleTo180_float(parWorld.rand.nextFloat()
                                * 360.0F), 0.0F);
                parWorld.spawnEntityInWorld(entityToSpawn);
                entityToSpawn.onSpawnWithEgg(null);
                entityToSpawn.playLivingSound();
            } else {
                //DEBUG
                LOGGER.warn("Entity not found creating spawn egg for [{}]", entityToSpawnName);
            }
        }

        return entityToSpawn;
    }


    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item parItem, CreativeTabs parTab, List parList) {
        parList.add(new ItemStack(parItem, 1, 0));
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType) {
        return (parColorType == 0) ? colorBase : colorSpots;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        return "Spawn " + LanguageHelper.getLocalization("entity." + MOD_ID + ":" + this.entityToSpawnName + ".name");
    }


    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        theIcon = par1IconRegister.registerIcon(getIconString() + "_overlay");
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Override
    public IIcon getIconFromDamageForRenderPass(int parDamageVal, int parRenderPass) {
        return parRenderPass > 0 ? theIcon : super.getIconFromDamageForRenderPass(parDamageVal,
                parRenderPass);
    }

    public void setColors(int parColorBase, int parColorSpots) {
        colorBase = parColorBase;
        colorSpots = parColorSpots;
    }

    public int getColorBase() {
        return colorBase;
    }

    public int getColorSpots() {
        return colorSpots;
    }

    public void setEntityToSpawnName(String parEntityToSpawnName) {
        entityToSpawnName = parEntityToSpawnName;
        entityToSpawnNameFull = MOD_ID + "." + entityToSpawnName;
    }

}