
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static HashMap< String, ArrayList<Site>> dic=leFicheiroObjetosHashMap();; //leFicheiroObjetosHashMap();  new HashMap< String, ArrayList<Site>>();
    public static ArrayList<Site> siteArray=leFicheiroObjetosSites();//leFicheiroObjetosSites(); new  ArrayList<Site>()

    public static void main(String[] args) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> countMap ;
        //leFicheiroObjetosHashMap()
        //leFicheiroObjetosSites()


        // Read website
        System.out.print("Website: ");
        try {
            String ws = bf.readLine();
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


           /* for (String word : dic.keySet()) {
                System.out.print(word + "-> ");
                for( i=0;i<dic.get(word).size();i++){
                    System.out.print(dic.get(word).get(i).url + " ");
                }
                System.out.println();
            }*/
            System.out.println("Keyword:");
            String kw = bf.readLine();
            int conta=0;
            Collections.sort(dic.get(kw), (Site s1, Site s2) -> (int)( s2.countPages-s1.countPages));
            if (dic.get(kw).size()<=20){
                for (i=0;i< dic.get(kw).size();i++){
                    if ( dic.get(kw).get(i).countPages!=1){
                        conta+=1;
                        System.out.println(dic.get(kw).get(i).url + " \n"+ dic.get(kw).get(i).text + "\n"+ dic.get(kw).get(i).countPages );
                        //Thread.sleep(50);
                        System.out.println();

                    }
                }
            }
            else{
                for (i=0;i< 20;i++){
                    if ( dic.get(kw).get(i).countPages!=1){
                        conta+=1;
                        System.out.println("Link: "+  dic.get(kw).get(i).url + " \nTexto: "+ dic.get(kw).get(i).text + "\nCount das páginas: "+ dic.get(kw).get(i).countPages );
                        //Thread.sleep(50);
                        System.out.println();

                    }
                }
            }
            System.out.println();
            System.out.println( "Foram encontrados "+ dic.get(kw).size() +" resultados! Mostrando os "+ conta + " mais relevantes.");


            escreveFicheiroObjetosHashMap();
            escreveFicheiroObjetosSites();
            // Get website text and count words

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void itera(String ws, int num) throws IOException {
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
                    Site site;
                    if (controlo==0){
                        site= new Site();
                        site.url=link.attr("href");
                        site.title=link.text();
                        site.text=text;
                        site.words=countMap.keySet().toArray(new String[countMap.size()]);
                        site.countPages=site.countPages+1;
                        siteArray.add(site);
                    }

                    else{
                        site=siteArray.get(i);
                        site.countPages=site.countPages+1;
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
    protected static void escreveFicheiroObjetosSites(){
        File f=new File("sites.txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(siteArray);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro locais.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro locais. ");
        }

    }
    protected static void escreveFicheiroObjetosHashMap(){
        File f=new File("hashmap.txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(dic);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro locais.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro locais. ");
        }

    }

    protected static ArrayList<Site> leFicheiroObjetosSites(){
        File f=new File("sites.txt");
       ArrayList<Site> siteArray= new ArrayList<Site>();
        int i=0;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            siteArray = (ArrayList<Site>)ois.readObject();

            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a abrir ficheiro locais.");
        } catch (IOException ex) {
            System.out.println("Erro a ler ficheiro locais.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro a converter objeto locais.");
        }

        return siteArray;
    }

    protected static HashMap< String, ArrayList<Site>>  leFicheiroObjetosHashMap(){
        File f=new File("HashMap.txt");
         HashMap< String, ArrayList<Site>> dic= new HashMap<>() ;
        int i=0;
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            dic = (HashMap< String, ArrayList<Site>>)ois.readObject();

            ois.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a abrir ficheiro locais.");
        } catch (IOException ex) {
            System.out.println("Erro a ler ficheiro locais.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro a converter objeto locais.");
        }
        return dic;

    }

    private static Map countWords(String text) {
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
}
class Site implements Serializable {
    String url;
    String title;
    String text;
    int countPages=0;
    String[] words;


}
