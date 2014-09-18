package it.ceneridiatlantis.trueportals.tileentity.renderer;

import it.ceneridiatlantis.trueportals.TruePortals;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityRenderGeneraTruePortal extends TileEntitySpecialRenderer 
{
	private final ResourceLocation texturePuzzonio = new ResourceLocation(TruePortals.MODID, "textures/blocks/puzzonio.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) 
	{
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		
		Tessellator tessellator = Tessellator.instance;
		this.bindTexture(texturePuzzonio);
		
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
		tessellator.draw(); //Ends of tessellator
		
		GL11.glPopMatrix();
	}

}
