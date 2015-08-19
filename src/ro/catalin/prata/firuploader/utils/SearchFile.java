package ro.catalin.prata.firuploader.utils;

import android.content.res.AXmlResourceParser;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.xmlpull.v1.XmlPullParser;
import ro.catalin.prata.firuploader.view.Main;

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
    public ZipFile zipFile;

    public SearchFile(String url){
        this.url = url;
    }
    public  InputStreamBody query(String name){

        int length = 0;
        byte b[] = new byte[1024];
        InputStreamBody fileContent = null;
        try {
//            Utils.postSuccessNoticeToSlack(url);
            zipFile = new ZipFile(new File(url));
            Enumeration<?> enumeration = zipFile.entries();
            ZipEntry zipEntry = null;

            while (enumeration.hasMoreElements()) {
                zipEntry = (ZipEntry) enumeration.nextElement();
                if (zipEntry.isDirectory()) {

                } else {

                    if (name.equals(zipEntry.getName())) {
//                        Utils.postSuccessNoticeToSlack(name);
//                        iconFile = new File("aa.png");
//                        if(!iconFile.exists()){
//                           iconFile.createNewFile();
//                        }
//                        FileOutputStream fop = new FileOutputStream(iconFile);


//                        OutputStream outputStream = new FileOutputStream(name);
                        InputStream inputStream = zipFile.getInputStream(zipEntry);
                        fileContent=new InputStreamBody(inputStream,name);
//                        while ((length = inputStream.read(b)) > 0)
//                            fop.write(b, 0, length);
//
//                        fop.flush();
//                        fop.close();


                    }

                }
            }
        } catch (IOException e) {
            Utils.postErrorNoticeTOSlack(e);
        }
        return fileContent;
    }
}
