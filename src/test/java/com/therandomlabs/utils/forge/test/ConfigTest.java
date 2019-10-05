package com.therandomlabs.utils.forge.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.therandomlabs.trlutils.config.Config;
import com.therandomlabs.trlutils.forge.config.ColorConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@Config(
		id = TRLUtilsForgeTest.MOD_ID,
		comment = "RandomLib Test main configuration",
		path = TRLUtilsForgeTest.MOD_ID + "/main"
)
public final class ConfigTest {
	public static final class FlyingPigs {
		public static final class Lol {
			@Config.Property("test")
			public static boolean test = true;
		}

		@Config.Category("lol")
		public static final Lol lol = null;

		//Blacklist the current directory
		@Config.Blacklist("")
		@Config.Property("The flying path.")
		public static Path flyingPath = Paths.get("over\\there");

		@Config.RequiresRestart
		@Config.Property("Whether to enable flying pigs.")
		public static boolean flyingPigs = true;

		@Config.RequiresReload
		@Config.RangeInt(min = -3, max = 3)
		@Config.Property("The flying pig range.")
		public static int flyingPigRange = 3;

		@Config.NonNull
		@Config.Blacklist({
				"minecraft:air",
				"minecraft:stick"
		})
		@Config.Property("The flying pig item.")
		public static Item flyingPigItem = Items.ACACIA_BOAT;

		@Config.Property("The flying pig color.")
		public static ColorConfig flyingPigColor = ColorConfig.BLUE;

		@Config.Property({
				"Test array property.",
				"Add integers to this array."
		})
		public static int[] testArrayProperty = {
				1, 3, 5, 7
		};

		@Config.Property("Test item array property.")
		public static Item[] testItemArrayProperty = {};

		@Config.Property("Null default item.")
		public static Item nullDefaultItem;

		@Config.Previous("flyingPigs.whereDidHeComeFrom")
		@Config.Property("Where did he come from, where did he go?")
		public static boolean whereDidHeGo;

		public static void onReload() {
			if(flyingPigItem == Items.APPLE) {
				flyingPigRange++; //If this is above 3, then it will be reset to 3
			}

			TRLUtilsForgeTest.LOGGER.info("Flying pig range: " + flyingPigRange);
		}
	}

	@Config.Category("Options related to flying pigs.")
	public static final FlyingPigs flyingPigs = null;
}
