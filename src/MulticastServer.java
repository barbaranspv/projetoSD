import java.io.Serializable;
import java.net.*;
import java.io.IOException;
import java.util.Scanner;


public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.3";
    private int PORT = 4321;

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));

    }

    public void enviaInfoRMI(DatagramSocket aSocket, InetAddress address, String toSend) {

        try {
            byte[] buffer2 = toSend.getBytes();
            DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, address, 4322);
            aSocket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                String[] result = message.split(" ; ");
                String[] type = result[0].split(" ! ");
                //System.out.println(type[1]);
                try {

                    if (type[1].equals("login")) {
                        String username = result[1].split(" ! ")[1];
                        System.out.println(username);
                        String password = result[2].split(" ! ")[1];
                        System.out.println(username + " " + password);
                        enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca");
                    } else if (type[1].equals("register")) {
                        String username = result[1].split(" ! ")[1];
                        //System.out.println(username);
                        String password = result[2].split(" ! ")[1];
                        System.out.println(username + " " + password);
                        enviaInfoRMI(socket, packet.getAddress(), "type ! status ; registed ! on ; msg ! Welcome to ucBusca");


                    }



                } catch (NumberFormatException n) {
                    System.out.println("Nao foi possivel fazer parseInt da mensagem"); // nao esta a dar bem
                }

                System.out.println(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }



    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
        MulticastUser user = new MulticastUser();
        user.start();
    }
}
class MulticastUser extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;


    public MulticastUser() {
        super("User " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
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
class Utilizador implements Serializable {
    String username;
    String password;
    boolean admin;

    public Utilizador(String username,String password){
        this.username=username;
        this.password=password;
    }

}