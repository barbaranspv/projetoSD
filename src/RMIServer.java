import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class RMIServer extends UnicastRemoteObject implements Hello_S_I {
	static Hello_C_I client;
	static private String MULTICAST_ADDRESS = "224.0.224.0";
	static private int PORT = 4321;

	public RMIServer() throws RemoteException {
		super();
	}

	public void print_on_server(String s) throws RemoteException {
		System.out.println("> " + s);
	}

	public void subscribe(String name, Hello_C_I c) throws RemoteException {
		System.out.println("Subscribing " + name);
		System.out.print("> ");
		client = c;
	}

	// =======================================================

	public static void main(String args[]) {
		String a;

		System.getProperties().put("java.security.policy", "policy.all");
		System.setSecurityManager(new RMISecurityManager());
		MulticastSocket socket = null;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		try {
			//User user = new User();
			RMIServer h = new RMIServer();
			Registry r = LocateRegistry.createRegistry(7000);
			r.rebind("project", h);
			System.out.println("Hello Server ready.");
			socket = new MulticastSocket(PORT);  // create socket and bind it
			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			socket.joinGroup(group);
			while (true) {
				/*System.out.print("> ");
				a = reader.readLine();
				client.print_on_client(a);*/
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				System.out.println("RMI...Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
				String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println(message);
				}
		} catch (Exception re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		} 
	}
}
