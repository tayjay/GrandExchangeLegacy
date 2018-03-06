package com.tayjay.grandexchange.command;

import com.tayjay.grandexchange.GrandExchange;
import com.tayjay.grandexchange.util.ChatHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayjay on 2018-03-03.
 */
public class CommandExchange extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "ge";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if ("ping".equals(args[0]))
        {
            GrandExchange.exchangeConnection.sendClientToExchange(((EntityPlayerMP) sender));
        } else if ("sell".equals(args[0]))
        {
            EntityPlayerMP player = ((EntityPlayerMP) sender);
            if (player.getHeldItem() != null)
            {
                GrandExchange.exchangeConnection.sellResourceToExchange(player,player.getHeldItem());
            }
            else
            {
                ChatHelper.sendTo(player,"Nothing Offered to sell.");
            }
        } else if ("buy".equals(args[0]))
        {
            if (args.length == 3)
            {
                String itemName = args[1];
                int quantity = Integer.parseInt(args[2]);
                GrandExchange.exchangeConnection.buyResourceFromExchange(((EntityPlayerMP) sender),itemName,quantity);
            }
        } else if ("item".equals(args[0]))
        {
            EntityPlayerMP player = ((EntityPlayerMP) sender);
            if (player.getHeldItem() != null)
            {
                NBTTagCompound tag = new NBTTagCompound();
                player.getHeldItem().writeToNBT(tag);
                ChatHelper.sendTo(player, tag.toString());
                ChatHelper.sendTo(player, player.getHeldItem().getItem().delegate.name());
                Object obj = Item.itemRegistry.getObject(player.getHeldItem().getItem().delegate.name());
                if (obj != null && obj instanceof Item)
                {
                    ChatHelper.sendTo(player, ((Item) obj).delegate.name());
                }
            } else
            {

            }
        } else if ("offer".equals(args[0]))
        {
            GrandExchange.exchangeConnection.offerItemToMarketPlace(((EntityPlayerMP) sender), ((EntityPlayerMP) sender).getHeldItem(), Float.valueOf(args[1]));
        } else if ("market".equals(args[0]))
        {
            GrandExchange.exchangeConnection.buyItemFromMarketPlace(((EntityPlayerMP) sender), Integer.parseInt(args[1]));
        }
    }
}
