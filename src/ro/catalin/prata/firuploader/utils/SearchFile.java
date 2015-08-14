package ro.catalin.prata.firuploader.utils;

import android.content.res.AXmlResourceParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/10
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class SearchFile {
    public String url;
    public File iconFile;

    public SearchFile(String url){
        this.url = url;
    }
    public  File query(String name){

        int length = 0;
        ZipFile zipFile;
        byte b[] = new byte[1024];
        try {
            zipFile = new ZipFile(new File(url));
            Enumeration<?> enumeration = zipFile.entries();
            ZipEntry zipEntry = null;
            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                if (zipEntry.isDirectory()) {

                } else {

                    if (name.equals(zipEntry.getName())) {
                        iconFile = new File("fir.im");
                        FileOutputStream fop = new FileOutputStream(iconFile);


//                        OutputStream outputStream = new FileOutputStream(name);
                        InputStream inputStream = zipFile.getInputStream(zipEntry);

                        while ((length = inputStream.read(b)) > 0)
                            fop.write(b, 0, length);

                        fop.flush();
                        fop.close();

                    }

                }
            }
            zipFile.close();
        } catch (IOException e) {
        }
        return iconFile;
    }
}
