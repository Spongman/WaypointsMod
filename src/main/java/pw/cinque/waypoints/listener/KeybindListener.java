package pw.cinque.waypoints.listener;

import net.minecraft.client.Minecraft;
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
			if (mc.isSingleplayer()) {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You can only create waypoints in multiplayer!"));
			} else {
				mc.displayGuiScreen(new GuiScreenCreateWaypoint());
			}
		} else if (WaypointsMod.bindWaypointMenu.isPressed()) {
			for (Waypoint waypoint : WaypointsMod.getWaypoints()) {
				if (waypoint.shouldRender()) {
					mc.displayGuiScreen(new GuiScreenWaypointsMenu());
					return;
				}
			}
			
			mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No waypoints found for this server/world!"));
		}
	}

}
