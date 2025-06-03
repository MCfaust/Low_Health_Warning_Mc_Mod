package slooth.lowhealth;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import com.google.gson.JsonObject;
import slooth.lowhealth.lowhealthconfig;
import slooth.lowhealth.LowHealthWarningClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class LowHealthConfigScreen extends Screen {

    private final JsonObject json;
    private final Screen parent;
    private TextFieldWidget messageField;
    private int messageFieldLabelX;
    private int messageFieldLabelY;

    public LowHealthConfigScreen(JsonObject json, Screen parent) {
        super(Text.of(json.get("title").getAsString()));
        this.json = json;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = height / 4;

        if (json.has("widgets")) {
            for (var widget : json.getAsJsonArray("widgets")) {
                var obj = widget.getAsJsonObject();
                String type = obj.get("type").getAsString();
                String label = obj.get("label").getAsString();

                if (type.equals("toggle")) {
                    boolean currentState = LowHealthWarningClient.warningEnabled;
                    String displayLabel = currentState ? "Disable Warning" : "Enable Warning";

                    ButtonWidget toggleButton = ButtonWidget.builder(Text.of(displayLabel), btn -> {
                        LowHealthWarningClient.warningEnabled = !LowHealthWarningClient.warningEnabled;
                        btn.setMessage(Text.of(LowHealthWarningClient.warningEnabled ? "Disable Warning" : "Enable Warning"));
                        LowHealthWarningClient.saveConfig();
                    }).dimensions(width / 2 - 100, y, 200, 20).build();

                    this.addDrawableChild(toggleButton);
                    y += 30;
                }

                else if (type.equals("slider")) {
                    int min = obj.get("min").getAsInt();
                    int max = obj.get("max").getAsInt();
                    int range = max - min;

                    // --- Warning Message Text Field + Label ---
                    String warningLabelStr = "Warning Message";
                    int labelWidth = textRenderer.getWidth(warningLabelStr);
                    int textFieldRightEdge = width / 2 + 100;
                    int textFieldWidth = textFieldRightEdge - (width / 2 - 100 + labelWidth + 10);
                    int fieldX = textFieldRightEdge - textFieldWidth;
                    int labelX = fieldX - labelWidth - 5;

                    messageField = new TextFieldWidget(
                            this.textRenderer, fieldX, y, textFieldWidth, 20, Text.of("Custom Message")
                    );
                    messageField.setText(LowHealthWarningClient.warningMessage);
                    messageField.setChangedListener(text -> {
                        LowHealthWarningClient.warningMessage = text;
                        LowHealthWarningClient.saveConfig();
                    });

                    this.addSelectableChild(messageField);
                    this.addDrawableChild(messageField);
                    messageFieldLabelX = labelX;
                    messageFieldLabelY = y + 6;

                    y += 35;

                    // --- Slider ---
                    SliderWidget slider = new SliderWidget(width / 2 - 100, y, 200, 20,
                            Text.of("Threshold: " + LowHealthWarningClient.warningThreshold),
                            (double)(LowHealthWarningClient.warningThreshold - min) / range) {
                        @Override
                        protected void updateMessage() {
                            setMessage(Text.of("Threshold: " + (min + (int)(value * range))));
                        }

                        @Override
                        protected void applyValue() {
                            LowHealthWarningClient.warningThreshold = min + (int)(this.value * range);
                            System.out.println("[LowHealth] Slider applied value: " + LowHealthWarningClient.warningThreshold);
                            LowHealthWarningClient.saveConfig();
                        }
                    };
                    this.addDrawableChild(slider);
                    y += 35;

                    // --- Display Mode Button ---
                    ButtonWidget modeButton = ButtonWidget.builder(Text.of(getModeLabel()), btn -> {
                        LowHealthWarningClient.displayMode = (LowHealthWarningClient.displayMode + 1) % 3;
                        btn.setMessage(Text.of(getModeLabel()));
                        LowHealthWarningClient.saveConfig();
                    }).dimensions(width / 2 - 100, y, 200, 20).build();

                    this.addDrawableChild(modeButton);
                    y += 35;
                }

                else if (type.equals("button") && label.equalsIgnoreCase("Back")) {
                    ButtonWidget button = ButtonWidget.builder(Text.of(label), btn -> {
                        LowHealthWarningClient.saveConfig();
                        if (parent != null) {
                            MinecraftClient.getInstance().setScreen(parent);
                        }
                    }).dimensions(width / 2 - 75, height - 40, 150, 20).build();

                    this.addDrawableChild(button);
                }
            }
        }
    }

    private String getModeLabel() {
        return switch (LowHealthWarningClient.displayMode) {
            case 0 -> "Just Overlay";
            case 1 -> "Just Title";
            default -> "Both";
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        if (messageField != null) {
            context.drawText(textRenderer, "Warning Message", messageFieldLabelX, messageFieldLabelY, 0xFFFFFF, false);
        }
    }
}