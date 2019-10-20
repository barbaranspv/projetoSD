import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class RMIClient {
    private static Hello_S_I server;
    public static Scanner scan=new Scanner(System.in);

    public static void menuInicial() throws RemoteException {
        System.out.println("Bem Vindo! O que desejas fazer? -");
        System.out.println("1- Login");
        System.out.println("2- Register");
        System.out.println("0- Sair");
        System.out.print("\n> Opcao: ");
        Scanner myObj = new Scanner(System.in);
        String opcao = myObj.nextLine();
        int op=Integer.parseInt(opcao);
        if (op==1){
            System.out.println(efetuarLogin());
        }if (op==2){
            System.out.println(registarUtilizador());
        }
        while (true){
            opcao = myObj.nextLine();

        }

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
            server = (Hello_S_I) LocateRegistry.getRegistry(7000).lookup("project");
            server.sayHello();

            menuInicial();

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

    }
}
