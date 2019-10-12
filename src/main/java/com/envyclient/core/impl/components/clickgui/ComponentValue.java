package com.envyclient.core.impl.components.clickgui;

import com.envyclient.core.Envy;
import com.envyclient.core.api.component.GuiComponent;
import com.envyclient.core.api.setting.Setting;
import com.envyclient.core.util.render.FontUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ComponentValue extends GuiComponent {

    private ComponentModule parent;
    private Setting<?> setting;
    private int index;

    ComponentValue(Setting<?> setting, ComponentModule parent, int width, int height, int index) {
        super(parent.getX(), parent.getY(), width, height);
        this.parent = parent;
        this.setting = setting;
        this.index = index;
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {
        setPosition(parent.getX() + parent.getXShrink() / 2, parent.getY() + (height + 2) * index);

        hovered = mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);

        Gui.drawRect(x, y, x + width, y + height,
                hovered ? new Color(37, 37, 37, 195).brighter().getRGB() : Envy.Colors.SECONDARY
        );

        FontUtils.drawTotalCenteredString(setting.getName(), x + width / 2, y + height / 2, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public int getIndex() {
        return index;
    }

}