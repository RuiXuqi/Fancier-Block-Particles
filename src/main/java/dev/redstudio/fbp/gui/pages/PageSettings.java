package dev.redstudio.fbp.gui.pages;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.BasePage;
import dev.redstudio.fbp.gui.GuiConfirmation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

import static dev.redstudio.fbp.gui.GuiConfirmation.Action.EnableDebug;
import static dev.redstudio.fbp.gui.GuiConfirmation.Action.EnableExperiments;

public class PageSettings extends BasePage {

    @Override
    public void initGui() {
        isSettings = true;
        super.initPage(null, null);

        addButton(1, I18n.format("menu.settings.experiments.title"), FBP.experiments, true);
        addButton(2, I18n.format("menu.settings.debug.title"), FBP.debug, true);

        super.updateScreen();
    }

    @Override
    protected void onActionPerformed(final GuiButton button) {
        switch (button.id) {
            case 1:
                if (FBP.experiments)
                    FBP.experiments = false;
                else
                    mc.displayGuiScreen(new GuiConfirmation(this, EnableExperiments, I18n.format("menu.experiments.confirmation")));

                writeConfig = true;
                break;
            case 2:
                if (FBP.debug)
                    FBP.debug = false;
                else
                    mc.displayGuiScreen(new GuiConfirmation(this, EnableDebug, I18n.format("menu.debug.confirmation")));

                FBP.updateDebugHandler();
                writeConfig = true;
                break;
        }
    }

    protected String updateDescription() {
        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                switch (button.id) {
                    case 1:
                        return I18n.format("menu.settings.experiments.description");
                    case 2:
                        return I18n.format("menu.settings.debug.description");
                }
            }
        }

        return descriptionFallBack;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(new Page0());
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }
}
