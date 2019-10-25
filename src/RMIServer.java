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
	//criar classe com extend thread para aplicar o run para o backup


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
		System.out.println(received);
		String[] result = received.split("-");
		if(result[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
			if(notificacoes.containsKey(username)){
				String notif = notificacoes.get(username);
				notificacoes.remove(username);
				return received+"-Tem uma notificação pendente: "+notif;
			}
		}
		return received+"-Nao tem notificacoes";
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

    public String verLigacoes(String username, String page){
        String toSend = "type ! verLigação ; username ! " + username + " ; pagina ! " + page;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }

    public String verPainelAdmin(String username){
        String toSend = "type ! verAdmin ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        System.out.println(received);
        return received;


    }
    public String verPesquisas(String username){
        String toSend = "type ! verPesquisas ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        if (received.equals("")){
            received= "Ainda não efetuou nenhuma pesquisa.";
        }
        return received;


    }

    public String pesquisar(String username, String pesquisa) {
        String toSend = "type ! search ; username ! " + username + " ; key words ! " + pesquisa;
        enviarPacote(toSend); //enviar ao Multicast Server
        String size;
        String received = "";
        int sizeint;
        size = recebePacote();
        sizeint = Integer.parseInt(size);
        if (sizeint != 0) {
            for (int i = 0; i < sizeint; i++) {
                received = received + recebePacote() + "\n\n";

            }

            received = received + recebePacote() + "Mostrando os " + sizeint + " mais relevantes!";
        }

        else
            received=recebePacote();

        System.out.println(received);
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
	public void deleteUserOnline(String username)
    {
        for(String name : usersOnline.keySet())
        {
            if(name.equals(username))
                usersOnline.remove(name);
        }
        System.out.println("User: "+username+ " ficou offline");
        for (String i : usersOnline.keySet()) {
            System.out.println("key: " + i + " | value: " + usersOnline.get(i));
        }
    }

	public void addUserOnline(String username, RMI_C_I cliente){
		usersOnline.put(username,cliente);
		/*
		if(username==null)
			System.out.println("Username a null");
		if(cliente==null)
			System.out.println("cliente a null");
		 */
		System.out.println("User: "+username+ " está online com o id: "+ cliente.toString());
        for (String i : usersOnline.keySet())
            System.out.println("key: " + i + " | value: " + usersOnline.get(i));
	}

	public String notifyUserToAdmin(String username,String adminName) throws RemoteException {
	    //verificar se o user existe
        //verificar se está online
        String toSend = "type ! verify ; username ! " + username + " ; msg ! Verify user";
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        String[] result = received.split(" ; ");
        String[] info = result[2].split(" ! ");
        String message = "O administrador "+adminName+" tornou-te tambem um Administrador! Parabens";
        if(info[1].equals("User successfully verified")) {
            if (usersOnline.containsKey(username)){ //se o user que se vai tornar admin estiver online, temos de o notificar
                //notificar o utilizador que é admin
                usersOnline.get(username).showNotification(message);
                return "Definiste o utilizador " + username + " como Administrador.";
            }
            else{
                //guardar notificacao para quando ficar online
                notificacoes.put(username,message);
                //quando alguem ficar online verificar as notificacoes
                return "\nDefiniste o utilizador " + username + " como Administrador. Ele sera notificado quando estiver online";
            }
        }
        else if(info[1].equals("User not found"))
            return "Utilizador não encontrado!";
        else {
            System.out.println(info);
            return "fail to give Admin permissions";
        }
    }

	// ==========================MAIN=============================


	public static void main(String args[]){

		try {
			RMIServer h = new RMIServer();
			Registry r = LocateRegistry.createRegistry(7500);
			System.out.println(LocateRegistry.getRegistry(7500));
			r.rebind("project", h);
		} catch (RemoteException | SocketException re) {
			System.out.println("Exception in RMIServer.main: " + re);
		}
	}

}