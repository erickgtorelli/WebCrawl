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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.print.DocFlavor.INPUT_STREAM;
import java.util.regex.Matcher;

/**
 *
 * @author egtor
 */
public class Util {

    private int fileCount = 0;
    private HashMap<String, ArrayList<String>> disallowed = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> allowed = new HashMap<String, ArrayList<String>>();

    /**
     * Download file from URL
     *
     * @param urlStr
     * @param file name to save the file
     * @param type content type of the file
     * @throws IOException
     */
    public int downloadfile(String fileUrl, Pair<String, String> pair) throws IOException {
        URL url = new URL(fileUrl);
        System.out.println("Downloading - " + fileUrl);
        String filename = "\\Files\\" + fileCount + pair.t;
        File file;
        file = new File(filename);
        file.createNewFile();
        fileCount++;
        int bytesCount = 0;
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(filename);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
            bytesCount += 1024;
        }
        //* save URL on url.txt history
        fis.close();
        bis.close();

        return bytesCount;
    }

    public String getPage(String url) {
        URL pagina;
        InputStream is = null;
        BufferedReader br;
        String line;
        StringBuilder total = new StringBuilder();
        try {
            pagina = new URL(url);
            is = pagina.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                total.append(line);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
        return total.toString();
    }

    /*
    Recibe el texto de la pagina en string y el url base para llegarle a las direcciones locales
     */
    public ArrayList<Pair<String, String>> extractUrls(String pageText, String baseUrl) {
        ArrayList<Pair<String, String>> urls = new ArrayList<Pair<String, String>>();
        Pattern filePattern = Pattern.compile("(href=\\s?\")((([A-Za-z]{3,9}:)?(?:\\/\\/))?([\\d\\w\\.\\-&^%$]*[\\d\\w\\-\\/&^%$]*)(\\.txt|\\.rtf|\\.doc|\\.docx|\\.xhtml|\\.pdf|\\.odt|\\.html|\\.htm)?)\"");
        Matcher matcher = filePattern.matcher(pageText);
        String matchUrl = null;
        String matchType = null;
        while (matcher.find()) {
            if (matcher.group(3) == null) {
                matchUrl = baseUrl + matcher.group(2);
            } else if (matcher.group(3).equals("//")) {
                matchUrl = "http://" + matcher.group(5);
            } else {
                matchUrl = matcher.group(2);
            }
            if(matcher.group(6) == null){
                matchType = ".html";
            }
            else {
                matchType = matcher.group(6);
            }
            urls.add(new Pair<String, String>(matchUrl, matchType));
        }

        return urls;
    }

    
    /*
    Revisa el robots.txt de la raiz de un url y agrega sus reglas a dos listas, allowed y disallowed
    Ambas listas se consultan con el url raiz 
    */
    public void addRules(String url){
        URL host;
        InputStream is = null;
        BufferedReader br;
        String line, hostString;
        boolean userAgent = false;
        ArrayList<String> allowed = new ArrayList<String>();
        ArrayList<String> disallowed = new ArrayList<String>();
        try {
            URL full = new URL(url);
            hostString = full.getProtocol() + "://" + full.getHost();
            //Revisa si ya hay un ruleset
            if (this.disallowed.containsKey(hostString)){
                return;
            }
            host = new URL(hostString + "/robots.txt");
            is = host.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            String path = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf("User-agent: *") != -1) {
                    userAgent = true;
                } else if (line.indexOf("User-agent:") != -1) {
                    userAgent = false;
                }
                if (userAgent) {
                    if (line.indexOf("Disallow: ") != -1) {
                        path = line.substring(10);
                        disallowed.add(path);

                    } else if (line.indexOf("Allow: ") != -1) {
                        path = line.substring(7);
                        allowed.add(path);
                    }
                }
            }
            this.allowed.put(hostString, allowed);
            this.disallowed.put(hostString, disallowed);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
           // ioe.printStackTrace();           
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

    public ArrayList<String> getAllowed(String host) throws MalformedURLException{
        URL url = new URL(host);
        return this.allowed.get(url.getProtocol() + "://" + url.getHost());
    }
    
     public ArrayList<String> getDisallowed(String host) throws MalformedURLException{
         URL url = new URL(host);
        return this.disallowed.get(url.getProtocol() + "://" + url.getHost());
    }
    
    
    
    public boolean crawlable(String url) throws MalformedURLException {
        URL host = new URL(url);
        String hostUrl = host.getProtocol() + "://" + host.getHost();
        boolean crawlable = true;
        ArrayList<String> disallowed = this.disallowed.get(hostUrl);
        /// Si no hay reglas de disallowed, podemos pasar
        if (disallowed == null){
            crawlable = true;
        }
        else{
            //Igual si hay un set vacio de reglas
            if (disallowed.isEmpty()){
                crawlable = true;
            }
            else{
                //Revisamos disallow para ver si nos incluye
                for (int i = 0; i < disallowed.size(); i++){
                    if(url.startsWith(hostUrl + disallowed.get(i))){
                        crawlable = false;
                        break;
                    }
                }
                ArrayList<String> allowed = this.allowed.get(hostUrl);
                //Revisamos allowed a ver si nos incluye
                if (allowed != null){
                    for (int i = 0; i < allowed.size(); i++){
                        if(url.startsWith(hostUrl + allowed.get(i))){
                            crawlable = true;
                            break;
                        }
                    }
                }
            }
        }
        return crawlable;
    }

}
