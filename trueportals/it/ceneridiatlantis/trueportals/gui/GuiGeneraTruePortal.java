package it.ceneridiatlantis.trueportals.gui;

import it.ceneridiatlantis.trueportals.TruePortals;
import it.ceneridiatlantis.trueportals.messenger;
import it.ceneridiatlantis.trueportals.container.ContainerGeneraTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiGeneraTruePortal extends GuiContainer
{
	
	public final ResourceLocation texture = new ResourceLocation(TruePortals.MODID,"textures/gui/generapuzzonio.png");
	
	private TileEntityGeneraTruePortal tileEntityGeneraTruePortal;
	
	private GuiTextField textfieldTarget;
	
	@Override
    public void initGui() 
	{
        super.initGui();
        //make buttons
                                //id, x, y, width, height, text
        this.buttonList.add(new GuiButton(1, guiLeft + 150, guiTop + 30, 20, 20, "+"));
        this.buttonList.add(new GuiButton(2, guiLeft + 150, guiTop + 50, 20, 20, "set"));

		FontRenderer fontRenderer = this.mc.fontRenderer;

        textfieldTarget = new GuiTextField(fontRenderer, guiLeft + 10, guiTop + 60, 150, 15);
        textfieldTarget.setFocused(false);
        textfieldTarget.setMaxStringLength(20);
    }

	public void keyTyped(char c, int i)
	{
		if (textfieldTarget.isFocused())
		{
			textfieldTarget.textboxKeyTyped(c, i);
			if (i == 1)
	        {
				textfieldTarget.setFocused(false);
	        }
		}
		else
		{
			super.keyTyped(c, i);
		}
		
	}
	
	public void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		textfieldTarget.mouseClicked(i, j, k);
	}
	
    protected void actionPerformed(GuiButton guibutton) {
        //id is the id you give your button
        switch(guibutton.id) 
        {
	        case 1:
	        	sendNameToServer();
	        	//TruePortals.network.sendToServer(new messenger("+,," + this.tileEntityGeneraPuzzonio.name + ",," + this.tileEntityGeneraPuzzonio.target));
	                //i += 1;
	        	//this.GeneraPuzzonio.target = this.tileEntityGeneraPuzzonio.name;
	            break;
	        case 2:
	        	this.tileEntityGeneraTruePortal.setTarget(textfieldTarget.getText());
	        	sendTargetToServer();
	        	
	        	//TruePortals.network.sendToServer(new messenger("foobar"));
        	//this.tileEntityGeneraPuzzonio.target = "";
                //i -= 1;
        }
    }

	public GuiGeneraTruePortal(InventoryPlayer inventoryPlayer, TileEntityGeneraTruePortal tileEntityGeneraTruePortal) 
	{
		super(new ContainerGeneraTruePortal(inventoryPlayer, tileEntityGeneraTruePortal));
		
		this.tileEntityGeneraTruePortal = tileEntityGeneraTruePortal;
		
		this.xSize = 176;
		this.ySize = 166;
		
	}
	
	public void drawGuiContainerForegroundLayer(int var1, int var2) 
	{
		FontRenderer fontRenderer = this.mc.fontRenderer;
		fontRenderer.drawString("Inventory", /*guiLeft +*/ 8,/* guiTop +*/ ySize - 96 + 2, 4210752);

		fontRenderer.drawString("Name:", /*guiLeft +*/ 50,/*guiTop +*/ 8, 4210752);
		fontRenderer.drawString(this.tileEntityGeneraTruePortal.name, /*guiLeft +*/ 50, /*guiTop +*/ 18, 4210752);
		
		fontRenderer.drawString("Target:",/* guiLeft*/ + 50, /*guiTop*/ + 38, 4210752);
		fontRenderer.drawString(this.tileEntityGeneraTruePortal.target, /*guiLeft +*/ 50, /*guiTop +*/ 48, 4210752);
	}
	
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) 
	{
		FontRenderer fontRenderer = this.mc.fontRenderer;
		
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		

		textfieldTarget.drawTextBox();
		
		//this.drawString(par1FontRenderer, "1", par3, par4, par5);
		
	}
	
	private void sendNameToServer()
	{
		String string;
		this.tileEntityGeneraTruePortal.setName("coord," + tileEntityGeneraTruePortal.xCoord + "," + tileEntityGeneraTruePortal.yCoord + "," + tileEntityGeneraTruePortal.zCoord); 
    	
		string = "name,," + tileEntityGeneraTruePortal.xCoord + ",," + tileEntityGeneraTruePortal.yCoord + ",,"+ tileEntityGeneraTruePortal.zCoord + ",," + tileEntityGeneraTruePortal.getName();
	    
	    TruePortals.network.sendToServer(new messenger(string));
	}
	
	
	private void sendTargetToServer() 
	{
		String string;
		
		string = "target,," + tileEntityGeneraTruePortal.xCoord + ",," + tileEntityGeneraTruePortal.yCoord + ",,"+ tileEntityGeneraTruePortal.zCoord + ",," + tileEntityGeneraTruePortal.getTarget();
	    
	    TruePortals.network.sendToServer(new messenger(string));
	}
}