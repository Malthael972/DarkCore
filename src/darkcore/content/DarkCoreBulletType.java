package darkcore.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class DarkCoreBulletType extends BulletType {

    static final Rand rand = new Rand();

    public int maxHops = 6;
    public float firstRange = 240f;
    public float hopRange = 90f;
    public float segLength = 8f;
    public float jitter = 5f;

    // Draws a jagged lightning arc between two points.
    public Effect arcEffect = new Effect(18f, 500f, e -> {
        if (!(e.data instanceof Vec2 end)) return;

        float dst = Mathf.dst(e.x, e.y, end.x, end.y);
        if (dst < 1f) return;

        float nx = (end.x - e.x) / dst;
        float ny = (end.y - e.y) / dst;

        int links = Mathf.ceil(dst / segLength);
        float spacing = dst / links;

        rand.setSeed(e.id);

        Draw.color(Color.white, e.color, e.fin());
        Lines.stroke(2.5f * e.fout());
        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        for (int i = 0; i < links; i++) {
            float px, py;

            if (i == links - 1) {
                px = end.x;
                py = end.y;
            } else {
                float along = spacing * (i + 1);

                Tmp.v1.setToRandomDirection(rand).scl(jitter);

                px = e.x + nx * along + Tmp.v1.x;
                py = e.y + ny * along + Tmp.v1.y;
            }

            Lines.linePoint(px, py);
        }

        Lines.endLine();
        Draw.reset();
    });

    public DarkCoreBulletType() {
        speed = 0f;
        lifetime = 1f;

        damage = 100f;
        splashDamage = 30f;
        splashDamageRadius = 40f;

        collides = false;
        collidesTiles = false;
        hittable = false;
        absorbable = false;
        reflectable = false;
        keepVelocity = false;

        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;

        lightningColor = Color.valueOf("8a4bff");

        setDefaults = false;
    }

    @Override
    protected float calculateRange() {
        return firstRange;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        // Track enemies already hit to prevent duplicate hits.
        IntSet hit = new IntSet();

        // Starting position of the first lightning strike.
        float cx = b.x;
        float cy = b.y;

        // Read the intended aim position.
        float aimX = b.aimX < 0f ? b.x : b.aimX;
        float aimY = b.aimY < 0f ? b.y : b.aimY;

        // Limit the first strike to firstRange.
        float dx = aimX - b.x;
        float dy = aimY - b.y;

        float distance = Mathf.dst(b.x, b.y, aimX, aimY);

        if (distance > firstRange) {
            float scale = firstRange / distance;

            aimX = b.x + dx * scale;
            aimY = b.y + dy * scale;
        }

        // FIRST STRIKE:
        // Always draw toward the cursor or maximum-range endpoint.
        arcEffect.at(
                cx,
                cy,
                0f,
                lightningColor,
                new Vec2(aimX, aimY)
        );

        // Search for enemies starting from the first strike's endpoint.
        cx = aimX;
        cy = aimY;

        // SUBSEQUENT HOPS:
        // Find enemies near the previous strike.
        for (int i = 1; i < maxHops; i++) {

            Teamc target = Units.closestTarget(
                    b.team,
                    cx,
                    cy,
                    hopRange,
                    u -> u.checkTarget(collidesAir, collidesGround)
                            && !hit.contains(u.id()),
                    t -> collidesGround
                            && !hit.contains(t.id())
            );

            // Stop if no more targets are in range.
            if (target == null) break;

            hit.add(target.id());

            float tx = target.x();
            float ty = target.y();

            // Draw lightning from the previous endpoint to this enemy.
            arcEffect.at(
                    cx,
                    cy,
                    0f,
                    lightningColor,
                    new Vec2(tx, ty)
            );

            // Apply direct damage on the server.
            if (!net.client() && target instanceof Healthc h) {
                h.damage(b.damage);
            }

            // Apply hit effects and splash damage.
            hit(b, tx, ty, false);

            // The next hop starts at this enemy.
            cx = tx;
            cy = ty;
        }
    }
}