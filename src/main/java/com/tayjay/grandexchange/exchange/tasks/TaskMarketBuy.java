package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.ExchangeItem;
import com.tayjay.gecommon.Ref;
import com.tayjay.gecommon.packets.RequestPacket;
import com.tayjay.grandexchange.util.ChatHelper;
import com.tayjay.grandexchange.util.CommonUtil;
import com.tayjay.grandexchange.util.ItemHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.io.IOException;

/**
 * Created by tayjay on 2018-03-04.
 */
public class TaskMarketBuy extends TaskBase<TaskMarketBuy.Result>
{
    int market_id;
    public TaskMarketBuy(EntityPlayerMP requester,int market_id)
    {
        super(requester);
        this.market_id = market_id;
    }

    @Override
    protected Result runInThread() throws IOException
    {
        sendRequest(new RequestPacket(CommonUtil.createClient(requester), Ref.RequestType.MARKET_BUY));
        out.writeInt(this.market_id);
        out.flush();

        boolean pass = in.readBoolean();
        String comment = in.readUTF();

        if (pass)
        {
            try
            {
                Object objItem = in.readObject();
                if (objItem != null && objItem instanceof ExchangeItem)
                {
                    ExchangeItem exchangeItem = ((ExchangeItem) objItem);
                    ItemStack stack = CommonUtil.createItemStackFromExhange(exchangeItem);
                    if (stack != null)
                    {
                        out.writeBoolean(true);
                        out.flush();
                        return new Result(true, exchangeItem,comment);
                    }
                    else
                    {
                        out.writeBoolean(false);
                        out.flush();
                        return new Result(false, exchangeItem,"Item not valid in this world!");
                    }
                }
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            return new Result(false, null,comment);
        }


        return new Result(false, null,"");


    }

    @Override
    public void finish()
    {
        if (result().pass)
        {
            if (result().exchangeItem != null)
            {
                ChatHelper.sendTo(requester,"Item Successfully Purchased.");
                ItemHelper.giveItemToPlayer(requester,ItemHelper.getItemStackFromNBTString(result().exchangeItem.nbt_string));
            }
            else
            {
                ChatHelper.sendTo(requester,"No item to return???");
            }
        }
        else
        {
            ChatHelper.sendTo(requester,result().comment);
        }
    }

    public class Result
    {
        boolean pass;
        ExchangeItem exchangeItem;
        String comment;

        public Result(boolean pass, ExchangeItem exchangeItem, String comment)
        {
            this.pass =pass;
            this.exchangeItem = exchangeItem;
            this.comment = comment;
        }
    }
}
