package net.famzangl.minecraft.minebot.ai.selectors;

import java.lang.reflect.Method;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class OwnTameableSelector implements IEntitySelector {
	private final EntityLivingBase owner;

	public OwnTameableSelector(EntityLivingBase owner) {
		super();
		this.owner = owner;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		return var1 instanceof EntityTameable && isMine((EntityTameable) var1);
	}

	private boolean isMine(EntityTameable var1) {
		Method m;
		try {
			m = EntityTameable.class.getMethod("func_152113_b");

			if (m != null) {
				// 1.7.10. No fix so far...
				return var1.isTamed();
			} else {
				return var1.getOwner() == owner;
			}

		} catch (final NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (final SecurityException e1) {
			e1.printStackTrace();
		}
		return true;
	}
}