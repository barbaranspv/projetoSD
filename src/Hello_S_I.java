import java.rmi.*;

public interface Hello_S_I extends Remote {
	public String sayHello() throws java.rmi.RemoteException;
    public String confereLogin(String username,String password) throws RemoteException;
    public String registaUtilizador(String username,String password)throws RemoteException;
}