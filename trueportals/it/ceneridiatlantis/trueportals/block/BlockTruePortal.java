package it.ceneridiatlantis.trueportals.block;

import it.ceneridiatlantis.trueportals.tileentity.TileEntityTruePortal;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockTruePortal extends BlockContainer {

	
	
	public BlockTruePortal() {
		super(Material.portal);
		// TODO Auto-generated constructor stub
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public TileEntity createNewTileEntity(World var1, int var2) 
	{
		return new TileEntityTruePortal();
	}
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
		
		if (entity.posZ >= z+0.3 && entity.posZ <= z+0.7)
		{
			//System.out.println("prima x: " + entity.posX + " y: " + entity.posY + " z: " + entity.posZ);
			//System.out.println("posx: " + entity.posX + " posz: " + entity.posZ + " x: " + x + " z: " + z);
			entity.lastTickPosZ += 20;
			//entity.prevPosZ += 5;
			entity.setPosition(entity.posX, entity.posY, entity.posZ + 20);
			//System.out.println("dopo x: " + entity.posX + " y: " + entity.posY + " z: " + entity.posZ);
		}
		
    }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }
}
