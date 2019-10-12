package com.envyclient.core.impl.components.clickgui;

import com.envyclient.core.Envy;
import com.envyclient.core.api.component.GuiComponent;
import com.envyclient.core.api.module.Module;
import com.envyclient.core.util.PlayerUtils;
import com.envyclient.core.util.render.FontUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.envyclient.core.Envy.Colors.MAIN;
import static com.envyclient.core.Envy.Colors.SECONDARY;
import static com.envyclient.core.Envy.Managers.CLICK_GUI;

public class ComponentModule extends GuiComponent {

    private ComponentPanel parent;
    private Module module;
    private boolean expanded;
    private int yOffset;
    private int xShrink;
    private int yMargin;

    private List<ComponentValue> valueList;

    ComponentModule(ComponentPanel parent, Module module, int height, int yOffset) {
        super(module.getName(), 0, 0, parent.getWidth() - 5, height);
        this.parent = parent;
        this.module = module;
        this.expanded = false;
        this.yOffset = yOffset;
        this.xShrink = 5;
        this.yMargin = 2;
        this.valueList = new ArrayList<>();

        AtomicInteger index = new AtomicInteger(1);
        Envy.Managers.SETTING.getSettings(module)
                .forEach(setting -> valueList.add(
                        new ComponentValue(setting, ComponentModule.this, width - xShrink, height, index.getAndAdd(1))
                        )
                );
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {

        setPosition(parent.getX(), (parent.getY() + 25 + yMargin) + yOffset);

        // extra hover check so you can't toggle modules from to panels at once
        List<GuiComponent> componentsList = CLICK_GUI.getClickGUI().getComponentList();
        if (componentsList.indexOf(parent) != componentsList.size() - 1 && componentsList.get(componentsList.size() - 1).isHovered()) {
            hovered = false;
        }

        // drawing the background
        Gui.drawRect(x + 5, y, x + width, y + height, hovered ?
                new Color(37, 37, 37, 195).brighter().getRGB() :
                module.isToggled() ? MAIN : SECONDARY);

        // drawing the module name
        FontUtils.drawTotalCenteredStringWithShadow(name, x + (width / 2), y + (height / 2), -1);

        if (expanded) {
            valueList.forEach(componentValue -> componentValue.drawScreen(mouseX, mouseY, partialTicks));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {

        // toggle the module and play the button sound
        if (hovered) {
            if (mouseButton == 0) {
                module.toggle();
                PlayerUtils.playButtonSound();
            } else if (mouseButton == 1 && !valueList.isEmpty()) {
                expanded = !expanded;
                PlayerUtils.playButtonSound();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return super.getHeight() + (expanded ? valueList.size() * (height + yMargin) : 0);
    }

    int getXShrink() {
        return xShrink;
    }

    void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

}
