package ro.catalin.prata.firuploader.utils;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ro.catalin.prata.firuploader.Model.UploadToken;
import ro.catalin.prata.firuploader.view.main;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 14/12/15
 * Time: 下午4:16
 * To change this template use File | Settings | File Templates.
 */
public class UploadToFIR {
    public static final String FIR_UPLOAD_TOKEN_URL = "http://fir.im/api/v2/info/" ;
    public static final String FIR_UPDATE_APP_INFO = "http://fir.im/api/v2/app/"  ;


    public  UploadToken  uploadToken ;

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public  String versionId;
    public String token;
    public String appName;
    public String appId;
    public String versionName;
    public String versionCode;
    public String changeLog;
    public String appShort;
    public String appOid;


    public UploadToFIR(String appId,String token,String appName,String versionName,String versionCode,String changeLog){
        main.getInstance().setTest("token...."+token+":appName:"+appName+":appid:"+appId+":versionCode:"+versionCode);
        this.token = token;
        this.appName = appName;
        this.appId = appId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.changeLog = changeLog;
        main.getInstance().setTest("====");
        try {
            HttpClient httpClient = new DefaultHttpClient();
            main.getInstance().setTest("httpClient->"+httpClient.toString());

            HttpGet httpGet = new HttpGet("http://fir.im/api/v2/app/info/"+appId+"?type=android&token="+token)   ;
            HttpResponse response = null;
            main.getInstance().setTest("httpGet->"+httpGet.toString());
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            main.getInstance().setTest("entity->"+entity.toString());
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
            main.getInstance().setTest("responseString->"+responseString);
            JSONObject js;
            js = new JSONObject(responseString);
            main.getInstance().setTest("jsonObject->"+js.toString());
            appShort = js.getString("short")     ;
            main.getInstance().setShortLink("http://fir.im/"+appShort);
            System.out.println("id::"+js.get("id"));
            appOid = js.getString("id");
            main.getInstance().setTest("id::"+js.get("id"));
            System.out.println("==============》获取上传token") ;
            uploadToken = new UploadToken(js)  ;
            main.getInstance().setTest("获取上传token....");
        } catch (IOException e) {
            main.getInstance().setTest("error...."+e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            main.getInstance().setTest("error...."+e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void putAppinfo() throws UnsupportedEncodingException {
        main.getInstance().setTest("更新APP信息....  start");
        HttpClient httpClient = new DefaultHttpClient() ;
        String putUrl = "http://fir.im/api/v2/app/"+this.appOid+"?token="+this.token;

        // String putUrl = "http://fir.im/api/v2/app/"+this.appOid+"?name="+this.appName+"&source=fir.plug&token="+this.token   ;
        HttpPut httpPut = new HttpPut(putUrl);

//        httpPut.addHeader("name",this.appName);
//        httpPut.addHeader("source","fir.plug");
//        httpPut.addHeader("token",this.token);
//        StringEntity se=new StringEntity("name="+this.appName+"&source=fir.plug&token="+this.token ,"UTF-8");
//        httpPut.setEntity(se);
        List<NameValuePair> parameters ;
        ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
        postParameters.add(new BasicNameValuePair("name", this.appName));
        postParameters.add(new BasicNameValuePair("source", "fir.plug"));
        postParameters.add(new BasicNameValuePair("token",this.token));
        httpPut.setEntity(new UrlEncodedFormEntity(postParameters,"UTF-8"));
        try {
            HttpResponse response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            main.getInstance().setTest("responseString---->"+responseString);
            System.out.println(responseString);
            JSONObject js;
            js = new JSONObject(responseString);
            main.getInstance().setTest("更新APP信息....");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
       System.out.println("==============>> 更新APP信息");
    }
    public void putAppversion(){
        HttpClient httpClient = new DefaultHttpClient() ;
        String putUrl = "http://fir.im/api/v2/appVersion/"+versionId+"/complete"  ;
        HttpPut httpPut = new HttpPut(putUrl);
        try {
            ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
            postParameters.add(new BasicNameValuePair("version", this.versionCode));
            postParameters.add(new BasicNameValuePair("versionShort", this.versionName));
            postParameters.add(new BasicNameValuePair("release_type","inhouse"));
            postParameters.add(new BasicNameValuePair("token",this.token));
            httpPut.setEntity(new UrlEncodedFormEntity(postParameters,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
            JSONObject js;
            js = new JSONObject(responseString);
            main.getInstance().setTest("更新version....");
            System.out.println("==============》更新version") ;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public void putChangeLog(){
        HttpClient httpClient = new DefaultHttpClient() ;
        String putUrl = "http://fir.im/api/v2/appVersion/"+versionId  ;
        HttpPut httpPut = new HttpPut(putUrl);
        HttpResponse response = null;
        try {
            ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
            postParameters.add(new BasicNameValuePair("changelog", this.changeLog));
            postParameters.add(new BasicNameValuePair("token", this.token));
            httpPut.setEntity(new UrlEncodedFormEntity(postParameters,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
            JSONObject js;
            js = new JSONObject(responseString);
            System.out.println("==============》更新日志");
            main.getInstance().setTest("更新日志....");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
