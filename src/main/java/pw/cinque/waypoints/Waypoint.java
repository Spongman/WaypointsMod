package pw.cinque.waypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Waypoint {

	private static Minecraft mc = Minecraft.getMinecraft();

	private final String name;
	private final String world;
	private final String server;
	private final int x, y, z;
	private final int color;
	
	public Waypoint(String name) {
		this.name = name;
		world = server = null;
		x = y = z = color = 0;
	}

	public Waypoint(String name, String world, String server, int x, int y, int z, int color) {
		this.name = name;
		this.world = world;
		this.server = server;
		this.x = x;
		this.z = z;
		this.y = y;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getWorld() {
		return world;
	}

	public String getServer() {
		return server;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getColor() {
		return color;
	}

	public boolean shouldRender() {
		if (world == null)
			return false;
		return world.equals(mc.theWorld.provider.getDimensionName()) && server.equals(WaypointsMod.getWorldName());
	}

	public double getDistance(Entity en) {
		double x = this.x - en.posX;
		double y = this.y - en.posY;
		double z = this.z - en.posZ;
		return Math.sqrt(x * x + y * y + z * z);
	}

	@Override
	public String toString() {
		if (world == null)
			return name;
		return name + ";" + world + ";" + server + ";" + x + ";" + y + ";" + z + ";" + color;
	}

	public static Waypoint fromString(String string) {
		if (!string.startsWith("#") && !string.startsWith("//")) {
			try {
				String[] parts = string.split(";");
				return new Waypoint(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]));
			} catch (Throwable t) {
			}
		}
		
		return new Waypoint(string);
	}
}
