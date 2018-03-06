package com.tayjay.grandexchange.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Created by tayjay on 2018-03-03.
 */
public class ChatHelper
{
    /**
     * Send a chat message to the world.
     * @param msg Message to be sent
     */
    public static void send(String msg) //Send to world
    {
        MinecraftServer mcServer = MinecraftServer.getServer();
        mcServer.getConfigurationManager().sendChatMsg(new ChatComponentText(msg));
    }

    /**
     * Send a chat message to a player.
     * @param player Recipient of message
     * @param msg Message to be sent.
     */
    public static void sendTo(EntityPlayer player, String msg) //Send to player
    {
        if(player!=null)
        {
            player.addChatMessage(new ChatComponentText(msg));
            //player.addChatComponentMessage(new ChatComponentText(msg));
        }
    }
}
