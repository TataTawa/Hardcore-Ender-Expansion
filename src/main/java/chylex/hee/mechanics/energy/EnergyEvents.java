package chylex.hee.mechanics.energy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EnergyEvents{
	public static void register(){
		FMLCommonHandler.instance().bus().register(new EnergyEvents());
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e){
		if (e.phase != Phase.START || e.world.provider.dimensionId != 1 || e.world.isRemote || ++updateTimer < 4)return;
		
		Stopwatch.timeAverage("EnergyEvents - WorldTick",20);
		
		List<Chunk> chunks = ((ChunkProviderServer)e.world.getChunkProvider()).loadedChunks;
		Set<EnergyChunkData> usedData = new HashSet<>();
		EnergySavefile file = WorldDataHandler.get(EnergySavefile.class);
		
		for(Chunk chunk:chunks){
			EnergyChunkData data = file.getFromChunkCoords(chunk.xPosition,chunk.zPosition);
			if (usedData.contains(data))continue;
			
			data.onUpdate(e.world.rand);
			
			for(int a = 0; a < 4; a++){
				data.onAdjacentInteract(e.world.rand,file.getFromChunkCoords(chunk.xPosition+Direction.offsetX[a]*EnergySavefile.sectionSize,chunk.zPosition+Direction.offsetZ[a]*EnergySavefile.sectionSize));
			}
			
			usedData.add(data);
		}

		updateTimer = 0;
		Stopwatch.finish("EnergyEvents - WorldTick");
	}
	
	private byte updateTimer;
	
	private EnergyEvents(){}
}