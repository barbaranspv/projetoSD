import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.3";
    private int PORT = 4371;
    private ArrayList<Utilizador> listaUsers = new ArrayList<Utilizador>();
    private HashMap< String, ArrayList<Site>> dic=leFicheiroObjetosHashMap();; //leFicheiroObjetosHashMap();  new HashMap< String, ArrayList<Site>>();
    private ArrayList<Site> siteArray=leFicheiroObjetosSites();//leFicheiroObjetosSites(); new  ArrayList<Site>()

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));

    }

    public void enviaInfoRMI(DatagramSocket aSocket, InetAddress address, String toSend) {

        try {
            byte[] buffer2 = toSend.getBytes();
            DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, address, 4370);
            aSocket.send(packet2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        boolean existUsername = false;
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
                String[] result = message.split(" ; ");
                String[] type = result[0].split(" ! ");
                //System.out.println(type[1]);
                try {

                    if (type[1].equals("login")) {
                        String username = result[1].split(" ! ")[1];
                        String password = result[2].split(" ! ")[1];
                        System.out.println(username + " " + password);
                        if (listaUsers.size() == 0)
                            enviaInfoRMI(socket, packet.getAddress(), "Utilizador não existente, por favor efetue o registo");
                        else {
                            int i = 0;
                            for (i = listaUsers.size() - 1; i >= 0; i--) {
                                if (username.equals(listaUsers.get(i).username)) {
                                    if (listaUsers.get(i).password.equals(password)) {
                                        if (listaUsers.get(i).admin == true) {
                                            listaUsers.get(i).online = true;
                                            enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca-admin-" + username);
                                            break;
                                        } else {
                                            listaUsers.get(i).online = true;
                                            enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -" + username);
                                            break;
                                        }
                                    } else {
                                        enviaInfoRMI(socket, packet.getAddress(), "Password incorreta! Tente novamente");
                                        break;
                                    }
                                }
                            }
                            if (i == 0)
                                enviaInfoRMI(socket, packet.getAddress(), "Utilizador não existente, por favor efetue o registo ou verifique o username colocado");
                        }
                    } else if (type[1].equals("register")) {
                        String username = result[1].split(" ! ")[1];
                        //System.out.println(username);
                        String password = result[2].split(" ! ")[1];
                        System.out.println(username + " " + password);
                        if (listaUsers.isEmpty() == true) {
                            Utilizador firstUser = new Utilizador(username, password, true);
                            listaUsers.add(firstUser);
                            File fich = new File("Users.txt");
                           /* try {
                               // FileOutputStream is = new FileOutputStream(fich);
                               // ObjectOutputStream ois = new ObjectOutputStream(is);
                                //ois.writeObject(listaUsers);
                                //ois.close();
                            } catch (FileNotFoundException b) {
                                System.out.println("Nao encontrei o ficheiro");
                            } catch (IOException b) {
                                System.out.println("Erro ao escrever no ficheiro");
                            }*/
                            firstUser.online = true;
                            enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca-admin-" + username);
                        } else if (listaUsers.isEmpty() == false) {
                            for (int i = listaUsers.size() - 1; i >= 0; i--) {
                                if (username.equals(listaUsers.get(i).username)) {
                                    existUsername = true;
                                }
                            }
                            if (existUsername == false) {
                                Utilizador user = new Utilizador(username, password, false);
                                listaUsers.add(user);
                                File fich = new File("Users.txt");
                                /* try {
                                    FileOutputStream is = new FileOutputStream(fich);
                                    ObjectOutputStream ois = new ObjectOutputStream(is);
                                    ois.writeObject(listaUsers);
                                    ois.close();
                                } catch (FileNotFoundException b) {
                                    System.out.println("Nao encontrei o ficheiro");
                                } catch (IOException b) {
                                    System.out.println("Erro ao escrever no ficheiro");
                                }*/
                                user.online = true;
                                enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -" + username);
                            } else {
                                System.out.println(username);
                                enviaInfoRMI(socket, packet.getAddress(), "Username já existente!");
                                existUsername = false;
                            }
                        }
                    } else if (type[1].equals("search")) {
                        String username = result[1].split(" ! ")[1];

                        String kw = result[2].split(" ! ")[1];

                        String search= search(kw);
                        System.out.println(username+" esta a fazer uma pesquisa");
                        enviaInfoRMI(socket, packet.getAddress(), search);

                    }else if (type[1].equals("indexar")) {
                        String username = result[1].split(" ! ")[1];
                        String ws = result[2].split(" ! ")[1];

                        String indexar= loadSite(ws);
                        System.out.println(username+" esta a indexar site");
                        enviaInfoRMI(socket, packet.getAddress(), indexar);

                    }else if (type[1].equals("logout")) {
                        String username = result[1].split(" ! ")[1];
                        System.out.println(username + " esta a fazer logout");
                        for (int i = listaUsers.size() - 1; i >= 0; i--) {
                            if (username.equals(listaUsers.get(i).username))
                                listaUsers.get(i).online = false;
                        }
                        enviaInfoRMI(socket, packet.getAddress(), "Fez Logout!");
                    }
                } catch (NumberFormatException n) {
                    System.out.println("Nao foi possivel fazer parseInt da mensagem"); // nao esta a dar bem
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Socket Exception");
        }finally {
            socket.close();
        }
    }
    private void lerFicheiroUsers(){
        File fich = new File("Users.txt");
        if(fich.exists() && fich.isFile())
        {
            try
            {
                FileReader fr=new FileReader(fich);
                BufferedReader br=new BufferedReader(fr);
                String line;
                if((line=br.readLine())!=null)
                {
                    br.close();
                    try
                    {
                        FileInputStream es = new FileInputStream(fich);
                        ObjectInputStream oi = new ObjectInputStream(es);
                        listaUsers=(ArrayList<Utilizador>)oi.readObject();
                        oi.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("Nao encontrei o ficheiro");
                    }
                    catch(IOException e)
                    {
                        System.out.println("Erro ao ler no ficheiro de alunos");
                    }
                    catch(ClassNotFoundException e)
                    {
                        System.out.println("Erro a criar objeto");
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("Nao encontrei o ficheiro");
            }
            catch(IOException e)
            {
                System.out.println("Erro ao ler no ficheiro");
            }
        }
    }

    public String loadSite(String ws){
        Map<String, Integer> countMap ;
        //leFicheiroObjetosHashMap()
        //leFicheiroObjetosSites()
        // Read website
        try {
            if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                ws = "http://".concat(ws);
            int i;

            int controlo=0;
            System.out.println("Loading websites...");
            for (i =0; i< siteArray.size();i++){
                if (siteArray.get(i).equals(ws)){
                    controlo=controlo+1;
                    break;
                }
            }
            if (controlo==0) {
                // Attempt to connect and get the document
                Document doc = Jsoup.connect(ws).get();
                Site site = new Site();
                site.url = ws;
                site.title = doc.title();
                site.text = doc.text();
                countMap = countWords(doc.text());
                site.words = countMap.keySet().toArray(new String[countMap.size()]);
                siteArray.add(site);
            }
            itera(ws,1);



            escreveFicheiroObjetosHashMap();
            escreveFicheiroObjetosSites();
            // Get website text and count words
            String answer=ws+ " indexado, bem como outros indexados recursivamente.";
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
            return "problema com indexação";
        }
    }

    public String search(String kw){
        int conta=0;
        int i;
        String result="";
        Collections.sort(dic.get(kw), (Site s1, Site s2) -> (int)( s2.countPages-s1.countPages));
        if (dic.get(kw).size()<=20){
            for (i=0;i< dic.get(kw).size();i++){
                if ( dic.get(kw).get(i).countPages!=1){
                    conta+=1;
                     result = result + dic.get(kw).get(i).url + " \n"+ dic.get(kw).get(i).text + "\n"+ dic.get(kw).get(i).countPages ;
                    //for (int k=0;  k<dic.get(kw).get(i).pages.size(); k++){
                       // System.out.println( dic.get(kw).get(i).pages.get(k).url);
                    //}
                    //System.out.println();

                }
            }
        }
        else{
            for (i=0;i< 20;i++){
                if ( dic.get(kw).get(i).countPages!=1){
                    conta+=1;
                    result = result + dic.get(kw).get(i).url + " \n"+ dic.get(kw).get(i).text + "\n"+ dic.get(kw).get(i).countPages ;
                    //for (int k=0;  k<dic.get(kw).get(i).pages.size(); k++){
                     //   System.out.println( dic.get(kw).get(i).pages.get(k).url);
                    //}
                    //Thread.sleep(50);
                    //System.out.println();

                }
            }
        }
        result=result+"\n"+ "Foram encontrados "+ dic.get(kw).size() +" resultados! Mostrando os "+ conta + " mais relevantes.";
        return result;
    }



    private void itera(String ws, int num) throws IOException {
        // Read website
        Map<String, Integer> countMap ;
        if (num>-1) {
            try {
                if (!ws.startsWith("http://") && !ws.startsWith("https://"))
                    ws = "http://".concat(ws);

                // Attempt to connect and get the document
                Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/

                // Title
                //System.out.println(doc.title() + "\n");

                // Get all links
                Elements links = doc.select("a[href]");
                int j;
                for (j=0;j<siteArray.size();j++){
                    if (siteArray.get(j).url.equals(ws))
                        break;
                }

                for (Element link : links) {
                    // Ignore bookmarks within the page
                    if (link.attr("href").startsWith("#")) {
                        continue;
                    }

                    // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                    if (!link.attr("href").startsWith("http")) {
                        continue;
                    }
                    // System.out.println("Link: " + link.attr("href"));
                    //System.out.println("Text: " + link.text() + "\n");
                    String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
                    countMap=countWords(text);
                    int controlo=0;
                    int i;
                    for (i =0; i< siteArray.size();i++){
                        if (siteArray.get(i).url.equals(link.attr("href"))){
                            controlo=controlo+1;
                            break;
                        }
                    }
                    Site site = null;
                    if (controlo==0){
                        site= new Site();
                        site.url=link.attr("href");
                        site.title=link.text();
                        site.text=text;
                        site.words=countMap.keySet().toArray(new String[countMap.size()]);
                        siteArray.add(site);
                    }

                    else if (controlo==1){
                        site=siteArray.get(i);

                    }
                    controlo=0;
                    for (i =0; i< site.pages.size();i++){
                        if (site.pages.get(i).url.equals(siteArray.get(j).url)){

                            controlo=controlo+1;
                            break;
                        }
                    }
                    if (controlo==0){
                        site.countPages=site.countPages+1;
                        site.pages.add(siteArray.get(j));
                    }

                    for (String word : countMap.keySet()) {
                        if (dic.get(word)==null){
                            ArrayList<Site> outrosLinks = new ArrayList<>();
                            outrosLinks.add(site);
                            dic.put(word,outrosLinks);
                        }
                        else {
                            controlo=0;
                            for (i=0;i< dic.get(word).size();i++){
                                if (dic.get(word).get(i).url.equals(site.url)){
                                    controlo=controlo+1;
                                    break;
                                }
                            }
                            if (controlo==0){
                                dic.get(word).add(site);
                            }
                        }
                    }
                    itera(link.attr("href"), num - 1);

                }


                // Get website text and count words

            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    }
    protected void escreveFicheiroObjetosSites(){
        File f=new File("sites.txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(siteArray);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro de sites.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro de sites. ");
        }

    }
    protected void escreveFicheiroObjetosHashMap(){
        File f=new File("hashmap.txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(dic);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro de hashmap.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro de hashmap. ");
        }

    }

    protected ArrayList<Site> leFicheiroObjetosSites(){
        File f=new File("sites.txt");
        ArrayList<Site> siteArray= new ArrayList<Site>();
        int i=0;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            siteArray = (ArrayList<Site>)ois.readObject();

            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a abrir ficheiro de sites.");
        } catch (IOException ex) {
            System.out.println("Erro a ler ficheiro de sites.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro a converter objeto de sites.");
        }

        return siteArray;
    }

    protected HashMap< String, ArrayList<Site>>  leFicheiroObjetosHashMap(){
        File f=new File("HashMap.txt");
        HashMap< String, ArrayList<Site>> dic= new HashMap<>() ;
        int i=0;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            dic = (HashMap< String, ArrayList<Site>>)ois.readObject();

            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a abrir ficheiro de hashmap.");
        } catch (IOException ex) {
            System.out.println("Erro a ler ficheiro de hashmap.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro a converter objeto de hashmap.");
        }
        return dic;

    }

    private Map countWords(String text) {
        Map<String, Integer> countMap = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;

        // Get words and respective count
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
                String[] words = line.split("[ ,;:.?!“”(){}\\[\\]<>']+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if ("".equals(word)) {
                        continue;
                    }
                    if (!countMap.containsKey(word)) {
                        countMap.put(word, 1);
                    }
                    else {
                        countMap.put(word, countMap.get(word) + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close reader
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display words and counts
        //for (String word : countMap.keySet()) {
        // if (word.length() >= 3) { // Shall we ignore small words?
        // System.out.println(word + "\t" + countMap.get(word));
        //}
        //}
        return countMap;
    }




    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
        server.lerFicheiroUsers();

        MulticastUser user = new MulticastUser();
        user.start();
    }









}









class MulticastUser extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4371;


    public MulticastUser() {
        super("User " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");
        try {
            socket = new MulticastSocket();  // create socket without binding it (only for sending)
            Scanner keyboardScanner = new Scanner(System.in);
            while (true) {
                String readKeyboard = keyboardScanner.nextLine();
                byte[] buffer = readKeyboard.getBytes();

                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class Utilizador implements Serializable {
    String username;
    String password;
    boolean admin;
    private ArrayList<String> listaURLS = new ArrayList<String>();
    boolean online = false;

    public Utilizador(String username,String password,boolean admin){
        this.username=username;
        this.password=password;
        this.admin=admin;
    }
}
class Site implements Serializable {
    String url;
    String title;
    String text;
    int countPages=0;
    ArrayList<Site> pages= new ArrayList<>();
    String[] words;


}