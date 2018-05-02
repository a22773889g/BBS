import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class Room {
	static Vector<Room> roomList = new Vector<Room>(5);
	public Vector room = new Vector(5);
	public String name;

	public Room(String name) {
		this.name = name;
	}
}
