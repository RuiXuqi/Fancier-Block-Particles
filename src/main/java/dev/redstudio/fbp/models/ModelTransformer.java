package dev.redstudio.fbp.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public final class ModelTransformer {

    public static void transform(IBakedModel model, IBlockState state, long rand, IVertexTransformer transformer) {
        try {
            DummyBakedModel out = new DummyBakedModel(model);

            for (int i = 0; i <= 6; i++) {
                EnumFacing side = (i == 6 ? null : EnumFacing.VALUES[i]);

                for (BakedQuad quad : model.getQuads(state, side, rand)) {
                    out.addQuad(side, transform(quad, transformer));
                }
            }

        } catch (Throwable t) {
            // Todo: (Debug Mode) This should count to the problem counter and should output a stack trace
        }
    }

    private static BakedQuad transform(BakedQuad quad, IVertexTransformer transformer) {
        VertexFormat format = quad.getFormat();
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        LightUtil.putBakedQuad(new VertexTransformerWrapper(builder, quad, transformer), quad);
        return builder.build();
    }

    private static final class VertexTransformerWrapper implements IVertexConsumer {
        private final IVertexConsumer parent;
        private final BakedQuad parentQuad;
        private final VertexFormat format;
        private final IVertexTransformer transformer;

        public VertexTransformerWrapper(IVertexConsumer parent, BakedQuad parentQuad, IVertexTransformer transformer) {
            this.parent = parent;
            this.parentQuad = parentQuad;
            format = parent.getVertexFormat();
            this.transformer = transformer;
        }

        @Override
        public VertexFormat getVertexFormat() {
            return format;
        }

        @Override
        public void setQuadTint(int tint) {
            parent.setQuadTint(tint);
        }

        @Override
        public void setQuadOrientation(EnumFacing orientation) {
            parent.setQuadOrientation(orientation);
        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {
            parent.setApplyDiffuseLighting(diffuse);
        }

        @Override
        public void setTexture(TextureAtlasSprite texture) {
            parent.setTexture(texture);
        }

        @Override
        public void put(int elementId, float... data) {
            VertexFormatElement element = format.getElement(elementId);
            parent.put(elementId, transformer.transform(parentQuad, element, data));
        }
    }

    public interface IVertexTransformer {
        float[] transform(BakedQuad quad, VertexFormatElement element, float... data);
    }
}
