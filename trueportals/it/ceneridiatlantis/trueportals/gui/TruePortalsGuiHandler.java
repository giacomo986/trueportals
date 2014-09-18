package it.ceneridiatlantis.trueportals.gui;

import it.ceneridiatlantis.trueportals.TruePortals;
import it.ceneridiatlantis.trueportals.container.ContainerGeneraTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class TruePortalsGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileentity = world.getTileEntity(x, y, z);

		switch(ID)
		{
			case TruePortals.guiIDGeneraPuzzonio:
				if (tileentity instanceof TileEntityGeneraTruePortal) 
				{
					return new ContainerGeneraTruePortal(player.inventory, (TileEntityGeneraTruePortal) tileentity);
				}
			//case 1:
			
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tileentity = world.getTileEntity(x, y, z);

		switch(ID)
		{
			case TruePortals.guiIDGeneraPuzzonio:
				if (tileentity instanceof TileEntityGeneraTruePortal) 
				{
					return new GuiGeneraTruePortal(player.inventory, (TileEntityGeneraTruePortal) tileentity);
				}
			//case 1:
			
		}
		return null;
	}

}
