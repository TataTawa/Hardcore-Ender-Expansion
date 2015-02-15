package chylex.hee.entity.projectile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileSpatialDash extends EntityThrowable{
	private List<Enum> enhancements = new ArrayList<>();
	private byte ticks;
	
	public EntityProjectileSpatialDash(World world){
		super(world);
	}

	public EntityProjectileSpatialDash(World world, EntityLivingBase thrower, List<Enum> enhancements){
		super(world,thrower);
		motionX *= 1.75D;
		motionY *= 1.75D;
		motionZ *= 1.75D;
		this.enhancements.addAll(enhancements);
	}

	@SideOnly(Side.CLIENT)
	public EntityProjectileSpatialDash(World world, double x, double y, double z){
		super(world,x,y,z);
	}
	
	@Override
	public void onUpdate(){
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		
		onEntityUpdate();

		if (!worldObj.isRemote){
			for(int cycles = enhancements.contains(SpatialDashGemEnhancements.INSTANT) ? 4 : 1; cycles > 0; cycles--){
				if (++ticks > (enhancements.contains(SpatialDashGemEnhancements.RANGE) ? 48 : 28))setDead();
		
				Vec3 vecPos = Vec3.createVectorHelper(posX,posY,posZ);
				Vec3 vecPosWithMotion = Vec3.createVectorHelper(posX+motionX,posY+motionY,posZ+motionZ);
				Vec3 hitVec;
				
				MovingObjectPosition mop = worldObj.rayTraceBlocks(vecPos,vecPosWithMotion);
				vecPos = Vec3.createVectorHelper(posX,posY,posZ);
		
				if (mop != null)hitVec = Vec3.createVectorHelper(mop.hitVec.xCoord,mop.hitVec.yCoord,mop.hitVec.zCoord);
				else hitVec = Vec3.createVectorHelper(posX+motionX,posY+motionY,posZ+motionZ);
			
				Entity finalEntity = null;
				List<Entity> collisionList = worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.addCoord(motionX,motionY,motionZ).expand(1D,1D,1D));
				double minDist = Double.MAX_VALUE, dist;
				EntityLivingBase thrower = getThrower();
	
				for(Entity e:collisionList){
					if (e.canBeCollidedWith() && (e != thrower || ticks >= 5)){
						AxisAlignedBB aabb = e.boundingBox.expand(0.3F,0.3F,0.3F);
						MovingObjectPosition mopTest = aabb.calculateIntercept(vecPos,hitVec);
	
						if (mopTest != null){
							dist = vecPos.distanceTo(mopTest.hitVec);
	
							if (dist < minDist){
								finalEntity = e;
								minDist = dist;
							}
						}
					}
				}
	
				if (finalEntity != null)mop = new MovingObjectPosition(finalEntity);
	
				if (mop != null)onImpact(mop);
				
				posX += motionX;
				posY += motionY;
				posZ += motionZ;
			}
			
			setPosition(posX,posY,posZ);
			
			PacketPipeline.sendToAllAround(this,128D,new C22EffectLine(FXType.Line.SPATIAL_DASH_MOVE,lastTickPosX,lastTickPosY,lastTickPosZ,posX,posY,posZ));
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (getThrower() != null && getThrower() instanceof EntityPlayerMP){
				EntityPlayerMP player = (EntityPlayerMP)getThrower();
				
				PacketPipeline.sendToAllAround(player,64D,new C21EffectEntity(FXType.Entity.GEM_TELEPORT_FROM,player));

				if (player.playerNetServerHandler.func_147362_b().isChannelOpen() && player.worldObj == worldObj){ // OBFUSCATED get network manager
					if (player.isRiding())player.mountEntity(null);
					if (player.posY <= 0D)player.addStat(AchievementManager.TP_NEAR_VOID,1);
					
					int x,y,z;
					
					if (mop.typeOfHit == MovingObjectType.BLOCK){
						x = mop.blockX;
						y = mop.blockY;
						z = mop.blockZ;
					}
					else if (mop.typeOfHit == MovingObjectType.ENTITY){
						x = MathUtil.floor(posX);
						y = MathUtil.floor(posY);
						z = MathUtil.floor(posZ);
					}
					else{
						setDead();
						return;
					}
					
					boolean found = false;
					Block block;
					
					for(int yTest = y; yTest <= y+8; yTest++){
						if ((block = worldObj.getBlock(x,yTest,z)).getBlockHardness(worldObj,x,yTest,z) == -1)break;
						
						if (canSpawnIn(block,worldObj.getBlock(x,yTest+1,z))){
							player.setPositionAndUpdate(x+0.5D,yTest+0.01D,z+0.5D);
							found = true;
							break;
						}
					}
					
					if (!found){
						for(int xTest = x-1; xTest <= x+1; xTest++){
							for(int zTest = z-1; zTest <= z+1; zTest++){
								for(int yTest = y+1; yTest <= y+8; yTest++){
									if ((block = worldObj.getBlock(x,yTest,z)).getBlockHardness(worldObj,x,yTest,z) == -1)break;
									
									if (canSpawnIn(block,worldObj.getBlock(xTest,yTest+1,zTest))){
										player.setPositionAndUpdate(xTest+0.5D,yTest+0.01D,zTest+0.5D);
										found = true;
										break;
									}
								}
							}
						}
					}
					
					if (!found)player.setPositionAndUpdate(x+0.5D,y+0.01D,z+0.5D);
					player.fallDistance = 0F;
					
					PacketPipeline.sendToAllAround(player,64D,new C20Effect(FXType.Basic.GEM_TELEPORT_TO,player));
				}
			}

			setDead();
		}
	}
	
	@Override
	public void setDead(){
		super.setDead();
		
		if (worldObj.isRemote){
			for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.spatialDashExplode(this);
		}
	}
	
	private boolean canSpawnIn(Block blockBottom, Block blockTop){
		return (blockBottom.getMaterial() == Material.air || !blockBottom.isOpaqueCube()) && (blockTop.getMaterial() == Material.air || !blockTop.isOpaqueCube());
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("tickTimer",ticks);
		nbt.setString("enhancements",EnhancementEnumHelper.serialize(enhancements));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		ticks = nbt.getByte("tickTimer");
		enhancements = EnhancementEnumHelper.deserialize("enhancements",SpatialDashGemEnhancements.class);
	}
}
