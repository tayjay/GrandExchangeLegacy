package com.tayjay.grandexchange;

import com.tayjay.grandexchange.command.CommandExchange;
import com.tayjay.grandexchange.exchange.ExchangeConnection;
import com.tayjay.grandexchange.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Created by tayjay on 2018-03-03.
 */
@Mod(modid = "grandexchange", version = "0.0.1")
public class GrandExchange
{
    @Mod.Instance
    public static GrandExchange instance;

    @SidedProxy(clientSide = "com.tayjay.grandexchange.proxy.ClientProxy", serverSide = "com.tayjay.grandexchange.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static ExchangeConnection exchangeConnection;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandExchange());
        GrandExchange.exchangeConnection = new ExchangeConnection();

        System.out.println("Refreshing Exchange Connection");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }
}
