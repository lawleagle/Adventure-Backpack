package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.darkona.adventurebackpack.reference.BackpackTypes.*;
import static com.darkona.adventurebackpack.reference.BackpackTypes.IRON_GOLEM;

public class BackpackModelRenderer extends ModelRenderer {
    public static void setOffset(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.offsetX = x;
        modelRenderer.offsetY = y;
        modelRenderer.offsetZ = z;
    }

    public static float scaleFactor = 0.9f;

    public ItemStack backpack;
    public BackpackTypes type;

    public BackpackModelRenderer(ItemStack backpack) {
        super(new ModelBiped(), 0, 0);
        this.backpack = backpack;

        InventoryBackpack inventoryBackpack = new InventoryBackpack(this.backpack);
        type = inventoryBackpack.getType();

        ModelBase model = new ModelBiped();
        model.textureWidth = 128;
        model.textureHeight = 64;

        // Main Backpack
        ModelRenderer mainBody = new ModelRenderer(model, 0, 9);
        mainBody.addBox(-5.0F, 0.0F, -3.0F, 10, 9, 5);
        mainBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        ModelRenderer leftStrap = new ModelRenderer(model, 21, 24);
        leftStrap.setRotationPoint(3.0F, 0.0F, -3.0F);
        leftStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        mainBody.addChild(leftStrap);

        ModelRenderer rightStrap = new ModelRenderer(model, 26, 24);
        rightStrap.setRotationPoint(-4.0F, 0.0F, -3.0F);
        rightStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        mainBody.addChild(rightStrap);

        ModelRenderer top = new ModelRenderer(model, 0, 0);
        top.setRotationPoint(0.0F, 0.0F, -3.0F);
        top.addBox(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        mainBody.addChild(top);

        ModelRenderer bottom = new ModelRenderer(model, 0, 34);
        bottom.setRotationPoint(-5.0F, 9.0F, -3.0F);
        bottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 4);
        mainBody.addChild(bottom);

        ModelRenderer pocketFace = new ModelRenderer(model, 0, 24);
        pocketFace.setRotationPoint(0.0F, 6.9F, 2.0F);
        pocketFace.addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        mainBody.addChild(pocketFace);

        // Left Tank
        ModelRenderer tankLeftTop = new ModelRenderer(model, 0, 40);
        tankLeftTop.setRotationPoint(5.0F, -1.0F, -2.5F);
        tankLeftTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        ModelRenderer tankLeftBottom = new ModelRenderer(model, 0, 46);
        tankLeftBottom.setRotationPoint(0.0F, 9.0F, 0.0F);
        tankLeftBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        tankLeftTop.addChild(tankLeftBottom);

        ModelRenderer tankLeftWall1 = new ModelRenderer(model, 0, 52);
        tankLeftWall1.setRotationPoint(3.0F, -8.0F, 0.0F);
        tankLeftWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankLeftBottom.addChild(tankLeftWall1);

        ModelRenderer tankLeftWall2 = new ModelRenderer(model, 5, 52);
        tankLeftWall2.setRotationPoint(0.0F, -8.0F, 0.0F);
        tankLeftWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankLeftBottom.addChild(tankLeftWall2);

        ModelRenderer tankLeftWall3 = new ModelRenderer(model, 10, 52);
        tankLeftWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        tankLeftWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankLeftBottom.addChild(tankLeftWall3);

        ModelRenderer tankLeftWall4 = new ModelRenderer(model, 15, 52);
        tankLeftWall4.setRotationPoint(3.0F, -8.0F, 3.0F);
        tankLeftWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankLeftBottom.addChild(tankLeftWall4);

        // Right Tank
        ModelRenderer tankRightTop = new ModelRenderer(model, 17, 40);
        tankRightTop.setRotationPoint(-9.0F, -1.0F, -2.5F);
        tankRightTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        ModelRenderer tankRightBottom = new ModelRenderer(model, 17, 46);
        tankRightBottom.setRotationPoint(0.0F, 9.0F, 0.0F);
        tankRightBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        tankRightTop.addChild(tankRightBottom);

        ModelRenderer tankRightWall1 = new ModelRenderer(model, 22, 52);
        tankRightWall1.setRotationPoint(3.0F, -8.0F, 3.0F);
        tankRightWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankRightBottom.addChild(tankRightWall1);

        ModelRenderer tankRightWall2 = new ModelRenderer(model, 27, 52);
        tankRightWall2.setRotationPoint(3.0F, -8.0F, 0.0F);
        tankRightWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankRightBottom.addChild(tankRightWall2);

        ModelRenderer tankRightWall3 = new ModelRenderer(model, 32, 52);
        tankRightWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        tankRightWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankRightBottom.addChild(tankRightWall3);

        ModelRenderer tankRightWall4 = new ModelRenderer(model, 37, 52);
        tankRightWall4.setRotationPoint(0.0F, -8.0F, 0.0F);
        tankRightWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tankRightBottom.addChild(tankRightWall4);

        // Bed
        ModelRenderer bed = new ModelRenderer(model, 31, 0);
        bed.setRotationPoint(-7.0F, 7.0F, 2.0F);
        bed.addBox(0.0F, 0.0F, 0.0F, 14, 2, 2);

        ModelRenderer bedStrapRightTop = new ModelRenderer(model, 40, 5);
        bedStrapRightTop.setRotationPoint(2.0F, -1.0F, 0.0F);
        bedStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        bed.addChild(bedStrapRightTop);

        ModelRenderer bedStrapRightMid = new ModelRenderer(model, 38, 10);
        bedStrapRightMid.setRotationPoint(2.0F, 0.0F, 2.0F);
        bedStrapRightMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        bed.addChild(bedStrapRightMid);

        ModelRenderer bedStrapRightBottom = new ModelRenderer(model, 42, 15);
        bedStrapRightBottom.setRotationPoint(2.0F, 2.0F, -1.0F);
        bedStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        bed.addChild(bedStrapRightBottom);

        ModelRenderer bedStrapLeftTop = new ModelRenderer(model, 31, 5);
        bedStrapLeftTop.setRotationPoint(11.0F, -1.0F, 0.0F);
        bedStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        bed.addChild(bedStrapLeftTop);

        ModelRenderer bedStrapLeftMid = new ModelRenderer(model, 31, 10);
        bedStrapLeftMid.setRotationPoint(10.0F, 0.0F, 2.0F);
        bedStrapLeftMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        bed.addChild(bedStrapLeftMid);

        ModelRenderer bedStrapLeftBottom = new ModelRenderer(model, 31, 15);
        bedStrapLeftBottom.setRotationPoint(10.0F, 2.0F, -1.0F);
        bedStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        bed.addChild(bedStrapLeftBottom);

        // Noses
        if (type == VILLAGER || type == IRON_GOLEM) {
            ModelRenderer villagerNose = new ModelRenderer(model, 64, 0);
            villagerNose.setRotationPoint(-1.0F, 4.0F, 4.0F);
            villagerNose.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2);
            this.addChild(villagerNose);
        }

        if (type == OCELOT) {
            ModelRenderer ocelotNose = new ModelRenderer(model, 74, 0);
            ocelotNose.setRotationPoint(-1.0F, 4.0F, 4.0F);
            ocelotNose.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);
            this.addChild(ocelotNose);
        }

        if (type == PIG || type == HORSE) {
            ModelRenderer pigNose = new ModelRenderer(model, 74, 0);
            pigNose.setRotationPoint(-2.0F, 4.0F, 4.0F);
            pigNose.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1);
            this.addChild(pigNose);
        }

        ModelRenderer lowerTool = new RendererStack(model, true);
        ModelRenderer upperTool = new RendererStack(model, false);

        this.addChild(mainBody);
        this.addChild(bed);
        this.addChild(tankLeftTop);
        this.addChild(tankRightTop);
        mainBody.addChild(lowerTool);
        mainBody.addChild(upperTool);

        float offsetZ = 0.4F;
        float offsetY = 0.2F;

        for (ModelRenderer part : (List<ModelRenderer>) this.childModels) {
            setOffset(part, part.offsetX + 0, part.offsetY + offsetY, part.offsetZ + offsetZ);
        }
    }



    @Override
    public void render(float scale) {
        ItemStack wearableCopy = backpack.copy();
        IBackWearableItem wearableItem = (IBackWearableItem) wearableCopy.getItem();
        ResourceLocation texture = wearableItem.getWearableTexture(wearableCopy);

        GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (type == QUARTZ || type == SLIME || type == SNOW) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        super.render(scale * scaleFactor);

        if (type == QUARTZ || type == SLIME || type == SNOW) {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glPopAttrib();
    }
}
