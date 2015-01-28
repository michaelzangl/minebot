package net.famzangl.minecraft.minebot.ai.selectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public final class IsSittingSelector extends OwnTameableSelector {
	private final boolean sitting;

	public IsSittingSelector(boolean sitting, EntityLivingBase owner) {
		super(owner);
		this.sitting = sitting;
	}

	@Override
	public boolean apply(Entity var1) {
		return super.apply(var1)
				&& ((EntityWolf) var1).isSitting() == sitting;
	}
}