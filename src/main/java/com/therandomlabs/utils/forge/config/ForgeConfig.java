package com.therandomlabs.trlutils.forge.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import com.therandomlabs.trlutils.config.ConfigManager;
import com.therandomlabs.trlutils.forge.ForgeUtils;
import net.minecraftforge.forgespi.language.MavenVersionAdapter;
import org.apache.maven.artifact.versioning.VersionRange;

public final class ForgeConfig {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface MCVersion {
		//Version range
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface MinForgeBuild {
		//Minimum Forge build
		int value();
	}

	private ForgeConfig() {}

	public static void initialize() {
		ConfigManager.setClient(ForgeUtils.IS_CLIENT);
		ConfigManager.registerVersionChecker(ForgeConfig::testVersionRange);
		ResourceLocationTypeAdapter.initialize();
	}

	private static boolean testVersionRange(Field field) {
		final MCVersion mcVersion = field.getAnnotation(MCVersion.class);

		if(mcVersion != null) {
			final String versionRange = mcVersion.value().trim();

			if(versionRange.isEmpty()) {
				throw new IllegalArgumentException("Version range must not be empty");
			}

			final VersionRange range = MavenVersionAdapter.createFromVersionSpec(versionRange);

			if(!range.containsVersion(ForgeUtils.MC_ARTIFACT_VERSION)) {
				return false;
			}
		}

		final MinForgeBuild minForgeBuild = field.getAnnotation(MinForgeBuild.class);

		if(minForgeBuild == null) {
			return true;
		}

		final int forgeBuild = minForgeBuild.value();

		if(forgeBuild < 1) {
			throw new IllegalArgumentException("Invalid Forge build: " + forgeBuild);
		}

		return ForgeUtils.FORGE_BUILD >= forgeBuild;
	}
}
