package rmiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_C_I extends Remote{
    void showNotification(String message) throws RemoteException;
}