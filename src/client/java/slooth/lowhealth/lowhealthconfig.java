package slooth.lowhealth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static slooth.lowhealth.LowHealthWarningClient.*;

public class lowhealthconfig extends ClickableWidget {
    public lowhealthconfig(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // We'll just draw a simple rectangle for now.
        // x1, y1, x2, y2, startColor, endColor
        int startColor = 0xFF00FF00; // Green
        int endColor = 0xFF0000FF; // Blue

        context.fillGradient(getX(), getY(), getX() + this.width, getY() + this.height, startColor, endColor);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // For brevity, we'll just skip this for now - if you want to add narration to your widget, you can do so here.
        return;
    }
    public static void loadConfig() {
        try {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("lowhealth_config.json");
            if (Files.exists(configPath)) {
                try (Reader reader = Files.newBufferedReader(configPath)) {
                    JsonObject config = new Gson().fromJson(reader, JsonObject.class);

                    if (config.has("warningThreshold")) {
                        warningThreshold = config.get("warningThreshold").getAsInt();
                        System.out.println("[LowHealth] Loaded warningThreshold = " + warningThreshold);
                    }

                    if (config.has("warningEnabled")) {
                        warningEnabled = config.get("warningEnabled").getAsBoolean();
                        System.out.println("[LowHealth] Loaded warningEnabled = " + warningEnabled);
                    }
                }
            } else {
                System.out.println("[LowHealth] No config file found, using defaults.");
            }
        } catch (Exception e) {
            System.err.println("[LowHealth] Failed to load config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("warningThreshold", warningThreshold);
            config.addProperty("warningEnabled", warningEnabled);
            config.addProperty("displayMode", displayMode);

            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("lowhealth_config.json");
            Files.createDirectories(configPath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(configPath,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                new Gson().toJson(config, writer);
            }

            System.out.println("[LowHealth] Config saved to: " + configPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("[LowHealth] Failed to save config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}