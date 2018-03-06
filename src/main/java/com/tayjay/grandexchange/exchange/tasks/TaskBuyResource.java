package com.tayjay.grandexchange.exchange.tasks;

import com.tayjay.gecommon.BuySell;
import com.tayjay.gecommon.ExClient;
import com.tayjay.gecommon.Ref;
import com.tayjay.gecommon.packets.RequestPacket;
import com.tayjay.grandexchange.util.ChatHelper;
import com.tayjay.grandexchange.util.CommonUtil;
import com.tayjay.grandexchange.util.ItemHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

/**
 * Created by tayjay on 2018-03-03.
 */
public class TaskBuyResource extends TaskBase<TaskBuyResource.Result>
{

    String resourceName;
    int quantity;
    ItemStack buying;

    public TaskBuyResource(EntityPlayerMP requester, String resourceName, int quantity)
    {
        super(requester);
        this.resourceName = resourceName;
        this.quantity = quantity;
    }

    @Override
    public void start()
    {
        if(BuySell.isValid(resourceName))
        {
            List<ItemStack> oreItems = ItemHelper.getItemByOreDictName(resourceName);

            if (oreItems.isEmpty())
            {
                //Item.itemRegistry.getObject()
                //buying = new ItemStack(Item.getByNameOrId(resourceName), quantity);
            }
            else
            {
                buying = oreItems.get(0);
                buying.stackSize = quantity;
            }

            if (buying == null)
            {
                ChatHelper.sendTo(requester,"Item not valid in this world.");
                return;
            }

            super.start();
        }
        else
        {
            ChatHelper.sendTo(requester,"Invalid resource name.");
        }


    }

    @Override
    protected Result runInThread()
    {
        ExClient client = CommonUtil.createClient(requester);
        try
        {
            sendRequest(new RequestPacket(client, Ref.RequestType.BUY_RESOURCE));
            out.writeUTF(resourceName);
            out.flush();
            out.writeInt(quantity);
            out.flush();

            boolean pass = in.readBoolean();
            int availableQuantity = in.readInt();

            return new Result(pass, availableQuantity);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result(false,0);
    }

    @Override
    public void finish()
    {
        if (result().pass)
        {
            if (result().newQuantity != quantity)
            {
                ChatHelper.sendTo(requester,"Insufficient resources, buying "+ result().newQuantity);
                buying.stackSize = result().newQuantity;
            }
            else
            {
                ChatHelper.sendTo(requester,"Successfully bought " + result().newQuantity + " " + buying.getDisplayName());
            }

            requester.worldObj.spawnEntityInWorld(new EntityItem(requester.worldObj, requester.posX, requester.posY, requester.posZ, buying));
        }
    }

    public class Result
    {
        boolean pass;
        int newQuantity;

        public Result(boolean pass, int newQuantity)
        {
            this.pass = pass;
            this.newQuantity = newQuantity;
        }


    }
}

