import java.rmi.*;

public interface Hello_C_I extends Remote{
	public String sayHello() throws java.rmi.RemoteException;
}