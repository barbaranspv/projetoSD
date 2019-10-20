import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Scanner;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
        MulticastServerReceive serverReceive = new MulticastServerReceive();
        serverReceive.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboardScanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class MulticastServerReceive extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
