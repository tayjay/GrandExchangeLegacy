package com.tayjay.grandexchange.exchange;

import com.tayjay.grandexchange.exchange.tasks.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tayjay on 2018-03-03.
 */
public class ExchangeConnection
{
    List<ITask> tasks;

    public ExchangeConnection()
    {
        this.tasks = new ArrayList<ITask>();
    }

    public void updateTasksOnGameLoop()
    {
        Iterator<ITask> it = tasks.iterator();
        ITask task;
        while(it.hasNext())
        {
            task = it.next();
            task.update();
            if(task.isDone())
                it.remove();
        }
    }

    public void registerTask(ITask task)
    {
        tasks.add(task);
        task.start();
    }

    public void sendClientToExchange(EntityPlayerMP requester)
    {
        registerTask(new TaskSendClient(requester));
    }

    public void sellResourceToExchange(EntityPlayerMP requester, ItemStack stack)
    {
        registerTask(new TaskSellResource(requester,stack));
    }

    public void buyResourceFromExchange(EntityPlayerMP requester, String itemName, int quantity)
    {
        registerTask(new TaskBuyResource(requester,itemName,quantity));
    }

    public void offerItemToMarketPlace(EntityPlayerMP seller, ItemStack stack,float price)
    {
        registerTask(new TaskMarketOffer(seller,stack,price));
    }

    public void buyItemFromMarketPlace(EntityPlayerMP buyer, int market_id)
    {
        registerTask(new TaskMarketBuy(buyer,market_id));
    }
}
