package dev.redstudio.fbp.handlers;

import dev.redstudio.fbp.FBP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static dev.redstudio.fbp.FBP.MC;
import static dev.redstudio.fbp.FBP.fancyEffectRenderer;
import static dev.redstudio.fbp.FBP.originalEffectRenderer;
import static dev.redstudio.fbp.ProjectConstants.VERSION;

public class DebugHandler {

    public static final String MIXIN_BOOTER_VERSION = Loader.instance().getIndexedModList().get("mixinbooter").getVersion();
    private static final String LATEST_MIXIN_BOOTER = "8.2";

    @SubscribeEvent
    public static void onDebugList(RenderGameOverlayEvent.Text event) {
        if (MC.gameSettings.showDebugInfo) {
            ArrayList<String> list = event.getLeft();

            list.remove(4);
            list.add(4, "T: " + MC.world.getDebugLoadedEntities());

            if (!list.get(list.size() - 1).equals("")) {
                list.add("");
            }

            list.add(String.format("%s<FBP>%s Fancier Block Particles version is %s, Mixin Booter is %sup to date (%s).", TextFormatting.RED, TextFormatting.RESET, VERSION, MIXIN_BOOTER_VERSION.equals(LATEST_MIXIN_BOOTER) ? "" : "not ", MIXIN_BOOTER_VERSION));
            list.add(String.format("%s<FBP>%s Running on %s", TextFormatting.RED, TextFormatting.RESET, System.getProperty("java.vm.name")));
            list.add(String.format("%s<FBP>%s Running on %s, version %s", TextFormatting.RED, TextFormatting.RESET, System.getProperty("os.name"), System.getProperty("os.version")));
            list.add("");
            list.add(String.format("%s<FBP>%s FBP is %s.", TextFormatting.RED, TextFormatting.RESET, FBP.enabled ? "enabled" : "disabled"));
            list.add("");
            list.add(String.format("%s<FBP>%s (CURRENTLY BROKEN) FBP is managing %s particles", TextFormatting.RED, TextFormatting.RESET, fancyEffectRenderer.getStatistics()));
            list.add(String.format("%s<FBP>%s (CURRENTLY BROKEN) MC is managing %s particles", TextFormatting.RED, TextFormatting.RESET, originalEffectRenderer.getStatistics()));
        }
    }

    // From https://github.com/criscky/OldJavaWarning/blob/1.12.2/src/main/java/net/darkhax/oldjava/OldJavaWarning.java#L116-L132
    private static boolean isJvm64bit() {

        final String[] propertyStrings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (final String property : propertyStrings) {

            final String value = System.getProperty(property);

            if (value != null && value.contains("64")) {

                return true;
            }
        }

        return false;
    }
}
