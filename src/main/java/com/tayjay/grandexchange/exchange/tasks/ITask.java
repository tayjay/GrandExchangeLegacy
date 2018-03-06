package com.tayjay.grandexchange.exchange.tasks;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by tayjay on 2018-03-03.
 */
public interface ITask<T>
{
    public void start();

    public void update();

    public boolean isDone();

    public void finish();

    public T result();

    public EntityPlayerMP getRequester();
}
