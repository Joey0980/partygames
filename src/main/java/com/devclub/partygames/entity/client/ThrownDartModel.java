package com.devclub.partygames.entity.client;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.custom.ThrownDartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ThrownDartModel extends EntityModel<ThrownDartEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(PartyGames.MOD_ID, "dart_item"), "main");
    private final ModelPart dart;

    public ThrownDartModel(ModelPart root) {
        this.dart = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 22).addBox(-17.0F, -4.0F, 7.0F, 18.0F, 1.0F, 1.0F, new CubeDeformation(-1.0F))
            .texOffs(32, 0).addBox(-11.0F, -6.0F, 6.0F, 8.0F, 5.0F, 3.0F, new CubeDeformation(-1.0F))
            .texOffs(0, 24).addBox(-11.0F, -5.0F, 5.0F, 8.0F, 3.0F, 5.0F, new CubeDeformation(-1.0F))
            .texOffs(0, 12).addBox(-18.0F, -5.0F, 4.0F, 9.0F, 3.0F, 7.0F, new CubeDeformation(-1.0F))
            .texOffs(0, 0).addBox(-17.0F, -5.0F, 3.0F, 7.0F, 3.0F, 9.0F, new CubeDeformation(-1.0F))
            .texOffs(26, 24).addBox(-18.0F, -7.0F, 6.0F, 9.0F, 7.0F, 3.0F, new CubeDeformation(-1.0F))
            .texOffs(0, 32).addBox(-17.0F, -8.0F, 6.0F, 7.0F, 9.0F, 3.0F, new CubeDeformation(-1.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ThrownDartEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        dart.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
