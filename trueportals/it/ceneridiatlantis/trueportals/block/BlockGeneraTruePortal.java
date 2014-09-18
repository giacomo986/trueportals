package it.ceneridiatlantis.trueportals.block;

import it.ceneridiatlantis.trueportals.TruePortals;
import it.ceneridiatlantis.trueportals.messenger;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public class BlockGeneraTruePortal extends BlockContainer 
{
	TileEntityGeneraTruePortal tileEntityGeneraPuzzonio;
	
	public BlockGeneraTruePortal() 
	{
		super(Material.ground);
	}
	
	public int getRenderType()
	{
		return -1;
	}
	
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public TileEntity createNewTileEntity(World world, int var2) 
	{
		this.tileEntityGeneraPuzzonio = new TileEntityGeneraTruePortal();
		return this.tileEntityGeneraPuzzonio;
		
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			FMLNetworkHandler.openGui(player, TruePortals.instance, TruePortals.guiIDGeneraPuzzonio, world, x, y, z);
		}
		return true;
	}
	
	public void onBlockAdded(World world, int x, int y, int z)
    {
		sendNameToServer(x, y, z);
		//this.tileEntityGeneraPuzzonio.setName("x: " + this.tileEntityGeneraPuzzonio.xCoord + ", y: " + this.tileEntityGeneraPuzzonio.yCoord + ", z: " + this.tileEntityGeneraPuzzonio.zCoord);
		
		if (world.isAirBlock(x+1, y, z) && world.isAirBlock(x+1, y+1, z) && world.isAirBlock(x+1, y+2, z) && world.isAirBlock(x+2, y+1, z) && world.isAirBlock(x+2, y, z) && world.isAirBlock(x+2, y+2, z))
        {
        	world.setBlock(x+1, y, z, TruePortals.blockTruePortal, 0, 2);
        	world.setBlock(x+1, y+1, z, TruePortals.blockTruePortal, 0, 2);
        	world.setBlock(x+1, y+2, z, TruePortals.blockTruePortal, 0, 2);
        	world.setBlock(x+2, y, z, TruePortals.blockTruePortal, 0, 2);
        	world.setBlock(x+2, y+1, z, TruePortals.blockTruePortal, 0, 2);
        	world.setBlock(x+2, y+2, z, TruePortals.blockTruePortal, 0, 2);
        }
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
	{
		if (world.getBlock(x+1, y, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+1, y, z);
		}
		if (world.getBlock(x+1, y+1, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+1, y+1, z);
		}
		if (world.getBlock(x+1, y+2, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+1, y+2, z);
		}
		if (world.getBlock(x+2, y, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+2, y, z);
		}
		if (world.getBlock(x+2, y+1, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+2, y+1, z);
		}
		if (world.getBlock(x+2, y+2, z).equals(TruePortals.blockTruePortal))
		{
			world.setBlockToAir(x+2, y+2, z);
		}
	}
	
	public void sendNameToServer(int x, int y, int z)
	{
		String string;
		this.tileEntityGeneraPuzzonio.setName("coord," + x + "," + y + "," + z); 
    	
		string = "name,," + x + ",," + y + ",,"+ z + ",," + tileEntityGeneraPuzzonio.getName();
	    
	    TruePortals.network.sendToServer(new messenger(string));
	}
}
