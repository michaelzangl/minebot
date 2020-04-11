package net.famzangl.minecraft.minebot.ai.profiler;

import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
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
public class InterceptingProfiler extends Profiler {

	private ConcurrentHashMap<String, Runnable> runOnSection = new ConcurrentHashMap<String, Runnable>();

	public InterceptingProfiler(long p_i225707_1_, IntSupplier p_i225707_3_, boolean p_i225707_4_) {
		super(p_i225707_1_, p_i225707_3_, p_i225707_4_);
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
		Profiler old = PrivateFieldUtils.getFieldValue(minecraft, Minecraft.class, Profiler.class);
		IntSupplier ticks = PrivateFieldUtils.getFieldValue(old, Profiler.class, IntSupplier.class);
		InterceptingProfiler profiler = new InterceptingProfiler(Util.nanoTime(), ticks, true);
		// set minecraft.mcProfiler
		PrivateFieldUtils.setFieldValue(minecraft, Minecraft.class, Profiler.class, profiler);
		return profiler;
	}
}
