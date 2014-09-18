package it.ceneridiatlantis.trueportals.tileentity.renderer;

import it.ceneridiatlantis.trueportals.TruePortals;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityRenderTruePortal extends TileEntitySpecialRenderer 
{
	private final ResourceLocation texturePuzzonio = new ResourceLocation(TruePortals.MODID, "textures/blocks/puzzonio.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) 
	{
		
		GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, (float)z);
			
			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(texturePuzzonio);
			
			GL11.glColorMask(false, false, false, false); // non renderizza i cubi che segnano le zone con stencil a 1
			GL11.glStencilFunc(GL11.GL_ALWAYS, 0x1, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); //quando il puzzonio è in vista scrive 1 sullo stencil buffer
			
            GL11.glDisable(GL11.GL_CULL_FACE);
            
			tessellator.startDrawingQuads(); // Starts the tessellator
			{
				tessellator.addVertexWithUV(0, 0, 1, 1, 1); // fronte
				tessellator.addVertexWithUV(0, 1, 1, 1, 0);
				tessellator.addVertexWithUV(0, 1, 0, 0, 0);
				tessellator.addVertexWithUV(0, 0, 0, 0, 1);
				
				tessellator.addVertexWithUV(1, 0, 1, 1, 1); // destra
				tessellator.addVertexWithUV(1, 1, 1, 1, 0);
				tessellator.addVertexWithUV(0, 1, 1, 0, 0);
				tessellator.addVertexWithUV(0, 0, 1, 0, 1);
				
				tessellator.addVertexWithUV(0, 0, 0, 1, 1); // sinistra
				tessellator.addVertexWithUV(0, 1, 0, 1, 0);
				tessellator.addVertexWithUV(1, 1, 0, 0, 0);
				tessellator.addVertexWithUV(1, 0, 0, 0, 1);
	
				tessellator.addVertexWithUV(1, 0, 0, 1, 1); // retro
				tessellator.addVertexWithUV(1, 1, 0, 1, 0);
				tessellator.addVertexWithUV(1, 1, 1, 0, 0);
				tessellator.addVertexWithUV(1, 0, 1, 0, 1);
	
				tessellator.addVertexWithUV(1, 1, 1, 1, 1); // sopra
				tessellator.addVertexWithUV(1, 1, 0, 1, 0);
				tessellator.addVertexWithUV(0, 1, 0, 0, 0);
				tessellator.addVertexWithUV(0, 1, 1, 0, 1);
	
				tessellator.addVertexWithUV(0, 0, 1, 1, 1); // sotto
				tessellator.addVertexWithUV(0, 0, 0, 1, 0);
				tessellator.addVertexWithUV(1, 0, 0, 0, 0);
				tessellator.addVertexWithUV(1, 0, 1, 0, 1); 
			}
			tessellator.draw();/**/ //Ends of tessellator

            GL11.glEnable(GL11.GL_CULL_FACE);
            
			GL11.glStencilFunc(GL11.GL_ALWAYS, 0x0, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // quando viene renderizzato un oggetto più vicino al puzzonio, bisogna ripristinare lo stencil buffer sullo 0 per la zona interessata
			
			GL11.glColorMask(true, true, true, false);
		
		GL11.glPopMatrix();
	}

}
