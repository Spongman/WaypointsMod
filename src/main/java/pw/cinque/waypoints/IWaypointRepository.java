package pw.cinque.waypoints;

import java.util.ArrayList;

public interface IWaypointRepository {
	void addWaypoint(Waypoint waypoint);

	void removeWaypoint(Waypoint waypoint);
	void hideWaypoint(Waypoint waypoint);

	void refreshWaypointsToRender();

	ArrayList<Waypoint> getWaypointsToRender();
}
