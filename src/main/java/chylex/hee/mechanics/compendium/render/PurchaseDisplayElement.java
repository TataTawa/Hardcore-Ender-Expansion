package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData.FragmentPurchaseStatus;

public class PurchaseDisplayElement{
	public final Object object;
	public final int price;
	public final FragmentPurchaseStatus status;
	private final int y;
	
	public PurchaseDisplayElement(KnowledgeFragment fragment, int y, FragmentPurchaseStatus status){
		this.object = fragment;
		this.price = fragment.getPrice();
		this.y = y;
		this.status = status;
	}
	
	public PurchaseDisplayElement(KnowledgeObject<?> object, int y, FragmentPurchaseStatus status){
		this.object = object;
		this.price = object.getUnlockPrice();
		this.y = y;
		this.status = status;
	}
	
	public void render(GuiScreen gui, int pageCenterX){
		pageCenterX += 3;
		
		GL11.glColor4f(1F,1F,1F,0.96F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texPage);
		gui.drawTexturedModalRect(pageCenterX-27,y-14,155,0,54,26);
		
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),GuiEnderCompendium.knowledgeFragmentIS,pageCenterX-22,y-10);
		RenderHelper.disableStandardItemLighting();
		
		String price = status == FragmentPurchaseStatus.NOT_BUYABLE ? "~~~" : String.valueOf(this.price);
		int color = status == FragmentPurchaseStatus.CAN_PURCHASE ? 0x404040 : status == FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED ? 0x888888 : status == FragmentPurchaseStatus.NOT_ENOUGH_POINTS ? 0xdd2020 : 0;
		gui.mc.fontRenderer.drawString(price,pageCenterX-gui.mc.fontRenderer.getStringWidth(price)+20,y-5,color);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int pageCenterX){
		return mouseX >= (pageCenterX+3)-27 && mouseY >= y-14 && mouseX <= (pageCenterX+3)+27 && mouseY <= y+12 && status == FragmentPurchaseStatus.CAN_PURCHASE;
	}
}
