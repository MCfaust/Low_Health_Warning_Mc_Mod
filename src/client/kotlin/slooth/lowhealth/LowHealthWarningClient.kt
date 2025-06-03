package slooth.lowhealth

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object LowHealthWarningClient : ClientModInitializer {

	@JvmStatic
	fun loadConfig() {
		if (!configFile.exists()) {
			println("[LowHealth] No config file found. Using defaults.")
			return
		}


		try {
			FileReader(configFile).use { reader ->
				val config = gson.fromJson(reader, JsonObject::class.java)
				if (config.has("warningThreshold")) {
					warningThreshold = config.get("warningThreshold").asInt
					println("[LowHealth] Loaded warningThreshold = $warningThreshold")
				}
				if (config.has("warningEnabled")) {
					warningEnabled = config.get("warningEnabled").asBoolean
					println("[LowHealth] Loaded warningEnabled = $warningEnabled")
				}
				if (config.has("displayMode")) {
					displayMode = config.get("displayMode").asInt
					println("[LowHealth] Loaded displayMode = $displayMode")
				}
				if (config.has("warningMessage")) {
					warningMessage = config.get("warningMessage").asString
					println("[LowHealth] Loaded warningMessage = $warningMessage")
				}
			}
		} catch (e: Exception) {
			System.err.println("[LowHealth] Failed to load config: ${e.message}")
			e.printStackTrace()
		}
	}

	@JvmField
	var warningMessage: String = "§cLow Health!"

	@JvmField
	var displayMode: Int = 2 // 0 = overlay, 1 = title, 2 = both
	@JvmField
	var warningActive = false

	@JvmField
	var warningEnabled = false

	@JvmField
	var warningThreshold = -1

	@JvmField
	var warningDisplayMode = 0 // 0 = overlay, 1 = title, 2 = both

	@JvmStatic
	fun saveConfig() {
		val config = JsonObject()
		config.addProperty("warningThreshold", warningThreshold)
		config.addProperty("warningEnabled", warningEnabled)
		config.addProperty("displayMode", displayMode)
		config.addProperty("warningMessage", warningMessage)
		println("[LowHealth] saveConfig() was called.")
		println("[LowHealth] warningThreshold = $warningThreshold")
		println("[LowHealth] warningEnabled = $warningEnabled")
		try {
			FileWriter(configFile).use { writer ->
				gson.toJson(config, writer)
			}
			println("[LowHealth] Config saved to: ${configFile.absolutePath}")
			println("[LowHealth] File exists: ${configFile.exists()}")
			println("[LowHealth] Path: ${configFile.absolutePath}")
			println("[LowHealth] Contents:\n" + configFile.readText())
		} catch (e: Exception) {
			System.err.println("[LowHealth] Failed to save config: ${e.message}")
			e.printStackTrace()
		}
	}

	private val gson = Gson()
	private val configFile: File = FabricLoader.getInstance().configDir.resolve("lowhealth_config.json").toFile()

	override fun onInitializeClient() {
		println("[LowHealth] Mod initializing...")
		loadConfig()
		ClientTickEvents.END_CLIENT_TICK.register {
			val client = MinecraftClient.getInstance()
			val player = client.player

			if (player != null && warningEnabled) {
				if (player.health <= warningThreshold) {
					when (displayMode) {
						0 -> { // Overlay only
							client.inGameHud.setOverlayMessage(Text.of(warningMessage), false)
						}
						1 -> { // Title only
							client.inGameHud.setTitle(Text.of(warningMessage))
							client.inGameHud.setSubtitle(Text.empty())
							client.inGameHud.setTitleTicks(0, 20, 10)
						}
						2 -> { // Both
							client.inGameHud.setOverlayMessage(Text.of(warningMessage), false)
							client.inGameHud.setTitle(Text.of(warningMessage))
							client.inGameHud.setSubtitle(Text.empty())
							client.inGameHud.setTitleTicks(0, 20, 10)
						}
					}
					warningActive = true
				} else if (warningActive) {
					client.inGameHud.setOverlayMessage(Text.empty(), false)
					client.inGameHud.setTitle(Text.empty())
					client.inGameHud.setSubtitle(Text.empty())
					warningActive = false
				}
			}
		}
	}
}