package com.darkona.adventurebackpack.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.reference.BackpackTypes;

import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.TabRegistry;

public class TConstructTab {

    public static void registerTab() {
        TabRegistry.registerTab(new Tab());
    }

    public static void updateTabValues(int cornerX, int cornerY, Class<?> selectedButton) {
        TabRegistry.updateTabValues(cornerX, cornerY, selectedButton);
    }

    public static void addTabsToList(List<?> buttonList) {
        TabRegistry.addTabsToList(buttonList);
    }

    public static class Tab extends AbstractTab {

        public static final RenderItem itemRenderer = new RenderItem();
        public static final ResourceLocation texture = new ResourceLocation(
                "textures/gui/container/creative_inventory/tabs.png");

        public Tab() {
            super(0, 0, 0, BackpackUtils.createBackpackStack(BackpackTypes.STANDARD));
        }

        @Override
        public void onTabClicked() {
            EntityClientPlayerMP playerMP = Minecraft.getMinecraft().thePlayer;
            byte backpackType;
            if (Wearing.getWearingBackpack(playerMP) != null) backpackType = GUIPacket.BACKPACK_GUI;
            else if (Wearing.getWearingCopter(playerMP) != null) backpackType = GUIPacket.COPTER_GUI;
            else if (Wearing.getWearingJetpack(playerMP) != null) backpackType = GUIPacket.JETPACK_GUI;
            else return;
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(backpackType, GUIPacket.FROM_WEARING));

        }

        @Override
        public boolean shouldAddToList() {
            return Wearing.isWearingWearable(Minecraft.getMinecraft().thePlayer);
        }

        /**
         * Draws this button to the screen. Code from TinkersConstruct and modified for dynamic and depth-enabled
         * backpack rendering
         */
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                ItemStack renderStack = Wearing.getWearingWearable(Minecraft.getMinecraft().thePlayer);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                int yTexPos = this.enabled ? 3 : 32;
                int ySize = this.enabled ? 25 : 32;
                int xOffset = this.id == 2 ? 0 : 1;
                int yPos = this.yPosition + (this.enabled ? 3 : 0);

                mc.renderEngine.bindTexture(texture);
                this.drawTexturedModalRect(this.xPosition, yPos, xOffset * 28, yTexPos, 28, ySize);

                RenderHelper.enableGUIStandardItemLighting();
                this.zLevel = 100.0F;
                itemRenderer.zLevel = 100.0F;
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                itemRenderer.renderItemAndEffectIntoGUI(
                        mc.fontRenderer,
                        mc.renderEngine,
                        renderStack,
                        xPosition + 6,
                        yPosition + 8);
                itemRenderer.renderItemOverlayIntoGUI(
                        mc.fontRenderer,
                        mc.renderEngine,
                        renderStack,
                        xPosition + 6,
                        yPosition + 8);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                itemRenderer.zLevel = 0.0F;
                this.zLevel = 0.0F;
                RenderHelper.disableStandardItemLighting();
            }
        }
    }
}
