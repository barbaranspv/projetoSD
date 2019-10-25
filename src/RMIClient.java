import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIClient extends UnicastRemoteObject implements RMI_C_I{
    private static RMI_S_I server;
    public static Scanner scan=new Scanner(System.in);
    private static RMIClient client;

    public RMIClient() throws RemoteException {
        super();
    }

    public static void menuInicial() throws RemoteException {
        Scanner myObj = new Scanner(System.in);
        String opcao;
        while (true) {
            System.out.println("Bem Vindo! O que desejas fazer? -");
            System.out.println("1- Login");
            System.out.println("2- Register");
            System.out.println("3- Sair");
            System.out.print("\n> Opcao: ");
            opcao = myObj.nextLine();
            int op = Integer.parseInt(opcao);
            if (op == 1) {
                while (true) {
                    String resposta = efetuarLogin();
                    String[] msg = resposta.split("-", 4);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]+" - "+msg[3]);
                        try {
                            server.addUserOnline(msg[2], client);
                        }
                        catch(RemoteException e){
                            int contador=0;
                            while(contador<30)
                            {
                                try {
                                    Thread.sleep(1000);
                                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                                    server.addUserOnline(msg[2], client);
                                }catch(NotBoundException | InterruptedException | RemoteException m){
                                    contador++;
                                    if(contador==30)
                                        System.exit(-1);
                                }
                            }
                        }
                        if (msg[1].equals("admin")) {
                            MenuAdmin(msg[2]);
                            break;
                        } else {
                            MenuPrincipal(msg[2]);
                            break;
                        }
                    } else if (msg[0].equals("Utilizador não existente, por favor efetue o registo")) {
                        System.out.println(msg[0]);
                        break;
                    } else if (msg[0].equals("Utilizador não existente, por favor efetue o registo ou verifique o username colocado")) {
                        System.out.println(msg[0]);
                        break;
                    } else if(msg[0].equals("Password incorreta! Tente novamente")) {
                        System.out.println(msg[0]);
                        break;
                    }
                }
            }
            else if (op == 2) {
                while (true) {
                    String resposta = registarUtilizador();
                    String[] msg = resposta.split("-", 3);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]);
                        server.addUserOnline(msg[2], client);
                        try {
                            server.addUserOnline(msg[2], client);
                        }
                        catch(RemoteException e){
                            int contador=0;
                            while(contador<30)
                            {
                                try {
                                    Thread.sleep(1000);
                                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                                    server.addUserOnline(msg[2], client);
                                }catch(NotBoundException | InterruptedException | RemoteException m){
                                    contador++;
                                    if(contador==30)
                                        System.exit(-1);
                                }
                            }
                        }
                        if (msg[1].equals("admin")) {
                            System.out.println(msg[2]);
                            MenuAdmin(msg[2]);
                            break;
                        } else {
                            MenuPrincipal(msg[2]);
                            break;
                        }
                    } else
                        System.out.println(resposta);
                }
            }
            else if (op == 3) {
                System.out.println("type ! status ; logged ! off ; msg ! Leaving...Bye");
                System.exit(0);
            }
        }
    }

    public static void MenuPrincipal(String username) throws RemoteException {
        while(true) {
            System.out.println("\n____________Menu Principal:___________");
            System.out.println("1-Efetuar Pesquisa");
            System.out.println("2-Ver ligações para uma determinada pagina");
            System.out.println("3-Ver historico de pesquisas");
            System.out.println("4-Logout");
            System.out.print("\n> Opcao: ");
            Scanner myObj = new Scanner(System.in);
            String opcao = myObj.nextLine();
            int op = Integer.parseInt(opcao);
            if (op == 1) {
                System.out.println(efetuarPesquisa(username));
            } else if (op == 2) {
                System.out.println(verificarLigacoes( username));
            } else if (op == 3) {

            } else if (op == 4) {
                String answer = efetuarLogout(username);
                System.out.println(answer);
                try {
                    server.deleteUserOnline(username);
                }
                catch(RemoteException e){
                    int contador=0;
                    while(contador<30)
                    {
                        try {
                            Thread.sleep(1000);
                            server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                            server.deleteUserOnline(username);
                        }catch(NotBoundException | InterruptedException | RemoteException m){
                            contador++;
                            if(contador==30)
                                System.exit(-1);
                        }
                    }
                }
                break;
            }
        }
    }

    public static void MenuAdmin(String username) throws RemoteException {
        while (true) {
            System.out.println("\n_________Menu de Administrador:________");
            System.out.println("1-Efetuar Pesquisa");
            System.out.println("2-Ver ligacoes para uma determinada página");
            System.out.println("3-Ver histórico de pesquisas");
            System.out.println("4-Indexar novo URL");
            System.out.println("5-Ver pagina de administracao");
            System.out.println("6-Dar previlegios de Admin a outro user");
            System.out.println("7-Logout");
            System.out.print("\n> Opcao: ");
            Scanner myObj = new Scanner(System.in);
            String opcao = myObj.nextLine();
            int op = Integer.parseInt(opcao);
            if (op == 1) {
                System.out.println(efetuarPesquisa(username));
            } else if (op == 2) {
                System.out.println(verificarLigacoes( username));
            } else if (op == 3) {

            } else if (op == 4) {
                System.out.println(indexarURL(username));
            } else if (op == 5) {

            } else if (op == 6) {
                System.out.println(darAdmin(username));
            } else if (op == 7) {
                String answer = efetuarLogout(username);
                System.out.println(answer);
                try {
                    server.deleteUserOnline(username);
                }
                catch(RemoteException e){
                    int contador=0;
                    while(contador<30)
                    {
                        try {
                            Thread.sleep(1000);
                            server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                            server.deleteUserOnline(username);
                        }catch(NotBoundException | InterruptedException | RemoteException m){
                            contador++;
                            if(contador==30)
                                System.exit(-1);
                        }
                    }
                }
                break;
            }
        }
    }
    public static String darAdmin(String adminName) throws RemoteException{
        System.out.println("Username da pessoa que desejas tornar Admin:");
        String user=scan.nextLine();
        String answer = null;
        try {
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
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return answer;
    }

    public static String efetuarLogout(String username) throws RemoteException {
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
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return flag;
    }
    public static String efetuarPesquisa(String username) throws RemoteException {
        System.out.println("Pesquisa por:");
        String pesquisa=scan.nextLine();
        String result = server.pesquisar(username,pesquisa);
        try {
            result = server.pesquisar(username,pesquisa);
        }
        catch(RemoteException e){
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                    result = server.pesquisar(username,pesquisa);
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return result;
    }


    public static String verificarLigacoes(String username) throws RemoteException {
        System.out.println("Quer ver as ligações para que página?");
        String page=scan.nextLine();
        String result = null;
        try {
            result = server.verLigacoes(username,page);
        }
        catch(RemoteException e){
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                    result = server.verLigacoes(username,page);
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return result;
    }

    public static String efetuarLogin() throws RemoteException {
        System.out.println("Username:");
        String user=scan.nextLine();
        System.out.println("Password:");
        String password=scan.nextLine();
        String flag=null;
        try {
            flag = server.confereLogin(user, password);
        }
        catch(RemoteException e){
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                    flag = server.confereLogin(user, password);
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return flag;

    }


    public static String indexarURL(String username) throws RemoteException {
        System.out.println("Que site quer indexar?");
        String site=scan.nextLine();
        String flag = null;
        try {
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
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return flag;
    }


    public static String registarUtilizador() throws RemoteException {
        System.out.println("Username:");
        String user=scan.nextLine();
        System.out.println("Password:");
        String password=scan.nextLine();
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
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
        return r;
    }

    public void showNotification(String message) throws RemoteException{
        System.out.println(message);
    }

    public static void main(String args[]) throws RemoteException {
        try {
            client = new RMIClient();
            server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
            server.sayHello();

            menuInicial();

        } catch (RemoteException | NotBoundException e) {
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }

    }
}

