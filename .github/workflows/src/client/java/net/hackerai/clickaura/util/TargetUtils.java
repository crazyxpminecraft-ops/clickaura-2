package net.hackerai.clickaura.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.concurrent.ThreadLocalRandom;

public class TargetUtils {

    public static Entity findBestTarget(MinecraftClient client, double maxRange) {
        if (client.player == null || client.world == null) return null;

        double actualRange = maxRange - ThreadLocalRandom.current().nextDouble(0.5);
        Vec3d eyePos = client.player.getEyePos();
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();
        Vec3d lookVec = getLookVector(yaw, pitch);

        Entity bestTarget = null;
        double bestScore = Double.MAX_VALUE;

        for (Entity entity : client.world.getEntities()) {
            if (!isValidTarget(client, entity)) continue;
            double distance = client.player.distanceTo(entity);
            if (distance > actualRange) continue;

            Vec3d toTarget = entity.getBoundingBox().getCenter().subtract(eyePos).normalize();
            double angle = Math.acos(MathHelper.clamp(lookVec.dotProduct(toTarget), -1.0, 1.0));
            double score = angle * 180.0 / Math.PI + (distance / actualRange) * 5.0;

            if (score < bestScore) { bestScore = score; bestTarget = entity; }
        }
        return bestTarget;
    }

    private static boolean isValidTarget(MinecraftClient client, Entity entity) {
        if (entity == client.player || !entity.isAlive() || !entity.isAttackable()) return false;
        if (entity.isInvisible() || !(entity instanceof LivingEntity) || !(entity instanceof PlayerEntity)) return false;
        PlayerEntity p = (PlayerEntity) entity;
        return !p.isCreative() && !p.isSpectator();
    }

    private static Vec3d getLookVector(float yaw, float pitch) {
        float yawRad = yaw * MathHelper.RADIANS_PER_DEGREE;
        float pitchRad = pitch * MathHelper.RADIANS_PER_DEGREE;
        float xzLen = MathHelper.cos(pitchRad);
        return new Vec3d(-xzLen * MathHelper.sin(yawRad), -MathHelper.sin(pitchRad), xzLen * MathHelper.cos(yawRad));
    }
}
