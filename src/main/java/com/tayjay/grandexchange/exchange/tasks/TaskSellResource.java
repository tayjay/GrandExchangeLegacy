package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.BuySell;
import com.tayjay.gecommon.ExClient;
import com.tayjay.gecommon.Ref;
import com.tayjay.gecommon.packets.RequestPacket;
import com.tayjay.grandexchange.util.ChatHelper;
import com.tayjay.grandexchange.util.CommonUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.IOException;

/**
 * Created by tayjay on 2018-03-03.
 */
public class TaskSellResource extends TaskBase<TaskSellResource.Result>
{
    ExClient client;
    ItemStack offer;
    String oreDictName;

    public TaskSellResource(EntityPlayerMP requester, ItemStack stack)
    {
        super(requester);
        this.offer =stack;
    }

    @Override
    public void start()
    {
        if (this.offer == null)
        {
            ChatHelper.sendTo(requester,"Nothing Offered...");
            return;
        }
        this.client = CommonUtil.createClient(requester);
        /*int[] oreids = OreDictionary.getOreIDs(offer);
        //String[] oreNames = new String[oreids.length];
        String oreName;
        if (oreids.length > 0)
        {
            for(int i =0;i<oreids.length;i++)
            {
                oreName = OreDictionary.getOreName(oreids[i]);
                if (BuySell.isValid(oreName))
                {
                    startThread();
                    return;
                }
            }
        }*/

        //startThread();
        ItemStack temp = offer.copy();

        String itemName = temp.getItem().delegate.name();
        itemName = itemName.substring(itemName.indexOf(":")+1);
        String oreName = "";
        int[] oreIDS = OreDictionary.getOreIDs(temp);
        if (oreIDS.length > 0)
        {
            for(int i =0;i<oreIDS.length;i++)
            {
                oreName = OreDictionary.getOreName(oreIDS[i]);
                if (BuySell.isValid(oreName))
                {
                    break;
                }
            }
        }
        if ((BuySell.isValid(itemName) || BuySell.isValid(oreName)))
        {
            requester.inventory.setInventorySlotContents(requester.inventory.currentItem, null);
            this.offer = temp;
            startThread();

            if(BuySell.isValid(oreName))
            {
                oreDictName = oreName;
            }
            else
            {
                oreDictName = itemName;
            }

        }
        else
        {
            ChatHelper.sendTo(requester,"Invalid offer: "+oreName+" / "+itemName);
            return;
        }
        //

    }

    @Override
    protected Result runInThread()
    {
        Result result = null;

        try
        {
            sendRequest(new RequestPacket(client, Ref.RequestType.SELL_RESOURCE));
            out.writeUTF(oreDictName);
            out.flush();
            out.writeInt(offer.stackSize);
            out.flush();

            boolean success = in.readBoolean();
            String response = in.readUTF();
            result = new Result(success, response);
        } catch (IOException e)
        {
            e.printStackTrace();
            result = new Result(false, "IO Error");
        }
        return result;
    }

    @Override
    public void finish()
    {
        if(result().pass)
            ChatHelper.sendTo(requester,result().reponse);
        else
        {
            ChatHelper.sendTo(requester,result().reponse);
            requester.worldObj.spawnEntityInWorld(new EntityItem(requester.worldObj, requester.posX, requester.posY, requester.posZ, this.offer));
        }
    }

    public class Result
    {
        public boolean pass;
        public String reponse;

        public Result(boolean pass, String response)
        {
            this.pass = pass;
            this.reponse = response;
        }
    }
}
