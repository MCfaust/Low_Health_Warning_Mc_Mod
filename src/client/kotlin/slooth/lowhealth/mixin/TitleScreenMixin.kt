package slooth.lowhealth.mixin

import slooth.lowhealth.lowhealthconfig
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
class TitleScreenMixin : Screen(Text.of("Title")) {

    @Inject(method = ["init"], at = [At("TAIL")])
    fun onInit(ci: CallbackInfo) {
        val widget = lowhealthconfig(
            this.width / 2 - 60,    // X position
            this.height / 4 + 120, // Y position
            120,                   // Width
            20                     // Height
        )
        this.addDrawableChild(widget)
    }
}