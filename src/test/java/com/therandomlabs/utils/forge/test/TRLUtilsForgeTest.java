package com.therandomlabs.utils.forge.test;

import com.therandomlabs.trlutils.config.ConfigManager;
import com.therandomlabs.trutils.forge.config.CommandConfigReload;
import com.therandomlabs.trutils.forge.config.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TRLUtilsForgeTest.MOD_ID)
public final class TRLUtilsForgeTest {
	public static final String MOD_ID = "trlutilsforgetest";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public TRLUtilsForgeTest() {
		ForgeConfig.initialize();
		ConfigManager.register(ConfigTest.class);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
	}

	private void setup(FMLCommonSetupEvent event) {
		//This must also be called in setup as registry entry properties are not loaded during
		//initialization
		ConfigManager.reloadFromDisk(ConfigTest.class);
	}

	private void serverStarting(FMLServerStartingEvent event) {
		CommandConfigReload.server(
				event.getCommandDispatcher(), "tuftreload", "tuftreloadclient", ConfigTest.class,
				"TRLUtils-Forge Test configuration reloaded!"
		);
	}
}
