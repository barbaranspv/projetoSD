import java.rmi.*;

public interface RMI_S_I extends Remote {
    public String sayHello() throws java.rmi.RemoteException;
    public String confereLogin(String username,String password) throws RemoteException;
    public String registaUtilizador(String username,String password)throws RemoteException;
    public String pesquisar(String username,String pesquisa) throws RemoteException;
    public String logout(String username) throws RemoteException;
}