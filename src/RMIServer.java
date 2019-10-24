import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;


public class RMIServer extends UnicastRemoteObject implements RMI_S_I {
	static private int PORT = 4371;
	private final String MULTICAST_ADDRESS = "224.3.2.3";
	private DatagramSocket dSocket = new DatagramSocket(4370);
	private static HashMap<String, String> notificacoes = new HashMap<>();
	private static HashMap<String,RMI_C_I> usersOnline = new HashMap<String,RMI_C_I>();

	public RMIServer() throws RemoteException, SocketException {
		super();
	}
	public void ping() {
		System.out.println("Ping recebido");
	}

	public void enviarPacote(String s) {
		try {
			MulticastSocket socket = new MulticastSocket();
			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			socket.joinGroup(group);

			byte[] buffer = s.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(MULTICAST_ADDRESS), PORT);
			socket.send(packet);

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public String recebePacote() {
		byte[] buffer = new byte[1000];
		DatagramPacket message = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				dSocket.setSoTimeout(40000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			try {
				dSocket.receive(message);
				System.out.println(message);
				break;
			} catch (IOException e) {
				return "fail";
			}
		}
		return new String(message.getData(), 0, message.getLength());
	}

	public String confereLogin(String username, String password) {
		String toSend = "type ! login ; username ! " + username + " ; password ! " + password;
		enviarPacote(toSend); //envia ao Multicast Server
		String received = recebePacote();
		return received;

	}

	public String registaUtilizador(String username, String password) {
		String toSend = "type ! register ; username ! " + username + " ; password ! " + password;
		enviarPacote(toSend); //enviar ao Multicast Server
		String received = recebePacote();
		return received;

	}

	public String sayHello() throws RemoteException {
		System.out.println("print do lado do servidor...!.");

		return "Hello, World!";
	}

	public String pesquisar(String username, String pesquisa) {
		String toSend = "type ! search ; username ! " + username + " ; key words ! " + pesquisa;
		enviarPacote(toSend); //enviar ao Multicast Server
		String size = recebePacote();
		String received="";
		int sizeint = Integer.parseInt(size);
		System.out.println(sizeint);
		if (sizeint!=0){
		for (int i=0; i< sizeint;i++){
			received=received + recebePacote()+ "\n\n";
		}
		received = received+ recebePacote() + "Mostrando os "+ sizeint + " mais relevantes!";
		}
		else{
			received="Não foram encontrados resultados!";
		}
		return received;
	}


    public String indexar(String username, String ws) {
        String toSend = "type ! indexar ; username ! " + username + " ; website ! " + ws;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }


	public String logout(String username) {
		String toSend = "type ! logout ; username ! " + username + " ; msg ! Logging out";
		enviarPacote(toSend); //enviar ao Multicast Server
		String received = recebePacote();
		return received;
	}

	public void addUserOnline(String username, RMI_C_I cliente){
		usersOnline.put(username,cliente);
		if(username==null)
			System.out.println("Username a null");
		if(cliente==null)
			System.out.println("cliente a null");
		System.out.println("User: "+username+ "está online com o id:"+ cliente.toString());
	}

	// =======================================================


	public static void main(String args[]) throws SocketException {

		try {
			RMIServer h = new RMIServer();
			Registry r = LocateRegistry.createRegistry(7500);
			System.out.println(LocateRegistry.getRegistry(7500));
			r.rebind("project", h);
			//System.out.println("Hello Server ready.");
		} catch (RemoteException re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		}
	}

}