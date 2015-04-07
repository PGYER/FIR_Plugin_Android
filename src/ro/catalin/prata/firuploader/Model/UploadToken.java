package ro.catalin.prata.firuploader.Model;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 14/12/15
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public class UploadToken {
    private String appShort;
    private String apkToken;

    public String getApkToken() {
        return apkToken;
    }

    public void setApkToken(String apkToken) {
        this.apkToken = apkToken;
    }

    public String getApkKey() {
        return apkKey;
    }

    public void setApkKey(String apkKey) {
        this.apkKey = apkKey;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getIconToken() {
        return iconToken;
    }

    public void setIconToken(String iconToken) {
        this.iconToken = iconToken;
    }

    public String getIconKey() {
        return iconKey;
    }

    public void setIconKey(String iconKey) {
        this.iconKey = iconKey;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    private String apkKey;
    private String apkUrl;
    private String iconToken;
    private String iconKey;
    private String iconUrl;
    private String appId;

    public String getAppShort() {
        return appShort;
    }

    public void setAppShort(String appShort) {
        this.appShort = appShort;
    }

    public  UploadToken(JSONObject jsonObject){
        try {

            appId =  jsonObject.getString("id");

            appShort = jsonObject.getString("short");
            JSONObject bundle = jsonObject.getJSONObject("bundle");
            JSONObject icon = bundle.getJSONObject("icon");
            iconToken = icon.getString("token");
            iconKey = icon.getString("key");
            iconUrl = icon.getString("url");
            JSONObject pkg = bundle.getJSONObject("pkg");
            apkKey = pkg.getString("key");
            apkToken = pkg.getString("token");
            apkUrl = pkg.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
