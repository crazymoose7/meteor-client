/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.util.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

import java.util.List;

public class ScreenHandlerSettingScreen extends LeftRightListSettingScreen<ScreenHandlerType<?>> {
    public ScreenHandlerSettingScreen(GuiTheme theme, Setting<List<ScreenHandlerType<?>>> setting) {
        super(theme, "Select Screen Handlers", setting, setting.get(), Registry.SCREEN_HANDLER);
    }

    @Override
    protected WWidget getValueWidget(ScreenHandlerType<?> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(ScreenHandlerType<?> type) {
        return Registry.SCREEN_HANDLER.getId(type).toString();
    }
}
