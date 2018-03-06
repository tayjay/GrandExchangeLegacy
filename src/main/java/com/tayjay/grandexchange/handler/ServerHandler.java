package com.tayjay.grandexchange.handler;

import com.tayjay.grandexchange.GrandExchange;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Created by tayjay on 2018-03-03.
 */
public class ServerHandler
{
    @SubscribeEvent
    public void tickServer(TickEvent.ServerTickEvent event)
    {
        if (GrandExchange.exchangeConnection != null)
        {
            GrandExchange.exchangeConnection.updateTasksOnGameLoop();
        }
    }
}
