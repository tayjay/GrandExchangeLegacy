package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.ExchangeItem;
import com.tayjay.gecommon.Ref;
import com.tayjay.gecommon.packets.RequestPacket;
import com.tayjay.grandexchange.util.ChatHelper;
import com.tayjay.grandexchange.util.CommonUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.io.IOException;

/**
 * Created by tayjay on 2018-03-04.
 */
public class TaskMarketOffer extends TaskBase<TaskMarketOffer.Result>
{
    ExchangeItem item;
    ItemStack stack;
    float price;

    public TaskMarketOffer(EntityPlayerMP requester, ItemStack stack,float price)
    {
        super(requester);
        this.item = CommonUtil.createExchangeItem(stack);
        this.item.setIcon(new int[16*16]);
        this.stack = stack;
        this.price = price;
    }

    @Override
    public void start()
    {
        //Take item from player first
        super.start();
    }

    @Override
    protected Result runInThread() throws IOException
    {
        sendRequest(new RequestPacket(CommonUtil.createClient(requester), Ref.RequestType.MARKET_OFFER));

        out.writeObject(item);
        out.flush();
        out.writeFloat(this.price);
        out.flush();

        boolean confirm = in.readBoolean();

        if (confirm)
        {
            boolean pass = in.readBoolean();
            int market_id = in.readInt();
            String comment = in.readUTF();
            return new Result(pass, market_id, comment);
        }
        else
        {
            return new Result(false,-1,"Server did not confirm.");
        }
    }

    @Override
    public void finish()
    {
        ChatHelper.sendTo(requester,result().comment);

        if (result().pass)
        {
            ChatHelper.sendTo(requester,"Market ID is: " + this.result().market_id);
        }
    }

    public class Result
    {
        boolean pass;
        int market_id;
        String comment="NOPE";

        public Result(boolean pass, int market_id, String comment)
        {
            this.pass = pass;
            this.market_id = market_id;
            this.comment = comment;
        }
    }
}
