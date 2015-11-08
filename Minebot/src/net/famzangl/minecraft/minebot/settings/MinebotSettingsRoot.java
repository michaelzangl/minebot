package net.famzangl.minecraft.minebot.settings;

import net.famzangl.minecraft.minebot.ai.tools.ToolRater;

/**
 * This is the root object for our json settings file.
 * 
 * @author Michael Zangl
 *
 */
@MinebotSettingObject
public class MinebotSettingsRoot {
	private PathfindingSettings pathfinding = new PathfindingSettings();
	
	private SaferuleSettings saferules = new SaferuleSettings();

	private MiningSettings mining = new MiningSettings();

	private ToolRater toolRater = ToolRater.createDefaultRater();
	
	public PathfindingSettings getPathfinding() {
		return pathfinding;
	}

	public MiningSettings getMining() {
		return mining ;
	}

	public ToolRater getToolRater() {
		return toolRater ;
	}
}
