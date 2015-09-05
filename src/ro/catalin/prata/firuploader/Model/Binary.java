package ro.catalin.prata.firuploader.Model;


import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import ro.catalin.prata.firuploader.utils.Utils;
import ro.catalin.prata.firuploader.view.Main;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/10
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */
public class Binary {
    public String name;
    public String icon;
    public String bundleId;
    public String aShort;
    public String versionName;
    public String versionCode;
    public String filePath;
    public static Binary binary;

    public Binary(){
        binary = this;
    }

    public Binary(String url){
        binary = this;
        this.filePath = url;
        parseApk(url);
    }

    public static Binary getInstance(){
        if(binary == null) return new Binary();
        return binary;
    }

    public void initPath(String url){
//        if(this.filePath == url){
//            return ;
//        }
        this.filePath = url;
        if(this.filePath.isEmpty())  return;
        parseApk(this.filePath);
    }

    public void parseApk(String url){
        ApkParser apkParser = null;
        try {
            apkParser = new ApkParser(new File(url));
            ApkMeta apkMeta = apkParser.getApkMeta();

            System.out.println(apkMeta.getLabel());
            System.out.println(apkMeta.getPackageName());
            System.out.println(apkMeta.getVersionCode());
            System.out.println(apkMeta.getLabel());
            System.out.println(apkMeta.getIcon().getPath());

            this.versionName = apkMeta.getVersionName();
            this.versionCode = apkMeta.getVersionCode().toString();
            this.bundleId = apkMeta.getPackageName();
            this.name = apkMeta.getLabel();
            this.icon = apkMeta.getIcon().getPath();
            apkParser.close();
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Utils.postErrorNoticeTOSlack(e);
        }
    }
}
