package net.famzangl.minecraft.minebot.ai.profiler;

import java.util.concurrent.ConcurrentHashMap;

import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;

/**
 * This is a special profiler class that allows you to run some code when
 * minecraft reaches a start of a profiler section.
 * 
 * @author michael
 *
 */
public class InterceptingProfiler extends Profiler {

	private ConcurrentHashMap<String, Runnable> runOnSection = new ConcurrentHashMap<String, Runnable>();

	@Override
	public void startSection(String name) {
		Runnable runnable = runOnSection.get(name);
		if (runnable != null) {
			runnable.run();
		}
		super.startSection(name);
	}
	
	public void addLisener(String name, Runnable r) {
		runOnSection.put(name, r);
	}

	public static InterceptingProfiler inject(Minecraft minecraft) {
		InterceptingProfiler profiler = new InterceptingProfiler();
		// set minecraft.mcProfiler
		PrivateFieldUtils.setFieldValue(minecraft, Minecraft.class, Profiler.class, profiler);
		return profiler;
	}
}
