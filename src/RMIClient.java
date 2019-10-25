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
        String op;

        while (true) {
            System.out.println("Bem Vindo! O que desejas fazer? -");
            System.out.println("1- Login");
            System.out.println("2- Register");
            System.out.println("3- Efetuar Pesquisa");
            System.out.println("4- Sair");
            System.out.print("\n> Opcao: ");
            op = myObj.nextLine();
            //int op = Integer.parseInt(opcao);
            if (op.equals("1")) {
                while (true) {
                    String resposta = efetuarLogin();
                    String[] msg = resposta.split("-", 3);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]);
                        server.addUserOnline(msg[2], client);
                        if (msg[1].equals("admin")) {
                            MenuAdmin(msg[2]);
                            break;
                        } else {
                            MenuPrincipal(msg[2]);
                            break;
                        }
                    } else if (resposta.equals("Utilizador não existente, por favor efetue o registo")) {
                        System.out.println(resposta);
                        break;
                    } else if (resposta.equals("Utilizador não existente, por favor efetue o registo ou verifique o username colocado")) {
                        System.out.println(resposta);
                        break;
                    } else if(resposta.equals("Password incorreta! Tente novamente"))
                    {
                        System.out.println(resposta);
                        break;
                    }
                }
            }
            else if (op.equals("2")) {
                while (true) {
                    String resposta = registarUtilizador();
                    String[] msg = resposta.split("-", 3);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]);
                        server.addUserOnline(msg[2], client);
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
            else if (op.equals("3")){
                System.out.println(efetuarPesquisa("anónimo"));
            }
            else if (op.equals("4")) {
                System.out.println("type ! status ; logged ! off ; msg ! Leaving...Bye");
                System.exit(0);
            }
            else{
                System.out.println("Comando incorreto, use outro por favor.");
                menuInicial();
            }

        }
    }

    public static void MenuPrincipal(String username) throws RemoteException {
        String op;
        while(true) {
            System.out.println("\n____________Menu Principal:___________");
            System.out.println("1-Efetuar Pesquisa");
            System.out.println("2-Ver ligações para uma determinada pagina");
            System.out.println("3-Ver historico de pesquisas");
            System.out.println("4-Logout");
            System.out.print("\n> Opcao: ");
            Scanner myObj = new Scanner(System.in);
            op = myObj.nextLine();
            //int op = Integer.parseInt(opcao);
            if (op.equals("1")) {
                System.out.println(efetuarPesquisa(username));
            } else if (op.equals( "2")) {
                System.out.println(verificarLigacoes( username));
            } else if (op.equals("3")) {
                System.out.println(server.verPesquisas(username));
            } else if (op.equals("4")) {
                String answer = efetuarLogout(username);
                System.out.println(answer);
                server.deleteUserOnline(username);
                break;
            }else{
                System.out.println("Comando incorreto, use outro por favor.");
                MenuPrincipal(username);
            }
        }
    }

    public static void MenuAdmin(String username) throws RemoteException {
        String op ;
        while (true) {
            System.out.println("\n_________Menu de Administrador:________");
            System.out.println("1-Efetuar Pesquisa");
            System.out.println("2-Ver ligacoes para uma determinada página");
            System.out.println("3-Ver histórico de pesquisas");
            System.out.println("4-Indexar novo URL");
            System.out.println("5-Ver pagina de administracao");
            System.out.println("6-Dar privilegios de Admin a outro user");
            System.out.println("7-Logout");
            System.out.print("\n> Opcao: ");
            Scanner myObj = new Scanner(System.in);
            op = myObj.nextLine();
            //int op = Integer.parseInt(opcao);
            if (op .equals("1")) {
                System.out.println(efetuarPesquisa(username));
            } else if (op.equals( "2")) {
                System.out.println(verificarLigacoes( username));
            } else if (op.equals( "3")) {
                System.out.println(server.verPesquisas(username));

            } else if (op.equals("4")) {
                System.out.println(indexarURL(username));
            } else if (op.equals( "5")) {

            } else if (op.equals( "6")) {
                System.out.println(darAdmin(username));
            } else if (op.equals("7")) {
                String answer = efetuarLogout(username);
                System.out.println(answer);
                server.deleteUserOnline(username);
                break;
            }
            else{
                System.out.println("Comando incorreto, use outro por favor.");
                MenuAdmin(username);
            }
        }
    }
    public static String darAdmin(String adminName) throws RemoteException{
        System.out.println("Username da pessoa que desejas tornar Admin:");
        String user=scan.nextLine();
        String answer = server.notifyUserToAdmin(user,adminName);
        return answer;
    }

    public static String efetuarLogout(String username) throws RemoteException {
        String flag = server.logout(username);
        return flag;
    }
    public static String efetuarPesquisa(String username) throws RemoteException {
        System.out.println("Pesquisa por:");
        String pesquisa=scan.nextLine();
        System.out.println("A pesquisar... Aguarde por favor.");
        String result = server.pesquisar(username,pesquisa);
        System.out.println(result);
        return result;
    }


    public static String verificarLigacoes(String username) throws RemoteException {
        System.out.println("Quer ver as ligações para que página?");
        String page=scan.nextLine();
        String result = server.verLigacoes(username,page);

        return result;
    }

    public static String efetuarLogin() throws RemoteException {
        System.out.println("Username:");
        String user=scan.nextLine();
        System.out.println("Password:");
        String password=scan.nextLine();
        String flag = server.confereLogin(user,password) ;
        return flag;

    }


    public static String indexarURL(String username) throws RemoteException {
        System.out.println("Que site quer indexar?");
        String site=scan.nextLine();
        String flag = server.indexar(username,site);
        return flag;
    }


    public static String registarUtilizador() throws RemoteException {
        System.out.println("Username:");
        String user=scan.nextLine();
        System.out.println("Password:");
        String password=scan.nextLine();
        String r = server.registaUtilizador(user,password) ;

        return r;
    }

    public void showNotification(String message) throws RemoteException{
        System.out.println(message);
    }

    public static void main(String args[]) {
        try {
            client = new RMIClient();
            server = (RMI_S_I) LocateRegistry.getRegistry(7500).lookup("project");
            server.sayHello();

            menuInicial();

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

    }
}

