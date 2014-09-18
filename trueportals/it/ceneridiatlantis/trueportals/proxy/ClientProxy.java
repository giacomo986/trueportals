package it.ceneridiatlantis.trueportals.proxy;

import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.renderer.TileEntityRenderGeneraTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.renderer.TileEntityRenderTruePortal;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy 
{
	public void registerProxies() 
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTruePortal.class, new TileEntityRenderTruePortal());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGeneraTruePortal.class, new TileEntityRenderGeneraTruePortal());
		//FMLCommonHandler.instance().bus().register(new RenderTickHandler(Minecraft.getMinecraft()));
	}
}
