package com.therandomlabs.utils.forge;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.Launcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ForgeUtils {
	public static final boolean IS_DEOBFUSCATED;
	public static final boolean IS_CLIENT = FMLEnvironment.dist.isClient();

	public static final String MC_VERSION = MCPVersion.getMCVersion();
	public static final int MC_VERSION_NUMBER = Integer.parseInt(MC_VERSION.split("\\.")[1]);
	public static final ArtifactVersion MC_ARTIFACT_VERSION =
			new DefaultArtifactVersion(MC_VERSION);

	public static final int FORGE_BUILD =
			Integer.parseInt(ForgeVersion.getVersion().split("\\.")[2]);

	static {
		try {
			final Object argumentHandler =
					findField(Launcher.class, "argumentHandler").get(Launcher.INSTANCE);

			final Object launchTarget =
					findMethod(ArgumentHandler.class, "getLaunchTarget").invoke(argumentHandler);

			IS_DEOBFUSCATED = "fmluserdevclient".equals(launchTarget) ||
					"fmluserdevserver".equals(launchTarget);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException("Failed to determine launch target", ex);
		}
	}

	private ForgeUtils() {}

	/**
	 * Returns the specified array as a primitive array.
	 *
	 * @param array a boxed array.
	 * @return the specified array as a primitive array.
	 */
	public static Object toPrimitiveArray(Object[] array) {
		if (array instanceof Boolean[]) {
			return ArrayUtils.toPrimitive((Boolean[]) array);
		}

		if (array instanceof Byte[]) {
			return ArrayUtils.toPrimitive((Byte[]) array);
		}

		if (array instanceof Character[]) {
			return ArrayUtils.toPrimitive((Character[]) array);
		}

		if (array instanceof Double[]) {
			return ArrayUtils.toPrimitive((Double[]) array);
		}

		if (array instanceof Float[]) {
			return ArrayUtils.toPrimitive((Float[]) array);
		}

		if (array instanceof Integer[]) {
			return ArrayUtils.toPrimitive((Integer[]) array);
		}

		if (array instanceof Long[]) {
			return ArrayUtils.toPrimitive((Long[]) array);
		}

		if (array instanceof Short[]) {
			return ArrayUtils.toPrimitive((Short[]) array);
		}

		throw new IllegalArgumentException("array should be a boxed array");
	}

	/**
	 * Returns the specified array as a boxed array.
	 *
	 * @param array a primitive array.
	 * @return the specified array as a boxed array.
	 */
	public static Object[] toBoxedArray(Object array) {
		Preconditions.checkNotNull(array, "array should not be null");

		if (array instanceof Object[]) {
			return (Object[]) array;
		}

		if (array instanceof boolean[]) {
			return ArrayUtils.toObject((boolean[]) array);
		}

		if (array instanceof byte[]) {
			return ArrayUtils.toObject((byte[]) array);
		}

		if (array instanceof char[]) {
			return ArrayUtils.toObject((char[]) array);
		}

		if (array instanceof double[]) {
			return ArrayUtils.toObject((double[]) array);
		}

		if (array instanceof float[]) {
			return ArrayUtils.toObject((float[]) array);
		}

		if (array instanceof int[]) {
			return ArrayUtils.toObject((int[]) array);
		}

		if (array instanceof long[]) {
			return ArrayUtils.toObject((long[]) array);
		}

		if (array instanceof short[]) {
			return ArrayUtils.toObject((short[]) array);
		}

		throw new IllegalArgumentException("array should be an array");
	}

	/**
	 * Returns the specified string as a normalized {@link Path}.
	 *
	 * @param path a path.
	 * @return the specified string as a normalized {@link Path}.
	 */
	public static Path getPath(String path) {
		return Paths.get(path).normalize();
	}

	/**
	 * Returns the string representation of the specified {@link Path} with Unix directory
	 * separators.
	 *
	 * @param path a {@link Path}.
	 * @return the string representation of the specified {@link Path} with Unix directory
	 * separators.
	 */
	public static String withUnixDirectorySeparators(Path path) {
		Preconditions.checkNotNull(path, "path should not be null");
		return withUnixDirectorySeparators(path.toString());
	}

	/**
	 * Returns the specified path with Unix directory separators.
	 *
	 * @param path a path.
	 * @return the specified path with Unix directory separators.
	 */
	public static String withUnixDirectorySeparators(String path) {
		Preconditions.checkNotNull(path, "path should not be null");
		return path.replace('\\', '/');
	}

	/**
	 * Quietly retrieves the field with any of the specified names in the specified class.
	 *
	 * @param clazz a class.
	 * @param names an array of possible field names.
	 * @return the {@link Field} with any of the specified names, or otherwise {@code null}.
	 */
	@Nullable
	public static Field findFieldNullable(Class<?> clazz, String... names) {
		Preconditions.checkNotNull(clazz, "clazz should not be null");
		Preconditions.checkNotNull(names, "names should not be null");

		for (Field field : clazz.getDeclaredFields()) {
			for (String name : names) {
				if (name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	/**
	 * Quietly retrieves the field with any of the specified names in the specified class.
	 *
	 * @param clazz a class.
	 * @param names an array of possible field names.
	 * @return the {@link Field} with any of the specified names.
	 * @throws IllegalArgumentException if the specified field is not found.
	 */
	public static Field findField(Class<?> clazz, String... names) {
		final Field field = findFieldNullable(clazz, names);

		if (field == null) {
			throw new IllegalArgumentException(
					"No such field " + Arrays.toString(names) + " in: " + clazz.getName()
			);
		}

		return field;
	}

	/**
	 * Quietly retrieves the method with the specified name and parameter types in the
	 * specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters, or otherwise {@code null}.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethodNullable(
			Class<?> clazz, String name, Class<?>... parameterTypes
	) {
		return findMethodNullable(clazz, name, name, parameterTypes);
	}

	/**
	 * Quietly retrieves the method with the specified name or obfuscated name and parameter types
	 * in the specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param obfuscatedName an obfuscated method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters, or otherwise {@code null}.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethodNullable(
			Class<?> clazz, String name, String obfuscatedName, Class<?>... parameterTypes
	) {
		Preconditions.checkNotNull(clazz, "clazz should not be null");
		Preconditions.checkNotNull(name, "name should not be null");
		Preconditions.checkNotNull(obfuscatedName, "obfuscatedName should not be null");
		Preconditions.checkNotNull(parameterTypes, "parameterTypes should not be null");

		for (Method method : clazz.getDeclaredMethods()) {
			final String methodName = method.getName();

			if ((name.equals(methodName) || obfuscatedName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	/**
	 * Quietly retrieves the method with the specified name and parameter types in the
	 * specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters.
	 * @throws IllegalArgumentException if the specified field is not found.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethod(
			Class<?> clazz, String name, Class<?>... parameterTypes
	) {
		final Method method = findMethodNullable(clazz, name, parameterTypes);

		if (method == null) {
			throw new IllegalArgumentException(
					"No such method " + name + " in: " + clazz.getName()
			);
		}

		return method;
	}

	/**
	 * Quietly retrieves the method with the specified name or obfuscated name and parameter types
	 * in the specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param obfuscatedName an obfuscated method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters.
	 * @throws IllegalArgumentException if the specified field is not found.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethod(
			Class<?> clazz, String name, String obfuscatedName, Class<?>... parameterTypes
	) {
		final Method method = findMethodNullable(clazz, name, obfuscatedName, parameterTypes);

		if (method == null) {
			throw new IllegalArgumentException(
					"No such method " + name + " in: " + clazz.getName()
			);
		}

		return method;
	}

	/**
	 * Quietly retrieves the class with the specified name.
	 *
	 * @param name a class name.
	 * @return the class with the specified name as returned by {@link Class#forName(String)},
	 * or {@code null} if it cannot be found.
	 */
	@Nullable
	public static Class<?> getClass(String name) {
		Preconditions.checkNotNull(name, "name should not be null");

		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ignored) {}

		return null;
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static ModLoadingStage getModLoadingStage() {
		return ModList.get().getModContainerById("forge").get().getCurrentState();
	}

	public static boolean hasReachedStage(ModLoadingStage stage) {
		return stage.ordinal() <= getModLoadingStage().ordinal();
	}

	/**
	 * Throws a {@link ReportedException} by constructing a {@link CrashReport} with the specified
	 * message and {@link Throwable}.
	 *
	 * @param message a message.
	 * @param throwable a {@link Throwable}.
	 */
	public static void crashReport(String message, Throwable throwable) {
		Preconditions.checkNotNull(message, "message should not be null");
		Preconditions.checkNotNull(throwable, "throwable should not be null");
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
