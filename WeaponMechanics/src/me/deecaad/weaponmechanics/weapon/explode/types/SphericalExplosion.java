package me.deecaad.weaponmechanics.weapon.explode.types;

import me.deecaad.core.file.Configuration;
import me.deecaad.core.utils.LogLevel;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.weapon.explode.ExplosionShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.deecaad.weaponmechanics.WeaponMechanics.debug;

public class SphericalExplosion implements ExplosionShape {

    private static final Configuration config = WeaponMechanics.getBasicConfigurations();

    private double radius;
    
    public SphericalExplosion(double radius) {
        this.radius = radius;
    }
    
    @Nonnull
    @Override
    public List<Block> getBlocks(@Nonnull Location origin) {
        List<Block> temp = new ArrayList<>();
        
        Location pos1 = origin.clone().add(-radius, -radius, -radius);
        Location pos2 = origin.clone().add(+radius, +radius, +radius);

        double noiseDistance = config.getDouble("Explosions.Spherical.Noise_Distance", 1.0);
        double noiseChance = config.getDouble("Explosions.Spherical.Noise_Chance", 0.10);

        // Loops through a cuboid region between pos1 and pos2
        // effectively looping through every single block inside
        // of a square
        for (int x = pos1.getBlockX(); x < pos2.getBlockX(); x++) {
            for (int y = pos1.getBlockY(); y < pos2.getBlockY(); y++) {
                for (int z = pos1.getBlockZ(); z < pos2.getBlockZ(); z++) {
                    Location loc = new Location(origin.getWorld(), x, y, z);
                    
                    // If the distance between the current iteration
                    // and the origin is less than the radius of the
                    // sphere. This "reshapes" the cube into a sphere
                    double distance = loc.distance(origin);
                    if (distance <= radius) {

                        boolean isNearEdge = radius - distance < noiseDistance;
                        if (isNearEdge && Math.random() < noiseChance) {
                            debug.log(LogLevel.DEBUG, "Skipping block (" + x + ", " + y + ", " + z + ") due to noise.");
                            continue; // outer noise checker
                        }

                        temp.add(loc.getBlock());
                    }
                }
            }
        }
        return temp;
    }
    
    @Nonnull
    @Override
    public Map<LivingEntity, Double> getEntities(@Nonnull Location origin) {
        Map<LivingEntity, Double> temp = new HashMap<>();

        for (LivingEntity entity: origin.getWorld().getLivingEntities()) {
            double distance = entity.getLocation().distance(origin);

            if (distance > radius) continue;

            temp.put(entity, distance / radius);
        }

        return temp;

    }

    @Override
    public String toString() {
        return "SphericalExplosion{" +
                "radius=" + radius +
                '}';
    }
}
