package slooth.lowhealth

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object LowHelthWarning : ModInitializer {
    private val logger = LoggerFactory.getLogger("lowhelthwarning")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
	}
}