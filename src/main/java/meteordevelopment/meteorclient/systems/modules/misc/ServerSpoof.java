/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.CustomPayloadC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.text.RunnableClickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ServerSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> resourcePack = sgGeneral.add(new BoolSetting.Builder()
        .name("resource-pack")
        .description("Spoof accepting server resource pack.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> blockChannels = sgGeneral.add(new BoolSetting.Builder()
        .name("block-channels")
        .description("Whether or not to block some channels.")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<String>> channels = sgGeneral.add(new StringListSetting.Builder()
        .name("channels")
        .description("If the channel contains the keyword, this outgoing channel will be blocked.")
        .defaultValue("fabric", "minecraft:register")
        .visible(blockChannels::get)
        .build()
    );

    public ServerSpoof() {
        super(Categories.Misc, "server-spoof", "Spoof client brand, resource pack and channels.");

        runInMainMenu = true;
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!isActive() || !(event.packet instanceof CustomPayloadC2SPacket)) return;
        CustomPayloadC2SPacketAccessor packet = (CustomPayloadC2SPacketAccessor) event.packet;
        Identifier id = packet.getChannel();

        if (blockChannels.get()) {
            for (String channel : channels.get()) {
                if (StringUtils.containsIgnoreCase(id.toString(), channel)) {
                    event.cancel();
                    return;
                }
            }
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!isActive()) return;

        if (resourcePack.get()) {
            if (!(event.packet instanceof ResourcePackSendS2CPacket packet)) return;
            event.cancel();

            MutableText msg = new LiteralText("");
            MutableText link = new LiteralText("[Download]");
            link.setStyle(link.getStyle()
                .withColor(Formatting.BLUE)
                .withUnderline(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, packet.getURL()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to download")))
            );

            MutableText acceptance = new LiteralText("[Spoof Acceptance]");
            acceptance.setStyle(acceptance.getStyle()
                .withColor(Formatting.DARK_GREEN)
                .withUnderline(true)
                .withClickEvent(new RunnableClickEvent(() -> {
                    event.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
                    event.connection.send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                }))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to spoof accepting the recourse pack.")))
            );

            msg.append(link).append(" ");
            msg.append(acceptance).append(".");
            info(msg);
        }
    }
}
