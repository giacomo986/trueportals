package it.ceneridiatlantis.trueportals;

import io.netty.buffer.ByteBuf;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class messenger implements IMessage
{
	
	private String text;
	private Packet packet;

    public messenger() 
    { 

    }

    public messenger(String text)
    {
        this.text = text;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
    }

    public static class Handler implements IMessageHandler<messenger, IMessage> {
       
        @Override
        public IMessage onMessage(messenger message, MessageContext ctx) {
            
        	
        	String messages[] = message.text.split(",,");
        	
        	//System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	
        	if (messages[0].compareTo("name") == 0)
			{
        		//System.out.println("coordinate: " + Integer.valueOf(messages[1]) + " " + Integer.valueOf(messages[2])+ " " + Integer.valueOf(messages[3]));
    		
        		TileEntity tileentity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(Integer.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]));
        		
        		if(tileentity instanceof TileEntityGeneraTruePortal)
        		{
        			TileEntityGeneraTruePortal tileEntityGeneraPuzzonio = (TileEntityGeneraTruePortal) tileentity;
        			tileEntityGeneraPuzzonio.setName(messages[4]);
        			ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(Integer.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]));
        		}
			}
        	else if (messages[0].compareTo("target") == 0)
			{
        		TileEntity tileentity = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(Integer.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]));
        		
        		if(tileentity instanceof TileEntityGeneraTruePortal)
        		{
        			TileEntityGeneraTruePortal tileEntityGeneraPuzzonio = (TileEntityGeneraTruePortal) tileentity;
        			tileEntityGeneraPuzzonio.setTarget(messages[4]);
        			ctx.getServerHandler().playerEntity.worldObj.markBlockForUpdate(Integer.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]));
        		}
			}
        		
            
        	
        	return null; // no response in this case
        }
    }
}
