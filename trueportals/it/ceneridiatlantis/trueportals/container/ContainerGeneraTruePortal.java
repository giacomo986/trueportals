package it.ceneridiatlantis.trueportals.container;

import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerGeneraTruePortal extends Container {

	private TileEntityGeneraTruePortal GeneraTruePortal;
	
	public ContainerGeneraTruePortal(InventoryPlayer inventoryPlayer, TileEntityGeneraTruePortal GeneraTruePortal)
	{
		this.GeneraTruePortal = GeneraTruePortal;
		
		this.addSlotToContainer(new Slot(GeneraTruePortal, 0, 15, 15));
		
		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + (18*i), 142));
		}
		
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, 9+j+(i*9), 8 + (18*j), 84 + (18*i)));
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}

}
