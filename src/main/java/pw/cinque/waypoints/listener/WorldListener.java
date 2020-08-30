package pw.cinque.waypoints.listener;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldListener {

	private Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onSwitchWorld(EntityJoinWorldEvent event) {
		if (event.entity == mc.thePlayer) {
			WaypointsMod.refreshWaypointsToRender();
		}
	}

	@SubscribeEvent
	public void onRenderLastEvent(RenderWorldLastEvent event) {

		final double maxDistance = 1000;
		Minecraft ms = Minecraft.getMinecraft();
		Entity viewEntity = mc.getRenderViewEntity();
		RenderManager renderManager = mc.getRenderManager();
		if (renderManager == null)
			return;
		FontRenderer fontrenderer = renderManager.getFontRenderer();
		if (fontrenderer == null)
			return;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		GlStateManager.pushAttrib();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableCull();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();

		EntityPlayerSP player = mc.thePlayer;
		float px = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks);
		float py = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks);
		float pz = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks);

		for (Waypoint wp : WaypointsMod.getWaypointsToRender()) {

			float x = (float) wp.getX() - px;
			float y = (float) wp.getY() - py;
			float z = (float) wp.getZ() - pz;

			double d0 = renderManager.getDistanceToCamera(x, y, z);
			if (d0 < maxDistance)
				continue;

			String str = wp.getName();
			int color = wp.getColor();
			int hiddenColor = (color & 0xffffff) | ((color & 0xfe000000) >> 1);

			float f1 = 0.016666668F * 20;
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.0F, (float) y + 0.5F, (float) z);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(-f1, -f1, f1);
			GlStateManager.disableLighting();
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			int j = fontrenderer.getStringWidth(str) / 2;
			GlStateManager.disableTexture2D();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos((double) (-j - 1), (double) (-1), 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos((double) (-j - 1), (double) (8), 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos((double) (j + 1), (double) (8), 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos((double) (j + 1), (double) (-1), 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
			fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, 0, color);
			GlStateManager.enableDepth();
			GlStateManager.depthMask(true);
			fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, 0, hiddenColor);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
	}
}
