package pw.cinque.waypoints.listener;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import pw.cinque.waypoints.IWaypointRepository;
import pw.cinque.waypoints.Waypoint;
import pw.cinque.waypoints.gui.screen.GuiScreenCreateWaypoint;
import pw.cinque.waypoints.gui.screen.GuiScreenWaypointsMenu;

public class KeybindListener {

	private final Minecraft mc = Minecraft.getMinecraft();

	private final IWaypointRepository waypoints;

	public static KeyBinding bindWaypointCreate = new KeyBinding("Create Waypoint", Keyboard.KEY_SEMICOLON, "Fyu's Waypoints");
	public static KeyBinding bindWaypointMenu = new KeyBinding("Open Menu", Keyboard.KEY_GRAVE, "Fyu's Waypoints");
	public static KeyBinding bindDeleteNearest = new KeyBinding("Delete Nearest", Keyboard.KEY_COLON, "Fyu's Waypoints");

	public KeybindListener(IWaypointRepository waypoints) {
		this.waypoints = waypoints;
		ClientRegistry.registerKeyBinding(bindWaypointCreate);
		ClientRegistry.registerKeyBinding(bindWaypointMenu);
		ClientRegistry.registerKeyBinding(bindDeleteNearest);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (bindWaypointCreate.isPressed()) {
			mc.displayGuiScreen(new GuiScreenCreateWaypoint(waypoints, null, null));
		} else if (bindWaypointMenu.isPressed()) {

			if (waypoints.getWaypointsToRender().size() > 0) {
				mc.displayGuiScreen(new GuiScreenWaypointsMenu(waypoints));
			} else {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No waypoints found for this server/world!"));
			}

		} else if (bindDeleteNearest.isPressed()) {

			Waypoint closest = null;
			Waypoint nextClosest = null;

			double closestDistance = Double.MAX_VALUE;
			double nextClosestDistance = Double.MAX_VALUE;

			final Entity player = Minecraft.getMinecraft().thePlayer;
			for (final Waypoint waypoint : waypoints.getWaypointsToRender()) {
				final double d = waypoint.getDistance(player);
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
				final String name = closest.getName();
				waypoints.hideWaypoint(closest);

				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Removed waypoint " + name));

				if (nextClosest != null) {
					mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Next closest is " + nextClosest.getName()));
				}
			} else {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No waypoints found for this server/world!"));
			}
		}

	}

}
