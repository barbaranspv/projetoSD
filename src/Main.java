
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static HashMap< String, ArrayList<Site>> dic= new HashMap<>() ;
    public static ArrayList<Site> siteArray= new ArrayList<Site>();

    public static void main(String[] args) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> countMap = new TreeMap<>();

        // Read website
        System.out.print("Website: ");
        try {
            String ws = bf.readLine();
            if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
                ws = "http://".concat(ws);

            // Attempt to connect and get the document
            Document doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/

            // Title
            System.out.println(doc.title() + "\n");

            // Get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                // Ignore bookmarks within the page
                if (link.attr("href").startsWith("#")) {
                    continue;
                }

                // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                if (!link.attr("href").startsWith("http")) {
                    continue;
                }

                System.out.println("Link: " + link.attr("href"));
                System.out.println("Text: " + link.text() + "\n");

                String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
                countMap=countWords(text);
                int controlo=0;
                int i;
                for (i =0; i< siteArray.size();i++){
                    if (siteArray.get(i).equals(link.attr("href"))){
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
                    siteArray.add(site);
                }
                else{
                    site=siteArray.get(i);
                }

                for (String word : countMap.keySet()) {
                    if (dic.get(word)==null){
                        ArrayList<Site> outrosLinks = new ArrayList<Site>();
                        outrosLinks.add(site);
                        dic.put(word,outrosLinks);
                    }
                    else {
                        controlo=0;
                        for (i=0;i< dic.get(word).size();i++){
                            if (dic.get(word).get(i).equals(site)){
                                controlo=controlo+1;
                                break;
                            }
                        }
                        if (controlo==0){
                          dic.get(word).add(site);
                        }

                    }
                }
                itera(link.attr("href"),0);


            }
            for (String word : dic.keySet()) {
                System.out.print(word + "-> ");
                for(int i=0;i<dic.get(word).size();i++){
                    System.out.print(dic.get(word).get(i).url + " ");
                }
                System.out.println();
            }
            System.out.println("Keyword:");
            String kw = bf.readLine();
            int i;
            for (i=0;i< dic.get(kw).size();i++){
                System.out.println(dic.get(kw).get(i).url + " ");
                System.out.println(dic.get(kw).get(i).text);
            }
            System.out.println();
            System.out.println( "Foram encontrados "+ dic.get(kw).size() +" resultados!" );

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
                System.out.println(doc.title() + "\n");

                // Get all links
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    // Ignore bookmarks within the page
                    if (link.attr("href").startsWith("#")) {
                        continue;
                    }

                    // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
                    if (!link.attr("href").startsWith("http")) {
                        continue;
                    }
                    System.out.println("Link: " + link.attr("href"));
                    System.out.println("Text: " + link.text() + "\n");
                    String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>
                    countMap=countWords(text);
                    int controlo=0;
                    int i;
                    for (i =0; i< siteArray.size();i++){
                        if (siteArray.get(i).equals(link.attr("href"))){
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
                        siteArray.add(site);
                    }
                    else{
                        site=siteArray.get(i);
                    }

                    for (String word : countMap.keySet()) {
                        if (dic.get(word)==null){
                            ArrayList<Site> outrosLinks = new ArrayList<Site>();
                            outrosLinks.add(site);
                            dic.put(word,outrosLinks);
                        }
                        else {
                            controlo=0;
                            for (i=0;i< dic.get(word).size();i++){
                                if (dic.get(word).get(i).equals(site)){
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
    String[] words;

}