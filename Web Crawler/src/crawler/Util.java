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

/**
 *
 * @author egtor
 */
public class Util {
    /**
     * Download file from URL 
     * @param urlStr
     * @param file name to save the file
     * @param type content type of the file 
     * @throws IOException 
     */
    public void downloadfile(String urlStr, String file, String type) throws IOException{
        URL url = new URL(urlStr);
        String filename = file + "." + type;
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(filename);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
}
