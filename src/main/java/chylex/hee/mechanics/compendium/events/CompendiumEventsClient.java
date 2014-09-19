package chylex.hee.mechanics.compendium.events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S03OpenCompendium;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CompendiumEventsClient{
	private static CompendiumEventsClient instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CompendiumEventsClient());
	}
	
	public static void loadClientData(PlayerCompendiumData data){
		instance.data = data;
		
		if (Minecraft.getMinecraft().currentScreen instanceof GuiEnderCompendium){
			((GuiEnderCompendium)Minecraft.getMinecraft().currentScreen).updateCompendiumData(data);
		}
	}
	
	public static int getCompendiumKeyCode(){
		return instance.keyOpenCompendium.getKeyCode();
	}
	
	private final KeyBinding keyOpenCompendium;
	private PlayerCompendiumData data;
	
	private CompendiumEventsClient(){
		keyOpenCompendium = new KeyBinding("key.openCompendium",25,"Hardcore Ender Expansion");
		ClientRegistry.registerKeyBinding(keyOpenCompendium);
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent e){
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.inGameHasFocus && keyOpenCompendium.isPressed()){
			if (data != null){
				GuiEnderCompendium compendium = new GuiEnderCompendium(data);
				mc.displayGuiScreen(compendium);
				
				if (mc.thePlayer.isSneaking()){
					KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = CompendiumEvents.getObservation(mc.thePlayer).getObject();
					if (obj != null)compendium.showObject(obj);
				}
				
				if (!mc.thePlayer.getStatFileWriter().hasAchievementUnlocked(AchievementManager.THE_MORE_YOU_KNOW))PacketPipeline.sendToServer(new S03OpenCompendium());
			}
			else mc.thePlayer.addChatMessage(new ChatComponentText("Error opening Ender Compendium, server did not provide required data. Relog, wait a few seconds, pray to your favourite deity and try again!"));
		}
	}
}
