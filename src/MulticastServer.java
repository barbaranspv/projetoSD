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
    public int id;
    //public ArrayList<Utilizador> listaUsers = new ArrayList<Utilizador>();
    //public HashMap<String, Integer> pesquisas = leFicheiroPesquisas();

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
        id= (int) (Math.random() * 1000);
        System.out.println(id);
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
                //System.out.println(message);

                String[] result = message.split(" ; ");
                String[] server = result[0].split(" !! ");
                String[] type = result[1].split(" ! ");
                //System.out.println(type[1]);
                try {
                    if (type[1].equals("checkIfOn")) {
                        DatagramSocket checkSocket = new DatagramSocket();
                        try {
                            String toSend= "server !! "+id;
                            byte[] buffer2 = toSend.getBytes();
                            DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, packet.getAddress(),4372);
                            checkSocket.send(packet2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Integer.parseInt(server[1])==id){
                        if (type[1].equals("login")) {
                            System.out.println("entrei no login");
                            String username = result[2].split(" ! ")[1];
                            System.out.println(username);
                            String password = result[3].split(" ! ")[1];
                            System.out.println(username + " " + password);
                            ArrayList<Utilizador> listaUsers =lerFicheiroUsers(); ;
                            if (listaUsers.size() == 0)
                                enviaInfoRMI(socket, packet.getAddress(), "Utilizador não existente, por favor efetue o registo");
                            else {
                                int i;
                                int flagPercorreuTudo=1;
                                for (i = listaUsers.size() - 1; i >= 0; i--) {
                                    if (username.equals(listaUsers.get(i).username)) {
                                        if (listaUsers.get(i).password.equals(password)) {
                                            if (listaUsers.get(i).admin == true) {
                                                enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca-admin-" + username);
                                                flagPercorreuTudo=0;
                                                break;
                                            } else {
                                                enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -" + username);
                                                flagPercorreuTudo=0;
                                                break;
                                            }
                                        } else {
                                            System.out.println("password incorreta");
                                            enviaInfoRMI(socket, packet.getAddress(), "Password incorreta! Tente novamente");
                                            flagPercorreuTudo=0;
                                            break;
                                        }
                                    }
                                }
                                if (flagPercorreuTudo == 1) {
                                    enviaInfoRMI(socket, packet.getAddress(), "Utilizador não existente, por favor efetue o registo ou verifique o username colocado");
                                }
                            }
                        }
                        else if (type[1].equals("register")) {
                            ArrayList<Utilizador> listaUsers =lerFicheiroUsers(); ;
                            String username = result[2].split(" ! ")[1];
                            //System.out.println(username);
                            String password = result[3].split(" ! ")[1];
                            System.out.println(username + " " + password);
                            if (listaUsers.isEmpty() == true) {
                                Utilizador firstUser = new Utilizador(username, password, true);
                                listaUsers.add(firstUser);
                                escreverFicheiroUsers(listaUsers);
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
                                    escreverFicheiroUsers(listaUsers);
                                    enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! on ; msg ! Welcome to ucBusca- -" + username);
                                } else {
                                    System.out.println(username);
                                    enviaInfoRMI(socket, packet.getAddress(), "Username já existente!");
                                    existUsername = false;
                                }
                            }
                        }

                        else if (type[1].equals("search")) {
                            String username = result[2].split(" ! ")[1];
                            String kw = result[3].split(" ! ")[1];
                            HashMap<String, Integer> pesquisas = leFicheiroPesquisas();
                            if (pesquisas.containsKey(kw))
                                pesquisas.put(kw,pesquisas.get(kw)+1);
                            else
                                pesquisas.put(kw,1);
                            escreveFicheiroPesquisas(pesquisas);
                            String[] kwArray = kw.split(" ");
                            String search="";
                            ArrayList<Utilizador> listaUsers =lerFicheiroUsers(); ;
                            for (int i=0; i< listaUsers.size();i++){
                                if (username.equals(listaUsers.get(i).username)){
                                    listaUsers.get(i).pesquisas.add(kw);
                                    escreverFicheiroUsers(listaUsers);
                                    break;
                                }
                            }
                            if (kwArray.length==1){
                                System.out.println(username + " esta a fazer uma pesquisa");
                                search=search(kwArray[0],socket,packet);
                                System.out.println(search);
                                enviaInfoRMI(socket, packet.getAddress(), search);
                            }
                            else {
                                System.out.println(username + " esta a fazer uma pesquisa");
                                search= searchMultiple(kwArray, socket,packet);
                                System.out.println(search);
                                enviaInfoRMI(socket, packet.getAddress(), search);

                            }
                        }else if (type[1].equals("indexar")) {
                            String username = result[2].split(" ! ")[1];
                            String ws = result[3].split(" ! ")[1];
                            String indexar= loadSite(ws);
                            System.out.println(username+" esta a indexar site");
                            enviaInfoRMI(socket, packet.getAddress(), indexar);

                        }
                        else if (type[1].equals("verLigação")){
                            String username = result[2].split(" ! ")[1];
                            String ws = result[3].split(" ! ")[1];
                            String ligacoes= verLigacoes(ws);
                            System.out.println(ligacoes);
                            System.out.println(username+" esta a ver ligacoes");
                            enviaInfoRMI(socket, packet.getAddress(), ligacoes);


                        }else if (type[1].equals("verPesquisas")){
                            String username = result[2].split(" ! ")[1];
                            String pesquisas="";
                            ArrayList<Utilizador> listaUsers =lerFicheiroUsers(); ;
                            for (int i=0; i< listaUsers.size();i++){
                                if (username.equals(listaUsers.get(i).username)){
                                    for (int j=0;j< listaUsers.get(i).pesquisas.size();j++ ){
                                        if (j==0)
                                            pesquisas="Pesquisas:\n";
                                        pesquisas=pesquisas+ listaUsers.get(i).pesquisas.get(j) +"\n";
                                    }
                                    break;
                                }
                            }enviaInfoRMI(socket, packet.getAddress(), pesquisas);
                        }

                        else if (type[1].equals("verAdmin")){
                            String username = result[2].split(" ! ")[1];
                            System.out.println(username + " esta a ver painel de admin");
                            String painelAdmin = verPainelAdmin();
                            System.out.println(painelAdmin);
                            enviaInfoRMI(socket, packet.getAddress(), painelAdmin);

                        }
                        else if (type[1].equals("logout")) {
                            System.out.println("entrei no logout");
                            String username = result[2].split(" ! ")[1];
                            System.out.println(username + " esta a fazer logout");
                            enviaInfoRMI(socket, packet.getAddress(), "type ! status ; logged ! off ; msg ! Goodbye!");
                            System.out.println("enviei a info");

                        }
                        else if (type[1].equals("verify")) {
                            int userExist=0;
                            ArrayList<Utilizador> listaUsers =lerFicheiroUsers(); ;

                            int i;
                            String username = result[2].split(" ! ")[1];
                            for (i = listaUsers.size() - 1; i >= 0; i--) {
                                if(listaUsers.get(i).username.equals(username)) {
                                    userExist = 1;
                                    break;
                                }
                            }
                            if(userExist==1) {
                                enviaInfoRMI(socket, packet.getAddress(), "type ! verify ; username ! " + username + " ; msg ! User successfully verified");
                                listaUsers.get(i).admin=true;
                                escreverFicheiroUsers(listaUsers);
                            }
                            else
                                enviaInfoRMI(socket, packet.getAddress(), "type ! verify ; username ! " + username + " ; msg ! User not found");
                            System.out.println("enviei a info sobre a verificacao de existencia do user");
                        }
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




    private String verPainelAdmin() {
        ArrayList<Site> siteArray = leFicheiroObjetosSites();
        HashMap<String, Integer> pesquisas = leFicheiroPesquisas();
        pesquisas = sortByValues(pesquisas);

        String info= "------------------PAINEL DE ADMINISTRAÇÃO-----------------\nWebsites mais importantes:\n";
        if (siteArray.size()>10) {
            for (int i = 0; i < 10; i++) {
                info = info + i + " - "+siteArray.get(i).title+" - " + siteArray.get(i).url + "\n";
            }
        }
        else{
            for (int i = 0; i < siteArray.size(); i++) {
                info = info + i + " - " +siteArray.get(i).title+" - "+ siteArray.get(i).url + "\n";
            }
        }
        info=info+ "\n" + "Pesquisas mais comuns: \n";
        int conta=0;

        if (pesquisas.size()>10){
            for (String key: pesquisas.keySet()){
                conta=conta+1;
                info=info+conta+ " - "+key+" - "+ pesquisas.get(key) + "\n";

                if (conta==10)
                    break;
            }
        }

        else{
            for (String key: pesquisas.keySet()) {
                conta = conta + 1;
                info = info + conta +" - "+key+ " - " + pesquisas.get(key) + "\n";
            }

        }
        return info;
    }


    private void escreverFicheiroUsers(ArrayList<Utilizador> listaUsers){
        File fich = new File("Users.txt");
        try {
            FileOutputStream is = new FileOutputStream(fich);
            ObjectOutputStream ois = new ObjectOutputStream(is);
            ois.writeObject(listaUsers);
            ois.close();
        } catch (FileNotFoundException b) {
            System.out.println("Nao encontrei o ficheiro");
        } catch (IOException b) {
            System.out.println("Erro ao escrever no ficheiro");
        }
    }
    private ArrayList<Utilizador> lerFicheiroUsers(){
        File fich = new File("Users.txt");
        ArrayList<Utilizador> listaUsers= new ArrayList<>() ;
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
        return listaUsers;
    }

    public String loadSite(String ws){
        Map<String, Integer> countMap ;
        HashMap<String, ArrayList<Site>> dic= leFicheiroObjetosHashMap();
        ArrayList<Site> siteArray = leFicheiroObjetosSites();
        // Read website
        try {
            if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                ws = "http://".concat(ws);
            int i;

            int controlo=0;
            System.out.println("Loading websites...");
            for (i =0; i< siteArray.size();i++){
                if (siteArray.get(i).url.equals(ws)){
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
                if (doc.text().length()>300)
                    site.text = doc.text().substring(0,300) + "..." ;
                else
                    site.text = doc.text();
                countMap = countWords(doc.text());
                site.words = countMap.keySet().toArray(new String[countMap.size()]);
                siteArray.add(site);
            }
            itera(ws,1,siteArray,dic);
            Collections.sort(siteArray, (Site s1, Site s2) ->  (s2.countPages - s1.countPages));
            escreveFicheiroObjetosHashMap(dic);
            escreveFicheiroObjetosSites(siteArray);
            // Get website text and count words
            String answer=ws+ " indexado, bem como outros indexados recursivamente.";
            return answer;

        }catch (org.jsoup.HttpStatusException d){
            return "";
        }

        catch (IOException e) {
            e.printStackTrace();
            return "problema com indexação";
        }

    }


    public String search(String kw, MulticastSocket socket ,DatagramPacket packet){
        String search;
        HashMap< String, ArrayList<Site>> dic=leFicheiroObjetosHashMap(); //leFicheiroObjetosHashMap();  new HashMap< String, ArrayList<Site>>();
        if (!dic.containsKey(kw) || (dic.get(kw).size() == 0)) {
            enviaInfoRMI(socket, packet.getAddress(), "0");
            return "Não foram encontrados resultados!";
        } else if (dic.get(kw).size() <= 20) {
            Collections.sort(dic.get(kw), (Site s1, Site s2) ->  (s2.countPages - s1.countPages));
            enviaInfoRMI(socket, packet.getAddress(), Integer.toString(dic.get(kw).size()));

            for (int i = 0; i < dic.get(kw).size(); i++) {
                search = dic.get(kw).get(i).title+ "\n"+ dic.get(kw).get(i).url + "\n"+ dic.get(kw).get(i).text + "\n"+ dic.get(kw).get(i).countPages+ "\n";
                enviaInfoRMI(socket, packet.getAddress(), search);
            }return "Foram encontrados "+ dic.get(kw).size()+ " resultados!";
        } else {
            Collections.sort(dic.get(kw), (Site s1, Site s2) ->  (s2.countPages - s1.countPages));
            enviaInfoRMI(socket, packet.getAddress(), "20");
            for (int i = 0; i < 20; i++) {
                search = dic.get(kw).get(i).title+ "\n"+ dic.get(kw).get(i).url + "\n"+ dic.get(kw).get(i).text + "\n"+ dic.get(kw).get(i).countPages+ "\n";
                enviaInfoRMI(socket, packet.getAddress(), search);

            }
            return "Foram encontrados "+ dic.get(kw).size()+ " resultados!";
        }

    }
    public String searchMultiple(String[] kw, MulticastSocket socket ,DatagramPacket packet) {
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<Site> search = new ArrayList<>();
        HashMap<String, ArrayList<Site>> dic=leFicheiroObjetosHashMap();
        for (int i = 0; i < kw.length; i++) {
            if (!dic.containsKey(kw[i]) || (dic.get(kw[i]).size() == 0)) {


            } else {
                for (int j = 0; j < dic.get(kw[i]).size(); j++)
                    temp.add(dic.get(kw[i]).get(j).url);
            }
        }
        Set<String> st = new HashSet<String>(temp);
        for (String s : st) {
            if (Collections.frequency(temp, s) == kw.length) {
                for (int i = 0; i < dic.get(kw[0]).size(); i++) {
                    if (dic.get(kw[0]).get(i).url.equals(s)) {
                        search.add(dic.get(kw[0]).get(i));
                    }
                }
            }

        }
        String searchStr="";
        if  (search.size() == 0) {
            enviaInfoRMI(socket, packet.getAddress(), "0");
            return "Não foram encontrados resultados";
        } else if (search.size() <= 20) {
            Collections.sort(search, (Site s1, Site s2) -> (s2.countPages - s1.countPages));
            enviaInfoRMI(socket, packet.getAddress(), Integer.toString(search.size()));
            for (int i = 0; i < search.size(); i++) {
                searchStr = search.get(i).title + "\n" + search.get(i).url + "\n" + search.get(i).text + "\n" + search.get(i).countPages;
                enviaInfoRMI(socket, packet.getAddress(), searchStr);
            }return "Foram encontrados "+ search.size()+ " resultados!";
        }
        else {
            Collections.sort(search, (Site s1, Site s2) ->  (s2.countPages - s1.countPages));
            enviaInfoRMI(socket, packet.getAddress(), "20");
            for (int i = 0; i < 20; i++) {
                searchStr = search.get(i).title + "\n" + search.get(i).url + "\n" + search.get(i).text + "\n" + search.get(i).countPages;
                enviaInfoRMI(socket, packet.getAddress(), searchStr);
            }
            return "Foram encontrados "+ search.size()+ " resultados!";
        }
    }


    public String verLigacoes(String ws){
        int i;
        String sites="";
        ArrayList<Site> siteArray=leFicheiroObjetosSites(); //leFicheiroObjetosSites(); new  ArrayList<Site>()
        if (!ws.startsWith("http://") && !ws.startsWith("https://"))
            ws = "http://".concat(ws);
        for (i = 0;i< siteArray.size();i++){

            if (siteArray.get(i).url.equals(ws)){
                if (siteArray.get(i).pages.size()==0)
                    return "Esse site não tem sites que apontem para ele.";
                else{
                    for(int j=0; j< siteArray.get(i).pages.size();j++){
                        sites=sites+ "Site "+ i+ ": "+ siteArray.get(i).pages.get(j).url+ "\n";
                    }
                    return sites;
                }
            }
        }
        return "Esse site não se encontra na base de dados .";
    }
    private void itera(String ws, int num,ArrayList<Site> siteArray, HashMap<String, ArrayList<Site>> dic) throws IOException {
        // Read website
        Map<String, Integer> countMap;
        if (num > -1) {
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
                for (j = 0; j < siteArray.size(); j++) {
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
                    countMap = countWords(text);
                    int controlo = 0;
                    int i;
                    for (i = 0; i < siteArray.size(); i++) {
                        if (siteArray.get(i).url.equals(link.attr("href"))) {
                            controlo = controlo + 1;
                            break;
                        }
                    }
                    Site site = null;
                    if (controlo == 0) {
                        site = new Site();
                        site.url = link.attr("href");
                        site.title = link.text();
                        if (doc.text().length()>300)
                            site.text = doc.text().substring(0,300) + "..." ;
                        else
                            site.text = doc.text();
                        site.words = countMap.keySet().toArray(new String[countMap.size()]);
                        siteArray.add(site);
                    } else if (controlo == 1) {
                        site = siteArray.get(i);

                    }
                    controlo = 0;
                    for (i = 0; i < site.pages.size(); i++) {
                        if (site.pages.get(i).url.equals(siteArray.get(j).url)) {

                            controlo = controlo + 1;
                            break;
                        }
                    }
                    if (controlo == 0) {
                        site.countPages = site.countPages + 1;
                        site.pages.add(siteArray.get(j));
                    }

                    for (String word : countMap.keySet()) {
                        if (!dic.containsKey(word)) {
                            ArrayList<Site> outrosLinks = new ArrayList<>();
                            outrosLinks.add(site);
                            dic.put(word, outrosLinks);
                        } else {
                            controlo = 0;
                            for (i = 0; i < dic.get(word).size(); i++) {
                                if (dic.get(word).get(i).url.equals(site.url)) {
                                    controlo = controlo + 1;
                                    break;
                                }
                            }
                            if (controlo == 0) {
                                dic.get(word).add(site);
                            }
                        }
                    }
                    itera(link.attr("href"), num - 1,siteArray, dic);

                }


                // Get website text and count words

            }catch(org.jsoup.HttpStatusException t) {

            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void escreveFicheiroObjetosSites(ArrayList<Site> siteArray){
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
    protected void escreveFicheiroObjetosHashMap(HashMap<String, ArrayList<Site>> dic){
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




    protected void escreveFicheiroPesquisas( HashMap<String, Integer> pesquisas ){
        File f=new File("pesquisas.txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(pesquisas);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro de hashmap.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro de hashmap. ");
        }

    }

    protected HashMap<String,Integer> leFicheiroPesquisas(){
        File f=new File("pesquisas.txt");
        HashMap<String, Integer> pesquisas = new HashMap<String, Integer> ();
        int i=0;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            pesquisas = (HashMap<String,Integer>)ois.readObject();

            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a abrir ficheiro de sites.");
        } catch (IOException ex) {
            System.out.println("Erro a ler ficheiro de sites.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro a converter objeto de sites.");
        }

        return pesquisas;
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
    
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
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
    protected String username;
    protected String password;
    boolean admin;
    protected ArrayList<String> pesquisas = new ArrayList<String>();

    public Utilizador(String username,String password,boolean admin){
        this.username=username;
        this.password=password;
        this.admin=admin;

    }
}
class Site implements Serializable {
    protected String url;
    protected String title;
    protected String text;
    protected int countPages=0;
    protected ArrayList<Site> pages= new ArrayList<>();
    protected String[] words;

}
