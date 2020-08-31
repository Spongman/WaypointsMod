package pw.cinque.waypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import pw.cinque.waypoints.listener.WorldListener;

public class Waypoint {

	private static Minecraft mc = Minecraft.getMinecraft();

	private final String world;
	private final String server;

	private String name;
	private int x, y, z;
	private int color;

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

	public void setColor(int color) {
		this.color = color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public boolean shouldRender() {
		if (world == null)
			return false;
		return world.equals(mc.theWorld.provider.getDimensionName()) && server.equals(WorldListener.getWorldName());
	}

	public double getDistance(Entity en) {
		final double x = this.x - en.posX;
		final double y = this.y - en.posY;
		final double z = this.z - en.posZ;
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
				final String[] parts = string.split(";");
				return new Waypoint(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]));
			} catch (final Throwable t) {
			}
		}

		return new Waypoint(string);
	}
}
