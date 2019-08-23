package com.envyclient.core.impl.guiscreen;

import com.envyclient.core.Envy;
import com.envyclient.core.api.component.GuiComponent;
import com.envyclient.core.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClickGUI extends GuiScreen {

    private List<GuiComponent> componentList = new CopyOnWriteArrayList<>();

    @Override
    public void initGui() {
        componentList.forEach(GuiComponent::initGui);
    }

    @Override
    public void updateScreen() {
        componentList.forEach(GuiComponent::updateScreen);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution wantedRes = getWantedResolution();

        this.width = wantedRes.getScaledWidth();
        this.height = wantedRes.getScaledHeight();

        GL11.glPushMatrix();
        Vector2f mousePosition = getMousePosition();
        mc.entityRenderer.setupOverlayRendering(wantedRes);
        componentList.forEach(c -> c.drawScreen(mousePosition.getX(), mousePosition.getY(), partialTicks));
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Vector2f mousePosition = getMousePosition();
        componentList.forEach(c -> c.mouseClicked(mousePosition.getX(), mousePosition.getY(), mouseButton));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        Vector2f mousePosition = getMousePosition();
        componentList.forEach(c -> c.mouseClickMove(mousePosition.getX(), mousePosition.getY(), clickedMouseButton, timeSinceLastClick));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        Vector2f mousePosition = getMousePosition();
        componentList.forEach(c -> c.mouseReleased(mousePosition.getX(), mousePosition.getY(), state));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        componentList.forEach(c -> c.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        componentList.forEach(GuiComponent::onGuiClosed);

        // turning off the clickgui module
        Module clickGUI = Envy.Managers.MODULE.getModule(com.envyclient.core.impl.modules.ClickGUI.class);
        if (clickGUI != null && clickGUI.isToggled()) {
            clickGUI.toggle();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private Vector2f getMousePosition() {
        return new Vector2f(
                Mouse.getX() * getWantedResolution().getScaledWidth() / (float) mc.displayWidth,
                getWantedResolution().getScaledHeight() - Mouse.getY() * getWantedResolution().getScaledHeight() / (float) mc.displayHeight - 1
        );
    }

    public ScaledResolution getWantedResolution() {
        return new ScaledResolution(mc, 2);
    }

    public List<GuiComponent> getComponentList() {
        return componentList;
    }
}