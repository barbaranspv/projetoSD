import java.rmi.*;

public interface RMI_C_I extends Remote{
	public String sayHello() throws java.rmi.RemoteException;
}