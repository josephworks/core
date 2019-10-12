package com.envyclient.core.impl.components.clickgui;

import com.envyclient.core.api.component.GuiComponent;
import com.envyclient.core.api.module.type.Category;
import com.envyclient.core.util.render.FontUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.envyclient.core.Envy.Colors.*;
import static com.envyclient.core.Envy.Managers.CLICK_GUI;
import static com.envyclient.core.Envy.Managers.MODULE;

public class ComponentPanel extends GuiComponent {

    private boolean open;

    // dragging the panels
    private double dragX;
    private double dragY;
    private boolean moving;

    public ComponentPanel(Category category, double x, double y, int width) {
        super(category.getName(), x, y, width, 0);

        int moduleButtonHeight = 18;

        AtomicInteger modulesOffset = new AtomicInteger(2);
        AtomicInteger mHeight = new AtomicInteger(0);
        MODULE.getModules(category).forEach(module ->
                {
                    children.add(
                            new ComponentModule(
                                    this,
                                    module,
                                    moduleButtonHeight,
                                    mHeight.getAndSet(moduleButtonHeight)
                            )
                    );
                    modulesOffset.addAndGet((moduleButtonHeight + 2));
                }
        );

        height = 25 + modulesOffset.get();
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {

        // duplicate hover check
        hovered = mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);

        if (moving) {
            setPosition(mouseX - dragX, mouseY - dragY);
        }

        // green bar at the top of the panel
        Gui.drawRect(x, y, x + width, y - 2, MAIN);

        // tittle background
        Gui.drawRect(x, y, x + width, y + 25, SECONDARY);

        // title of the panel
        float scale = 2;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        FontUtils.drawStringWithShadow(name, (x + 5) / scale, (y + 5) / scale, -1);
        GlStateManager.scale(1, 1, 1);
        GlStateManager.popMatrix();

        if (open) {

            AtomicInteger yOffset = new AtomicInteger(0);
            getChildren().stream().filter(guiComponent -> guiComponent instanceof ComponentModule)
                    .map(ComponentModule.class::cast)
                    .forEach(module -> module.setYOffset(yOffset.getAndAdd(module.getHeight() + 2)));

            // modules list background
            Gui.drawRect(x, y + 25, x + width, y + 25 + yOffset.get() + 4, SECONDARY_BRIGHTER);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {

        List<GuiComponent> componentsList = CLICK_GUI.getClickGUI().getComponentList();
        if (componentsList.indexOf(this) != componentsList.size() - 1 && componentsList.get(componentsList.size() - 1).isHovered()) {
            return;
        }

        // custom hover check for the title part
        if (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + 25)) {
            switch (mouseButton) {
                case 1: {
                    if (open) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("tile.piston.in"), 1));
                        open = false;
                    } else {
                        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("tile.piston.out"), 1));
                        open = true;
                    }
                    break;
                }
                case 0: {
                    // setting the clicked panel as the top panel
                    componentsList.remove(this);
                    componentsList.add(this);

                    moving = true;
                    dragX = mouseX - x;
                    dragY = mouseY - y;
                    break;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        moving = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
