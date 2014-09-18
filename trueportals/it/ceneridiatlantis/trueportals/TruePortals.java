package it.ceneridiatlantis.trueportals;

import it.ceneridiatlantis.trueportals.block.BlockGeneraTruePortal;
import it.ceneridiatlantis.trueportals.block.BlockTruePortal;
import it.ceneridiatlantis.trueportals.event.TruePortalsRenderEvent;
import it.ceneridiatlantis.trueportals.gui.TruePortalsGuiHandler;
import it.ceneridiatlantis.trueportals.proxy.CommonProxy;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityGeneraTruePortal;
import it.ceneridiatlantis.trueportals.tileentity.TileEntityTruePortal;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = TruePortals.MODID, version = TruePortals.VERSION)
public class TruePortals {
	
    public static final String MODID = "trueportals";
    public static final String VERSION = "1.0.0.0.0";
    
    public static SimpleNetworkWrapper network;
    
    public static TruePortals instance;
    
    public static Block blockTruePortal;
    public static Block blockGeneraTruePortal;
    public static Item itemPuzzetto;
    
    public static List listGeneraTruePortals;
   
    
    
    public static final int guiIDGeneraPuzzonio = 0;
    
    @SidedProxy(clientSide="it.ceneridiatlantis.trueportals.proxy.ClientProxy", serverSide="it.ceneridiatlantis.trueportals.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    public static CreativeTabs trueportalsTab = new CreativeTabs("trueportalsTab")
    {
    	public Item getTabIconItem(){
    		return Items.emerald;
    	}
    };
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	instance = this;
    	
    	itemPuzzetto = new Item().setUnlocalizedName("puzzetto").setCreativeTab(trueportalsTab).setTextureName(MODID + ":" + "puzzetto");
    	GameRegistry.registerItem(itemPuzzetto, "Puzzetto");
    	
    	
    	blockTruePortal = new BlockTruePortal().setBlockName("truePortal").setCreativeTab(trueportalsTab).setBlockTextureName(MODID + ":" + "puzzonio");
    	blockGeneraTruePortal = new BlockGeneraTruePortal().setBlockName("generaTruePortal").setCreativeTab(trueportalsTab).setBlockTextureName(MODID + ":" + "generaPuzzonio");
    	
    	GameRegistry.registerBlock(blockTruePortal, "blockTruePortal");
    	GameRegistry.registerBlock(blockGeneraTruePortal, "blockGeneraTruePortal");
    	
    	network = NetworkRegistry.INSTANCE.newSimpleChannel("TruePortalsChannel");
        network.registerMessage(messenger.Handler.class, messenger.class, 0, Side.SERVER);
        //network.registerMessage(messenger.Handler.class, messenger.class, 1, Side.CLIENT);
        
        GL11.glEnable(GL11.GL_STENCIL_TEST);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	GameRegistry.registerTileEntity(TileEntityTruePortal.class, "tileEntityTruePortal");
    	GameRegistry.registerTileEntity(TileEntityGeneraTruePortal.class, "tileEntityGeneraTruePortal");
    	
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new TruePortalsGuiHandler());
        
        MinecraftForge.EVENT_BUS.register(new TruePortalsRenderEvent(Minecraft.getMinecraft()));
        
        proxy.registerProxies();
    }
}
