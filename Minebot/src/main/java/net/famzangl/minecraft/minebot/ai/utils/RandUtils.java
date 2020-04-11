package net.famzangl.minecraft.minebot.ai.utils;

import java.util.Random;

public class RandUtils {
	private static final Random RANDOM = new Random(0x837a);
	/**
	 * Get a rand between a and b.
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getBetween(double a, double b) {
		return getBetweenCentered(a, b, 1);
	}

	/**
	 * Gets a rand between .1 and .9, scaled to a...b
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getBetweenNoCorner(double a, double b) {
		return getBetweenCentered(a, b, .8);
	}

	public static double getBetweenCentered(double a, double b, double centered) {
		if (centered <= 0) {
			return (a + b) / 2;
		}
		
		centered = clamp(centered, 0, 1);
		double size = centered * (b - a);
		double min = (a + b) / 2 - centered * (b - a) / 2;
		return RANDOM.nextDouble() * size + min;
		
	}
	
	private static double clamp(double value, int min, int max) {
		return Math.max(Math.min(value, max), min);
	}
}
