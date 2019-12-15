package rmiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface RMI_S_I extends Remote {
    public String ola() throws RemoteException;
    public void sayHello() throws RemoteException;
    public String confereLogin(String username, String password) throws RemoteException;
    public String registaUtilizador(String username, String password)throws RemoteException;
    public ArrayList<String > pesquisar(String username, String pesquisa) throws RemoteException;
    public String logout(String username) throws RemoteException;
    String indexar(String username, String site) throws RemoteException;
    public void ping() throws RemoteException;
    //public void addUserOnline(String username, RMI_C_I client) throws RemoteException;
    public String verifyAdminPermissions(String username) throws RemoteException;

    String verLigacoes(String username, String page)throws RemoteException;

    public void deleteUserOnline(String username) throws RemoteException;
    public String notifyUserToAdmin(String username, String adminName) throws RemoteException;

    String verPesquisas(String username) throws RemoteException;

    String verPainelAdmin(String username) throws RemoteException;

    HashMap<String, String> buscaParaNotificar(String username) throws RemoteException;
    String traduzir(String s) throws RemoteException;
    String addFacebook() throws RemoteException;
    String authUser(String code) throws RemoteException;





}