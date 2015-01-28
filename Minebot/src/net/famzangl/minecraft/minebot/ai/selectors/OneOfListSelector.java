package net.famzangl.minecraft.minebot.ai.selectors;

import java.util.List;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;

public class OneOfListSelector implements Predicate<Entity> {

	private final List<Entity> list;

	public OneOfListSelector(List<Entity> list) {
		this.list = list;
	}

	@Override
	public boolean apply(Entity var1) {
		return list.contains(var1);
	}

}
