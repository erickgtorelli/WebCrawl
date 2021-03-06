/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egtor
 */
public class Spider {
    // Fields
    private int MAX_LEVEL_TO_EXPLORE;
    private int MAX_SIZE_TO_DOWNLOAD;
    private int MAX_NUM_DOCUMENTS;
    private int SIZE_BUFFER_OF_DOCUMENTS;
    private int level = 0;
    private int MBDownloaded = 0;
    private int numOfDocuments = 0;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<Pair<String, String>> pagesToVisit = new LinkedList<Pair<String, String>>();
    private List<Pair<String, String>> pagesToDownload = new LinkedList<Pair<String, String>>();
    private List<String> URLsToDownload = new LinkedList<String>();
    private Util SpiderLeg = new Util();
    
    
    
    Spider(int MAX_LEVEL_TO_EXPLORE, int MAX_SIZE_TO_DOWNLOAD, int MAX_NUM_DOCUMENTS, int SIZE_BUFFER_OF_DOCUMENTS) {
        this.MAX_LEVEL_TO_EXPLORE = MAX_LEVEL_TO_EXPLORE;
        this.MAX_SIZE_TO_DOWNLOAD = MAX_SIZE_TO_DOWNLOAD * 1000;
        this.MAX_NUM_DOCUMENTS = MAX_NUM_DOCUMENTS;
        this.SIZE_BUFFER_OF_DOCUMENTS = SIZE_BUFFER_OF_DOCUMENTS;
    }
    
    public void search() throws InterruptedException, MalformedURLException, IOException{
        String currentPage;
        String currentUrl;
        String currentType;
        Pair<String, String> current;
     while((this.pagesVisited.size() < MAX_NUM_DOCUMENTS) && 
             (!this.pagesToVisit.isEmpty()) && (MBDownloaded < MAX_SIZE_TO_DOWNLOAD))
      {
          //Visitar la pag y guardar sus datos
          current = this.pagesToVisit.remove(0); 
          //Agregar reglas asociadas a la página
          this.SpiderLeg.addRules(current.u);
          currentPage = this.SpiderLeg.getPage(current.u);
          if (!SpiderLeg.isOK(current.u)){
              continue;
          }
          System.out.println("Current is " + current.u);
          pagesToDownload.add(new Pair<String, String>(currentPage, current.t));
          URLsToDownload.add(current.u);
         //**pagesToDownloadType.add(url type)
         //Si el buffer está lleno 
         System.out.println("Buffer is " + pagesToDownload.size() + " of " + SIZE_BUFFER_OF_DOCUMENTS);
         System.out.println("Download is at " + MBDownloaded+ " of " + MAX_SIZE_TO_DOWNLOAD);

            if (pagesToDownload.size() >= SIZE_BUFFER_OF_DOCUMENTS) {
                System.out.println("Buffer over capacity");

                try {
                    System.out.println("Downloading Files");

                    this.Downloadfiles();
                } catch (IOException ex) {
                    Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(String.format("**Success** files downloaded"));
                System.out.println("Total number downloaded: " + numOfDocuments);
            }
            this.pagesVisited.add(current.u);
            //Extract the urls from the pag and added to the pageToVisit List 
            ArrayList<Pair<String, String>> arrayList = SpiderLeg.extractUrls(currentPage, SpiderLeg.getBase(current.u));

            if (!arrayList.isEmpty()) {
            }

            for (Pair<String, String> url : arrayList) {
                // if already visited
                if (pagesVisited.contains(url.u)) {

                    continue;
                }
                // if banned by robots.txt
                if (!this.SpiderLeg.crawlable(url.u)) {

                    continue;
                } 
                else {

                    this.pagesToVisit.add(url);
                }
            }
            TimeUnit.SECONDS.sleep(1);
            

        }

    }
    
    public void Downloadfiles() throws IOException{
        int ArraysSize = pagesToDownload.size();
        int bytesCount = 0;
        for(int i=0;i < ArraysSize -1;i++){
            bytesCount += SpiderLeg.downloadfile(URLsToDownload.remove(0), pagesToDownload.remove(0));
        }
        numOfDocuments += ArraysSize;
        MBDownloaded += bytesCount;
    }
    
    

    
    private void scanPage(String url){
        String pagina;
        pagina = SpiderLeg.getPage(url);
        System.out.print(pagina);
    }

    public void loadFile(String filepath){
        BufferedReader br = null;
	FileReader fr = null;
        System.out.println("Loading..." + filepath);
		try
                {
                  BufferedReader reader = new BufferedReader(new FileReader(filepath));
                  String line;
                  while ((line = reader.readLine()) != null)
                  {
                    pagesToVisit.add(new Pair<String, String>(line, SpiderLeg.getContentType(line)));
                  }
                  reader.close();

                }
                catch (Exception e)
                {
                  System.err.format("Exception occurred trying to read '%s'.", filepath);
                  e.printStackTrace();
                }

	}
        //Load all the URLs from the file
  }


