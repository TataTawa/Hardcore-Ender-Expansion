package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C10ParticleEnergyTransfer extends AbstractClientPacket{
	private double startX,startY,startZ;
	private double targetX,targetY,targetZ;
	private byte red,green,blue;
	
	public C10ParticleEnergyTransfer(){}
	
	public C10ParticleEnergyTransfer(TileEntity tile, TileEntityEnergyCluster cluster){
		this(tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,cluster.xCoord+0.5D,cluster.yCoord+0.5D,cluster.zCoord+0.5D,cluster.getColorRaw(0),cluster.getColorRaw(1),cluster.getColorRaw(2));
	}
	
	public C10ParticleEnergyTransfer(TileEntity tile, double targetX, double targetY, double targetZ, byte red, byte green, byte blue){
		this(tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,targetX,targetY,targetZ,red,green,blue);
	}
	
	public C10ParticleEnergyTransfer(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, byte red, byte green, byte blue){
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(startX).writeDouble(startY).writeDouble(startZ);
		buffer.writeDouble(targetX).writeDouble(targetY).writeDouble(targetZ);
		buffer.writeByte(red).writeByte(green).writeByte(blue);
	}

	@Override
	public void read(ByteBuf buffer){
		startX = buffer.readDouble();
		startY = buffer.readDouble();
		startZ = buffer.readDouble();
		targetX = buffer.readDouble();
		targetY = buffer.readDouble();
		targetZ = buffer.readDouble();
		red = buffer.readByte();
		green = buffer.readByte();
		blue = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Vec3 vec = Vec3.createVectorHelper(targetX-startX,targetY-startY,targetZ-startZ);
		int steps = (int)Math.floor(vec.lengthVector()*5D);
		vec = vec.normalize();
		
		for(int a = 0; a < steps; a++){
			for(int b = 0; b < 3; b++)HardcoreEnderExpansion.fx.energyClusterMoving(player.worldObj,startX+rand(0.05D),startY+rand(0.05D),startZ+rand(0.05D),rand(0.02D),rand(0.02D),rand(0.02D),(red+128F)/255F,(green+128F)/255F,(blue+128F)/255F);
			startX += vec.xCoord*0.2D;
			startY += vec.yCoord*0.2D;
			startZ += vec.zCoord*0.2D;
		}
	}
	
	/**
	 * Helper method that returns random number between -1 and 1 multiplied by number provided.
	 */
	private double rand(double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
}
