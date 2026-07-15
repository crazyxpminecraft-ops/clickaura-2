package net.hackerai.clickaura.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {
    public static double[] calculateLookAt(LivingEntity source, Entity target) {
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d sourcePos = source.getEyePos();
        double dx = targetPos.x - sourcePos.x;
        double dy = targetPos.y - sourcePos.y;
        double dz = targetPos.z - sourcePos.z;
        double hDist = Math.sqrt(dx * dx + dz * dz);
        return new double[]{
                Math.toDegrees(Math.atan2(-dx, dz)),
                -Math.toDegrees(Math.atan2(dy, hDist))
        };
    }
}
