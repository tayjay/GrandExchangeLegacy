package com.tayjay.grandexchange.util;

import com.tayjay.gecommon.ExClient;
import com.tayjay.gecommon.ExchangeItem;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

/**
 * Created by tayjay on 2018-03-03.
 */
public class CommonUtil
{
    public static ExClient createClient(EntityPlayerMP player)
    {
        WorldServer worldServer = (WorldServer) player.getEntityWorld();
        MinecraftServer server = player.mcServer;
        String playerIP = player.getPlayerIP();
        String username = player.getCommandSenderName();
        UUID uuid = player.getUniqueID();
        boolean isCheat = worldServer.getWorldInfo().areCommandsAllowed();
        boolean isCreative = player.theItemInWorldManager.isCreative();
        boolean isOnline = player.getGameProfile().isComplete()&&!server.isServerInOnlineMode();
        int hash = worldServer.hashCode()+username.hashCode()+uuid.hashCode();

        return new ExClient(username, playerIP, uuid, isCheat, isCreative, isOnline, hash);
    }

    public static ItemStack createItemStackFromExhange(ExchangeItem item)
    {
        //Validate that this item is valid in this world
        String disp_name= item.disp_name;
        String reg_name = item.reg_name;
        String mod_name = item.mod_name;
        String mc_version = item.mc_version;
        String nbt_string = item.nbt_string;

        if(MinecraftForge.MC_VERSION.equals(mc_version))
        {
            //Same version of Minecraft
        }
        else
        {
            //Warn that they are different versions
            System.out.println("This item is loading from MC version "+mc_version);
        }
        NBTTagCompound tag = new NBTTagCompound();
        // taking inspiration from GameRegistry.makeItemStack()

        Item item1 = GameRegistry.findItem(mod_name, reg_name);
        //Item item = GameData.getItemRegistry().getObject(new ResourceLocation(mod_name+":"+reg_name));
        if (item1 == null)
        {
            boolean modValid=false;
            //This item does not exist
            for (ModContainer mod : Loader.instance().getModList())
            {
                if (mod.getModId().equals(mod_name))
                {
                    //The mod for it is present
                    modValid = true;
                    break;
                }
            }
            if(!modValid)
                return null;
        }
        //Item is valid
        try
        {
            tag = (NBTTagCompound) JsonToNBT.func_150315_a(nbt_string);
            tag = ItemHelper.oldifyNBT(tag);
            return ItemStack.loadItemStackFromNBT(tag);

        } catch (NBTException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static ExchangeItem createExchangeItem(ItemStack stack)
    {
        NBTTagCompound item_nbt = new NBTTagCompound();
        stack.writeToNBT(item_nbt);
        item_nbt = ItemHelper.newifyNBT(item_nbt);
        String nbt_string = item_nbt.toString();
        String disp_name = stack.getDisplayName();
        String reg_name = stack.getItem().delegate.name().substring(stack.getItem().delegate.name().indexOf(":")+1);
        String mod_name = stack.getItem().delegate.name().substring(0,stack.getItem().delegate.name().indexOf(":"));
        String mc_version = MinecraftForge.MC_VERSION;
        byte stacksize = (byte) stack.stackSize;
        short metadata = (short) stack.getMaxDamage();
        byte rarity = ((byte) stack.getRarity().ordinal());
        return new ExchangeItem(disp_name,reg_name,mod_name,mc_version,stacksize, metadata,rarity,stack.getTooltip(Minecraft.getMinecraft().thePlayer,true),nbt_string);
    }
}
