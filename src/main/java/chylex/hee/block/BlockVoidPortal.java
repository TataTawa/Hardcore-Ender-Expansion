package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityVoidPortal;
import chylex.hee.world.util.EntityPortalStatus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidPortal extends BlockEndPortal{
	private final EntityPortalStatus portalStatus = new EntityPortalStatus();
	
	public BlockVoidPortal(){
		super(Material.portal);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityVoidPortal();
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity.posY <= y+0.05D && entity instanceof EntityPlayerMP){
			Pos pos = Pos.at(x,y,z);
			int meta = pos.getMetadata(world);
			EntityPlayerMP player = (EntityPlayerMP)entity;
			
			if (portalStatus.onTouch(player)){
				// TODO
			}
		}
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB checkAABB, List list, Entity entity){
		AxisAlignedBB collisionBox = AxisAlignedBB.getBoundingBox(x,y,z,x+1D,y+0.025D,z+1D);
		if (checkAABB.intersectsWith(collisionBox))list.add(collisionBox);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){}
}