package net.famzangl.minecraft.minebot.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.util.math.BlockPos;

public class IconDefinition {
	private final BlockPos position;
	private final String comment;
	private final IconType icon;

	public IconDefinition(BlockPos position, String comment, IconType icon) {
		super();
		this.position = position;
		this.comment = comment;
		this.icon = icon;
	}

	public String getComment() {
		return comment;
	}

	public BlockPos getPosition() {
		return position;
	}

	public IconType getIcon() {
		return icon;
	}

	public String[] getIconNames(RenderMode mode) {
		String[] strs = new String[4];
		int i = 0;
		for (String s2 : new String[] { "-" + mode.toString().toLowerCase(), "" }) {
			for (String s1 : new String[] { "-" + icon.toString().toLowerCase(), "" }) {
				strs[i++] = "icon" + s1 + s2 + ".png";
			}
		}
		return strs;
	}

	public BufferedImage getIcon(RenderMode mode) {
		for (String name : getIconNames(mode)) {
			URL resource = getClass().getResource(name);
			if (resource != null) {
				try {
					return ImageIO.read(resource);
				} catch (IOException e) {
				}
			}
		}
		throw new IllegalArgumentException("Could not find image.");
	}

	@Override
	public String toString() {
		return "IconDefinition [position=" + position + ", comment=" + comment
				+ ", icon=" + icon + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IconDefinition other = (IconDefinition) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (icon != other.icon)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
}