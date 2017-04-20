/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author egtor
 */
public class Spider {
    // Fields
    private int MAX_PAGES_TO_SEARCH;
    private int BUFFER_DOCUMENTS_SIZE;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private List<String> pagesToDownload = new LinkedList<String>();
    private List<String> pagesToDownloadType = new LinkedList<String>();
    private List<String> URLsToDownload = new LinkedList<String>();
    private Util SpiderLeg = new Util();

    private void search(){
     while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH && !this.pagesToVisit.isEmpty())
      {
          String currentUrl;
          currentUrl = this.pagesToVisit.remove(0);         
          pagesToDownload.add(SpiderLeg.getPage(currentUrl, MAX_PAGES_TO_SEARCH));
          
               
          System.out.println(String.format("**Success** file downloaded %s", currentUrl));
          this.pagesVisited.add(currentUrl);
         // this.pagesToVisit.addAll(//get links));
      }
     
      System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
  
  
    }

    
    private void scanPage(String url){
        String pagina;
        pagina = Util.getPage(url);
        System.out.print(pagina);
    }



}
