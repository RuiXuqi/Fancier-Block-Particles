package dev.redstudio.fbp.renderer.light;

import net.minecraft.util.EnumFacing;

public interface ILightCoordProvider {

    int getLightCoord(EnumFacing facing);

}
