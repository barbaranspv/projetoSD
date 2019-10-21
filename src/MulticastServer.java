import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;


public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.3";
    private int PORT = 4321;
    private ArrayList<Utilizador> listaUsers = new ArrayList<Utilizador>();

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
        boolean existUsername=false;
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
                        if(listaUsers.size()==0)
                            enviaInfoRMI(socket, packet.getAddress(), "Utilizador não existente, por favor efetue o registo");
                        for(int i=listaUsers.size()-1; i>=0; i--)
                        {
                            if(username.equals(listaUsers.get(i).username))
                            {
                                if(listaUsers.get(i).password.equals(password)) {
                                    if(listaUsers.get(i).admin==true)
                                        enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca-admin-"+username);
                                    else
                                        enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -"+username);
                                    System.out.println(message + " tamanho da lista:" + listaUsers.size());
                                }
                                else
                                    enviaInfoRMI(socket, packet.getAddress(), "Password incorreta! Tente novamente");
                            }
                            else
                                enviaInfoRMI(socket, packet.getAddress(), "Username não encontrado, por favor efetue o registo ou verifique o username colocado");
                        }
                    } else if (type[1].equals("register")) {
                        String username = result[1].split(" ! ")[1];
                        //System.out.println(username);
                        String password = result[2].split(" ! ")[1];
                        System.out.println(username + " " + password);

                        //System.out.println("size:"+listaUsers.size());

                        if(listaUsers.isEmpty()==true){
                            Utilizador firstUser = new Utilizador(username,password,true);
                            listaUsers.add(firstUser);
                            File fich = new File("Users.txt");
                            try
                            {
                                FileOutputStream is = new FileOutputStream(fich);
                                ObjectOutputStream ois = new ObjectOutputStream(is);
                                ois.writeObject(listaUsers);
                                ois.close();
                            }
                            catch (FileNotFoundException b)
                            {
                                System.out.println("Nao encontrei o ficheiro");
                            }
                            catch(IOException b)
                            {
                                System.out.println("Erro ao escrever no ficheiro");
                            }
                            enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca-admin-"+username);
                        }
                        else if(listaUsers.isEmpty()==false){
                            for(int i=listaUsers.size()-1; i>=0; i--)
                            {
                                if(username.equals(listaUsers.get(i).username))
                                {
                                    existUsername=true;
                                }
                            }
                            if(existUsername==false) {
                                Utilizador user = new Utilizador(username,password,false);
                                listaUsers.add(user);
                                File fich = new File("Users.txt");
                                try
                                {
                                    FileOutputStream is = new FileOutputStream(fich);
                                    ObjectOutputStream ois = new ObjectOutputStream(is);
                                    ois.writeObject(listaUsers);
                                    ois.close();
                                }
                                catch (FileNotFoundException b)
                                {
                                    System.out.println("Nao encontrei o ficheiro");
                                }
                                catch(IOException b)
                                {
                                    System.out.println("Erro ao escrever no ficheiro");
                                }
                                enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -"+username);
                            }
                            else {
                                System.out.println(username);
                                enviaInfoRMI(socket, packet.getAddress(), "Username já existente!");
                                existUsername=false;
                            }
                        }
                    }
                }
                catch (NumberFormatException n)
                {
                    System.out.println("Nao foi possivel fazer parseInt da mensagem"); // nao esta a dar bem
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
    private void lerFicheiroUsers()
    {
        File fich = new File("Users.txt");
        if(fich.exists() && fich.isFile())
        {
            try
            {
                FileReader fr=new FileReader(fich);
                BufferedReader br=new BufferedReader(fr);
                String line;
                if((line=br.readLine())!=null)
                {
                    br.close();
                    try
                    {
                        FileInputStream es = new FileInputStream(fich);
                        ObjectInputStream oi = new ObjectInputStream(es);
                        listaUsers=(ArrayList<Utilizador>)oi.readObject();
                        oi.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("Nao encontrei o ficheiro");
                    }
                    catch(IOException e)
                    {
                        System.out.println("Erro ao ler no ficheiro de alunos");
                    }
                    catch(ClassNotFoundException e)
                    {
                        System.out.println("Erro a criar objeto");
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("Nao encontrei o ficheiro");
            }
            catch(IOException e)
            {
                System.out.println("Erro ao ler no ficheiro");
            }
        }
    }
    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
        server.lerFicheiroUsers();
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
    private ArrayList<String> listaURLS = new ArrayList<String>();
    boolean online = false;

    public Utilizador(String username,String password,boolean admin){
        this.username=username;
        this.password=password;
        this.admin=admin;
    }

}