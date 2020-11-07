package net.famzangl.minecraft.minebot.ai.profiler;

import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * This is a special profiler class that allows you to run some code when
 * minecraft reaches a start of a profiler section.
 *
 * At some places, we cannot hook into minecraft using normal Forge methods.
 * We use this to hook e.g. into the rendering code.
 *
 * @author michael
 */
@ParametersAreNonnullByDefault
public class InterceptingProfiler implements IProfiler {

	private final ConcurrentHashMap<String, Runnable> runOnSection = new ConcurrentHashMap<String, Runnable>();

	public InterceptingProfiler(IntSupplier tickCounter) {
		//For extending Profiler, we would use: super(Util.nanoTimeSupplier, tickCounter,true);
	}

	@Override
	public void startTick() {
		// NOP
	}

	@Override
	public void endTick() {
		// NOP
	}

	//Failed Mapping for the parameters, but otherwise not really sure what to use it for(?)
	@Override
	public void startSection(String name) {
		Runnable runnable = runOnSection.get(name);
		if (runnable != null) {
			runnable.run();
		}
	}

	@Override
	public void startSection(Supplier<String> nameSupplier) {
		startSection(nameSupplier.get());
	}

	@Override
	public void endSection() {
		// NOP
	}

	@Override
	public void endStartSection(String name) {
		startSection(name);
	}

	@Override
	public void endStartSection(Supplier<String> nameSupplier) {
		startSection(nameSupplier.get());
	}

	@Override
	public void func_230035_c_(String p_230035_1_) {
		// This method counts invocations
		// NOP
	}

	@Override
	public void func_230036_c_(Supplier<String> p_230036_1_) {
		// NOP
	}

	public void addListener(String name, Runnable r) {
		runOnSection.put(name, r);
	}

	public static InterceptingProfiler inject(Minecraft minecraft) {
		// No way to get tick count in client, but we probably don't need it.
		InterceptingProfiler profiler = new InterceptingProfiler(() -> 0);
		// set minecraft.Profiler
		PrivateFieldUtils.setFieldValue(minecraft, Minecraft.class, IProfiler.class, profiler);
		return profiler;
	}
}
