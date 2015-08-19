package ro.catalin.prata.firuploader.utils;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ro.catalin.prata.firuploader.view.Main;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;


public class Utils {

    /**
     * Checks if the given string is null and if it is, it returns an empty string
     *
     * @param string can be null
     * @return empty string if the string is null, the passed string otherwise
     */
    public static String validateString(String string) {
        if (string == null) {
            return "";
        } else {
            return string;
        }
    }

    /**
     * Used to build a FileChooserDescriptor object with the given FileType and has the following options enabled:
     * - controls whether files can be chosen
     * - controls whether folders can be chosen
     * - controls whether .jar files can be chosen
     * - controls whether .jar files will be returned as files or as folders
     *
     * @param fileType the FileType used for the filtering
     * @return new FileChooserDescriptor
     */
    public static FileChooserDescriptor createSingleFileDescriptor(final FileType fileType) {
        return new FileChooserDescriptor(true, true, true, true, false, false) {
            @Override
            public boolean isFileVisible(final VirtualFile file, final boolean showHiddenFiles) {
                return file.isDirectory() || file.getFileType() == fileType;
            }

            @Override
            public boolean isFileSelectable(final VirtualFile file) {
                return super.isFileSelectable(file) && file.getFileType() == fileType;
            }
        };
    }

    /**
     * 发送slack通知
     * @param msg
     */
    public static void postNoticeTOSlack(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient() ;
                String postUrl = "https://hooks.slack.com/services/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"  ;
                HttpPost httppost = new HttpPost(postUrl);
                HttpResponse response = null;
                try {
                    ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
                    JSONObject obj = new JSONObject();
                    obj.append("text",msg) ;
                    postParameters.add(new BasicNameValuePair("payload", obj.toString()));
                    httppost.setEntity(new UrlEncodedFormEntity(postParameters,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Utils.postErrorNoticeTOSlack(e);
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (JSONException e) {
                    Utils.postErrorNoticeTOSlack(e);
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                try {
                    response = httpClient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");

                } catch (IOException e) {
                    Utils.postErrorNoticeTOSlack(e);
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();
    }

    public static void postSuccessNoticeToSlack(final String msg){
        postNoticeTOSlack("#AndroidStudio#success#"+msg);
    }

    public static void postErrorNoticeTOSlack(final Exception e){
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer,true));

        postNoticeTOSlack("#AndroidStudio#Error#"+writer.toString());

    }

    public static void local(){
        Locale locale = Locale.getDefault();
        System.out.println(locale.getLanguage());
        System.out.println(locale.getCountry());

        String language = locale.getLanguage();
        String country = locale.getCountry();
    }

    /**
     * 国际化判断
     * @return
     */
    public static boolean isZh(){
       Locale locale = Locale.getDefault();
       String language = locale.getLanguage();
       if(language.equals("zh")){
           return true;
       }else{
           return false ;
       }
    }

    /**
     * 获取文件的MD5
     * @param path
     * @return
     */
    public static String getMd5(String path){
        String value = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(path);

            byte[] dataBytes = new byte[1024];

            int nRead = 0;
            while ((nRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nRead);
            };
            byte[] mBytes = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mBytes.length; i++) {
                sb.append(Integer.toString((mBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            System.out.println("Digest(in hex format):: " + sb.toString());
            value = sb.toString();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return value;
    }

}



