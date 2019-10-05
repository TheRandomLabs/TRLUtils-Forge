package com.therandomlabs.utils.forge.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.utils.config.TypeAdapter;
import com.therandomlabs.utils.config.TypeAdapters;
import com.therandomlabs.utils.forge.ForgeUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public final class ResourceLocationTypeAdapter implements TypeAdapter {
	private final Class<IForgeRegistryEntry<?>> registryEntryClass;
	private final IForgeRegistry<?> registry;
	private final boolean isArray;

	public ResourceLocationTypeAdapter(
			Class<IForgeRegistryEntry<?>> registryEntryClass, boolean isArray
	) {
		this.registryEntryClass = registryEntryClass;
		registry = RegistryManager.ACTIVE.getRegistry(registryEntryClass);
		this.isArray = isArray;
	}

	@Override
	public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
		if(!isArray) {
			final String locationString = config.get(name);

			if(locationString.isEmpty()) {
				return defaultValue;
			}

			final ResourceLocation location =
					new ResourceLocation(locationString.replaceAll("\\s", ""));
			return registry.containsKey(location) ? registry.getValue(location) : defaultValue;
		}

		final List<String> list = config.get(name);
		final List<Object> values = new ArrayList<>(list.size());

		for(String element : list) {
			final Object object =
					registry.getValue(new ResourceLocation(element.replaceAll("\\s", "")));

			if(object != null) {
				values.add(object);
			}
		}

		return values.toArray((Object[]) Array.newInstance(registryEntryClass, 0));
	}

	@Override
	public void setValue(CommentedFileConfig config, String name, Object value) {
		if(isArray) {
			config.set(
					name,
					Arrays.stream((Object[]) value).
							map(this::asString).
							collect(Collectors.toList())
			);
		} else {
			config.set(name, asString(value));
		}
	}

	@Override
	public String asString(Object value) {
		return value == null ? "" : ((IForgeRegistryEntry) value).getRegistryName().toString();
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean shouldLoad() {
		return ForgeUtils.hasReachedStage(ModLoadingStage.COMMON_SETUP);
	}

	@Override
	public boolean canBeNull() {
		return true;
	}

	@Override
	public Object reloadDefault(Object defaultValue) {
		if(!isArray()) {
			if(defaultValue == null) {
				return null;
			}

			return registry.getValue(((IForgeRegistryEntry) defaultValue).getRegistryName());
		}

		final Object[] oldDefaults = (Object[]) defaultValue;
		final List<Object> newDefaults = new ArrayList<>(oldDefaults.length);

		for(Object oldDefault : oldDefaults) {
			newDefaults.add(
					registry.getValue(((IForgeRegistryEntry) oldDefault).getRegistryName())
			);
		}

		return newDefaults.toArray(Arrays.copyOf(oldDefaults, 0));
	}

	@SuppressWarnings("unchecked")
	public static void registerIfRegistryEntry(Class<?> clazz) {
		if(IForgeRegistryEntry.class.isAssignableFrom(clazz)) {
			register((Class<IForgeRegistryEntry<?>>) clazz);
		} else if(clazz.isArray()) {
			final Class<?> componentType = clazz.getComponentType();

			if(IForgeRegistryEntry.class.isAssignableFrom(componentType)) {
				register((Class<IForgeRegistryEntry<?>>) componentType);
			}
		}
	}

	static void initialize() {
		TypeAdapters.registerAutoRegistrar(ResourceLocationTypeAdapter::registerIfRegistryEntry);
	}

	private static void register(Class<IForgeRegistryEntry<?>> clazz) {
		TypeAdapters.register(clazz, new ResourceLocationTypeAdapter(clazz, false));
		TypeAdapters.register(
				Array.newInstance(clazz, 0).getClass(),
				new ResourceLocationTypeAdapter(clazz, true)
		);
	}
}
