package ro.catalin.prata.firuploader.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/7
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class UploadTicket {
    public String id;
    public String type;
    public String appShort;
    public String iconUploadUrl;
    public String binaryUploadUrl;
    public String iconKey;
    public String iconToken;
    public String binaryKey;
    public String binaryToken;


    public UploadTicket(JSONObject jsonObject){

        try {
            this.id = jsonObject.getString("id");
            this.type = jsonObject.getString("type");
            this.appShort = jsonObject.getString("short");

            JSONObject cert = jsonObject.getJSONObject("cert");
            JSONObject iconCert = cert.getJSONObject("icon");
            JSONObject binaryCert = cert.getJSONObject("binary");

            this.iconKey = iconCert.getString("key");
            this.iconToken = iconCert.getString("token");
            this.binaryKey = binaryCert.getString("key");
            this.binaryToken = binaryCert.getString("token");
            this.iconUploadUrl = iconCert.getString("upload_url");
            this.binaryUploadUrl = binaryCert.getString("upload_url");

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
