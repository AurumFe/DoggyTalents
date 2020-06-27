package doggytalents.client.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;

public class RenderUtil {

    public static <T extends Entity> void renderLabelWithScale(T entity, EntityRenderer<T> renderer, ITextComponent text, MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float scale, float yChange) {
        RenderUtil.renderLabelWithScale(entity, renderer, text.getFormattedText(), stack, buffer, packedLightIn, scale, yChange);
    }

    public static <T extends Entity> void renderLabelWithScale(T entity, EntityRenderer<T> renderer, String text, MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float scale, float yChange) {
        renderLabelWithScale(!entity.isDiscrete(), renderer.getRenderManager(), text, stack, buffer, packedLightIn, scale, yChange + entity.getHeight() + 0.5F);
    }

    public static void renderLabelWithScale(boolean flag, EntityRendererManager renderManager, ITextComponent text, MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float scale, float yOffset) {
        renderLabelWithScale(flag, renderManager, text.getFormattedText(), stack, buffer, packedLightIn, scale, yOffset);
    }

    public static void renderLabelWithScale(boolean flag, EntityRendererManager renderManager, String text, MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float scale, float yOffset) {
        stack.push();
        stack.translate(0.0D, yOffset, 0.0D);
        stack.rotate(renderManager.getCameraOrientation());
        stack.scale(-scale, -scale, scale);
        Matrix4f matrix4f = stack.getLast().getMatrix();
        float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255.0F) << 24;
        FontRenderer fontrenderer = renderManager.getFontRenderer();
        float f2 = -fontrenderer.getStringWidth(text) / 2F;
        fontrenderer.renderString(text, f2, 0, 553648127, false, matrix4f, buffer, flag, j, packedLightIn);
        if (flag) {
            fontrenderer.renderString(text, f2, 0, -1, false, matrix4f, buffer, false, 0, packedLightIn);
        }

        stack.pop();
    }

    // From net.minecraft.client.gui.AbstractGui
    public static void blit(int x, int y, int zLevel, int width, int height, TextureAtlasSprite sprite) {
        innerBlit(x, x + width, y, y + height, zLevel, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void blit(int x, int y, int width, int height, int textureX, int textureY) {
        blit(x, y, 0, width, height, textureX, textureY, 256, 256);
    }

    public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, float p_blit_3_, float p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_) {
        innerBlit(p_blit_0_, p_blit_0_ + p_blit_5_, p_blit_1_, p_blit_1_ + p_blit_6_, p_blit_2_, p_blit_5_, p_blit_6_, p_blit_3_, p_blit_4_, p_blit_8_, p_blit_7_);
    }

    public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, float p_blit_4_, float p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_) {
        innerBlit(p_blit_0_, p_blit_0_ + p_blit_2_, p_blit_1_, p_blit_1_ + p_blit_3_, 0, p_blit_6_, p_blit_7_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_);
    }

    public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_) {
        blit(p_blit_0_, p_blit_1_, p_blit_4_, p_blit_5_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_);
    }

    public static void innerBlit(int minX, int maxX, int yMin, int yMax, int zLevel, int textureXMin, int textureXMax, float textureYMin, float textureYMax, int textureXScale, int textureYScale) {
        innerBlit(minX, maxX, yMin, yMax, zLevel, textureYMin / textureXScale, (textureYMin + textureXMin) / textureXScale, textureYMax / textureYScale, (textureYMax + textureXMax) / textureYScale);
    }

    public static void innerBlit(int minX, int maxX, int yMin, int yMax, int zLevel, float textureXMin, float textureXMax, float textureYMin, float textureYMax) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(minX, yMax, zLevel).tex(textureXMin, textureYMax).endVertex();
        bufferbuilder.pos(maxX, yMax, zLevel).tex(textureXMax, textureYMax).endVertex();
        bufferbuilder.pos(maxX, yMin, zLevel).tex(textureXMax, textureYMin).endVertex();
        bufferbuilder.pos(minX, yMin, zLevel).tex(textureXMin, textureYMin).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }
}
