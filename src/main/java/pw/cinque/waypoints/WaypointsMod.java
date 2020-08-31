package pw.cinque.waypoints;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pw.cinque.waypoints.listener.KeybindListener;
import pw.cinque.waypoints.listener.WorldListener;

@Mod(name = WaypointsMod.NAME, modid = WaypointsMod.MOD_ID, version = WaypointsMod.VERSION)
public class WaypointsMod implements IWaypointRepository {

	public static final String NAME = "Fyu's Waypoints";
	public static final String MOD_ID = "waypoints";
	public static final String VERSION = "1.0-Beta";

	private File WAYPOINTS_FILE;

	private static ArrayList<Waypoint> waypoints = new ArrayList<>();
	private static ArrayList<Waypoint> waypointsToRender = new ArrayList<>();

	private static WaypointsMod instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		instance = this;

		final File root = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "Fyu's Waypoints");
		root.mkdir();

		WAYPOINTS_FILE = new File(root, "waypoints");

		final ModMetadata metadata = event.getModMetadata();
		metadata.version = VERSION;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (WAYPOINTS_FILE.exists()) {
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(WAYPOINTS_FILE));
				String readLine;

				while ((readLine = reader.readLine()) != null) {
					waypoints.add(Waypoint.fromString(readLine));
				}

				reader.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		MinecraftForge.EVENT_BUS.register(new KeybindListener(this));
		MinecraftForge.EVENT_BUS.register(new WorldListener(this));
	}

	@Override
	public void addWaypoint(Waypoint waypoint) {
		waypoints.add(waypoint);
		refreshWaypointsToRender();
		writeWaypointsToDisk();
	}

	@Override
	public void removeWaypoint(Waypoint waypoint) {
		waypoints.remove(waypoint);
		refreshWaypointsToRender();
		writeWaypointsToDisk();
	}

	private static void writeWaypointsToDisk() {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(instance.WAYPOINTS_FILE));

			for (final Waypoint w : waypoints) {
				writer.write(w.toString());
				writer.newLine();
			}

			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refreshWaypointsToRender() {
		waypointsToRender.clear();

		for (final Waypoint waypoint : waypoints)
			if (waypoint.shouldRender()) {
				waypointsToRender.add(waypoint);
			}
	}

	@Override
	public ArrayList<Waypoint> getWaypointsToRender() {
		return waypointsToRender;
	}
}
