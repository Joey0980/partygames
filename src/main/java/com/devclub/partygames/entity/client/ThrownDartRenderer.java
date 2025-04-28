package com.devclub.partygames.entity.client;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.custom.ThrownDartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ThrownDartRenderer extends EntityRenderer<ThrownDartEntity> {
    private final ThrownDartModel model;

    public ThrownDartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownDartModel(context.bakeLayer(ThrownDartModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownDartEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Interpolated rotation
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        // Move pivot forward so the tip is at the center of the hitbox
        poseStack.translate(0d, 0d, 0d);

        // Apply shake animation if dart is embedded
        float shakeTime = entity.shakeTime - partialTicks;
        if (shakeTime > 0) {
            float shake = -Mth.sin(shakeTime * 3.0F) * shakeTime;
            poseStack.mulPose(Axis.ZP.rotationDegrees(shake));
        }

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
            buffer,
            this.model.renderType(this.getTextureLocation(entity)),
            false,
            false
        );
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownDartEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(PartyGames.MOD_ID, "textures/entity/dart/red_dart_ent.png");
    }
}
