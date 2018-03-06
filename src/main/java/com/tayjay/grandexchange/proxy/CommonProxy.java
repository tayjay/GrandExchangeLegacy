package com.tayjay.grandexchange.proxy;

import com.tayjay.grandexchange.handler.ServerHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by tayjay on 2018-03-03.
 */
public class CommonProxy
{
    public void preInit()
    {

    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(new ServerHandler());
        FMLCommonHandler.instance().bus().register(new ServerHandler());
    }


}
