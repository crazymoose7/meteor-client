/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.glfw.GLFW.*;

public class GUIMove extends Module {
    public enum Screens {
        GUI,
        Inventory,
        Both
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Screens> screens = sgGeneral.add(new EnumSetting.Builder<Screens>()
        .name("guis")
        .description("Which GUIs to move in.")
        .defaultValue(Screens.Inventory)
        .build()
    );

    private final Setting<Boolean> jump = sgGeneral.add(new BoolSetting.Builder()
        .name("jump")
        .description("Allows you to jump while in GUIs.")
        .defaultValue(true)
        .onChanged(aBoolean -> {
            if (isActive() && !aBoolean) set(mc.options.keyJump, false);
        })
        .build()
    );

    private final Setting<Boolean> sneak = sgGeneral.add(new BoolSetting.Builder()
        .name("sneak")
        .description("Allows you to sneak while in GUIs.")
        .defaultValue(true)
        .onChanged(aBoolean -> {
            if (isActive() && !aBoolean) set(mc.options.keySneak, false);
        })
        .build()
    );

    private final Setting<Boolean> sprint = sgGeneral.add(new BoolSetting.Builder()
        .name("sprint")
        .description("Allows you to sprint while in GUIs.")
        .defaultValue(true)
        .onChanged(aBoolean -> {
            if (isActive() && !aBoolean) set(mc.options.keySprint, false);
        })
        .build()
    );

    private final Setting<Boolean> arrowsRotate = sgGeneral.add(new BoolSetting.Builder()
        .name("arrows-rotate")
        .description("Allows you to use your arrow keys to rotate while in GUIs.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> rotateSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rotate-speed")
        .description("Rotation speed while in GUIs.")
        .defaultValue(4)
        .min(0)
        .build()
    );

    public GUIMove() {
        super(Categories.Movement, "gui-move", "Allows you to perform various actions while in GUIs.");
    }

    @Override
    public void onDeactivate() {
        set(mc.options.keyForward, false);
        set(mc.options.keyBack, false);
        set(mc.options.keyLeft, false);
        set(mc.options.keyRight, false);

        if (jump.get()) set(mc.options.keyJump, false);
        if (sneak.get()) set(mc.options.keySneak, false);
        if (sprint.get()) set(mc.options.keySprint, false);
    }

    public boolean disableSpace() {
        return isActive() && jump.get() && mc.options.keyJump.isDefault();
    }
    public boolean disableArrows() {
        return isActive() && arrowsRotate.get();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (skip()) return;
        if (screens.get() == Screens.GUI && !(mc.currentScreen instanceof WidgetScreen)) return;
        if (screens.get() == Screens.Inventory && mc.currentScreen instanceof WidgetScreen) return;

        set(mc.options.keyForward, Input.isPressed(mc.options.keyForward));
        set(mc.options.keyBack, Input.isPressed(mc.options.keyBack));
        set(mc.options.keyLeft, Input.isPressed(mc.options.keyLeft));
        set(mc.options.keyRight, Input.isPressed(mc.options.keyRight));

        if (jump.get()) set(mc.options.keyJump, Input.isPressed(mc.options.keyJump));
        if (sneak.get()) set(mc.options.keySneak, Input.isPressed(mc.options.keySneak));
        if (sprint.get()) set(mc.options.keySprint, Input.isPressed(mc.options.keySprint));

    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (skip()) return;
        if (screens.get() == Screens.GUI && !(mc.currentScreen instanceof WidgetScreen)) return;
        if (screens.get() == Screens.Inventory && mc.currentScreen instanceof WidgetScreen) return;

        float rotationDelta = Math.min((float) (rotateSpeed.get() * event.frameTime * 20f), 100);

        if (arrowsRotate.get()) {
            float yaw = mc.player.getYaw(mc.getTickDelta());
            float pitch = mc.player.getPitch(mc.getTickDelta());

            if (Input.isKeyPressed(GLFW_KEY_LEFT)) yaw -= rotationDelta;
            if (Input.isKeyPressed(GLFW_KEY_RIGHT)) yaw += rotationDelta;
            if (Input.isKeyPressed(GLFW_KEY_UP)) pitch -= rotationDelta;
            if (Input.isKeyPressed(GLFW_KEY_DOWN)) pitch += rotationDelta;


            pitch = MathHelper.clamp(pitch, -90, 90);

            mc.player.yaw = yaw;
            mc.player.pitch = pitch;
        }
    }

    private void set(KeyBinding bind, boolean pressed) {
        boolean wasPressed = bind.isPressed();
        bind.setPressed(pressed);

        InputUtil.Key key = ((KeyBindingAccessor) bind).getKey();
        if (wasPressed != pressed && key.getCategory() == InputUtil.Type.KEYSYM) {
            MeteorClient.EVENT_BUS.post(KeyEvent.get(key.getCode(), 0, pressed ? KeyAction.Press : KeyAction.Release));
        }
    }

    public boolean skip() {
        return mc.currentScreen == null || (mc.currentScreen instanceof CreativeInventoryScreen && CreativeInventoryScreenAccessor.getSelectedTab() == ItemGroups.getSearchGroup()) || mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof SignEditScreen || mc.currentScreen instanceof AnvilScreen || mc.currentScreen instanceof AbstractCommandBlockScreen || mc.currentScreen instanceof StructureBlockScreen;
    }
}
