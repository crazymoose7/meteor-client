/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class FakePlayerEntity extends OtherClientPlayerEntity {
    public boolean doNotPush, hideWhenInsideCamera;

    public FakePlayerEntity(PlayerEntity player, String name, float health, boolean copyInv) {
        super(mc.world, new GameProfile(UUID.randomUUID(), name));

        copyPositionAndRotation(player);

        prevYaw = getYaw(mc.getTickDelta());
        prevPitch = getPitch(mc.getTickDelta());
        headYaw = player.headYaw;
        prevHeadYaw = headYaw;
        bodyYaw = player.bodyYaw;
        prevBodyYaw = bodyYaw;

        Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(player.getAttributes());
        setPose(player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) this.inventory.clone(player.inventory);
    }

    public void spawn() {
        this.removed = false;
        mc.world.addEntity(this.getEntityId(), this);
    }

    public void despawn() {
        mc.world.removeEntity(this.getEntityId());
        this.removed = true;
    }
}
