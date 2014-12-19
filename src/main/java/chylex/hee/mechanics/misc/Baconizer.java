package chylex.hee.mechanics.misc;
import java.util.regex.Pattern;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import chylex.hee.proxy.ModCommonProxy;

public final class Baconizer{
	private static final Pattern lcword = Pattern.compile("\\b([a-z]+?)\\b",Pattern.MULTILINE),
								 fcword = Pattern.compile("\\b([A-Z][a-z]+?)\\b",Pattern.MULTILINE),
								 ucword = Pattern.compile("\\b([A-Z]+?)\\b",Pattern.MULTILINE);
	
	public static void load(){
		if (!ModCommonProxy.hardcoreEnderbacon)return;
		
		Blocks.end_stone.setBlockName("baconStone");
	}
	
	public static String unlocalizedName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name+".bacon" : name;
	}
	
	public static String textureName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name+"_bacon" : name;
	}
	
	public static String mobName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name.substring(0,name.length()-4)+"bacon.name" : name;
	}
	
	public static String sentence(String text){
		if (!ModCommonProxy.hardcoreEnderbacon)return text;
		
		String lc = StatCollector.translateToLocal("baconizer.bacon"), fc = Character.toUpperCase(lc.charAt(0))+lc.substring(1), uc = lc.toUpperCase();
		
		text = lcword.matcher(text).replaceAll(lc);
		text = fcword.matcher(text).replaceAll(fc);
		text = ucword.matcher(text).replaceAll(uc);
		return text;
	}
	
	private Baconizer(){}
}
