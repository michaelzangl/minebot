package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public final class IsSittingSelector implements IEntitySelector {
	private final boolean sitting;
	private final EntityLivingBase owner;

	public IsSittingSelector(boolean sitting, EntityLivingBase owner) {
		super();
		this.sitting = sitting;
		this.owner = owner;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		return var1 instanceof EntityWolf
				&& ((EntityWolf) var1).getOwner() == owner
				&& ((EntityWolf) var1).isSitting() == sitting;
	}
}