package dev.redstudio.fbp.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class DummyBakedModel implements IBakedModel {

    private final List<BakedQuad>[] quads = new List[7];
    private final IBakedModel parent;
    private TextureAtlasSprite particle;

    public DummyBakedModel(IBakedModel parent) {
        this.parent = parent;
        for (int i = 0; i < quads.length; i++) {
            quads[i] = new ArrayList<>();
        }
    }

    public void setParticle(TextureAtlasSprite particle) {
        this.particle = particle;
    }

    public void addQuad(EnumFacing side, BakedQuad quad) {
        quads[side == null ? 6 : side.ordinal()].add(quad);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return quads[side == null ? 6 : side.ordinal()];
    }

    @Override
    public boolean isAmbientOcclusion() {
        return parent == null || parent.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parent == null || parent.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        if (particle != null) {
            return particle;
        } else {
            return parent != null ? parent.getParticleTexture() : null;
        }
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> pair = parent.handlePerspective(cameraTransformType);
        if (pair.getLeft() != parent) {
            return pair;
        } else {
            return ImmutablePair.of(this, pair.getRight());
        }
    }
}
