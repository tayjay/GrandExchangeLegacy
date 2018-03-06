package com.tayjay.grandexchange.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayjay on 2018-03-03.
 */
public class ItemHelper
{
    public static Item getItemByName(String name)
    {
        Object obj = Item.itemRegistry.getObject(name);
        if (obj != null && obj instanceof Item)
        {
            return ((Item) obj);
        }
        else
        {
            return null;
        }
    }

    public static ItemStack getItemStackByName(String name)
    {
        Item item = getItemByName(name);
        if (item != null)
        {
            return new ItemStack(item);
        }
        return null;
    }

    public static List<ItemStack> getItemByOreDictName(String oreDictName)
    {
        ArrayList<ItemStack> itemList = OreDictionary.getOres(oreDictName);
        if (itemList != null && !itemList.isEmpty())
        {
            //There is an oreDictionary entry for this item
            return itemList;
        } else
        {
            Item basicItem = getItemByName(oreDictName);
            if (basicItem != null)
            {
                ArrayList<ItemStack> singleList = new ArrayList<ItemStack>(1);
                singleList.add(new ItemStack(basicItem));
                return singleList;
            }
        }
        return new ArrayList<ItemStack>(0);
    }

    /**
     * Take NBT tag from server, which has id as a string, then convert it to the appropriate ID for this world
     * @param newNBT NBT tag with ID as a string
     * @return Converted NBT with ID as int
     */
    public static NBTTagCompound oldifyNBT(NBTTagCompound newNBT)
    {
        if (newNBT.hasKey("id"))
        {
            String regName = newNBT.getString("id");
            if (regName.equals(""))//This happens if "id" is not a string
            {
                return newNBT;
            }
            Item item = ItemHelper.getItemByName(regName);
            if (item != null)
            {
                int itemID = Item.getIdFromItem(item);
                newNBT.setShort("id", ((short) itemID));
            }
            else
            {
                System.out.println("Error on oldifying NBT for item "+regName);
            }
        }
        return newNBT;
    }

    /**
     * Replace number for itemID to String for server to handle
     * @param oldNBT Old tag with short as id
     * @return New tag with String as id
     */
    public static NBTTagCompound newifyNBT(NBTTagCompound oldNBT)
    {
        if (oldNBT.hasKey("id"))
        {
            short itemID = oldNBT.getShort("id");
            if (itemID == 0)//This happens if "id" is not a number
            {
                return oldNBT;
            }
            String itemName = Item.getItemById(itemID).delegate.name();
            oldNBT.setString("id", itemName);
        }
        return oldNBT;
    }

    public static ItemStack getItemStackFromNBT(NBTTagCompound tagCompound)
    {
        ItemStack stack = ItemStack.loadItemStackFromNBT(tagCompound);
        if (stack != null)
        {
            return stack;
        }
        else
        {
            NBTTagCompound oldTag = oldifyNBT(tagCompound);
            ItemStack secondTryItem = ItemStack.loadItemStackFromNBT(oldTag);
            if (secondTryItem != null)
            {
                return secondTryItem;
            }
            else
            {
                return null;
            }
        }
    }

    public static ItemStack getItemStackFromNBTString(String nbtString)
    {
        try
        {
            NBTTagCompound tag = ((NBTTagCompound) JsonToNBT.func_150315_a(nbtString));
            tag = oldifyNBT(tag);
            ItemStack stack = getItemStackFromNBT(tag);
            return stack;
        } catch (NBTException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void giveItemToPlayer(EntityPlayerMP player, ItemStack itemStack)
    {
        player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, itemStack));
    }


}
