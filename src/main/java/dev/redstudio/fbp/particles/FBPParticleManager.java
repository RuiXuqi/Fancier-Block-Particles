package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.handlers.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;

import static dev.redstudio.fbp.FBP.MC;

public class FBPParticleManager extends ParticleManager {

    public FBPParticleManager(World worldIn, TextureManager rendererIn) {
        super(worldIn, rendererIn);
    }

    public void carryOver() {
        if (MC.effectRenderer == this)
            return;

        fxLayers = (MC.effectRenderer).fxLayers;
        particleEmitters = (MC.effectRenderer).particleEmitters;
        queue = (MC.effectRenderer).queue;
    }

    @Override
    public void addEffect(Particle effect) {
        Particle toAdd = effect;

        if (!(toAdd instanceof FBPParticleSnow) && !(toAdd instanceof FBPParticleRain)) {
            IBlockState blockState;
            if (FBP.fancyWeather && toAdd instanceof ParticleRain) {
                effect.setAlphaF(0);
            } else if (toAdd instanceof FBPParticleDigging) {
                blockState = ((ParticleDigging) effect).sourceState;

                if (blockState != null && !(FBP.frozen && !FBP.spawnWhileFrozen)) {
                    if (blockState.getBlock() instanceof BlockLiquid || ConfigHandler.isBlacklisted(blockState.getBlock(), true)) {
                        effect.setExpired();
                        return;
                    }
                }
            } else if (toAdd instanceof ParticleDigging) {
                blockState = ((ParticleDigging) effect).sourceState;

                if (blockState != null && !(FBP.frozen && !FBP.spawnWhileFrozen)) {
                    effect.setExpired();

                    if (!(blockState.getBlock() instanceof BlockLiquid) && !ConfigHandler.isBlacklisted(blockState.getBlock(), true)) {
                        toAdd = new FBPParticleDigging(world, effect.posX, effect.posY - 0.1, effect.posZ, 0, 0, 0, effect.particleScale, toAdd.getRedColorF(), toAdd.getGreenColorF(), toAdd.getBlueColorF(), blockState, null, effect.particleTexture);
                    } else
                        return;
                }
            } else if (MC.gameSettings.particleSetting < 2) {
                if (FBP.fancyFlame && toAdd instanceof ParticleFlame && !(toAdd instanceof FBPParticleFlame)) {
                    toAdd = new FBPParticleFlame(world, effect.posX, effect.posY, effect.posZ, FBP.RANDOM.nextDouble() * 0.25, true);
                    effect.setExpired();
                } else if (FBP.fancySmoke && toAdd instanceof ParticleSmokeNormal && !(toAdd instanceof FBPParticleSmoke)) {
                    ParticleSmokeNormal particle = (ParticleSmokeNormal) effect;

                    toAdd = new FBPParticleSmoke(world, effect.posX, effect.posY, effect.posZ, effect.motionX, effect.motionY, effect.motionZ, effect.particleScale, particle);

                    toAdd.setRBGColorF(effect.getRedColorF() + 0.1f, effect.getGreenColorF() + 0.1f, effect.getBlueColorF() + 0.1f);

                    toAdd.setMaxAge(effect.particleMaxAge);
                }
            }
        }

        if (toAdd != effect)
            effect.setExpired();

        super.addEffect(toAdd);
    }

    @Override
    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        Block block = state.getBlock();

        if (!block.isAir(state, world, pos) && !block.addDestroyEffects(world, pos, this) && block != FBP.DUMMY_BLOCK) {
            state = state.getActualState(world, pos);
            block = state.getBlock();

            TextureAtlasSprite texture = MC.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);

            for (int i = 0; i < FBP.particlesPerAxis; ++i) {
                for (int j = 0; j < FBP.particlesPerAxis; ++j) {
                    for (int k = 0; k < FBP.particlesPerAxis; ++k) {
                        double posX = pos.getX() + ((i + 0.5) / FBP.particlesPerAxis);
                        double posY = pos.getY() + ((j + 0.5) / FBP.particlesPerAxis);
                        double posZ = pos.getZ() + ((k + 0.5) / FBP.particlesPerAxis);

                        if ((!(block instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)) && !ConfigHandler.isBlacklisted(block, true)) {
                            double scale = FBP.RANDOM.nextDouble(0.75, 1);

                            FBPParticleDigging toSpawn = new FBPParticleDigging(world, posX, posY, posZ, posX - pos.getX() - 0.5, -0.001, posZ - pos.getZ() - 0.5, (float) scale, 1, 1, 1, state, null, texture).setBlockPos(pos);

                            addEffect(toSpawn);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock() == FBP.DUMMY_BLOCK)
            return;

        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            int sidePosX = pos.getX();
            int sidePosY = pos.getY();
            int sidePosZ = pos.getZ();
            AxisAlignedBB boundingBox = iblockstate.getBoundingBox(world, pos);

            double hitPosX, hitPosY, hitPosZ;

            RayTraceResult target = MC.objectMouseOver;

            if (target == null || target.hitVec == null)
                target = new RayTraceResult(null, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));

            if (FBP.smartBreaking && (!(iblockstate.getBlock() instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen))) {
                hitPosX = target.hitVec.x + FBP.RANDOM.nextDouble(-0.21, 0.21) * Math.abs(boundingBox.maxX - boundingBox.minX);
                hitPosY = target.hitVec.y + FBP.RANDOM.nextDouble(-0.21, 0.21) * Math.abs(boundingBox.maxY - boundingBox.minY);
                hitPosZ = target.hitVec.z + FBP.RANDOM.nextDouble(-0.21, 0.21) * Math.abs(boundingBox.maxZ - boundingBox.minZ);
            } else {
                hitPosX = sidePosX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX - 0.2) + 0.1 + boundingBox.minX;
                hitPosY = sidePosY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY - 0.2) + 0.1 + boundingBox.minY;
                hitPosZ = sidePosZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ - 0.2) + 0.1 + boundingBox.minZ;
            }

            switch (side) {
                case DOWN:
                    hitPosY = sidePosY + boundingBox.minY - 0.1;
                    break;
                case EAST:
                    hitPosX = sidePosX + boundingBox.maxX + 0.1;
                    break;
                case NORTH:
                    hitPosZ = sidePosZ + boundingBox.minZ - 0.1;
                    break;
                case SOUTH:
                    hitPosZ = sidePosZ + boundingBox.maxZ + 0.1;
                    break;
                case UP:
                    hitPosY = sidePosY + boundingBox.maxY + 0.1;
                    break;
                case WEST:
                    hitPosX = sidePosX + boundingBox.minX - 0.1;
                    break;
                default:
                    break;
            }

            if ((!(iblockstate.getBlock() instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen))) {

                int damage = 0;

                DestroyBlockProgress progress;
                Map<Integer, DestroyBlockProgress> damagedBlocks = (MC.renderGlobal).damagedBlocks;

                if (!damagedBlocks.isEmpty()) {

                    for (DestroyBlockProgress o : damagedBlocks.values()) {
                        progress = o;

                        if (progress.getPosition().equals(pos)) {
                            damage = progress.getPartialBlockDamage();
                            break;
                        }
                    }
                }

                Particle toSpawn;

                if (!ConfigHandler.isBlacklisted(iblockstate.getBlock(), true)) {
                    toSpawn = new FBPParticleDigging(world, hitPosX, hitPosY, hitPosZ, 0, 0, 0, -2, 1, 1, 1, iblockstate, side, null).setBlockPos(pos);

                    if (FBP.smartBreaking) {
                        toSpawn = ((FBPParticleDigging) toSpawn).MultiplyVelocity(side == EnumFacing.UP ? 0.7F : 0.15F);
                        toSpawn = toSpawn.multipleParticleScaleBy(0.325F + (damage / 10f) * 0.5F);
                    } else {
                        toSpawn = ((FBPParticleDigging) toSpawn).MultiplyVelocity(0.2F);
                        toSpawn = toSpawn.multipleParticleScaleBy(0.6F);
                    }

                    addEffect(toSpawn);
                }
            }
        }
    }
}
