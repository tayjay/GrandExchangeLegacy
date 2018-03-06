package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.ExClient;
import com.tayjay.gecommon.Ref;
import com.tayjay.gecommon.packets.RequestPacket;
import com.tayjay.grandexchange.util.ChatHelper;
import com.tayjay.grandexchange.util.CommonUtil;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;

/**
 * Created by tayjay on 2018-03-03.
 */
public class TaskSendClient extends TaskBase<TaskSendClient.Result>
{

    public TaskSendClient(EntityPlayerMP requester)
    {
        super(requester);
    }

    @Override
    protected Result runInThread() throws IOException
    {
        try
        {
            System.out.println("Transferring object, initializing connection");

            System.out.println("Read/Write created.");
            ExClient client = CommonUtil.createClient(requester);
            System.out.println("Client object created");
            RequestPacket packetInit = new RequestPacket(client, Ref.RequestType.ECHO_CLIENT);
            System.out.println("Request packet Created.");
            out.writeObject(packetInit);
            System.out.println("Sent request");

            String response = in.readUTF();
            System.out.println("Got response");

            socket.close();
            return new Result(response);


        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result("FAIL");
    }

    @Override
    public void finish()
    {
        if (result().response != null)
        {
            ChatHelper.sendTo(requester, result().response);
        }
    }

    public class Result
    {
        String response;

        public Result(String response)
        {
            this.response = response;
        }
    }
}
