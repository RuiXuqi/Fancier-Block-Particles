package dev.redstudio.fbp.gui;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.elements.Button;
import dev.redstudio.fbp.gui.elements.*;
import dev.redstudio.fbp.gui.pages.Page0;
import dev.redstudio.fbp.gui.pages.PageExperiments;
import dev.redstudio.fbp.gui.pages.PageSettings;
import dev.redstudio.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.redcore.utils.MathUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import static dev.redstudio.fbp.ProjectConstants.NAME;
import static dev.redstudio.fbp.ProjectConstants.VERSION;
import static dev.redstudio.fbp.gui.GuiConfirmation.Action.DefaultConfig;
import static dev.redstudio.fbp.gui.elements.Button.ButtonSize.large;
import static dev.redstudio.fbp.gui.elements.Button.ButtonSize.medium;
import static dev.redstudio.fbp.gui.elements.Button.ButtonSize.small;

public abstract class BasePage extends GuiBase {

    protected boolean writeConfig;
    protected boolean isSettings, isExperiments;
    private boolean containSliders;

    protected static int middleX;
    protected static int y;

    private long lastTime;

    private float hoverBoxY;
    private float targetHoverBoxY;

    protected static final String descriptionFallBack = I18n.format("menu.noDescriptionFound");
    protected static String description = "";

    private GuiScreen previousPage, nextPage;

    public void initPage(final GuiScreen previousPage, final GuiScreen nextPage) {
        middleX = width / 2;
        y = height / 5 + 148;

        addButton(0, middleX - 100, y, medium, I18n.format("menu.defaults"));
        addButton(-1, middleX + 2, y, medium, I18n.format("menu.reloadConfig"));

        addButton(-2, middleX - 100, y + 22, large, I18n.format("menu.done"));

        buttonList.addAll(Arrays.asList(new ButtonGlobalToggle(-3, width - (isSettings ? 64 : 96), 6), new ButtonIssue(-4, width - (isSettings ? 32 : 64), 6)));

        if (!isSettings)
            buttonList.add(new ButtonSettings(-5, width - 32, 6));

        if (!isExperiments && FBP.experiments)
            buttonList.add(new ButtonExperiments(-8, 4, 6));

        this.previousPage = previousPage;
        this.nextPage = nextPage;

        if (previousPage != null)
            addButton(-6, middleX - 145, y - 50, small, "<<");

        if (nextPage != null)
            addButton(-7, middleX + 125, y - 50, small, ">>");
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiConfirmation(this, DefaultConfig, I18n.format("menu.defaultConfig.confirmation")));
                break;
            case -1:
                ConfigHandler.init();
                break;
            case -2:
                if (!isSettings && !isExperiments) {
                    mc.displayGuiScreen(null);

                    if (FBP.MC.world != null)
                        FBP.MC.entityRenderer.stopUseShader();
                } else
                    mc.displayGuiScreen(new Page0());
                break;
            case -3:
                FBP.setEnabled(!FBP.enabled);
                writeConfig = true;
                break;
            case -4:
                try {
                    Desktop.getDesktop().browse(new URI("https://linkify.cz/FancierBugReport"));
                } catch (Exception exception) {
                    // Todo: (Debug Mode) This should count to the problem counter and should output a stack trace
                }
                break;
            case -5:
                mc.displayGuiScreen(new PageSettings());
                break;
            case -6:
                mc.displayGuiScreen(previousPage);
                break;
            case -7:
                mc.displayGuiScreen(nextPage);
                break;
            case -8:
                mc.displayGuiScreen(new PageExperiments());
                break;
        }

        onActionPerformed(button);
    }

    protected void onActionPerformed(final GuiButton button) {
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        buttonList.forEach(button -> {
            if (button instanceof Slider) {
                Slider slider = (Slider) button;

                if (slider.isMouseOver())
                    targetHoverBoxY = slider.y;

                if (!((MathUtil.round(slider.originalValue, 2)) == (MathUtil.round(slider.value, 2))))
                    writeConfig = true;
            }
        });

        description = updateDescription();

        if (containSliders)
            updateTitles();
    }

    protected abstract String updateDescription();

    protected void updateTitles() {
    }

    protected void updateSliderHoverBox() {
        float step = 0.5F;
        long time = System.currentTimeMillis();

        if (lastTime > 0)
            step = (time - lastTime) / 3F;

        lastTime = time;

        if (hoverBoxY > targetHoverBoxY) {
            if (hoverBoxY - targetHoverBoxY <= step)
                hoverBoxY = targetHoverBoxY;
            else
                hoverBoxY -= step;
        }

        if (hoverBoxY < targetHoverBoxY) {
            if (targetHoverBoxY - hoverBoxY <= step)
                hoverBoxY = targetHoverBoxY;
            else
                hoverBoxY += step;
        }
    }

    @Override
    public void drawScreen(final int mouseXIn, final int mouseYIn, final float partialTicks) {
        drawBackground(mouseXIn, mouseYIn);

        if (!FBP.enabled)
            drawCenteredString("§L= " + I18n.format("menu.disabled") + " =", GuiUtils.RED, middleX, y - 193);

        drawCenteredString("§L= " + NAME + " =", "#FFAA00", middleX, y - 183);

        if (isSettings)
            drawCenteredString("§L= " + I18n.format("menu.settings") + " =", GuiUtils.GREEN, middleX, y - 173);
        else if (isExperiments)
            drawCenteredString("§L= " + I18n.format("menu.experiments") + " =", GuiUtils.GREEN, middleX, y - 173);
        else
            drawCenteredString("§L= " + VERSION + " =", GuiUtils.GREEN, middleX, y - 173);

        if (targetHoverBoxY > 0)
            updateSliderHoverBox();

        for (GuiButton button : super.buttonList) {
            if (button.id <= 0)
                continue;

            if (button.isMouseOver()) {
                if (button instanceof Slider)
                    GuiUtils.drawRectangle(middleX - 102, hoverBoxY + 2, 204, 16, new Color(200, 200, 200, 35));

                drawCenteredString(description, button.enabled ? GuiUtils.WHITE : GuiUtils.GREY, width / 2, height / 5 + 131);

                break;
            }
        }

        super.drawScreen(mouseXIn, mouseYIn, partialTicks);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0) {
            for (GuiButton guibutton : super.buttonList) {
                if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                    if (!guibutton.isMouseOver())
                        return;

                    actionPerformed(guibutton);
                }
            }
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);

            if (FBP.MC.world != null)
                FBP.MC.entityRenderer.stopUseShader();

            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        final int scrollAmount = Mouse.getEventDWheel();

        if (scrollAmount != 0) {
            if (scrollAmount > 0) {
                if (previousPage != null)
                    mc.displayGuiScreen(previousPage);
            } else if (nextPage != null)
                mc.displayGuiScreen(nextPage);
        }
    }

    @Override
    public void onGuiClosed() {
        if (writeConfig)
            ConfigHandler.writeMainConfig();
    }

    protected Button addButton(final int id, final int x, final int y, final Button.ButtonSize size, final String text) {
        Button button = new Button(id, x, y, size, text, false, false);
        buttonList.add(button);

        return button;
    }

    protected void addButton(final int id, final String text, final Boolean toggle, final Boolean toggleButton, final Boolean... disabled) {
        buttonList.add(new Button(id, middleX - 100, calculatePosition(id), large, text, toggleButton, toggle, disabled.length >= 1));
    }

    protected Slider addSlider(final int id, final float minValue, final float inputValue, final float maxValue, final Boolean... disabled) {
        Slider slider = new Slider(id, middleX - 100, calculatePosition(id), minValue, inputValue, maxValue, disabled.length >= 1);
        buttonList.add(slider);

        containSliders = true;

        if (hoverBoxY == 0) {
            hoverBoxY = slider.y;
            targetHoverBoxY = slider.y;
        }

        return slider;
    }

    private int calculatePosition(final int id) {
        final int evenButtonSpacing = 26;
        final int oddButtonSpacing = 21;

        int totalSpacing = 0;

        for (int i = 1; i < id; i++) {
            totalSpacing += (i % 2 == 0) ? evenButtonSpacing : oddButtonSpacing;
        }

        return height / 5 - 6 + totalSpacing;
    }
}
