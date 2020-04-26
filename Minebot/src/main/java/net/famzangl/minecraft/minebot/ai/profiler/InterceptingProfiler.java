package net.famzangl.minecraft.minebot.ai.profiler;

import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;

/**
 * This is a special profiler class that allows you to run some code when
 * minecraft reaches a start of a profiler section.
 * 
 * @author michael
 *
 */
public class InterceptingProfiler extends DebugProfiler {

	private ConcurrentHashMap<String, Runnable> runOnSection = new ConcurrentHashMap<String, Runnable>();

	public InterceptingProfiler(IntSupplier tickCounter) {
		super(tickCounter);
	}

	@Override
	public void startSection(String name) {
		Runnable runnable = runOnSection.get(name);
		if (runnable != null) {
			runnable.run();
		}
		super.startSection(name);
	}
	
	public void addListener(String name, Runnable r) {
		runOnSection.put(name, r);
	}

	public static InterceptingProfiler inject(Minecraft minecraft) {
		DebugProfiler old = PrivateFieldUtils.getFieldValue(minecraft, Minecraft.class, DebugProfiler.class);
		IntSupplier ticks = PrivateFieldUtils.getFieldValue(old, DebugProfiler.class, IntSupplier.class); // < the field is in the parent class
		InterceptingProfiler profiler = new InterceptingProfiler(ticks);
		// set minecraft.debugProfiler
		PrivateFieldUtils.setFieldValue(minecraft, Minecraft.class, DebugProfiler.class, profiler);
		return profiler;
	}
}
