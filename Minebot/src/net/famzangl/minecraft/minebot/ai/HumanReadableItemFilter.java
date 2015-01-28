package net.famzangl.minecraft.minebot.ai;

/**
 * Item filters implementing this interface can be represented in a human
 * readable form.
 * 
 * @author michael
 *
 */
public interface HumanReadableItemFilter extends ItemFilter {
	public String getDescription();
}
