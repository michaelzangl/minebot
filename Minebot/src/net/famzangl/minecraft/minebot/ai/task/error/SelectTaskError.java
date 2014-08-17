package net.famzangl.minecraft.minebot.ai.task.error;

import net.famzangl.minecraft.minebot.ai.ItemFilter;

public final class SelectTaskError extends TaskError {
	private final ItemFilter filter;

	public SelectTaskError(ItemFilter filter) {
		super("Cannot select: " + filter);
		this.filter = filter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (filter == null ? 0 : filter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SelectTaskError other = (SelectTaskError) obj;
		if (filter == null) {
			if (other.filter != null) {
				return false;
			}
		} else if (!filter.equals(other.filter)) {
			return false;
		}
		return true;
	}

}