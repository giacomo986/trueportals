package it.ceneridiatlantis.trueportals.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGeneraTruePortal extends TileEntity implements ISidedInventory
{
	public String name = "name";
	public String target = "target";
	
	private ItemStack[] slots = new ItemStack[1];
	
	/*public TileEntityGeneraPuzzonio()
	{
		this.name = "ciao";
		this.target = "puzzi";
	}*/
	
	public void updateEntity()
	{
		
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		NBTTagList list = nbt.getTagList("slots", 10);
		this.slots = new ItemStack[getSizeInventory()];
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound item = list.getCompoundTagAt(i);
			byte b = item.getByte("Item");
			
			if (b >= 0 && b< this.slots.length)
			{
				this.slots[b] = ItemStack.loadItemStackFromNBT(item);
			}
		}

		this.target = nbt.getString("target");
		this.name = nbt.getString("name");
			
		//System.out.println("read from NBT, name "+ this.name + " , target " + this.target + " x " + this.xCoord);
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		nbt.setString("name", this.name);
		nbt.setString("target", this.target);
		
		NBTTagList list = new NBTTagList();
		
		for (int i = 0; i < this.slots.length; i++)
		{
			if (this.slots[i] != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("item", (byte) i);
				this.slots[i].writeToNBT(item);
				list.appendTag(item);
			}
		}
		
		nbt.setTag("slots", list);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTarget()
	{
		return this.target;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	public int getSizeInventory() 
	{
		return this.slots.length;
	}

	public ItemStack getStackInSlot(int i) 
	{
		return this.slots[i];
	}

	public ItemStack decrStackSize(int i, int j) 
	{
		if (this.slots[i] != null)
		{
			ItemStack itemstack;
			
			if (this.slots[i].stackSize <= j)
			{
				itemstack = this.slots[i];
				this.slots[i] = null;
			} else
			{
				itemstack = this.slots[i].splitStack(j);
				
				if (this.slots[i].stackSize == 0)
				{
					this.slots[i] = null;
				}
			}
			return itemstack;
		}
		return null;
	}

	public ItemStack getStackInSlotOnClosing(int i) 
	{
		if (this.slots[i] != null)
		{
			ItemStack itemstack = this.slots[i];
			this.slots[i] = null;
			return itemstack;
		}
		
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) 
	{
		this.slots[i] = itemstack;
		
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	public String getInventoryName() {
		return null;
	}

	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	public int getInventoryStackLimit() 
	{
		return 64;
	}

	public void setInventoryName(String string)
	{
		
	}
	
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return false;
	}

	public void openInventory() 
	{
		
	}

	public void closeInventory() 
	{
		
	}

	public boolean isItemValidForSlot(int var1, ItemStack var2) 
	{
		return false;
	}

	public int[] getAccessibleSlotsFromSide(int var1) 
	{
		return null;
	}

	public boolean canInsertItem(int var1, ItemStack var2, int var3) 
	{
		return false;
	}

	public boolean canExtractItem(int var1, ItemStack var2, int var3) 
	{
		return false;
	}

    public Packet getDescriptionPacket() 
    {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		readFromNBT(packet.func_148857_g());
	}
	
}
