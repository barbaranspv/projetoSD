package rmiserver;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import uc.sd.apis.FacebookApi2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class RMIServer extends UnicastRemoteObject implements RMI_S_I {
    static private int PORT = 4371;
    private final String MULTICAST_ADDRESS="224.3.2.3";
    private MulticastSocket dSocket;
    private static ArrayList<String> multicastServers = new ArrayList<>();
    private  HashMap<String, String> notificacoes;
    private  HashMap<String, String> notificacoesBean= new HashMap<>();
    private  HashMap<String,RMI_C_I> usersOnline;
    private static DatagramSocket nSocket;
    private static boolean run=true;
    private static checkServers check=new checkServers();
    private OAuthService service;

    public RMIServer() throws RemoteException, SocketException {
        super();
        check.start();
        try{

            dSocket =  new MulticastSocket(4370);


        }
        catch(IOException b){
            System.out.println("Falha ao criar o socket");
        }
        notificacoes = new HashMap<>();
        usersOnline = new HashMap<>();
    }
    //criar classe com extend thread para aplicar o run para o backup



    public String ola() throws RemoteException{
        System.out.println("Print no rmi do tomcat");
        return "ola123";
    }


    public void ping() {
        System.out.println("Ping recebido");
    }

    /**
     * Função para enviar pacote p multicast
     * @param s
     */
//Função para enviar pacote p multicast
    public void enviarPacote(String s) {
        try {
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            byte[] buffer = s.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(MULTICAST_ADDRESS), PORT);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Função para receber pacote do multicast
     * @return
     */
//Função para receber pacote do multicast
    public String recebePacote() {

        byte[] buffer = new byte[2000];
        DatagramPacket message = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                dSocket.setSoTimeout(40000);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                dSocket.receive(message);
                System.out.println("Mensagem: "+message.toString());
                break;
            } catch (IOException e) {
                //dSocket.close();
                //return "Ocorreu um problema, tente mais tarde.";
            }
        }
        return new String(message.getData(), 0, message.getLength());
    }

    /**
     * Função que envia e recebe info do multicast quanto ao login
     * @param username
     * @param password
     * @return
     */
    public String confereLogin(String username, String password) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! login ; username ! " + username + " ; password ! " + password;
        enviarPacote(toSend); //envia ao Multicast Server
        String received = recebePacote();
        System.out.println(received);
        String[] result = received.split("-");
        if(result[0].equals("type ! status ; logged ! on ; msg ! Welcome to ucBusca")) {
            if(notificacoes.containsKey(username)){
                String notif = notificacoes.get(username);
                notificacoes.remove(username);
                return received+"-Tem uma notificação pendente: "+notif;
            }
        }
        return received+"-Nao tem notificacoes";
    }

    /**
     * Função que envia e recebe info do multicast face ao registo
     * @param username
     * @param password
     * @return
     */

    public String registaUtilizador(String username, String password) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! register ; username ! " + username + " ; password ! " + password;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }

    /**
     * Função que faz print quando servidor começa a correr e inicia checkservers, que irá verificar servidores multicast ativos
     * @throws RemoteException
     */
    public void sayHello() throws RemoteException {
        System.out.println("Servidor a correr");

    }

    /**
     * Função que comunica com multicast para ver ligacoes de uma certa pagina
     * @param username
     * @param page
     * @return
     */
//Função que comunica com multicast para ver ligacoes de uma certa pagina
    public String verLigacoes(String username, String page){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! verLigação ; username ! " + username + " ; pagina ! " + page;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }


    /**
     * Função que comunica com multicast para ver painel administraçao
     * @param username
     * @return
     */
    //Função que comunica com multicast para ver painel administraçao
    public String verPainelAdmin(String username){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! verAdmin ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        System.out.println(received);
        received=received+ "Servidores multicast ativos:\n"+ multicastServers.size();
        return received;


    }


    /**
     * Função que comunica com multicast para ver pesquisas
     * @param username
     * @return
     */
    //Função que comunica com multicast para ver pesquisas
    public String verPesquisas(String username){
        String id=chooseMulticastServer();
        String toSend ="server !! "+id+  " ; type ! verPesquisas ; username ! " + username ;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        if (received.equals("")){
            received= "Ainda não efetuou nenhuma pesquisa.";
        }
        return received;


    }

    /**
     * Função que comunica com multicast para efetuar pesquisas
     * @param username
     * @param pesquisa
     * @return
     */
    //Função que comunica com multicast para efetuar pesquisas
    public ArrayList pesquisar(String username, String pesquisa) {
        String id=chooseMulticastServer();
        String toSend ="server !! "+id+  " ; type ! search ; username ! " + username + " ; key words ! " + pesquisa;
        enviarPacote(toSend); //enviar ao Multicast Server
        String size;
        String recebe;
        ArrayList<String> received = new ArrayList();
        int sizeint;
        size = recebePacote();
        sizeint = Integer.parseInt(size);
        if (sizeint != 0) {
            for (int i = 0; i < sizeint; i++) {
                recebe=recebePacote();
                received.add(recebe + "Linguagem original: " + getLang(recebe)+"\n");
            }

            received.add( recebePacote() + "Mostrando os " + sizeint + " mais relevantes!");
        }

        else
            received.add(recebePacote());

        //System.out.println(received);
        return received;
    }





    public String getLang(String string) {
        String lang="";
        String text=null;
        String[] temp=null;
        try {
            if (!string.equals("")){
                // Initiate the REST client.
                temp=string.split("\n");
                text=temp[2].replaceAll(" ","%20");

                System.out.println(string);
                URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/detect?key=trnsl.1.1.20191211T202548Z.c64b5c1f5cdd8ab5.be998cf218e819ba4779266908b1cbc1bc41c18d&text="+text);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // HTTP Verb
                connection.setRequestMethod("GET");
                // Get requests data from the server.

                connection.setRequestProperty("Content-Type", "application/json");

                int status = connection.getResponseCode();
                BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();
                System.out.println(content);
                JSONObject jsonObj = (JSONObject) JSONValue.parse(content.toString());

                lang= (String) jsonObj.get("lang");
                Reader streamReader = null;

                if (status > 299) {
                    streamReader = new InputStreamReader(connection.getErrorStream());
                } else {
                    streamReader = new InputStreamReader(connection.getInputStream());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return lang;
    }





    public String traduzir(String texto) {
        JSONArray translated=null;
        String translatedstr=null;
        String text=null;
        try {
            // Initiate the REST client.
            text=texto.replaceAll(" ","%20");

            URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20191211T202548Z.c64b5c1f5cdd8ab5.be998cf218e819ba4779266908b1cbc1bc41c18d&text="+text+"&lang=pt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // HTTP Verb
            connection.setRequestMethod("GET");
            // Get requests data from the server.

            connection.setRequestProperty("Content-Type", "application/json");

            int status = connection.getResponseCode();
            BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            System.out.println(content);
            JSONObject jsonObj = (JSONObject) JSONValue.parse(content.toString());

            translated= (JSONArray)jsonObj.get("text");
            if (translated != null) {

                translatedstr=translated.get(0).toString();

            }
            System.out.println(translatedstr);
            Reader streamReader = null;

            if (status > 299) {
                streamReader = new InputStreamReader(connection.getErrorStream());
            } else {
                streamReader = new InputStreamReader(connection.getInputStream());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return translatedstr;
    }
    // ==========================MAIN=============================

    public static void main(String args[]) throws RemoteException, SocketException {
        RMI_S_I server=new RMIServer();
        try {
            Registry r = LocateRegistry.createRegistry(7500);
            System.out.println(LocateRegistry.getRegistry(7500));
            r.rebind("project", server);


        } catch (RemoteException e) {
            boolean programFails = true;
            while (programFails) {
                programFails = false;
                try {
                    Thread.sleep(500);
                    LocateRegistry.createRegistry(7500).rebind("project",server);
                    run=false;
                    System.out.println("Connected! Server Backup assumed");

                    run=true;
                    check = new checkServers();
                    check.start();

                } catch (RemoteException | InterruptedException b) {
                    System.out.println("Main RMI Server working... Waiting for failures");
                    programFails = true;

                }
            }
        }
    }
    /**
     * Função que comunica com multicast para indexar url e urls por recursao
     * @param username
     * @param ws
     * @return
     */
    //Função que comunica com multicast para indexar url e urls por recursao
    public String indexar(String username, String ws) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! indexar ; username ! " + username + " ; website ! " + ws;
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }

    /**
     * Função que comunica com multicast para fazer logout
     * @param username
     * @return
     */
    public String logout(String username) {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+ " ; type ! logout ; username ! " + username + " ; msg ! Logging out";
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }


    /**
     * Função que remove user de users online
     * @param username
     */
    public void deleteUserOnline(String username)
    {
        for(String name : usersOnline.keySet())
        {
            if(name.equals(username))
                usersOnline.remove(name);
        }
        System.out.println("User: "+username+ " ficou offline");
        for (String i : usersOnline.keySet()) {
            System.out.println("key: " + i + " | value: " + usersOnline.get(i));
        }
    }

    /**
     * Função que adiciona user a users online
     * @param username
     * @param cliente
     */
    public void addUserOnline(String username, RMI_C_I cliente){
        usersOnline.put(username,cliente);
		/*
		if(username==null)
			System.out.println("Username a null");
		if(cliente==null)
			System.out.println("cliente a null");
		 */
        System.out.println("User: "+username+ " está online com o id: "+ cliente.toString());
        for (String i : usersOnline.keySet())
            System.out.println("key: " + i + " | value: " + usersOnline.get(i));
    }



    public String addFacebook() throws RemoteException{


        Token EMPTY_TOKEN = null;
        // Replace these with your own api key and secret
        String apiKey = "xxxxxxx";
        String apiSecret = "xxxxxx";

        service = new ServiceBuilder()
                .provider(FacebookApi2.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("https://localhost:8443/ProjetoSD/adfacebook2.action") // Do not change this.
                .scope("public_profile")
                .build();

        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Scribe here:");
        System.out.println(authorizationUrl);
        return authorizationUrl;
    }

    @Override
    public String authUser(String code) throws RemoteException {
        String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
        Token EMPTY_TOKEN = null;

        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        Verifier verifier = new Verifier(code);


        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
        return response.getBody();
    }




    /**
     * Função para notificar user que é admin agora
     * @param username
     * @param adminName
     * @return
     * @throws RemoteException
     */
    public String notifyUserToAdmin(String username,String adminName) throws RemoteException {
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+" ; type ! verify ; username ! " + username + " ; msg ! Verify user";
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        String[] result = received.split(" ; ");
        String[] info = result[2].split(" ! ");
        String message = "O administrador "+adminName+" tornou-te tambem um Administrador! Parabens";
        if(info[1].equals("User successfully verified")) {
            if (usersOnline.containsKey(username)){ //se o user que se vai tornar admin estiver online, temos de o notificar
                //notificar o utilizador que é admin
                usersOnline.get(username).showNotification(message);
                return "n|Definiste o utilizador " + username + " como Administrador.";
            }
            else{
                //guardar notificacao para quando ficar online
                notificacoes.put(username,message);
                //quando alguem ficar online verificar as notificacoes
                return "n|Definiste o utilizador " + username + " como Administrador. Ele sera notificado quando estiver online";
            }
        }
        else if(info[1].equals("User not found"))
            return "Utilizador não encontrado!";
        else {
            System.out.println(info);
            return "fail to give Admin permissions";
        }
    }



    public HashMap<String, String> buscaParaNotificar(String username){
        HashMap<String, String> notificacoesBean2;
        System.out.println("notificacoesBean size: "+this.notificacoesBean.size());
        notificacoesBean2=(HashMap) this.notificacoesBean.clone();
        System.out.println("notificacoesBean2 size: "+notificacoesBean2.size());
        this.notificacoesBean.remove(username);
        System.out.println("notificacoesBean2 size apos remove: "+notificacoesBean2.size());
        return notificacoesBean2;
    }

    /**
     * Verifica se é admin
     * @param username
     * @return
     */
    public String verifyAdminPermissions(String username){
        String id=chooseMulticastServer();
        String toSend = "server !! "+id+" ; type ! verifyAdmin ; username ! " + username + " ; msg ! Verify admin permissions";
        enviarPacote(toSend); //enviar ao Multicast Server
        String received = recebePacote();
        return received;
    }



    private static class checkServers extends Thread {
        @Override
        public void run() {
            while (run){
                multicastServers=checkActiveMulticastServers();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Funçao que verifica que servers multicast estao online atraves do envio e rececao de pacotes
     * @return
     */
//Funçao que verifica que servers multicast estao online atraves do envio e rececao de pacotes
    public static ArrayList<String> checkActiveMulticastServers() {
        String s = "server !! 0 ; type ! checkIfOn ; ";
        try {
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName("224.3.2.3");
            socket.joinGroup(group);
            System.out.println("Demora");
            byte[] buffer = s.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("224.3.2.3"), PORT);
            socket.send(packet);
            System.out.println("done");

        } catch (IOException e) {
            e.printStackTrace();
        }
        nSocket = null;

        try {
            nSocket = new DatagramSocket(4372);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        ArrayList<String> arrayList = new ArrayList<>();
        byte[] buffer = new byte[1000];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        while (true) {

            try {

                nSocket.receive(request);
                String temp = new String(request.getData(), 0, request.getLength());
                String[] split = temp.split(" !! ");
                arrayList.add(split[1]);
                break;
            } catch (IOException e) {
                nSocket.close();

            }


        }
        nSocket.close();
        return arrayList;
    }

    /**
     * Escolhe ao acaso que server multicast utilizar
     * @return
     */
//Escolhe ao acaso que server multicast utilizar
    public String chooseMulticastServer() {
        int max = multicastServers.size()-1;
        int min = 0;
        int n = (int) Math.floor(Math.random() * ((max - min) + 1) + min);

        System.out.println("Escolheu o servidor "+ multicastServers.get(n));


        return multicastServers.get(n);
    }
}