
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class RMIServer extends UnicastRemoteObject implements RMI_S_I {
	static private int PORT = 4371;
	private final String MULTICAST_ADDRESS="224.3.2.3";
	private MulticastSocket dSocket;
    private static ArrayList<String> multicastServers = new ArrayList<>();
    private  HashMap<String, String> notificacoes;
	private  HashMap<String,RMI_C_I> usersOnline;
	private static DatagramSocket nSocket;
	private static boolean run=true;

	public RMIServer() throws RemoteException, SocketException {
		super();
		try{

			dSocket =  new MulticastSocket(4370);
		}
		catch(IOException b){
			System.out.println("Falha ao criar o socket");
		}
		notificacoes = new HashMap<>();
		usersOnline = new HashMap<>();
	}
	//criar classe com extend thread para aplicar o run para o backup


	public void ping() {
		System.out.println("Ping recebido");
	}
//Função para enviar pacote p multicast
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
//Função para receber pacote do multicast
	public String recebePacote() {

		byte[] buffer = new byte[2000];
		DatagramPacket message = new DatagramPacket(buffer, buffer.length);
		while (true) {
			try {
				dSocket.setSoTimeout(40000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			try {
				dSocket.receive(message);
				System.out.println("MESSAGE: "+message.toString());
				break;
			} catch (IOException e) {
				//dSocket.close();
				//return "Ocorreu um problema, tente mais tarde.";
			}
		}
		return new String(message.getData(), 0, message.getLength());
	}


	public String confereLogin(String username, String password) {
	    String id=chooseMulticastServer();
		String toSend = "server !! "+id+ " ; type ! login ; username ! " + username + " ; password ! " + password;
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
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! register ; username ! " + username + " ; password ! " + password;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }
	public void sayHello() throws RemoteException {
		System.out.println("Servidor a correr");
        checkServers check = new checkServers();
        check.start();
	}
//Função que comunica com multicast para ver ligacoes de uma certa pagina
    public String verLigacoes(String username, String page){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! verLigação ; username ! " + username + " ; pagina ! " + page;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }
    //Função que comunica com multicast para ver painel administraçao
    public String verPainelAdmin(String username){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! verAdmin ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        System.out.println(received);
        return received;


    }
    //Função que comunica com multicast para ver pesquisas
    public String verPesquisas(String username){
        String id=chooseMulticastServer();
        String toSend ="server !! "+id+  " ; type ! verPesquisas ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        if (received.equals("")){
            received= "Ainda não efetuou nenhuma pesquisa.";
        }
        return received;


    }
    //Função que comunica com multicast para efetuar pesquisas
    public String pesquisar(String username, String pesquisa) {
        String id=chooseMulticastServer();
        String toSend ="server !! "+id+  " ; type ! search ; username ! " + username + " ; key words ! " + pesquisa;
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

        //System.out.println(received);
        return received;
    }

    //Função que comunica com multicast para indexar url e urls por recursao
    public String indexar(String username, String ws) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! indexar ; username ! " + username + " ; website ! " + ws;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }

    public String logout(String username) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! logout ; username ! " + username + " ; msg ! Logging out";
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
		System.out.println("User: "+username+ " está online com o id: "+ cliente.toString());
        for (String i : usersOnline.keySet())
            System.out.println("key: " + i + " | value: " + usersOnline.get(i));
	}

	public String notifyUserToAdmin(String username,String adminName) throws RemoteException {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+" ; type ! verify ; username ! " + username + " ; msg ! Verify user";
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
    public String verifyAdminPermissions(String username){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+" ; type ! verifyAdmin ; username ! " + username + " ; msg ! Verify admin permissions";
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }

	// ==========================MAIN=============================

	public static void main(String args[]) throws RemoteException, SocketException {
		RMI_S_I server=new RMIServer();
		try {
			Registry r = LocateRegistry.createRegistry(7500);
			System.out.println(LocateRegistry.getRegistry(7500));
			r.rebind("project", server);


		} catch (RemoteException e) {
			boolean programFails = true;
			while (programFails) {
				programFails = false;
				try {
					Thread.sleep(500);

					LocateRegistry.createRegistry(7500).rebind("project",server);
                    run=false;
					System.out.println("Connected! Server Backup assumed");
					run=true;
					checkServers check = new checkServers();
                    check.start();

				} catch (RemoteException | InterruptedException b) {
					System.out.println("Main RMI Server working... Waiting for failures");
					programFails = true;

				}
			}
		}
	}

    private static class checkServers extends Thread {
        @Override
        public void run() {
            while (run){
                multicastServers=checkActiveMulticastServers();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
	}
//Funçao que verifica que servers multicast estao online atraves do envio e rececao de pacotes
        public static ArrayList<String> checkActiveMulticastServers() {
            String s = "server !! 0 ; type ! checkIfOn ; ";
            try {
                MulticastSocket socket = new MulticastSocket();
                InetAddress group = InetAddress.getByName("224.3.2.3");
                socket.joinGroup(group);

                byte[] buffer = s.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("224.3.2.3"), PORT);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
            nSocket = null;

            try {
                nSocket = new DatagramSocket(4372);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            ArrayList<String> arrayList = new ArrayList<>();
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while (true) {

                try {

                    nSocket.receive(request);
                    String temp = new String(request.getData(), 0, request.getLength());
                    String[] split = temp.split(" !! ");

                    arrayList.add(split[1]);
                    break;
                } catch (IOException e) {
                    nSocket.close();

                }


            }
            nSocket.close();
            return arrayList;
        }


//Escolhe ao acaso que server multicast utilizar
    public String chooseMulticastServer() {
        int max = multicastServers.size()-1;
        int min = 0;
        int n = (int) Math.floor(Math.random() * ((max - min) + 1) + min);

        System.out.println("Escolheu o servidor "+ multicastServers.get(n));


        return multicastServers.get(n);
    }
}


