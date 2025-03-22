package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.renderer.CubeBatchRenderer;
import dev.redstudio.fbp.renderer.RenderType;
import dev.redstudio.fbp.renderer.color.ColorUtil;
import dev.redstudio.fbp.renderer.light.LightUtil;
import dev.redstudio.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.redcore.utils.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static dev.redstudio.fbp.FBP.MC;

public class FBPParticleRain extends ParticleDigging {

    float AngleY;

    double particleHeight, prevParticleScale, prevParticleHeight, prevParticleAlpha;
    double scalar = FBP.scaleMult;
    double endMult = 1;

    public FBPParticleRain(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

        sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

        AngleY = (float) (FBP.RANDOM.nextDouble() * 45);

        motionX = xSpeedIn;
        motionY = -ySpeedIn;
        motionZ = zSpeedIn;

        particleGravity = 0.025F;

        particleMaxAge = (int) FBP.RANDOM.nextDouble(50, 70);

        particleAlpha = 0;
        particleScale = 0;

        canCollide = true;

        if (FBP.randomFadingSpeed)
            endMult *= FBP.RANDOM.nextDouble(0.85, 1);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        prevParticleAlpha = particleAlpha;
        prevParticleScale = particleScale;
        prevParticleHeight = particleHeight;

        if (!MC.isGamePaused()) {
            particleAge++;

            if (posY < MC.player.posY - (MC.gameSettings.renderDistanceChunks * 9))
                setExpired();

            if (!onGround) {
                if (particleAge < particleMaxAge) {
                    double max = scalar * 0.5;

                    if (particleScale < max) {
                        if (FBP.randomFadingSpeed)
                            particleScale += 0.05 * endMult;
                        else
                            particleScale += 0.05;

                        if (particleScale > max)
                            particleScale = (float) max;

                        particleHeight = particleScale;
                    }

                    if (particleAlpha < 0.65) {
                        if (FBP.randomFadingSpeed)
                            particleAlpha += 0.085 * endMult;
                        else
                            particleAlpha += 0.085;

                        if (particleAlpha > 0.65)
                            particleAlpha = 0.65F;
                    }
                } else
                    setExpired();
            }

            if (world.getBlockState(new BlockPos(posX, posY, posZ)).getMaterial().isLiquid())
                setExpired();

            motionY -= 0.04 * particleGravity;

            move(motionX, motionY, motionZ);

            motionY *= 1;

            if (onGround) {
                motionX = 0;
                motionY = -0.25;
                motionZ = 0;

                if (particleHeight > 0.075)
                    particleHeight *= 0.725;

                float max = (float) scalar * 4.25F;

                if (particleScale < max) {
                    particleScale += max / 10;

                    if (particleScale > max)
                        particleScale = max;
                }

                if (particleScale >= max / 2) {
                    if (FBP.randomFadingSpeed)
                        particleAlpha *= 0.75 * endMult;
                    else
                        particleAlpha *= 0.75;

                    if (particleAlpha <= 0.001)
                        setExpired();
                }
            }
        }

        particleRed = (float) MC.world.getSkyColor(MC.player, 0).x;
        particleGreen = MathUtil.clampMaxFirst((float) (MC.world.getSkyColor(MC.player, 0).y + 0.25F), 0.25F, 1);
        particleBlue = MathUtil.clampMaxFirst((float) (MC.world.getSkyColor(MC.player, 0).z + 0.5F), 0.5F, 1);

        if (particleGreen > 1)
            particleGreen = 1;
        if (particleBlue > 1)
            particleBlue = 1;
    }

    @Override
    public void move(double x, double y, double z) {
        double X = x;
        double Y = y;
        double Z = z;

        List<AxisAlignedBB> list = world.getCollisionBoxes(null, getBoundingBox().expand(x, y, z));

        for (AxisAlignedBB axisalignedbb : list) {
            y = axisalignedbb.calculateYOffset(getBoundingBox(), y);
        }

        setBoundingBox(getBoundingBox().offset(0, y, 0));

        for (AxisAlignedBB axisalignedbb : list) {
            x = axisalignedbb.calculateXOffset(getBoundingBox(), x);
        }

        setBoundingBox(getBoundingBox().offset(x, 0, 0));

        for (AxisAlignedBB axisalignedbb : list) {
            z = axisalignedbb.calculateZOffset(getBoundingBox(), z);
        }

        setBoundingBox(getBoundingBox().offset(0, 0, z));

        resetPositionToBB();

        onGround = y != Y && Y < 0;

        if (x != X)
            motionX *= 0.69;
        if (z != Z)
            motionZ *= 0.69;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!FBP.enabled && particleMaxAge != 0)
            particleMaxAge = 0;

        float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        int brightness = getBrightnessForRender(partialTicks);

        float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

        float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);
        scale *= 0.1F;
        float height = (float) (prevParticleHeight + (particleHeight - prevParticleHeight) * partialTicks);
        height *= 0.1F;

        y += height;

        CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE_ITEM_LIGHTING, x, y, z, 0.0F, AngleY, 0.0F, scale, height, scale,
                TextureUtil.particleTexCoordProvider(particleTexture, particleTextureJitterX, particleTextureJitterY, particleTextureIndexX, particleTextureIndexY),
                ColorUtil.uniformColorProvider(particleRed, particleGreen, particleBlue, alpha),
                LightUtil.uniformLightCoordProvider(brightness));
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
    }
}
