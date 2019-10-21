import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class RMIClient {
    private static RMI_S_I server;
    public static Scanner scan=new Scanner(System.in);

    public static void menuInicial() throws RemoteException {
        while (true) {
            System.out.println("Bem Vindo! O que desejas fazer? -");
            System.out.println("1- Login");
            System.out.println("2- Register");
            System.out.println("0- Sair");
            System.out.print("\n> Opcao: ");
            Scanner myObj = new Scanner(System.in);
            String opcao = myObj.nextLine();
            int op = Integer.parseInt(opcao);
            if (op == 1) {
                while (true) {
                    String resposta = efetuarLogin();
                    String[] msg = resposta.split("-", 3);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]);
                        if (msg[1].equals("admin")) {
                            MenuAdmin(msg[2]);
                            break;
                        } else {
                            MenuPrincipal();
                            break;
                        }
                    } else if (resposta.equals("Utilizador não existente, por favor efetue o registo")) {
                        System.out.println(resposta);
                        menuInicial();
                        break;
                    } else if (resposta.equals("Username não encontrado, por favor efetue o registo ou verifique o username colocado")) {
                        System.out.println(resposta);
                        menuInicial();
                    }
                }

            }
            if (op == 2) {
                while (true) {
                    String resposta = registarUtilizador();
                    String[] msg = resposta.split("-", 3);
                    if (msg[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
                        System.out.println(msg[0]);
                        if (msg[1].equals("admin")) {
                            MenuAdmin(msg[2]);
                            break;
                        } else {
                            MenuPrincipal();
                            break;
                        }
                    } else
                        System.out.println(resposta);
                }
            }
            if (op == 3) {
                System.exit(0);
            }
        }
    }

    public static void MenuPrincipal(String username) throws RemoteException {
        System.out.println("\n____________Menu Principal:___________");
        System.out.println("1-Efetuar Pesquisa");
        System.out.println("2-Ver ligações de uma determinada pagina");
        System.out.println("3-Ver historico de pesquisas");
        System.out.println("4-Logout");
        System.out.print("\n> Opcao: ");
        Scanner myObj = new Scanner(System.in);
        String opcao = myObj.nextLine();
        int op=Integer.parseInt(opcao);
        if(op==1) {
            System.out.println("Pesquisa por:");
            Scanner myObj2 = new Scanner(System.in);
            String pesquisa = myObj.nextLine();
            String resposta = efetuarPesquisa(username,pesquisa);
        }
    }

    public static void MenuAdmin() throws RemoteException {
        System.out.println("\n_________Menu de Administrador:________");
        System.out.println("1-Efetuar Pesquisa");
        System.out.println("2-Ver ligacoes de uma determinada página");
        System.out.println("3-Ver histórico de pesquisas");
        System.out.println("4-Indexar novo URL");
        System.out.println("5-Ver pagina de administracao");
        System.out.println("6-Dar previlegios de Admin a outro user");
        System.out.println("7-Logout");
        System.out.print("\n> Opcao: ");
        Scanner myObj = new Scanner(System.in);
        String opcao = myObj.nextLine();
        int op=Integer.parseInt(opcao);
    }

    public static String efetuarLogin() throws RemoteException {
        System.out.println("Username:");
        String user=scan.nextLine();
        System.out.println("Password:");
        String password=scan.nextLine();
        String flag = server.confereLogin(user,password) ;
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

    public static void main(String args[]) {


        try {
            server = (RMI_S_I) LocateRegistry.getRegistry(7000).lookup("project");
            server.sayHello();

            menuInicial();

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

    }
}
