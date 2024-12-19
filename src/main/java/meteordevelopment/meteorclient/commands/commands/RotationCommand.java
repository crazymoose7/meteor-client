/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class RotationCommand extends Command {
    public RotationCommand() {
        super("rotation", "Modifies your rotation.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .then(literal("set")
                .then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
                    .executes(context -> {
                        mc.player.pitch = context.getArgument("pitch", Float.class);

                        return SINGLE_SUCCESS;
                    })
                    .then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
                        .executes(context -> {
                            mc.player.pitch = context.getArgument("pitch", Float.class);
                            mc.player.yaw = context.getArgument("yaw", Float.class);

                            return SINGLE_SUCCESS;
                        })
                    )
                )
            )
            .then(literal("add")
                .then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
                    .executes(context -> {
                        float pitch = mc.player.getPitch(mc.getTickDelta()) + context.getArgument("pitch", Float.class);
                        mc.player.pitch = pitch >= 0 ? Math.min(pitch, 90) : Math.max(pitch, -90);

                        return SINGLE_SUCCESS;
                    })
                    .then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
                        .executes(context -> {
                            float pitch = mc.player.getPitch(mc.getTickDelta()) + context.getArgument("pitch", Float.class);
                            mc.player.pitch = pitch >= 0 ? Math.min(pitch, 90) : Math.max(pitch, -90);

                            float yaw = mc.player.getYaw(mc.getTickDelta()) + context.getArgument("yaw", Float.class);
                            mc.player.yaw = MathHelper.wrapDegrees(yaw);

                            return SINGLE_SUCCESS;
                        })
                    )
                )
            );
    }
}
