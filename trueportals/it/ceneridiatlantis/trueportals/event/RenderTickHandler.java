package it.ceneridiatlantis.trueportals.event;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.ForgeHooksClient;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTickHandler
{
	/** Stores an instance of Minecraft for easy access */
	private Minecraft mc;
	
	// create a constructor that takes a Minecraft argument; now we have it whenever we need it
	public RenderTickHandler(Minecraft mc)
	{
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if (this.mc.theWorld != null)
		{
			if (event.phase == Phase.START) 
			{
			
				GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
				
				GL11.glStencilFunc(GL11.GL_EQUAL, 0x0, 0xFF);
				GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);/**/
				
				
			} else if (event.phase == Phase.END) 
			{
				
			}
		}
	}
	
}