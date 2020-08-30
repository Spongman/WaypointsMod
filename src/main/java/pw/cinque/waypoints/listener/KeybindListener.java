package pw.cinque.waypoints.listener;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.WaypointsMod;
import pw.cinque.waypoints.gui.screen.GuiScreenCreateWaypoint;
import pw.cinque.waypoints.gui.screen.GuiScreenWaypointsMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindListener {

	private Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (WaypointsMod.bindWaypointCreate.isPressed()) {
			mc.displayGuiScreen(new GuiScreenCreateWaypoint(null, null));
		} else if (WaypointsMod.bindWaypointMenu.isPressed()) {

			if (WaypointsMod.getWaypointsToRender().size() > 0)
				mc.displayGuiScreen(new GuiScreenWaypointsMenu());
			else
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No waypoints found for this server/world!"));

		} else if (WaypointsMod.bindDeleteNearest.isPressed()) {

			Waypoint closest = null;
			Waypoint nextClosest = null;

			double closestDistance = Double.MAX_VALUE;
			double nextClosestDistance = Double.MAX_VALUE;

			Entity player = Minecraft.getMinecraft().thePlayer;
			for (Waypoint waypoint : WaypointsMod.getWaypointsToRender()) {
				double d = waypoint.getDistance(player);
				if (d < closestDistance) {
					nextClosestDistance = closestDistance;
					nextClosest = closest;
					closestDistance = d;
					closest = waypoint;
				} else if (d < nextClosestDistance) {
					nextClosestDistance = d;
					nextClosest = waypoint;
				}
			}

			if (closest != null) {
				WaypointsMod.removeWaypoint(closest);

				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Removed waypoint " + closest.getName()));

				if (nextClosest != null) {
					mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Next closest is " + nextClosest.getName()));
				}
			} else {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No waypoints found for this server/world!"));
			}
		}

	}

}
