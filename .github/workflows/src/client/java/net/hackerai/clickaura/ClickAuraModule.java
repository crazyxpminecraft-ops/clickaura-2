package net.hackerai.clickaura;

import net.hackerai.clickaura.util.RotationUtils;
import net.hackerai.clickaura.util.TargetUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import java.util.concurrent.ThreadLocalRandom;

public class ClickAuraModule {
    private boolean enabled = false;
    private long lastAttackTime = 0;
    private Entity currentTarget = null;
    private long targetSwitchTime = 0;
    private static final long TARGET_SWITCH_COOLDOWN_MS = 500;

    public void setEnabled(boolean state) {
        this.enabled = state;
        if (!state) this.currentTarget = null;
    }

    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }

    public void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;

        Entity bestTarget = TargetUtils.findBestTarget(client, ClickAuraMod.CONFIG.range);
        if (bestTarget == null) { currentTarget = null; return; }

        long now = System.currentTimeMillis();
        if (bestTarget != currentTarget) {
            if (now - targetSwitchTime < TARGET_SWITCH_COOLDOWN_MS) return;
            currentTarget = bestTarget;
            targetSwitchTime = now;
        }

        double cps = 8.0 + ThreadLocalRandom.current().nextDouble(5.0);
        long attackIntervalMs = (long) (1000.0 / cps);
        attackIntervalMs += (long) (attackIntervalMs * (ThreadLocalRandom.current().nextGaussian() * 0.15));
        attackIntervalMs = Math.max(55, Math.min(150, attackIntervalMs));

        if (now - lastAttackTime < attackIntervalMs) return;

        double[] rotations = RotationUtils.calculateLookAt(client.player, bestTarget);
        client.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.LookAndOnGround(
                        (float) rotations[0], (float) rotations[1], client.player.isOnGround()
                )
        );

        PlayerInteractEntityC2SPacket attackPacket = PlayerInteractEntityC2SPacket.attack(
                bestTarget, client.player.isSneaking()
        );
        client.getNetworkHandler().getConnection().send(attackPacket);

        client.player.swingHand(bestTarget.getActiveHand());
        lastAttackTime = now;
    }
}
