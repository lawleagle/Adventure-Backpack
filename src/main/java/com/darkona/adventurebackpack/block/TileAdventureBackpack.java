package com.darkona.adventurebackpack.block;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import com.darkona.adventurebackpack.inventory.SlotBackpack;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.CoordsUtils;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;

import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_LEFT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_RIGHT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;
import static com.darkona.adventurebackpack.common.Constants.TOOL_LOWER;
import static com.darkona.adventurebackpack.common.Constants.TOOL_UPPER;

/**
 * Created by Darkona on 12/10/2014.
 */
public class TileAdventureBackpack extends TileEntity implements IInventoryAdventureBackpack, ISidedInventory
{
    private ItemStack[] inventory = new ItemStack[Constants.INVENTORY_SIZE];
    private FluidTank leftTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);

    private NBTTagList ench;
    private BackpackTypes type;

    private NBTTagCompound extendedProperties;
    private boolean disableCycling;
    private boolean disableNVision;
    private int lastTime;

    private boolean sleepingBagDeployed;
    private int sbdir;
    private int sbx;
    private int sby;
    private int sbz;
    private int luminosity;

    private int checkTime = 0;

    public TileAdventureBackpack()
    {
        sleepingBagDeployed = false;
        setType(BackpackTypes.STANDARD);
        luminosity = 0;
        lastTime = 0;
        checkTime = 0;
        extendedProperties = new NBTTagCompound();
    }

    public int getLuminosity()
    {
        return luminosity;
    }

    @Override
    public int getLastTime()
    {
        return lastTime;
    }

    @Override
    public void setLastTime(int lastTime)
    {
        this.lastTime = lastTime;
    }

    @Override
    public NBTTagCompound getExtendedProperties()
    {
        return extendedProperties;
    }

    public boolean deploySleepingBag(EntityPlayer player, World world, int meta, int cX, int cY, int cZ)
    {
        if (world.isRemote)
            return false;

        sleepingBagDeployed = CoordsUtils.spawnSleepingBag(player, world, meta, cX, cY, cZ);
        if (sleepingBagDeployed)
        {
            sbx = cX;
            sby = cY;
            sbz = cZ;
            sbdir = meta;
        }
        return sleepingBagDeployed;
    }

    public void setSleepingBagDeployed(boolean state)
    {
        this.sleepingBagDeployed = state;
    }

    public boolean removeSleepingBag(World world)
    {
        if (sleepingBagDeployed)
        {
            if (world.getBlock(sbx, sby, sbz) == ModBlocks.blockSleepingBag)
            {
                world.func_147480_a(sbx, sby, sbz, false);
                this.sleepingBagDeployed = false;
                markDirty();
                return true;
            }
        }
        else
        {
            this.sleepingBagDeployed = false;
            markDirty();
        }
        return false;
    }

    //=====================================================GETTERS====================================================//

    @Override
    public BackpackTypes getType()
    {
        return type;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return this.inventory;
    }

    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot];
    }

    @Override
    public String getInventoryName()
    {
        return "";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public FluidTank getLeftTank()
    {
        return leftTank;
    }

    @Override
    public FluidTank getRightTank()
    {
        return rightTank;
    }

    //=====================================================SETTERS====================================================//

    public void setType(BackpackTypes type)
    {
        this.type = type;
    }

    //=====================================================BOOLEANS===================================================//
    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return this.sleepingBagDeployed;
    }

    //=======================================================NBT======================================================//
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        loadFromNBT(compound);
        sleepingBagDeployed = compound.getBoolean("sleepingbag");
        sbx = compound.getInteger("sbx");
        sby = compound.getInteger("sby");
        sbz = compound.getInteger("sbz");
        sbdir = compound.getInteger("sbdir");
        luminosity = compound.getInteger("lumen");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        saveToNBT(compound);
        compound.setBoolean("sleepingbag", sleepingBagDeployed);
        compound.setInteger("sbx", sbx);
        compound.setInteger("sby", sby);
        compound.setInteger("sbz", sbz);
        compound.setInteger("sbdir", sbdir);
        compound.setInteger("lumen", luminosity);
    }

    private void convertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
        NBTTagCompound oldBackpackTag = compound.getCompoundTag("backpackData");
        NBTTagList oldItems = oldBackpackTag.getTagList("ABPItems", NBT.TAG_COMPOUND);
        leftTank.readFromNBT(oldBackpackTag.getCompoundTag(TAG_LEFT_TANK));
        rightTank.readFromNBT(oldBackpackTag.getCompoundTag(TAG_RIGHT_TANK));
        type = BackpackTypes.getType(oldBackpackTag.getString("colorName"));

        NBTTagCompound newBackpackTag = new NBTTagCompound();
        newBackpackTag.setTag(TAG_INVENTORY, oldItems);
        newBackpackTag.setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setByte(TAG_TYPE, BackpackTypes.getMeta(type));

        compound.setTag(TAG_WEARABLE_COMPOUND, newBackpackTag);
        compound.removeTag("backpackData");
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("backpackData"))
            convertFromOldNBTFormat(compound);

        if (compound.hasKey("ench"))
            ench = compound.getTagList("ench", NBT.TAG_COMPOUND);

        NBTTagCompound backpackTag = compound.getCompoundTag(TAG_WEARABLE_COMPOUND);
        type = BackpackTypes.getType(backpackTag.getByte(TAG_TYPE));
        NBTTagList items = backpackTag.getTagList(TAG_INVENTORY, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
        leftTank.readFromNBT(backpackTag.getCompoundTag(TAG_LEFT_TANK));
        rightTank.readFromNBT(backpackTag.getCompoundTag(TAG_RIGHT_TANK));
        extendedProperties = backpackTag.getCompoundTag("extended");
        disableCycling = backpackTag.getBoolean("disableCycling");
        disableNVision = backpackTag.getBoolean("disableNVision");
        lastTime = backpackTag.getInteger("lastTime");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        if (ench != null)
            compound.setTag("ench", ench);

        NBTTagCompound backpackTag = new NBTTagCompound();
        backpackTag.setByte(TAG_TYPE, BackpackTypes.getMeta(type));
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        backpackTag.setTag(TAG_INVENTORY, items);
        backpackTag.setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag("extended", extendedProperties);
        backpackTag.setBoolean("disableCycling", disableCycling);
        backpackTag.setBoolean("disableNVision", disableNVision);
        backpackTag.setInteger("lastTime", lastTime);

        compound.setTag(TAG_WEARABLE_COMPOUND, backpackTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{leftTank, rightTank};
    }

    //====================================================TAG_INVENTORY===================================================//
    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (slot <= Constants.END_OF_INVENTORY)
        {
            return SlotBackpack.isValidItem(stack);
        }
        return (slot == TOOL_UPPER || slot == TOOL_LOWER) && SlotTool.isValidTool(stack);
    }

    @Override
    public ItemStack decrStackSize(int i, int count)
    {
        ItemStack itemstack = getStackInSlot(i);

        if (itemstack != null)
        {
            if (itemstack.stackSize <= count)
            {
                setInventorySlotContents(i, null);
            }
            else
            {
                itemstack = itemstack.splitStack(count);
            }
        }
        markDirty();
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (slot == BUCKET_IN_LEFT || slot == BUCKET_IN_RIGHT || slot == BUCKET_OUT_LEFT || slot == BUCKET_OUT_RIGHT)
        {
            return inventory[slot];
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public void markDirty()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null)
            {
                if ((i == BUCKET_IN_LEFT || i == BUCKET_IN_RIGHT)
                        || (i == BUCKET_OUT_LEFT || i == BUCKET_OUT_RIGHT) && inventory[i].getItem() instanceof ItemHose)
                {
                    updateTankSlots();
                }
            }
        }
        super.markDirty();
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean result = false;
        while (InventoryActions.transferContainerTank(this, getLeftTank(), BUCKET_IN_LEFT))
            result = true;
        while (InventoryActions.transferContainerTank(this, getRightTank(), BUCKET_IN_RIGHT))
            result = true;
        return result;
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack itemstack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null)
        {
            if (stack.stackSize <= amount)
            {
                setInventorySlotContentsNoSave(slot, null);
            }
            else
            {
                stack = stack.splitStack(amount);
            }
        }
        return stack;
    }

    @Override
    public boolean hasItem(Item item)
    {
        return InventoryActions.hasItem(this, item);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        InventoryActions.consumeItemInInventory(this, item);
    }

    //===================================================TILE ENTITY==================================================//

    //SEND SYNC PACKET
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, compound);
    }

    //RECEIV SYNC PACKET - This is necessary for the TileEntity to load the nbt as soon as it is loaded and be rendered
    //properly when the custom renderer reads it
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void updateEntity()
    {
        //Execute this backpack's ability. No, seriously. You might not infer that from the code. Just sayin'
        if (ConfigHandler.backpackAbilities && BackpackTypes.hasProperty(type, BackpackTypes.Props.TILE))
        {
            BackpackAbilities.backpackAbilities.executeTileAbility(this.worldObj, this);
        }

        //Check for backpack luminosity and a deployed sleeping bag, just in case because i'm super paranoid.
        if (checkTime == 0)
        {
            int lastLumen = luminosity;
            int left = (leftTank.getFluid() != null) ? leftTank.getFluid().getFluid().getLuminosity() : 0;
            int right = (rightTank.getFluid() != null) ? rightTank.getFluid().getFluid().getLuminosity() : 0;
            luminosity = Math.max(left, right);
            if (luminosity != lastLumen)
            {
                int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
                worldObj.setBlock(xCoord, yCoord, zCoord, ModBlocks.blockBackpack, meta, 3);
                worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, luminosity);
            }
            if (worldObj.getBlock(sbx, sby, sbz) != ModBlocks.blockSleepingBag)
            {
                sleepingBagDeployed = false;
            }
            checkTime = 20;
        }
        else
        {
            checkTime--;
        }
    }

    @Override
    public TileAdventureBackpack getTile()
    {
        return this;
    }

    @Override
    public ItemStack getParentItemStack()
    {
        return null;
    }

    private ItemStack transferToItemStack(ItemStack stack)
    {
        NBTTagCompound compound = new NBTTagCompound();
        saveToNBT(compound);
        stack.setTagCompound(compound);
        return stack;
    }

    //=====================================================BACKPACK===================================================//
    public boolean equip(World world, EntityPlayer player, int x, int y, int z)
    {
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        removeSleepingBag(world);
        if (BackpackUtils.equipWearable(stacky, player) != BackpackUtils.Reasons.SUCCESSFUL)
        {
            if (Wearing.isWearingWearable(player))
            {
                if (Wearing.isWearingBackpack(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.backpack"));
                }
                else if (Wearing.isWearingCopter(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.copterpack"));
                }
                else if (Wearing.isWearingJetpack(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.jetpack"));
                }
            }
            if (!player.inventory.addItemStackToInventory(stacky))
            {
                return drop(world, player, x, y, z);
            }
        }
        return true;
    }

    public boolean drop(World world, EntityPlayer player, int x, int y, int z)
    {
        removeSleepingBag(world);
        if (player.capabilities.isCreativeMode) return true;
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        float spawnX = x + world.rand.nextFloat();
        float spawnY = y + world.rand.nextFloat();
        float spawnZ = z + world.rand.nextFloat();

        EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stacky);

        float mult = 0.05F;

        droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
        droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
        droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;

        return world.spawnEntityInWorld(droppedItem);
    }

    @Override
    public void dirtyTanks()
    {

    }

    @Override
    public void dirtyTime()
    {

    }

    @Override
    public void dirtyExtended()
    {

    }

    @Override
    public void dirtyInventory()
    {

    }

    //=================================================ISidedInventory================================================//

    private static final int[] SLOTS = Utils.createSlotArray(0, Constants.INVENTORY_MAIN_SIZE);

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (GeneralReference.isDimensionAllowed(this.worldObj.provider.dimensionId))
        {
            return SLOTS;
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side)
    {
        return this.isItemValidForSlot(slot, item);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side)
    {
        return true;
    }
}