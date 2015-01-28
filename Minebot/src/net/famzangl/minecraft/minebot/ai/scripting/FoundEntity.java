package net.famzangl.minecraft.minebot.ai.scripting;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;

public class FoundEntity extends DoublePos {
	private final Entity entity;
	private final Pos pos;
	private EnumDyeColor color = null;
	
	public FoundEntity(Entity entity) {
		super(entity);
		this.entity = entity;
		pos = new Pos((int) Math.floor(entity.posX), (int) Math.floor(entity.posY), (int) Math.floor(entity.posZ));
		if (entity instanceof EntitySheep) {
			color = ((EntitySheep) entity).getFleeceColor();
		} else if (entity instanceof EntityWolf) {
			color = ((EntityWolf) entity).getCollarColor();
		}
	}

	public Pos getPos() {
		return pos;
	}

	public Class<?> getType()  {
		return entity.getClass();
	}
	
	public String getTypeName()  {
		return entity.getClass().getSimpleName();
	}

	public String getColor() {
		return color == null ? null : color.getName();
	}
	
	public String getName() {
		return entity.getCommandSenderEntity().getName();
	}
	
	public String getCustomName() {
		if (entity instanceof EntityLiving) {
			return ((EntityLiving) entity).hasCustomName() ? ((EntityLiving) entity).getCustomNameTag() : null;
		} else if (entity instanceof EntityMinecart) {
			return ((EntityMinecart) entity).hasCustomName() ? entity.getCommandSenderEntity().getName() : null;
		} else {
			return null;
		}
	}
}