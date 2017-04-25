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
    private List<String> pagesToVisit = new LinkedList<String>();
    private List<String> pagesToDownload = new LinkedList<String>();
    private List<String> pagesToDownloadType = new LinkedList<String>();
    private List<String> URLsToDownload = new LinkedList<String>();
    private Util SpiderLeg = new Util();
    
    Spider(int MAX_LEVEL_TO_EXPLORE, int MAX_SIZE_TO_DOWNLOAD, int MAX_NUM_DOCUMENTS, int SIZE_BUFFER_OF_DOCUMENTS) {
        this.MAX_LEVEL_TO_EXPLORE = MAX_LEVEL_TO_EXPLORE;
        this.MAX_SIZE_TO_DOWNLOAD = MAX_SIZE_TO_DOWNLOAD * 1000;
        this.MAX_NUM_DOCUMENTS = MAX_NUM_DOCUMENTS;
        this.SIZE_BUFFER_OF_DOCUMENTS = SIZE_BUFFER_OF_DOCUMENTS;
    }
    
    public void search() throws InterruptedException, MalformedURLException{
        String currentUrl;
        String currentPage;
     while((this.pagesVisited.size() > MAX_NUM_DOCUMENTS + SIZE_BUFFER_OF_DOCUMENTS) && 
             (!this.pagesToVisit.isEmpty()) && 
             (MBDownloaded < MAX_SIZE_TO_DOWNLOAD))
      {
          //Visitar la pag y guardar sus datos
          currentUrl = this.pagesToVisit.remove(0); 
          //Agregar reglas asociadas a la página
          this.SpiderLeg.addRules(currentUrl);
          currentPage = this.SpiderLeg.getPage(currentUrl);
          pagesToDownload.add(currentPage);
          URLsToDownload.add(currentUrl);
         //**pagesToDownloadType.add(url type)
         //Si el buffer está lleno 
          if(pagesToDownload.size() >= SIZE_BUFFER_OF_DOCUMENTS){
              try {
                  this.Downloadfiles();
              } catch (IOException ex) {
                  Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
              }
              System.out.println(String.format("**Success** files downloaded"));
          }        
         this.pagesVisited.add(currentUrl);
         //Extract the urls from the pag and added to the pageToVisit List 
         ArrayList<String> arrayList = SpiderLeg.extractUrls(currentPage, currentUrl);
         
         if(!arrayList.isEmpty()){}
         for (String url : arrayList) {
             // if already visited
             if (pagesVisited.contains(url)) {
                 continue;
             }
             // if banned by robots.txt
             if (!this.SpiderLeg.crawlable(url)){
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
            bytesCount += SpiderLeg.downloadfile(URLsToDownload.remove(0), pagesToDownload.remove(0),pagesToDownloadType.remove(0));
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
                    pagesToVisit.add(line);
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


