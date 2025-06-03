package slooth.lowhealth;

import net.minecraft.client.gui.screen.Screen;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import slooth.lowhealth.LowHealthConfigScreen;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;

public class LowHealthConfigUI {

    private static final LowHealthConfigUI instance = new LowHealthConfigUI();
    private final Gson gson = new Gson();

    public static LowHealthConfigUI getInstance() {
        return instance;
    }

    public Screen getScreen(Screen parent) {
        try (InputStreamReader reader = new InputStreamReader(
                LowHealthConfigUI.class.getClassLoader().getResourceAsStream("lowhealth/ui.json"))) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            return new LowHealthConfigScreen(json, parent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}