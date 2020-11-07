package net.famzangl.minecraft.minebot.ai.profiler;

import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * This is a special profiler class that allows you to run some code when
 * minecraft reaches a start of a profiler section.
 *
 * @author michael
 *
 */
public class InterceptingProfiler extends Profiler implements IProfiler {

	private ConcurrentHashMap<String, Runnable> runOnSection = new ConcurrentHashMap<String, Runnable>();

	public InterceptingProfiler(LongSupplier tickCounter) {
		super(tickCounter,null,true);
	}
	//Failed Mapping for the parameters, but otherwise not really sure what to use it for(?)
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
		//Profiler old = PrivateFieldUtils.getFieldValue(minecraft, Minecraft.class, Profiler.class);
		IProfiler old = minecraft.getProfiler();
		IntSupplier ticks = PrivateFieldUtils.getFieldValue(old, Profiler.class, IntSupplier.class); // < the field is in the parent class
		InterceptingProfiler profiler = new InterceptingProfiler((LongSupplier) ticks);
		//Not sure why?
		// set minecraft.Profiler
		PrivateFieldUtils.setFieldValue(minecraft, Minecraft.class, Profiler.class, profiler);
		return profiler;
	}
}
