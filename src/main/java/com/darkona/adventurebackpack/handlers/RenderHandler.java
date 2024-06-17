package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.client.render.BackpackModelRenderer;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

import com.darkona.adventurebackpack.proxy.ClientProxy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RenderHandler {
    public static boolean equipped = false;
    public static int tick = 0;
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void playerSpecialsRendering(RenderPlayerEvent.Specials.Pre event) {

        float rotationY = event.renderer.modelBipedMain.bipedBody.rotateAngleY;
        float rotationX = event.renderer.modelBipedMain.bipedBody.rotateAngleX;
        float rotationZ = event.renderer.modelBipedMain.bipedBody.rotateAngleZ;

        double x = event.entity.posX;
        double y = event.entity.posY;
        double z = event.entity.posZ;

        float pitch = event.entity.rotationPitch;
        float yaw = event.entity.rotationYaw;

        ItemStack wearable = Wearing.getWearingWearable(event.entityPlayer);

        boolean hasBackpack = false;
        boolean shouldHaveBackpack = wearable == null;

        if (wearable == null) {
            for (int i = 0; i < event.renderer.modelBipedMain.bipedBody.childModels.size(); i++) {
                if (event.renderer.modelBipedMain.bipedBody.childModels.get(i) instanceof BackpackModelRenderer) {
                    event.renderer.modelBipedMain.bipedBody.childModels.remove(i);
                    break;
                }
            }
            equipped = false;
        }
        else if (equipped == false) {
            event.renderer.modelBipedMain.bipedBody.addChild(new BackpackModelRenderer(wearable));
            equipped = true;
        }
        
        event.renderCape = false;
    }
}
