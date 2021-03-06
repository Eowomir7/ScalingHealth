package net.silentchaos512.scalinghealth.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBlightFire extends Entity implements IEntityAdditionalSpawnData {

  public static final String NBT_PARENT = "ParentBlight";

  private EntityLivingBase parent;

  public EntityBlightFire(World worldIn) {

    super(worldIn);
  }

  public EntityBlightFire(EntityLivingBase parent) {

    super(parent.world);
    this.parent = parent;
  }

  @Override
  public void onUpdate() {

    // Server side only, blight fire must have a parent.
    if (!world.isRemote && (parent == null || parent.isDead)) {
      setDead();
      return;
    }

    // Occasionally players get a blight fire... what?
    if (parent instanceof EntityPlayer) {
      setDead();
    }

    // Update position manually in case fire is not riding the blight.
    if (parent != null) {
      this.posX = parent.posX;
      this.posY = parent.posY + parent.height / 1.5;
      this.posZ = parent.posZ;
    }
  }

  @Override
  protected void entityInit() {

  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound compound) {

    if (compound.hasKey(NBT_PARENT)) {
      int id = compound.getInteger(NBT_PARENT);
      Entity entity = world.getEntityByID(id);
      if (entity instanceof EntityLivingBase)
        parent = (EntityLivingBase) entity;
    }
  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound compound) {

    if (parent != null) {
      compound.setInteger(NBT_PARENT, parent.getEntityId());
    }
  }

  public EntityLivingBase getParent() {

    return parent;
  }

  @Override
  public void writeSpawnData(ByteBuf buffer) {

    buffer.writeInt(parent == null ? -1 : parent.getEntityId());
  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {

    int id = additionalData.readInt();
    if (id != -1) {
      Entity entity = world.getEntityByID(id);
      if (entity instanceof EntityLivingBase)
        parent = (EntityLivingBase) entity;
    }
  }
}
