package com.fanboy.game.manager.physics;

import com.fanboy.category.EnemyCategory;
import com.fanboy.category.ExplodingWeaponCategory;
import com.fanboy.category.LivingCategory;
import com.fanboy.category.NonExplodingWeaponCategory;
import com.fanboy.entity.player.ServerPlayer;

class CollisionProcessor {

    static void jumpOn(Body body1, Body body2) {
        if (body1.getUserData() instanceof ServerPlayer) {
            if (body2.getUserData() instanceof LivingCategory) {
                body1.setVelocity(body1.getVelocity().x, 40);
                if (((LivingCategory) body2.getUserData()).kill()) {
                    body1.getUserData().addKill();
                }
            }
        } else if (body1.getUserData() instanceof EnemyCategory) {
            if (body2.getUserData() instanceof ServerPlayer) {
                ((ServerPlayer) body2.getUserData()).kill();
            }
        }
        processWeapons(body1, body2);
        processWeapons(body2, body1);
    }

    static void jumpedOn(Body body1, Body body2) {
        if (body1.getUserData() instanceof EnemyCategory) {
            if (body2.getUserData() instanceof ServerPlayer) {
                if (((LivingCategory) body1.getUserData()).kill()) {
                    body2.getUserData().addKill();
                }
                body2.setVelocity(body1.getVelocity().x, 40);
            }
        } else if (body1.getUserData() instanceof ServerPlayer) {
            if (body2.getUserData() instanceof EnemyCategory) {
                ((ServerPlayer) body1.getUserData()).kill();
            } else if (body2.getUserData() instanceof ServerPlayer) {
                ((ServerPlayer) body1.getUserData()).kill();
                body2.getUserData().addKill();
            }
        }
        processWeapons(body1, body2);
        processWeapons(body2, body1);
    }

    static void touchLeftAndRight(Body body1, Body body2) {
        if (body1.getUserData() instanceof EnemyCategory) {
            if (body2.getUserData() instanceof ServerPlayer) {
                ((ServerPlayer) body2.getUserData()).kill();
            }
        } else if (body1.getUserData() instanceof ServerPlayer) {
            if (body2.getUserData() instanceof EnemyCategory) {
                ((ServerPlayer) body1.getUserData()).kill();
            }
        }
        processWeapons(body1, body2);
        processWeapons(body2, body1);
    }

    private static void processWeapons(Body body1, Body body2) {
        if (body1.getUserData() instanceof ExplodingWeaponCategory) {
            if (body2.isDynamic()) {
                if (body1.toDestroy) {
                    return;
                }
                ((ExplodingWeaponCategory) body1.getUserData()).explode();
            }
        } else if (body1.getUserData() instanceof NonExplodingWeaponCategory) {
            if (body2.toDestroy)
                return;
            if (body2.getUserData() instanceof LivingCategory) {
                if (((LivingCategory) body2.getUserData()).kill() && body2 != body1.getUserData().body) {
                    ((NonExplodingWeaponCategory) body1.getUserData()).getShooter().addKill();
                }
            }
            body1.getUserData().dispose();
        }
    }
}
