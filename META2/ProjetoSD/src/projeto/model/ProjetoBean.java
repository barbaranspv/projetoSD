
package projeto.model;

import rmiserver.RMI_S_I;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ProjetoBean {
	private RMI_S_I server;
	private String username; // username and password supplied by the user
	private String password;
	private Set<WebSocketAnnotation> websockets;
	HashMap<String, String> usersParaDarAdmin;

	public ProjetoBean() {
		try {
			server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");

		} catch (RemoteException | NotBoundException e) {
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
	}

	public String getOla() throws RemoteException {
		return server.ola();
	}



	public String confereLogin(String user, String password) throws RemoteException {
		String resposta;
		String[] msg;
		resposta = efetuarLogin(user,password);
		msg = resposta.split("-", 4);
		if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
			//server.addUserOnline(msg[2], client);

			System.out.println(msg[0] + " - " + msg[3]);
			if (msg[1].equals("admin")) {
				//MenuAdmin(msg[2]);
				return "admin"+ "-" + msg[3];
			} else {
				//MenuPrincipal(msg[2]);
				return "normal"+ "-" + msg[3]+"-"+msg[4];
			}
		}

		if (msg[0].equals("Utilizador não existente, por favor efetue o registo")) {
			System.out.println(msg[0]);
			return "Utilizador não existente, por favor efetue o registo";
		}

		else if (msg[0].equals("Utilizador não existente, por favor efetue o registo ou verifique o username colocado")) {
			System.out.println(msg[0]);
			return "Utilizador não existente, por favor efetue o registo ou verifique o username colocado";

		}else if (msg[0].equals("Password incorreta! Tente novamente")){
			return "Password incorreta! Tente novamente";
		}
		return "Ocorreu um erro, volte a tentar.";

}

	public String registaUtilizador(String user, String password) throws RemoteException {
		String resposta = registarUtilizador( user, password);
		String[] msg = resposta.split("-", 3);
		if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
			System.out.println(msg[0]);
			//server.addUserOnline(msg[2], client);
			//try {
				//server.addUserOnline(msg[2], client);
			//}
			//catch(RemoteException e){
				int contador=0;
				while(contador<30)
				{
					try {
						Thread.sleep(1000);
						server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
						//server.addUserOnline(msg[2], client);
						break;
					}catch(NotBoundException | InterruptedException | RemoteException m){
						contador++;
						if(contador==30)
							System.exit(-1);
					}
				}
			//}
			if (msg[1].equals("admin")) {
				System.out.println(msg[2]);
				return "admin";
				//MenuAdmin(msg[2]);
			} else {
				return "normal";
				//MenuPrincipal(msg[2]);
			}

		} else
			System.out.println(resposta);
			return resposta;

	}




	public String registarUtilizador(String user, String password) throws RemoteException {

		String r = null;
		try {
			r = server.registaUtilizador(user,password);

		}
		catch(RemoteException e){
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					r = server.registaUtilizador(user,password);
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
		return r;
	}

	public String efetuarLogin(String user, String password) throws RemoteException {

		String flag=null;

		try {
			flag = server.confereLogin(user, password);
		} catch (RemoteException var7) {
			int contador = 0;

			while(contador < 30) {
				try {
					Thread.sleep(1000L);
					server = (RMI_S_I)LocateRegistry.getRegistry(7500).lookup("project");
					flag = server.confereLogin(user, password);
					break;
				} catch (InterruptedException | RemoteException | NotBoundException var6) {
					++contador;
					if (contador == 30) {
						System.exit(-1);
					}
				}
			}
		}

		return flag;
	}

		public  Boolean indexarURL(String username, String site) throws RemoteException {
		System.out.println("Que site quer indexar?");
		String flag = null;
		try {
			System.out.println("A indexar website... Aguarde por favor");
			flag = server.indexar(username,site);
		}
		catch(RemoteException e){
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					flag = server.indexar(username,site);
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
		if (flag.equals( "problema com indexação"))
			return false;
		else
			return true;
	}




	public String[] verificarLigacoes(String username, String page) throws RemoteException {

		String[] result=null;
		String temp ;
		System.out.println("A buscar ligações.Aguarde por favor...");
		try {
			temp = server.verLigacoes(username,page);
			result=temp.split("\n");
		}
		catch(RemoteException e){
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					temp = server.verLigacoes(username,page);
					result=temp.split("\n");
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
		return result;

	}
	public String adFacebook() throws RemoteException {
		String result;
		result=server.addFacebook();
		return result;
	}



	public String adFacebook2(String code) throws RemoteException {
		String result;
		result=server.authUser(code);
		System.out.println(result);
		return result;
	}
	public void traduzPesquisa(ArrayList<String[]> temp)throws RemoteException{
		for (int i=0;i<temp.size();i++){
			for (int j=0;j<temp.get(i).length;j++){
				if (j==0 || j==2){
					if (!temp.get(i)[j].equals("")){
						temp.get(i)[j]=server.traduzir(temp.get(i)[j]);
						System.out.println(temp.get(i)[j]);}
				}
			}

		}
	}
	public ArrayList efetuarPesquisa(String username, String pesquisa) throws RemoteException {
        ArrayList<String[]> result=new ArrayList<>();
		ArrayList<String> temp;
        System.out.println("A pesquisar... Aguarde por favor.");
        try {
            temp = server.pesquisar(username,pesquisa);
            for (int i=0;i<temp.size();i++){
            	result.add(temp.get(i).split("\n"));

			}
        }
        catch(RemoteException e){
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					temp = server.pesquisar(username,pesquisa);
					for (int i=0;i<temp.size();i++){
						result.add(temp.get(i).split("\n"));

					}                    break;
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return result;
	}

	public String efetuarLogout(String username) throws RemoteException {
		String flag = null;
		try {
			flag = server.logout(username);
		}
		catch(RemoteException e){
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					flag = server.logout(username);
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
		return flag;
	}


	public String[] painelAdmin(String username) throws RemoteException {
		String temp;
		String[] result;
		temp = server.verPainelAdmin(username);
		result= temp.split("\n");
		return result;
	}


	public String[] verPesquisas(String username) throws RemoteException {
		String[] result;
		String temp;
		temp = server.verPesquisas(username);
		result= temp.split("\n");
		return result;
	}

	public  String darAdmin(String adminName,String user) throws RemoteException{
		System.out.println("Username da pessoa que desejas tornar Admin:");
		String answer = null;
		int flagAcheiSocketUser=0;
		try {
			System.out.println(user + adminName);
			answer = server.notifyUserToAdmin(user,adminName);
		}
		catch(RemoteException e){
			int contador=0;
			while(contador<30)
			{
				try {
					Thread.sleep(1000);
					server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
					answer = server.notifyUserToAdmin(user,adminName);
					break;
				}catch(NotBoundException | InterruptedException | RemoteException m){
					contador++;
					if(contador==30)
						System.exit(-1);
				}
			}
		}
		String[] arrOfStr = answer.split("|", 2);
		System.out.println("ESTOU NA FUNCAO DAR ADMIN: "+answer);
		if(arrOfStr[0].equals("n"))
		{
			String[] descobreUser = arrOfStr[1].split(" ");
			websockets= WebSocketAnnotation.utilizadores.get(descobreUser[3]);

			for(WebSocketAnnotation socket:websockets){
				socket.sendMessage("O administrador "+adminName+" tornou-te tambem um Administrador! Dá refresh para teres acesso ao novo menu!");
			}
		}
		return answer;
	}

	public void getNotificacoes(String username) throws RemoteException {
		this.usersParaDarAdmin = (HashMap) server.buscaParaNotificar(username).clone();
		if(usersParaDarAdmin.containsKey(username)){
			websockets= WebSocketAnnotation.utilizadores.get(username);
			for(WebSocketAnnotation socket:websockets){
				socket.sendMessage("Foste promovido a administrador enquanto estiveste fora!");
			}
		}
	}


	public void setUsername(String username) {

		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
