package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.packets.RequestPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by tayjay on 2018-03-03.
 */
public abstract class TaskBase<T> implements ITask
{
    protected Future<T> output;
    protected EntityPlayerMP requester;
    protected Socket socket;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;

    public TaskBase(EntityPlayerMP requester)
    {
        this.requester = requester;
    }

    protected void initConnection()
    {
        try
        {
            socket = new Socket("138.68.12.167", 20123);
            //socket.setSoTimeout(3000);
            System.out.println("Socket Connected: "+socket.isConnected());
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            out = new ObjectOutputStream(outputStream);
            in = new ObjectInputStream(inputStream);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    protected void startThread()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        this.output = executor.submit(new Callable<T>()
        {
            @Override
            public T call() throws Exception
            {
                initConnection();
                T returning = null;
                try
                {
                    returning = runInThread();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                return returning;
            }
        });
    }

    /**
     * This will be ran out of sync from the game world, inside the call method of another thread
     * DO NOT ACCESS ANYTHING ON MC SIDE!!!
     * @return
     */
    protected abstract T runInThread() throws IOException;

    @Override
    public void start()
    {
        startThread();
    }

    @Override
    public T result()
    {
        if(this.isDone())
        {
            try
            {
                return output.get();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void update()
    {
        if (isDone())
        {
            finish();
        }
    }

    protected void sendRequest(RequestPacket request)
    {
        try
        {
            out.writeObject(request);
            out.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    @Override
    public EntityPlayerMP getRequester()
    {
        return requester;
    }

    @Override
    public boolean isDone()
    {
        return output != null && output.isDone();
    }
}
