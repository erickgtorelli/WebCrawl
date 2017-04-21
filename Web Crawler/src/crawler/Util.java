/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.nio.file.StandardCopyOption.*;
import java.util.ArrayList;

import javax.print.DocFlavor.INPUT_STREAM;
import java.util.regex.Matcher;

/**
 *
 * @author egtor
 */
public class Util {
    private int fileCount = 0;
    /**
     * Download file from URL 
     * @param urlStr
     * @param file name to save the file
     * @param type content type of the file 
     * @throws IOException 
     */
    public int downloadfile(String urlStr, String file, String type) throws IOException{
        URL url = new URL(urlStr);
        String filename = "\\Files\\" + fileCount + "." + type;
        fileCount++;
        int bytesCount = 0;
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(filename);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
            bytesCount += 1024;
        }
        //* save URL on url.txt history
        fis.close();
        bis.close();
        
        return bytesCount;
    }
    
    

    public String getPage(String url){
        URL pagina;
        InputStream is = null;
        BufferedReader br;
        String line;
        StringBuilder total = new StringBuilder();
        try {
            pagina = new URL(url);
            is = pagina.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null){
                total.append(line);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try{
                if (is != null) {
                    is.close();
                }
            } catch(IOException ioe){}
        }
        return total.toString();
    }

    /*
    Recibe el texto de la pagina en string y el url base para llegarle a las direcciones locales
    */
    public ArrayList<String> extractUrls(String pageText, String baseUrl){
        ArrayList<String> urls = new ArrayList<String>();
        System.out.print(pageText);
        Pattern filePattern = Pattern.compile("(href=\\s?\")((([A-Za-z]{3,9}:)?(?:\\/\\/))?([\\d\\w\\.\\-&^%$]*[\\d\\w\\-\\/&^%$]*)(\\.txt|\\.rtf|\\.doc|\\.docx|\\.xhtml|\\.pdf|\\.odt|\\.html|\\.htm)?)\"");
        Matcher buscador = filePattern.matcher(pageText);
        while(buscador.find()){
            if(buscador.group(3) == null){
                urls.add(baseUrl + buscador.group(2));
            }
            else if (buscador.group(3).equals("//")){
                urls.add("http://"+buscador.group(5));
            }
            else {
                urls.add(buscador.group(2));
            }
        }
        return urls;
    }


}
