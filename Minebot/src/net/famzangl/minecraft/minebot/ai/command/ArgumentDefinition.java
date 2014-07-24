package net.famzangl.minecraft.minebot.ai.command;

import java.util.Collection;

/**
 * This is the definition of a single argument to be passed on the command line.
 * 
 * @author michael
 * 
 */
public class ArgumentDefinition {
	private final String descriptionType;
	private final String descriptionInfo;

	public ArgumentDefinition() {
		this("", "");
	}

	public ArgumentDefinition(String descriptionType) {
		this(descriptionType, "");
	}

	public ArgumentDefinition(String descriptionType,
			String descriptionInfo) {
		super();
		this.descriptionType = descriptionType;
		this.descriptionInfo = descriptionInfo;
	}

	public void getTabCompleteOptions(String currentStart,
			Collection<String> addTo) {
	}

	/**
	 * Check if this argument could be from that parameter. Checks e.g. for
	 * integer values.
	 * 
	 * @param string
	 * @return
	 */
	public boolean couldEvaluateAgainst(String string) {
		return true;
	}

	public final String getDescriptionString() {
		String info = getDescriptionInfo();
		return getDescriptionType()
				+ (info == null || info.isEmpty() ? "" : ": " + info);
	}

	public String getDescriptionType() {
		return descriptionType;
	}

	private String getDescriptionInfo() {
		return descriptionInfo;
	}
}
