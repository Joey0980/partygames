package com.devclub.partygames.entity.client;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.custom.ThrownDartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ThrownDartRenderer extends EntityRenderer<ThrownDartEntity> {
    private ThrownDartModel model;

    public ThrownDartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownDartModel(context.bakeLayer(ThrownDartModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownDartEntity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(
            bufferSource, this.model.renderType(this.getTextureLocation(p_entity)),false, false);
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownDartEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PartyGames.MOD_ID, "textures/entity/dart/dart_item.png");
    }

}
