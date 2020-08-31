package pw.cinque.waypoints.listener;

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
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pw.cinque.waypoints.IWaypointRepository;
import pw.cinque.waypoints.Waypoint;

public class WorldListener {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final IWaypointRepository waypoints;

	public WorldListener(IWaypointRepository waypoints) {
		this.waypoints = waypoints;
	}

	@SubscribeEvent
	public void onSwitchWorld(EntityJoinWorldEvent event) {
		if (event.entity == mc.thePlayer) {
			waypoints.refreshWaypointsToRender();
		}
	}

	public static String getWorldName() {
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.isSingleplayer())
			return mc.getIntegratedServer().getFolderName();
		else
			return mc.getCurrentServerData().serverIP;
	}

	@SubscribeEvent
	public void onRenderLastEvent(RenderWorldLastEvent event) {

		final float maxDistance = 1000F;
		final RenderManager renderManager = mc.getRenderManager();
		if (renderManager == null)
			return;
		final FontRenderer fontrenderer = renderManager.getFontRenderer();
		if (fontrenderer == null)
			return;
		final Tessellator tessellator = Tessellator.getInstance();
		final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		final EntityPlayerSP player = mc.thePlayer;
		final float px = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks);
		final float py = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks);
		final float pz = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks);

		GlStateManager.pushAttrib();

		// GlStateManager.shadeModel(GL11.GL_FLAT);
		// GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();

		for (final Waypoint wp : waypoints.getWaypointsToRender()) {

			final float x = wp.getX() - px;
			final float y = wp.getY() - py;
			final float z = wp.getZ() - pz;

			final double d0 = renderManager.getDistanceToCamera(x, y, z);
			if (d0 < maxDistance) {
				continue;
			}

			final String str = wp.getName();
			final float width = fontrenderer.getStringWidth(str);
			final float halfWidth = width / 2;
			final int color = wp.getColor();
			final int hiddenColor = (color & 0xffffff) | ((color >> 1) & 0x7f000000);

			final float f1 = 0.016666668F * 20;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.0F, y + 0.5F, z);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(-f1, -f1, f1);
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.disableTexture2D();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(-(halfWidth + 1), -1, 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos(-(halfWidth + 1), 8, 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos(halfWidth + 1, 8, 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			worldrenderer.pos(halfWidth + 1, -1, 0.0D).color(0.2F, 0.2F, 0.2F, 0.4F).endVertex();
			tessellator.draw();

			GlStateManager.enableTexture2D();

			fontrenderer.drawString(str, -halfWidth, 0, hiddenColor, false);

			GlStateManager.enableDepth();
			GlStateManager.depthMask(true);
			fontrenderer.drawString(str, -halfWidth, 0, color, false);

			GlStateManager.popMatrix();
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();

		GlStateManager.popAttrib();
	}
}
