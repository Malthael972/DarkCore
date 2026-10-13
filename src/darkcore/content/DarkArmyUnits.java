package darkcore.content;

import arc.graphics.Color;
import arc.math.geom.Rect;
import mindustry.content.*;
import mindustry.gen.Sounds;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.TankUnitType;

public class DarkArmyUnits extends UnitTypes {

    static UnitType darkStell;


    public static void loadContent() {

        darkStell = new TankUnitType("dark-stell"){{
            hitSize = 12f;
            treadPullOffset = 3;
            speed = 0.80f;
            rotateSpeed = 3.5f;
            health = 1000;
            armor = 7f;
            itemCapacity = 0;
            floorMultiplier = 0.95f;
            treadRects = new Rect[]{new Rect(12 - 32f, 7 - 32f, 14, 51)};
            researchCostMultiplier = 0f;

            tankMoveVolume *= 0.32f;
            tankMoveSound = Sounds.tankMoveSmall;

            weapons.add(new Weapon("stell-weapon"){{
                shootSound = Sounds.shootStell;
                layerOffset = 0.0001f;
                reload = 40f;
                shootY = 4.5f;
                recoil = 1f;
                rotate = true;
                rotateSpeed = 2.2f;
                mirror = false;
                x = 0f;
                y = -0.75f;
                heatColor = Color.valueOf("f9350f");
                cooldownTime = 30f;

                bullet = new DarkCoreBulletType(){{
                    speed = 0f;
                    maxHops = 8;
                    firstRange = 200f;
                    hopRange = 90f;
                    lifetime = 1f;

                    damage = 150f;
                    splashDamage = 50f;
                    splashDamageRadius = 50f;

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
                    status = StatusEffects.shocked;

                }};
            }});
        }};


    }
}

