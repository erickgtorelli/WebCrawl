/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egtor
 */
public class Main {
   
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
      int MAX_LEVEL_TO_EXPLORE = 10000;
      int MAX_SIZE_TO_DOWNLOAD = 1000;
      int MAX_NUM_DOCUMENTS = 10000;
      int SIZE_BUFFER_OF_DOCUMENTS = 10;
      Spider spider;
      System.out.print("Main");
      /*
        for(int i = 0; i < args.length; i++) {
            switch(i){
                case 1: MAX_LEVEL_TO_EXPLORE = Integer.parseInt(args[i]);
                    break;
                case 2: MAX_SIZE_TO_DOWNLOAD = Integer.parseInt(args[i]);
                    break;
                case 3: MAX_NUM_DOCUMENTS = Integer.parseInt(args[i]);
                    break;
                case 4: SIZE_BUFFER_OF_DOCUMENTS = Integer.parseInt(args[i]);
                    break;
            }
        }
       */
        spider = new Spider(MAX_LEVEL_TO_EXPLORE,MAX_SIZE_TO_DOWNLOAD,MAX_NUM_DOCUMENTS,SIZE_BUFFER_OF_DOCUMENTS);
        spider.loadFile("urlsToVisit.txt");
        try {
            spider.search();
        } catch (InterruptedException | MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
