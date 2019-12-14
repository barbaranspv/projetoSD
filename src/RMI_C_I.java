import java.rmi.*;

public interface RMI_C_I extends Remote{
    void showNotification(String message) throws RemoteException;
}