package it.ceneridiatlantis.trueportals.event;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlCapsChecker;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TruePortalsRenderEvent 
{
	/** Stores an instance of Minecraft for easy access */
	private Minecraft mc;
    private float farPlaneDistance;
	
    public EntityClientPlayerMP theCamera;
    

    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    

    /** OpenGL occlusion query base */
    private IntBuffer glOcclusionQueryBase;
    /** Is occlusion testing enabled */
    private boolean occlusionEnabled;

    /** Occlusion query result */
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
    
    /** The star GL Call list */
    private int starGLCallList;
    /** OpenGL sky list */
    private int glSkyList;
    /** OpenGL sky list 2 */
    private int glSkyList2;
    
    
    /** OpenGL sky list 3 */
    private int glSkyList3; // for horizon

    /** Entity renderer update count */
    private int rendererUpdateCount;
    
    /** Cloud fog mode */
    private boolean cloudFog;
    /** Fog color buffer */
    FloatBuffer fogColorBuffer;
    /** red component of the fog color */
    float fogColorRed;
    /** green component of the fog color */
    float fogColorGreen;
    /** blue component of the fog color */
    float fogColorBlue;
    /** Fog color 2 */
    private float fogColor2;
    /** Fog color 1 */
    private float fogColor1;
    

    /** FOV modifier hand */
    private float fovModifierHand;
    /** FOV modifier hand prev */
    private float fovModifierHandPrev;

    private double cameraZoom;
    private double cameraYaw;
    private double cameraPitch;
    

    private float thirdPersonDistance = 4.0F;
    /** Third person distance temp */
    private float thirdPersonDistanceTemp = 4.0F;
    private float debugCamYaw;
    private float prevDebugCamYaw;
    private float debugCamPitch;
    private float prevDebugCamPitch;
    
    private float debugCamFOV;
    private float prevDebugCamFOV;
    private float camRoll;
    private float prevCamRoll;
	
    /**
     * Debug view direction (0=OFF, 1=Front, 2=Right, 3=Back, 4=Left, 5=TiltLeft, 6=TiltRight)
     */
    public int debugViewDirection;
    
	// create a constructor that takes a Minecraft argument; now we have it whenever we need it
	public TruePortalsRenderEvent(Minecraft mc)
	{
		this.mc = mc;
		this.RenderGlobal();
        this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
        

        this.cameraZoom = 1.0D;
	}
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{

		if (this.mc.theWorld != null)
		{
			this.theCamera = new EntityClientPlayerMP(mc, mc.theWorld, mc.getSession(), mc.getNetHandler(), new StatFileWriter());
			
		}
		
        
        this.theCamera = (EntityClientPlayerMP) this.mc.renderViewEntity;/**/
        
        this.updateRenderer();
		
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        
        //copiaCamera();
        
        //this.mc.renderViewEntity.getBrightnessForRender(event.partialTicks);
        
		GL11.glPushMatrix();
	        
        	this.updateFogColor(event.partialTicks);

            //this.setupCameraTransform(event.partialTicks, 0);
        	/*
        	double d0 = this.mc.renderViewEntity.lastTickPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.lastTickPosX) * (double)event.partialTicks;
	        double d1 = this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)event.partialTicks;
	        double d2 = this.mc.renderViewEntity.lastTickPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.lastTickPosZ) * (double)event.partialTicks;
	        */
	        double d0 = this.theCamera.lastTickPosX + (this.theCamera.posX - this.theCamera.lastTickPosX) * (double)event.partialTicks;
	        double d1 = this.theCamera.lastTickPosY + (this.theCamera.posY - this.theCamera.lastTickPosY) * (double)event.partialTicks;
	        double d2 = this.theCamera.lastTickPosZ + (this.theCamera.posZ - this.theCamera.lastTickPosZ) * (double)event.partialTicks;
		
			GL11.glStencilFunc(GL11.GL_EQUAL, 0x1, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			

            GL11.glTranslatef(0.0F, 0F, -20.0F); // traslazione a scopo di test
            

            //this.setupFog(-1, event.partialTicks);
            if (this.mc.gameSettings.renderDistanceChunks >= 4)
            {
                //this.setupFog(-1, event.partialTicks);
                //this.mc.mcProfiler.endStartSection("sky");
                this.renderSky(event.partialTicks);
            }


            GL11.glEnable(GL11.GL_FOG);
            this.setupFog(1, event.partialTicks);
            
            
            Frustrum frustrum = new Frustrum();
            frustrum.setPosition(d0, d1, d2 - 30);
            this.mc.renderGlobal.clipRenderersByFrustum(frustrum, event.partialTicks);

            //if (this.mc.renderViewEntity.posY < 128.0D)
            if (this.theCamera.posY < 128.0D)
            {
                this.renderCloudsCheck(event.context, event.partialTicks); // renderizza le nuvole
            }
            
            //this.setupFog(0, event.partialTicks);
            //GL11.glEnable(GL11.GL_FOG);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            
            //GL11.glEnable(GL11.GL_FOG);
            
            //event.context.sortAndRender(this.mc.renderViewEntity, 0, (double)event.partialTicks); // resetta il render dei blocchi dell'ambiente
            
            event.context.sortAndRender(this.theCamera, 0, (double)event.partialTicks); // resetta il render dei blocchi dell'ambiente

            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            this.setupFog(0, event.partialTicks);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDepthMask(false);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            
            //event.context.sortAndRender(this.mc.renderViewEntity, 1, (double)event.partialTicks); // renderizza i blocchi dell'ambiente

            event.context.sortAndRender(this.theCamera, 1, (double)event.partialTicks); // renderizza i blocchi dell'ambiente
            

	        //this.mc.renderViewEntity.posZ += 20;

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_FOG);
            


	        /*this.theCamera.posY -= 10;
	        this.theCamera.prevPosY -= 10;
	        this.theCamera.lastTickPosY -= 10;/**/
		GL11.glPopMatrix();
		

			
	}
	
	@SubscribeEvent
	public void onHandRender(RenderHandEvent event)
	{

		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

		GL11.glStencilFunc(GL11.GL_EQUAL, 0x0, 0xFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
	}

	@SubscribeEvent
	public void RenderGameOverlay(RenderGameOverlayEvent event)
	{

	}/**/
	
	/**
     * Renders the sky with the partial tick time. Args: partialTickTime
     */
	private void renderSky(float par1)
	{
		IRenderHandler skyProvider = null;
        if ((skyProvider = this.mc.theWorld.provider.getSkyRenderer()) != null)
        {
            skyProvider.render(par1, this.mc.theWorld, mc);
            return;
        }

		if (this.mc.theWorld.provider.isSurfaceWorld())
        {
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        //Vec3 vec3 = this.mc.theWorld.getSkyColor(this.mc.renderViewEntity, par1);
	        Vec3 vec3 = this.mc.theWorld.getSkyColor(this.theCamera, par1);
	        float f1 = (float)vec3.xCoord;
	        float f2 = (float)vec3.yCoord;
	        float f3 = (float)vec3.zCoord;
	        float f6;
	
	        /*if (this.mc.gameSettings.anaglyph)
	        {
	            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
	            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
	            f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
	            f1 = f4;
	            f2 = f5;
	            f3 = f6;
	        }*/
	
	        GL11.glColor3f(f1, f2, f3);
	        Tessellator tessellator1 = Tessellator.instance;
	        GL11.glDepthMask(false);
	        GL11.glEnable(GL11.GL_FOG);
	        GL11.glColor3f(f1, f2, f3);
	        GL11.glCallList(this.glSkyList);
	        GL11.glDisable(GL11.GL_FOG);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_BLEND);
	        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	        RenderHelper.disableStandardItemLighting();
	        float[] afloat = this.mc.theWorld.provider.calcSunriseSunsetColors(this.mc.theWorld.getCelestialAngle(par1), par1);
	        float f7;
	        float f8;
	        float f9;
	        float f10;
	
	        if (afloat != null)
	        {
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glShadeModel(GL11.GL_SMOOTH);
	            GL11.glPushMatrix();
	            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef(MathHelper.sin(this.mc.theWorld.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
	            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
	            f6 = afloat[0];
	            f7 = afloat[1];
	            f8 = afloat[2];
	            float f11;
	
	            if (this.mc.gameSettings.anaglyph)
	            {
	                f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
	                f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
	                f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
	                f6 = f9;
	                f7 = f10;
	                f8 = f11;
	            }
	
	            tessellator1.startDrawing(6);
	            tessellator1.setColorRGBA_F(f6, f7, f8, afloat[3]);
	            tessellator1.addVertex(0.0D, 100.0D, 0.0D);
	            byte b0 = 16;
	            tessellator1.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);
	
	            for (int j = 0; j <= b0; ++j)
	            {
	                f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
	                float f12 = MathHelper.sin(f11);
	                float f13 = MathHelper.cos(f11);
	                tessellator1.addVertex((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3]));
	            }
	
	            tessellator1.draw();
	            GL11.glPopMatrix();
	            GL11.glShadeModel(GL11.GL_FLAT);
	        }
	
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        OpenGlHelper.glBlendFunc(770, 1, 1, 0);
	        GL11.glPushMatrix();
	        f6 = 1.0F - this.mc.theWorld.getRainStrength(par1);
	        f7 = 0.0F;
	        f8 = 0.0F;
	        f9 = 0.0F;
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, f6);
	        GL11.glTranslatef(f7, f8, f9);
	        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(this.mc.theWorld.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);
	        f10 = 30.0F;
	        //this.renderEngine.bindTexture(locationSunPng);
	        this.mc.getTextureManager().bindTexture(locationSunPng);
	        tessellator1.startDrawingQuads();
		        tessellator1.addVertexWithUV((double)(-f10), 100.0D, (double)(-f10), 0.0D, 0.0D);
		        tessellator1.addVertexWithUV((double)f10, 100.0D, (double)(-f10), 1.0D, 0.0D);
		        tessellator1.addVertexWithUV((double)f10, 100.0D, (double)f10, 1.0D, 1.0D);
		        tessellator1.addVertexWithUV((double)(-f10), 100.0D, (double)f10, 0.0D, 1.0D);
	        tessellator1.draw();
	        f10 = 20.0F;
	        //this.renderEngine.bindTexture(locationMoonPhasesPng);
	        this.mc.getTextureManager().bindTexture(locationMoonPhasesPng);
	        int k = this.mc.theWorld.getMoonPhase();
	        int l = k % 4;
	        int i1 = k / 4 % 2;
	        float f14 = (float)(l + 0) / 4.0F;
	        float f15 = (float)(i1 + 0) / 2.0F;
	        float f16 = (float)(l + 1) / 4.0F;
	        float f17 = (float)(i1 + 1) / 2.0F;
	        tessellator1.startDrawingQuads();
		        tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)f10, (double)f16, (double)f17);
		        tessellator1.addVertexWithUV((double)f10, -100.0D, (double)f10, (double)f14, (double)f17);
		        tessellator1.addVertexWithUV((double)f10, -100.0D, (double)(-f10), (double)f14, (double)f15);
		        tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)(-f10), (double)f16, (double)f15);
	        tessellator1.draw();
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        float f18 = this.mc.theWorld.getStarBrightness(par1) * f6;
	
	        if (f18 > 0.0F)
	        {
	            GL11.glColor4f(f18, f18, f18, f18);
	            GL11.glCallList(this.starGLCallList);
	        }
	
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_FOG);
	        GL11.glPopMatrix();
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glColor3f(0.0F, 0.0F, 0.0F);
	        //double d0 = mc.renderViewEntity.getPosition(par1).yCoord - this.mc.theWorld.getHorizon();
	        double d0 = theCamera.getPosition(par1).yCoord - this.mc.theWorld.getHorizon();
	        
	        if (d0 < 0.0D)
	        {
	            GL11.glPushMatrix();
	            GL11.glTranslatef(0.0F, 12.0F, 0.0F);
	            GL11.glCallList(this.glSkyList2);
	            GL11.glPopMatrix();
	            f8 = 1.0F;
	            f9 = -((float)(d0 + 65.0D));
	            f10 = -f8;
	            tessellator1.startDrawingQuads();
		            tessellator1.setColorRGBA_I(0, 255);
		            tessellator1.addVertex((double)(-f8), (double)f9, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f9, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f10, (double)f8);
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
		            
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
		            tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
		            tessellator1.addVertex((double)f8, (double)f9, (double)(-f8));
		            tessellator1.addVertex((double)(-f8), (double)f9, (double)(-f8));
		            
		            tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
		            tessellator1.addVertex((double)f8, (double)f10, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f9, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f9, (double)(-f8));
		            
		            tessellator1.addVertex((double)(-f8), (double)f9, (double)(-f8));
		            tessellator1.addVertex((double)(-f8), (double)f9, (double)f8);
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
		            
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
		            tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f10, (double)f8);
		            tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
	            tessellator1.draw();
	        }
	
	        if (this.mc.theWorld.provider.isSkyColored())
	        {
	            GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
	        }
	        else
	        {
	            GL11.glColor3f(f1, f2, f3);
	        }
	
	        GL11.glPushMatrix();
	        GL11.glTranslatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
	        GL11.glCallList(this.glSkyList2);
	        GL11.glPopMatrix();
	        
	        /*GL11.glPushMatrix();
	        	GL11.glCallList(this.glSkyList3);
	        GL11.glPopMatrix();*/
	        
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glDepthMask(true);
        }
	}
	
	private void renderStars()
    {
        Random random = new Random(10842L);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        for (int i = 0; i < 1500; ++i)
        {
            double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D)
            {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j)
                {
                    double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = d18 * d16 - d19 * d15;
                    double d21 = d19 * d16 + d18 * d15;
                    double d22 = d20 * d12 + d17 * d13;
                    double d23 = d17 * d12 - d20 * d13;
                    double d24 = d23 * d9 - d21 * d10;
                    double d25 = d21 * d9 + d23 * d10;
                    tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
                }
            }
        }

        tessellator.draw();
    }
	
	public void RenderGlobal(/*Minecraft par1Minecraft*/)
    {
        //this.mc = par1Minecraft;
        //this.renderEngine = par1Minecraft.getTextureManager();
        byte b0 = 34;
        byte b1 = 16;
        //this.glRenderListBase = GLAllocation.generateDisplayLists(b0 * b0 * b1 * 3);
        //this.displayListEntitiesDirty = false;
        //this.displayListEntities = GLAllocation.generateDisplayLists(1);
        this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();
        

        //this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (this.occlusionEnabled)
        {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(b0 * b0 * b1);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(b0 * b0 * b1);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }/**/

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();/**/
        
        Tessellator tessellator = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        
        
        
        byte b2 = 64;
        int i = 256 / b2 + 2;
        float f = 16.0F;
        int j;
        int k;
        this.drawHorizon(tessellator, b2, f, i);
        
        for (j = -b2 * i; j <= b2 * i; j += b2)
        {
            for (k = -b2 * i; k <= b2 * i; k += b2)
            {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
                tessellator.draw();
            }
        }
        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        f = -16.0F;
        tessellator.startDrawingQuads();

        for (j = -b2 * i; j <= b2 * i; j += b2)
        {
            for (k = -b2 * i; k <= b2 * i; k += b2)
            {
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
            }
        }

        tessellator.draw();
        GL11.glEndList();
    }
	
	/**
     * calculates fog and calls glClearColor
     */
    private void updateFogColor(float par1)
    {
        WorldClient worldclient = this.mc.theWorld;
        //EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
        EntityLivingBase entitylivingbase = this.theCamera;
        float f1 = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 16.0F;
        f1 = 1.0F - (float)Math.pow((double)f1, 0.25D);
        //Vec3 vec3 = worldclient.getSkyColor(this.mc.renderViewEntity, par1);
        Vec3 vec3 = worldclient.getSkyColor(this.theCamera, par1);
        float f2 = (float)vec3.xCoord;
        float f3 = (float)vec3.yCoord;
        float f4 = (float)vec3.zCoord;
        Vec3 vec31 = worldclient.getFogColor(par1);
        this.fogColorRed = (float)vec31.xCoord;
        this.fogColorGreen = (float)vec31.yCoord;
        this.fogColorBlue = (float)vec31.zCoord;
        float f5;

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
        	Vec3 vec32 = MathHelper.sin(worldclient.getCelestialAngleRadians(par1)) > 0.0F ? Vec3.createVectorHelper(-1.0D, 0.0D, 0.0D) : Vec3.createVectorHelper(1.0D, 0.0D, 0.0D);
            f5 = (float)entitylivingbase.getLook(par1).dotProduct(vec32);

            if (f5 < 0.0F)
            {
                f5 = 0.0F;
            }

            if (f5 > 0.0F)
            {
                float[] afloat = worldclient.provider.calcSunriseSunsetColors(worldclient.getCelestialAngle(par1), par1);

                if (afloat != null)
                {
                    f5 *= afloat[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - f5) + afloat[0] * f5;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + afloat[1] * f5;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + afloat[2] * f5;
                }
            }
        }

        this.fogColorRed += (f2 - this.fogColorRed) * f1;
        this.fogColorGreen += (f3 - this.fogColorGreen) * f1;
        this.fogColorBlue += (f4 - this.fogColorBlue) * f1;
        float f8 = worldclient.getRainStrength(par1);
        float f9;

        if (f8 > 0.0F)
        {
            f5 = 1.0F - f8 * 0.5F;
            f9 = 1.0F - f8 * 0.4F;
            this.fogColorRed *= f5;
            this.fogColorGreen *= f5;
            this.fogColorBlue *= f9;
        }

        f5 = worldclient.getWeightedThunderStrength(par1);

        if (f5 > 0.0F)
        {
            f9 = 1.0F - f5 * 0.5F;
            this.fogColorRed *= f9;
            this.fogColorGreen *= f9;
            this.fogColorBlue *= f9;
        }

        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entitylivingbase, par1);
        float f10;

        if (this.cloudFog)
        {
            Vec3 vec33 = worldclient.getCloudColour(par1);
            this.fogColorRed = (float)vec33.xCoord;
            this.fogColorGreen = (float)vec33.yCoord;
            this.fogColorBlue = (float)vec33.zCoord;
        }
        else if (block.getMaterial() == Material.water)
        {
            f10 = (float)EnchantmentHelper.getRespiration(entitylivingbase) * 0.2F;
            this.fogColorRed = 0.02F + f10;
            this.fogColorGreen = 0.02F + f10;
            this.fogColorBlue = 0.2F + f10;
        }
        else if (block.getMaterial() == Material.lava)
        {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        }

        f10 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * par1;
        this.fogColorRed *= f10;
        this.fogColorGreen *= f10;
        this.fogColorBlue *= f10;
        double d0 = (entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * (double)par1) * worldclient.provider.getVoidFogYFactor();

        if (entitylivingbase.isPotionActive(Potion.blindness))
        {
            int i = entitylivingbase.getActivePotionEffect(Potion.blindness).getDuration();

            if (i < 20)
            {
                d0 *= (double)(1.0F - (float)i / 20.0F);
            }
            else
            {
                d0 = 0.0D;
            }
        }

        if (d0 < 1.0D)
        {
            if (d0 < 0.0D)
            {
                d0 = 0.0D;
            }

            d0 *= d0;
            this.fogColorRed = (float)((double)this.fogColorRed * d0);
            this.fogColorGreen = (float)((double)this.fogColorGreen * d0);
            this.fogColorBlue = (float)((double)this.fogColorBlue * d0);
        }

        float f11;

        /*if (this.bossColorModifier > 0.0F)
        {
            f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * par1;
            this.fogColorRed = this.fogColorRed * (1.0F - f11) + this.fogColorRed * 0.7F * f11;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f11) + this.fogColorGreen * 0.6F * f11;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f11) + this.fogColorBlue * 0.6F * f11;
        }*/

        float f6;

        if (entitylivingbase.isPotionActive(Potion.nightVision))
        {
            f11 = this.getNightVisionBrightness(this.mc.thePlayer, par1);
            f6 = 1.0F / this.fogColorRed;

            if (f6 > 1.0F / this.fogColorGreen)
            {
                f6 = 1.0F / this.fogColorGreen;
            }

            if (f6 > 1.0F / this.fogColorBlue)
            {
                f6 = 1.0F / this.fogColorBlue;
            }

            this.fogColorRed = this.fogColorRed * (1.0F - f11) + this.fogColorRed * f6 * f11;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f11) + this.fogColorGreen * f6 * f11;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f11) + this.fogColorBlue * f6 * f11;
        }

        if (this.mc.gameSettings.anaglyph)
        {
            f11 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            f6 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
            this.fogColorRed = f11;
            this.fogColorGreen = f6;
            this.fogColorBlue = f7;
        }

        GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }
	
	/**
     * Sets up the fog to be rendered. If the arg passed in is -1 the fog starts at 0 and goes to 80% of far plane
     * distance and is used for sky rendering.
     */
    private void setupFog(int par1, float par2)
    {
        //EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
        EntityLivingBase entitylivingbase = this.theCamera;
        boolean flag = false;

        if (entitylivingbase instanceof EntityPlayer)
        {
            flag = ((EntityPlayer)entitylivingbase).capabilities.isCreativeMode;
        }

        if (par1 == 999)
        {
            GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            GL11.glFogf(GL11.GL_FOG_START, 0.0F);
            GL11.glFogf(GL11.GL_FOG_END, 8.0F);

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }

            GL11.glFogf(GL11.GL_FOG_START, 0.0F);
        }
        else
        {
            GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
            GL11.glNormal3f(0.0F, -1.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entitylivingbase, par2);
            float f1;

            if (entitylivingbase.isPotionActive(Potion.blindness))
            {
                f1 = 5.0F;
                int j = entitylivingbase.getActivePotionEffect(Potion.blindness).getDuration();

                if (j < 20)
                {
                    f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)j / 20.0F);
                }

                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);

                if (par1 < 0)
                {
                    GL11.glFogf(GL11.GL_FOG_START, 0.0F);
                    GL11.glFogf(GL11.GL_FOG_END, f1 * 0.8F);
                }
                else
                {
                    GL11.glFogf(GL11.GL_FOG_START, f1 * 0.25F);
                    GL11.glFogf(GL11.GL_FOG_END, f1);
                }

                if (GLContext.getCapabilities().GL_NV_fog_distance)
                {
                    GL11.glFogi(34138, 34139);
                }
            }
            else if (this.cloudFog)
            {
                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
                GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
            }
            else if (block.getMaterial() == Material.water)
            {
                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);

                if (entitylivingbase.isPotionActive(Potion.waterBreathing))
                {
                    GL11.glFogf(GL11.GL_FOG_DENSITY, 0.05F);
                }
                else
                {
                    GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F - (float)EnchantmentHelper.getRespiration(entitylivingbase) * 0.03F);
                }
            }
            else if (block.getMaterial() == Material.lava)
            {
                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
                GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
            }
            else
            {
                f1 = this.farPlaneDistance;

                if (this.mc.theWorld.provider.getWorldHasVoidParticles() && !flag)
                {
                    double d0 = (double)((entitylivingbase.getBrightnessForRender(par2) & 15728640) >> 20) / 16.0D + (entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * (double)par2 + 4.0D) / 32.0D;

                    if (d0 < 1.0D)
                    {
                        if (d0 < 0.0D)
                        {
                            d0 = 0.0D;
                        }

                        d0 *= d0;
                        float f2 = 100.0F * (float)d0;

                        if (f2 < 5.0F)
                        {
                            f2 = 5.0F;
                        }

                        if (f1 > f2)
                        {
                            f1 = f2;
                        }
                    }
                }

                GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);

                if (par1 < 0)
                {
                    GL11.glFogf(GL11.GL_FOG_START, 0.0F);
                    GL11.glFogf(GL11.GL_FOG_END, f1);
                }
                else
                {
                    GL11.glFogf(GL11.GL_FOG_START, f1 * 0.75F);
                    GL11.glFogf(GL11.GL_FOG_END, f1);
                }

                if (GLContext.getCapabilities().GL_NV_fog_distance)
                {
                    GL11.glFogi(34138, 34139);
                }

                if (this.mc.theWorld.provider.doesXZShowFog((int)entitylivingbase.posX, (int)entitylivingbase.posZ))
                {
                    GL11.glFogf(GL11.GL_FOG_START, f1 * 0.05F);
                    GL11.glFogf(GL11.GL_FOG_END, Math.min(f1, 192.0F) * 0.5F);
                }
            }

            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
        }
    }
    

    /**
     * Update and return fogColorBuffer with the RGBA values passed as arguments
     */
    private FloatBuffer setFogColorBuffer(float par1, float par2, float par3, float par4)
    {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(par1).put(par2).put(par3).put(par4);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }
    
    /**
     * Render clouds if enabled
     */
    private void renderCloudsCheck(RenderGlobal par1RenderGlobal, float par2)
    {
        if (this.mc.gameSettings.shouldRenderClouds())
        {
            //this.mc.mcProfiler.endStartSection("clouds");
            GL11.glPushMatrix();
	            this.setupFog(0, par2);
	            GL11.glEnable(GL11.GL_FOG);
	            par1RenderGlobal.renderClouds(par2);
	            GL11.glDisable(GL11.GL_FOG);
	            this.setupFog(1, par2);
            GL11.glPopMatrix();
        }
    }
    
    /**
     * Updates the entity renderer
     */
    public void updateRenderer()
    {
        /*if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null)
        {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }

        this.updateFovModifierHand();
        this.updateTorchFlicker();*/
        this.fogColor2 = this.fogColor1;
        /*this.thirdPersonDistanceTemp = this.thirdPersonDistance;
        this.prevDebugCamYaw = this.debugCamYaw;
        this.prevDebugCamPitch = this.debugCamPitch;
        this.prevDebugCamFOV = this.debugCamFOV;
        this.prevCamRoll = this.camRoll;*/
        float f;
        float f1;

        /*if (this.mc.gameSettings.smoothCamera)
        {
            f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            f1 = f * f * f * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        }

        if (this.mc.renderViewEntity == null)
        {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }*/
        
        //f = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
        f = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.theCamera.posX), MathHelper.floor_double(this.theCamera.posY), MathHelper.floor_double(this.theCamera.posZ));
        f1 = (float)(this.mc.gameSettings.renderDistanceChunks / 16);
        float f2 = f * (1.0F - f1) + f1;
        this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
        //++this.rendererUpdateCount;
        //this.itemRenderer.updateEquippedItem();
        //this.addRainParticles();
        //this.bossColorModifierPrev = this.bossColorModifier;

        /*if (BossStatus.hasColorModifier)
        {
            this.bossColorModifier += 0.05F;

            if (this.bossColorModifier > 1.0F)
            {
                this.bossColorModifier = 1.0F;
            }

            BossStatus.hasColorModifier = false;
        }
        else if (this.bossColorModifier > 0.0F)
        {
            this.bossColorModifier -= 0.0125F;
        }*/
    }

    /**
     * Gets the night vision brightness
     */
    private float getNightVisionBrightness(EntityPlayer par1EntityPlayer, float par2)
    {
        int i = par1EntityPlayer.getActivePotionEffect(Potion.nightVision).getDuration();
        return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - par2) * (float)Math.PI * 0.2F) * 0.3F;
    }
    
    private void drawHorizon(Tessellator tessellator, byte b2, float f, int i)
    {
    	
		 tessellator.startDrawingQuads(); // orizzonte
		 tessellator.addVertex((double)(-b2*i), (double)-f, (double)2); // da mettere a posto
		 tessellator.addVertex((double)(b2*i), (double)-f, (double)2);
		 tessellator.addVertex((double)(b2*i), (double)f, (double)2);
		 tessellator.addVertex((double)(-b2*i), (double)f, (double)2);
		 tessellator.draw();
		 
		 tessellator.startDrawingQuads(); // orizzonte
		 tessellator.addVertex((double)(-b2*i), (double)f, (double)-2);
		 tessellator.addVertex((double)(b2*i), (double)f, (double)-2);
		 tessellator.addVertex((double)(b2*i), (double)-f, (double)-2);
		 tessellator.addVertex((double)(-b2*i), (double)-f, (double)-2);
		 tessellator.draw();
		 
		 tessellator.startDrawingQuads(); // orizzonte
		 tessellator.addVertex((double)2, (double)-f, (double)(-b2*i));
		 tessellator.addVertex((double)2, (double)-f, (double)(b2*i));
		 tessellator.addVertex((double)2, (double)f, (double)(b2*i));
		 tessellator.addVertex((double)2, (double)f, (double)(-b2*i));
		 tessellator.draw();
		 
		 tessellator.startDrawingQuads(); // orizzonte
		 tessellator.addVertex((double)-2, (double)f, (double)(-b2*i));
		 tessellator.addVertex((double)-2, (double)f, (double)(b2*i));
		 tessellator.addVertex((double)-2, (double)-f, (double)(b2*i));
		 tessellator.addVertex((double)-2, (double)-f, (double)(-b2*i)); 
		 tessellator.draw();
    }
    
    /**
     * sets up projection, view effects, camera position/rotation
     */
    private void setupCameraTransform(float p_78479_1_, int p_78479_2_)
    {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float f1 = 0.07F;

        if (this.mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(-(p_78479_2_ * 2 - 1)) * f1, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D)
        {
            GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        Project.gluPerspective(this.getFOVModifier(p_78479_1_, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        float f2;

        if (this.mc.playerController.enableEverythingIsScrewedUpMode())
        {
            f2 = 0.6666667F;
            GL11.glScalef(1.0F, f2, 1.0F);
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        if (this.mc.gameSettings.anaglyph)
        {
            GL11.glTranslatef((float)(p_78479_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(p_78479_1_);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.setupViewBobbing(p_78479_1_);
        }

        f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * p_78479_1_;

        if (f2 > 0.0F)
        {
            byte b0 = 20;

            if (this.mc.thePlayer.isPotionActive(Potion.confusion))
            {
                b0 = 7;
            }

            float f3 = 5.0F / (f2 * f2 + 5.0F) - f2 * 0.04F;
            f3 *= f3;
            GL11.glRotatef(((float)this.rendererUpdateCount + p_78479_1_) * (float)b0, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / f3, 1.0F, 1.0F);
            GL11.glRotatef(-((float)this.rendererUpdateCount + p_78479_1_) * (float)b0, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(p_78479_1_);

        if (this.debugViewDirection > 0)
        {
            int j = this.debugViewDirection - 1;

            if (j == 1)
            {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 2)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 3)
            {
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 4)
            {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (j == 5)
            {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }
    /**
     * Changes the field of view of the player depending on if they are underwater or not
     */
    private float getFOVModifier(float p_78481_1_, boolean p_78481_2_)
    {
        if (this.debugViewDirection > 0)
        {
            return 90.0F;
        }
        else
        {
            EntityLivingBase entityplayer = (EntityLivingBase)this.mc.renderViewEntity;
            float f1 = 70.0F;

            if (p_78481_2_)
            {
                f1 = this.mc.gameSettings.fovSetting;
                f1 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * p_78481_1_;
            }

            if (entityplayer.getHealth() <= 0.0F)
            {
                float f2 = (float)entityplayer.deathTime + p_78481_1_;
                f1 /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
            }

            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entityplayer, p_78481_1_);

            if (block.getMaterial() == Material.water)
            {
                f1 = f1 * 60.0F / 70.0F;
            }

            return f1 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * p_78481_1_;
        }
    }

    private void hurtCameraEffect(float p_78482_1_)
    {
        EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
        float f1 = (float)entitylivingbase.hurtTime - p_78482_1_;
        float f2;

        if (entitylivingbase.getHealth() <= 0.0F)
        {
            f2 = (float)entitylivingbase.deathTime + p_78482_1_;
            GL11.glRotatef(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
        }

        if (f1 >= 0.0F)
        {
            f1 /= (float)entitylivingbase.maxHurtTime;
            f1 = MathHelper.sin(f1 * f1 * f1 * f1 * (float)Math.PI);
            f2 = entitylivingbase.attackedAtYaw;
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f1 * 14.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        }
    }

    /**
     * Setups all the GL settings for view bobbing. Args: partialTickTime
     */
    private void setupViewBobbing(float p_78475_1_)
    {
        if (this.mc.renderViewEntity instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)this.mc.renderViewEntity;
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * p_78475_1_);
            float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78475_1_;
            float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78475_1_;
            GL11.glTranslatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float)Math.PI) * f3), 0.0F);
            GL11.glRotatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(f2 * (float)Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
        }
    }

    /**
     * sets up player's eye (or camera in third person mode)
     */
    private void orientCamera(float p_78467_1_)
    {
        //EntityLivingBase entitylivingbase = this.mc.renderViewEntity;

        EntityLivingBase entitylivingbase = this.theCamera;
        float f1 = entitylivingbase.yOffset - 1.62F;
        double d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * (double)p_78467_1_;
        double d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * (double)p_78467_1_ - (double)f1;
        double d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * (double)p_78467_1_;
        GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * p_78467_1_, 0.0F, 0.0F, 1.0F);

        if (entitylivingbase.isPlayerSleeping())
        {
            f1 = (float)((double)f1 + 1.0D);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);

            if (!this.mc.gameSettings.debugCamEnable)
            {
                ForgeHooksClient.orientBedCamera(mc, entitylivingbase);
                GL11.glRotatef(entitylivingbase.prevRotationYaw + (entitylivingbase.rotationYaw - entitylivingbase.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(entitylivingbase.prevRotationPitch + (entitylivingbase.rotationPitch - entitylivingbase.prevRotationPitch) * p_78467_1_, -1.0F, 0.0F, 0.0F);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0)
        {
            double d7 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * p_78467_1_);
            float f2;
            float f6;

            if (this.mc.gameSettings.debugCamEnable)
            {
                f6 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * p_78467_1_;
                f2 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * p_78467_1_;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d7));
                GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f6, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                f6 = entitylivingbase.rotationYaw;
                f2 = entitylivingbase.rotationPitch;

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    f2 += 180.0F;
                }

                double d3 = (double)(-MathHelper.sin(f6 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d7;
                double d4 = (double)(MathHelper.cos(f6 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d7;
                double d5 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * d7;

                for (int k = 0; k < 8; ++k)
                {
                    float f3 = (float)((k & 1) * 2 - 1);
                    float f4 = (float)((k >> 1 & 1) * 2 - 1);
                    float f5 = (float)((k >> 2 & 1) * 2 - 1);
                    f3 *= 0.1F;
                    f4 *= 0.1F;
                    f5 *= 0.1F;
                    MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), Vec3.createVectorHelper(d0 - d3 + (double)f3 + (double)f5, d1 - d5 + (double)f4, d2 - d4 + (double)f5));

                    if (movingobjectposition != null)
                    {
                        double d6 = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(d0, d1, d2));

                        if (d6 < d7)
                        {
                            d7 = d6;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(entitylivingbase.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(entitylivingbase.rotationYaw - f6, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d7));
                GL11.glRotatef(f6 - entitylivingbase.rotationYaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f2 - entitylivingbase.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.debugCamEnable)
        {
            GL11.glRotatef(entitylivingbase.prevRotationPitch + (entitylivingbase.rotationPitch - entitylivingbase.prevRotationPitch) * p_78467_1_, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(entitylivingbase.prevRotationYaw + (entitylivingbase.rotationYaw - entitylivingbase.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, f1, 0.0F);
        d0 = entitylivingbase.prevPosX + (entitylivingbase.posX - entitylivingbase.prevPosX) * (double)p_78467_1_;
        d1 = entitylivingbase.prevPosY + (entitylivingbase.posY - entitylivingbase.prevPosY) * (double)p_78467_1_ - (double)f1;
        d2 = entitylivingbase.prevPosZ + (entitylivingbase.posZ - entitylivingbase.prevPosZ) * (double)p_78467_1_;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, p_78467_1_);
    }

    private void copiaCamera()
    {
    	this.theCamera.posX = this.mc.renderViewEntity.posX;
        this.theCamera.posY = this.mc.renderViewEntity.posY;
        this.theCamera.posZ = this.mc.renderViewEntity.posZ;

    	this.theCamera.prevPosX = this.mc.renderViewEntity.prevPosX;
        this.theCamera.prevPosY = this.mc.renderViewEntity.prevPosY;
        this.theCamera.prevPosZ = this.mc.renderViewEntity.prevPosZ;
        
        this.theCamera.lastTickPosX = this.mc.renderViewEntity.lastTickPosX;
        this.theCamera.lastTickPosY = this.mc.renderViewEntity.lastTickPosY;
        this.theCamera.lastTickPosZ = this.mc.renderViewEntity.lastTickPosZ;
        
    	this.theCamera.motionX = this.mc.renderViewEntity.motionX;
        this.theCamera.motionY = this.mc.renderViewEntity.motionY;
        this.theCamera.motionZ = this.mc.renderViewEntity.motionZ;

    	this.theCamera.chunkCoordX = this.mc.renderViewEntity.chunkCoordX;
        this.theCamera.chunkCoordY = this.mc.renderViewEntity.chunkCoordY;
        this.theCamera.chunkCoordZ = this.mc.renderViewEntity.chunkCoordZ;
        
        this.theCamera.boundingBox.setBB(this.mc.renderViewEntity.boundingBox.copy());
        
        this.theCamera.yOffset = this.mc.renderViewEntity.yOffset;
        this.theCamera.ySize = this.mc.renderViewEntity.ySize;
        this.theCamera.worldObj = this.mc.renderViewEntity.worldObj;
        
        this.theCamera.rotationPitch = this.mc.renderViewEntity.rotationPitch;
        this.theCamera.rotationYaw = this.mc.renderViewEntity.rotationYaw;
        this.theCamera.rotationYawHead = this.mc.renderViewEntity.rotationYawHead;
        
        this.theCamera.cameraPitch = this.mc.renderViewEntity.cameraPitch;
        this.theCamera.prevCameraPitch = this.mc.renderViewEntity.prevCameraPitch;

        this.theCamera.prevRotationPitch = this.mc.renderViewEntity.prevRotationPitch;
        this.theCamera.prevRotationYaw = this.mc.renderViewEntity.prevRotationYaw;
        this.theCamera.prevRotationYawHead = this.mc.renderViewEntity.prevRotationYawHead;
        
        this.theCamera.chunkCoordX = this.mc.renderViewEntity.chunkCoordX;
        this.theCamera.chunkCoordY = this.mc.renderViewEntity.chunkCoordY;
        this.theCamera.chunkCoordZ = this.mc.renderViewEntity.chunkCoordZ;
        
        this.theCamera.serverPosX = this.mc.renderViewEntity.serverPosX;
        this.theCamera.serverPosY = this.mc.renderViewEntity.serverPosY;
        this.theCamera.serverPosZ = this.mc.renderViewEntity.serverPosZ;
        
    }
}
