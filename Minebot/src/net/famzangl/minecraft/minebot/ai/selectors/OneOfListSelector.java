package net.famzangl.minecraft.minebot.ai.selectors;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class OneOfListSelector implements IEntitySelector {
	
	private final List<Entity> list;
	
	public OneOfListSelector(List<Entity> list) {
		this.list = list;
	}

	@Override
	public boolean isEntityApplicable(Entity var1) {
		return list.contains(var1);
	}

}
