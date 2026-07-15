package net.hackerai.clickaura;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickAuraHud {
    private boolean guiOpen = false;
    private boolean shiftWasPressed = false;

    public ClickAuraHud() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!guiOpen) return;
            renderGui(drawContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean shiftPressed = InputUtil.isKeyPressed(
                    MinecraftClient.getInstance().getWindow().getHandle(),
                    GLFW.GLFW_KEY_LEFT_SHIFT
            );
            if (shiftPressed && !shiftWasPressed) guiOpen = !guiOpen;
            shiftWasPressed = shiftPressed;
        });
    }

    private void renderGui(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        TextRenderer renderer = client.textRenderer;
        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;
        int boxWidth = 140, boxHeight = 80;
        int boxX = centerX - boxWidth / 2, boxY = centerY - boxHeight / 2 - 30;

        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x90000000);
        context.drawCenteredTextWithShadow(renderer, Text.literal("§6§lClick Aura"), centerX, boxY + 8, 0xFFFFFF);

        boolean enabled = ClickAuraMod.MODULE.isEnabled();
        String status = enabled ? "§a✔ ENABLED" : "§c✘ DISABLED";
        context.drawCenteredTextWithShadow(renderer, Text.literal("Status: " + status), centerX, boxY + 30, 0xFFFFFF);
        context.drawCenteredTextWithShadow(renderer, Text.literal("§7[F] Toggle  |  [Shift] Close"), centerX, boxY + 55, 0xAAAAAA);

        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_F)) {
            ClickAuraMod.MODULE.toggle();
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
    }
}
