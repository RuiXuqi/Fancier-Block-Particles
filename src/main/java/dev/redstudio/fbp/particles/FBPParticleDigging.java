package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.keys.KeyBindings;
import dev.redstudio.fbp.models.ModelHelper;
import dev.redstudio.fbp.renderer.CubeBatchRenderer;
import dev.redstudio.fbp.renderer.RenderType;
import dev.redstudio.fbp.renderer.color.ColorUtil;
import dev.redstudio.fbp.renderer.light.LightUtil;
import dev.redstudio.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.redcore.utils.MathUtil;
import io.redstudioragnarok.redcore.vectors.Vector3F;
import net.jafama.FastMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import javax.annotation.Nullable;
import java.util.List;

import static dev.redstudio.fbp.FBP.MC;

public class FBPParticleDigging extends ParticleDigging {

    private final IBlockState blockState;

    private final float prevGravity;

    private final double startY;
    private final double scaleAlpha;
    private double prevParticleScale;
    private double prevParticleAlpha;
    private double prevMotionX;
    private double prevMotionZ;
    private double endMult = 0.75;

    private boolean modeDebounce;
    private boolean wasFrozen;
    private final boolean destroyed;

    private final EnumFacing facing;

    private final Vector3F rot;
    private final Vector3F prevRot;
    private Vector3F rotStep;

    private AxisAlignedBB boundingBox;

    static Entity dummyEntity = new Entity(null) {
        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {
            // TODO Auto-generated method stub?
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {
            // TODO Auto-generated method stub?
        }

        @Override
        protected void entityInit() {
            // TODO Auto-generated method stub?
        }
    };

    protected FBPParticleDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale, float R, float G, float B, IBlockState state, @Nullable EnumFacing facing, @Nullable TextureAtlasSprite texture) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

        particleRed = R;
        particleGreen = G;
        particleBlue = B;

        rot = new Vector3F();
        prevRot = new Vector3F();

        this.facing = facing;

        createRotationMatrix();

        sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

        if (scale > -1)
            particleScale = scale;

        if (scale < -1) {
            if (facing != null) {
                if (facing == EnumFacing.UP && FBP.smartBreaking) {
                    motionX *= 1.5;
                    motionY *= 0.1;
                    motionZ *= 1.5;

                    double particleSpeed = FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ);

                    float x = MathUtil.addOrSubtractBasedOnSign((float) cameraViewDir.x, 0.01F);
                    float z = MathUtil.addOrSubtractBasedOnSign((float) cameraViewDir.z, 0.01F);

                    motionX = x * particleSpeed;
                    motionZ = z * particleSpeed;
                }
            }
        }

        if (modeDebounce == !FBP.randomRotation) {
            rot.zero();
            calculateYAngle();
        }

        blockState = state;

        Block block = state.getBlock();

        particleGravity = block.blockParticleGravity * FBP.gravityMult;

        particleScale = FBP.scaleMult * (FBP.randomizedScale ? particleScale : 1);
        particleMaxAge = (int) FBP.RANDOM.nextDouble(FBP.minAge, FBP.maxAge + 0.5);

        scaleAlpha = particleScale * 0.82;

        destroyed = facing == null;

        if (texture == null && ModelHelper.isModelValid(state)) {
            BlockModelShapes blockModelShapes = MC.getBlockRendererDispatcher().getBlockModelShapes();

            // GET THE TEXTURE OF THE BLOCK FACE
            if (!destroyed) {
                try {
                    List<BakedQuad> quads = blockModelShapes.getModelForState(state).getQuads(state, facing, 0);

                    if (!quads.isEmpty())
                        particleTexture = quads.get(0).getSprite();
                } catch (Exception e) {
                    // Todo: (Debug Mode) This should count to the problem counter and should output a stack trace
                }
            }

            if (particleTexture == null || particleTexture.getIconName().equals("missingno"))
                setParticleTexture(blockModelShapes.getTexture(state));
        } else
            particleTexture = texture;

        if (FBP.randomFadingSpeed)
            endMult = MathUtil.clampMaxFirst((float) FBP.RANDOM.nextDouble(0.5, 0.9), 0.55F, 0.8F);

        prevGravity = particleGravity;

        startY = posY;

        multipleParticleScaleBy(1);
    }

    @Override
    public Particle multipleParticleScaleBy(float scale) {
        Particle particle = super.multipleParticleScaleBy(scale);

        float newScale = particleScale / 10;

        if (destroyed)
            posY = prevPosY = startY - newScale;

        setBoundingBox(new AxisAlignedBB(posX - newScale, posY, posZ - newScale, posX + newScale, posY + 2 * newScale, posZ + newScale));

        return particle;
    }

    public Particle MultiplyVelocity(float multiplier) {
        motionX *= multiplier;
        motionY = (motionY - 0.1) * (multiplier / 2) + 0.1;
        motionZ *= multiplier;
        return this;
    }

    @Override
    protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
        if (blockState.getBlock() == Blocks.GRASS && facing != EnumFacing.UP)
            return;

        int i = MC.getBlockColors().colorMultiplier(blockState, world, p_187154_1_, 0);
        particleRed *= (i >> 16 & 255) / 255.0;
        particleGreen *= (i >> 8 & 255) / 255.0;
        particleBlue *= (i & 255) / 255.0;
    }

    @Override
    public FBPParticleDigging init() {
        multiplyColor(new BlockPos(posX, posY, posZ));
        return this;
    }

    @Override
    public FBPParticleDigging setBlockPos(BlockPos pos) {
        multiplyColor(pos);
        return this;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        if (KeyBindings.KILL_PARTICLES.isPressed())
            setExpired();

        boolean allowedToMove = MathUtil.absolute((float) motionX) > 0.0001 || MathUtil.absolute((float) motionZ) > 0.0001;

        if (!FBP.frozen && FBP.bounceOffWalls && !MC.isGamePaused() && particleAge > 0) {
            if (!wasFrozen && allowedToMove) {
                boolean xCollided = prevPosX == posX;
                boolean zCollided = prevPosZ == posZ;

                if (xCollided)
                    motionX = -prevMotionX * 0.625;
                if (zCollided)
                    motionZ = -prevMotionZ * 0.625;

                if (!FBP.randomRotation && (xCollided || zCollided))
                    calculateYAngle();
            } else
                wasFrozen = false;
        }
        if (FBP.frozen && FBP.bounceOffWalls && !wasFrozen)
            wasFrozen = true;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        prevRot.copy(rot);

        prevParticleAlpha = particleAlpha;
        prevParticleScale = particleScale;

        if (!MC.isGamePaused() && (!FBP.frozen)) {
            if (!FBP.randomRotation) {
                if (!modeDebounce) {
                    modeDebounce = true;

                    rot.z = 0;

                    calculateYAngle();
                }

                if (allowedToMove) {
                    float x = MathUtil.absolute(rotStep.x * getMult());

                    if (motionX > 0) {
                        if (motionZ > 0)
                            rot.x -= x;
                        else if (motionZ < 0)
                            rot.x += x;
                    } else if (motionX < 0) {
                        if (motionZ < 0)
                            rot.x += x;
                        else if (motionZ > 0) {
                            rot.x -= x;
                        }
                    }
                }
            } else {
                if (modeDebounce) {
                    modeDebounce = false;

                    rot.z = (float) FBP.RANDOM.nextDouble(30, 400);
                }

                if (allowedToMove) {
                    Vector3F newVector = new Vector3F(rotStep);
                    newVector.scale(getMult());
                    rot.add(newVector);
                }
            }

            if (!FBP.infiniteDuration)
                particleAge++;

            if (particleAge >= particleMaxAge) {
                particleScale *= 0.88 * endMult;

                if (particleAlpha > 0.01 && particleScale <= scaleAlpha)
                    particleAlpha *= 0.68 * endMult;

                if (particleAlpha <= 0.01)
                    setExpired();
            }

            if (!onGround)
                motionY -= 0.04 * particleGravity;

            move(motionX, motionY, motionZ);

            if (onGround) {
                rot.x = (float) FastMath.round(rot.x / 90) * 90;
                rot.z = (float) FastMath.round(rot.z / 90) * 90;
            }

            if (MathUtil.absolute((float) motionX) > 0.00001)
                prevMotionX = motionX;
            if (MathUtil.absolute((float) motionZ) > 0.00001)
                prevMotionZ = motionZ;

            if (allowedToMove) {
                motionX *= 0.98;
                motionZ *= 0.98;
            }

            motionY *= 0.98;

            // PHYSICS
            if (FBP.entityCollision) {
                List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getBoundingBox());

                for (Entity entityIn : list) {
                    if (!entityIn.noClip) {
                        float posX = (float) (this.posX - entityIn.posX);
                        float posZ = (float) (this.posZ - entityIn.posZ);
                        float posMax = MathUtil.absoluteMax(posX, posZ);

                        if (posMax >= 0.0099) {
                            posMax = (float) FastMath.sqrtQuick(posMax);
                            posX /= posMax;
                            posZ /= posMax;

                            float f3 = 1 / posMax;

                            if (f3 > 1)
                                f3 = 1;

                            motionX += posX * f3 / 20;
                            motionZ += posZ * f3 / 20;

                            if (!FBP.randomRotation)
                                calculateYAngle();
                            if (!FBP.frozen)
                                onGround = false;
                        }
                    }
                }
            }

            if (FBP.waterPhysics) {
                if (isInWater()) {
                    handleWaterMovement();

                    if (FBP.floatingMaterials.contains(blockState.getMaterial())) {
                        motionY = 0.11 + (particleScale / 1.25) * 0.02;
                    } else {
                        motionX *= 0.93;
                        motionZ *= 0.93;
                        particleGravity = 0.35F;

                        motionY *= 0.85;
                    }

                    if (!FBP.randomRotation)
                        calculateYAngle();

                    if (onGround)
                        onGround = false;
                } else {
                    particleGravity = prevGravity;
                }
            }

            if (onGround) {
                if (FBP.lowTraction) {
                    motionX *= 0.93;
                    motionZ *= 0.93;
                } else {
                    motionX *= 0.66;
                    motionZ *= 0.66;
                }
            }
        }
    }

    public boolean isInWater() {
        double scale = particleScale / 20;

        int minX = (int) FastMath.floor(posX - scale);
        int maxX = (int) FastMath.ceil(posX + scale);

        int minY = (int) FastMath.floor(posY - scale);
        int maxY = (int) FastMath.ceil(posY + scale);

        int minZ = (int) FastMath.floor(posZ - scale);
        int maxZ = (int) FastMath.ceil(posZ + scale);

        if (world.isAreaLoaded(new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ), true)) {
            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        IBlockState block = world.getBlockState(new BlockPos(x, y, z));

                        if (block.getMaterial() == Material.WATER) {
                            double floatLine = (float) (y + 1) - BlockLiquid.getLiquidHeightPercent(block.getValue(BlockLiquid.LEVEL));

                            if (posY <= floatLine)
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void handleWaterMovement() {
        dummyEntity.motionX = motionX;
        dummyEntity.motionY = motionY;
        dummyEntity.motionZ = motionZ;

        if (world.handleMaterialAcceleration(getBoundingBox().expand(0, -0.4, 0).contract(0.001, 0.001, 0.001), Material.WATER, dummyEntity)) {

            motionX = dummyEntity.motionX;
            motionY = dummyEntity.motionY;
            motionZ = dummyEntity.motionZ;
        }
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

        // RESET
        resetPositionToBB();
        onGround = y != Y && Y < 0;

        if (!FBP.lowTraction && !FBP.bounceOffWalls) {
            if (x != X)
                motionX *= 0.69;
            if (z != Z)
                motionZ *= 0.69;
        }
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

        y += scale;

        Vector3F smoothRot = new Vector3F(0, 0, 0);

        if (FBP.rotationMult > 0) {
            smoothRot.y = rot.y;
            smoothRot.z = rot.z;

            if (!FBP.randomRotation)
                smoothRot.x = rot.x;

            // SMOOTH ROTATION
            if (!FBP.frozen) {
                Vector3F vector = new Vector3F();
                vector.lerp(prevRot, partialTicks, rot);

                if (FBP.randomRotation) {
                    smoothRot.y = vector.y;
                    smoothRot.z = vector.z;
                } else {
                    smoothRot.x = vector.x;
                }
            }
        }

        CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE_ITEM_LIGHTING, x, y, z, smoothRot.x, smoothRot.y, smoothRot.z, scale, scale, scale,
                TextureUtil.particleTexCoordProvider(particleTexture, particleTextureJitterX, particleTextureJitterY, particleTextureIndexX, particleTextureIndexY),
                ColorUtil.uniformColorProvider(particleRed, particleGreen, particleBlue, alpha),
                LightUtil.uniformLightCoordProvider(brightness));
    }

    private void createRotationMatrix() {
        double rx0 = FBP.RANDOM.nextDouble();
        double ry0 = FBP.RANDOM.nextDouble();
        double rz0 = FBP.RANDOM.nextDouble();

        rotStep = new Vector3F(rx0 > 0.5 ? 1 : -1, ry0 > 0.5 ? 1 : -1, rz0 > 0.5 ? 1 : -1);

        rot.copy(rotStep);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        AxisAlignedBB boundingBox = getBoundingBox();
        float boundingBoxHeight = (float) ((boundingBox.maxY - boundingBox.minY) * 0.66);
        float y = (float) (posY + boundingBoxHeight + 0.01 - particleScale / 10);
        return LightUtil.getCombinedLight((float) posX, y, (float) posZ);
    }

    private void calculateYAngle() {
        float angleSin = (float) FastMath.toDegrees(FastMath.asin(motionX / FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ)));

        if (motionZ > 0)
            rot.y = -angleSin;
        else
            rot.y = angleSin;
    }

    float getMult() {
        return (float) (FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ) * (FBP.randomRotation ? 200 : 500) * FBP.rotationMult);
    }
}
