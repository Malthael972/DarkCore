
package darkcore.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

public class BloodLigtheningBulletType extends ContinuousLaserBulletType {

    // Beam appearance
    public Color bloodColor = Color.valueOf("8f1238");
    public Color lightningColor = Color.valueOf("a64dff");
    public Color coreColor = Color.valueOf("ffb5dc");

    // Thorn appearance
    public float thornSpacing = 18f;
    public float thornLength = 12f;
    public float thornWidth = 1.8f;

    // Lightning appearance
    public float arcSpacing = 27f;
    public float arcLength = 9f;

    public BloodLigtheningBulletType() {
        super();

        // Beam behavior
        damage = 45f;
        length = 220f;
        width = 8f;
        lifetime = 30f;
        damageInterval = 5f;
        continuous = true;
        timescaleDamage = true;

        // Beam colors, from outer glow to bright center
        colors = new Color[]{
                bloodColor.cpy().a(0.35f),
                lightningColor.cpy().a(0.75f),
                coreColor.cpy(),
                Color.white
        };

        // Laser appearance
        fadeTime = 8f;
        lightStroke = 35f;
        strokeFrom = 2.5f;
        strokeTo = 0.5f;
        frontLength = 20f;
        backLength = 6f;

        // Effects and collision behavior
        hitColor = lightningColor;
        shootEffect = mindustry.content.Fx.none;
        smokeEffect = mindustry.content.Fx.none;
        hitEffect = mindustry.content.Fx.hitLancer;
        despawnEffect = mindustry.content.Fx.none;

        // Prevent ordinary projectile movement
        speed = 0f;
        keepVelocity = false;
    }

    @Override
    public void draw(Bullet b) {
        // Draw the normal continuous laser first.
        super.draw(b);

        float beamLength = length;
        float angle = b.rotation();

        float radians = angle * Mathf.degreesToRadians;
        float dirX = Mathf.cos(radians);
        float dirY = Mathf.sin(radians);

        // Perpendicular direction, used to grow thorns
        // from alternating sides of the beam.
        float sideX = -dirY;
        float sideY = dirX;

        Draw.z(Layer.effect);

        // Draw blood thorns and branching lightning along the beam.
        for (float distance = 10f; distance < beamLength; distance += thornSpacing) {
            float wave = Mathf.sin(distance * 0.11f + b.time * 0.12f);
            float side = (Mathf.mod((int)(distance / thornSpacing), 2) == 0) ? 1f : -1f;

            float x = b.x + dirX * distance;
            float y = b.y + dirY * distance;

            float branchX = x + sideX * side * thornLength
                    + dirX * wave * 3f;

            float branchY = y + sideY * side * thornLength
                    + dirY * wave * 3f;

            // Main thorn
            Draw.color(bloodColor);
            Lines.stroke(thornWidth);
            Lines.line(x, y, branchX, branchY);

            // Thorn tip
            float tipX = branchX + dirX * 4f - sideX * side * 4f;
            float tipY = branchY + dirY * 4f - sideY * side * 4f;

            Lines.stroke(thornWidth * 0.7f);
            Lines.line(branchX, branchY, tipX, tipY);

            // Bright lightning branch
            float arcWave = Mathf.sin(distance * 0.19f + b.time * 0.25f);
            float arcX = x + sideX * side * arcLength
                    + dirX * arcWave * 4f;

            float arcY = y + sideY * side * arcLength
                    + dirY * arcWave * 4f;

            Draw.color(lightningColor);
            Lines.stroke(1.2f);
            Lines.line(x, y, arcX, arcY);

            // Smaller secondary branch
            float endX = arcX + dirX * 4f - sideX * side * 3f;
            float endY = arcY + dirY * 4f - sideY * side * 3f;

            Lines.line(arcX, arcY, endX, endY);
        }

        Draw.reset();
    }
}
