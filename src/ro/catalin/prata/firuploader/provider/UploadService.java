package ro.catalin.prata.firuploader.provider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import ro.catalin.prata.firuploader.Model.CustomMultiPartEntity;

import java.io.File;
import java.nio.charset.Charset;

import  ro.catalin.prata.firuploader.utils.UploadToFIR;
import ro.catalin.prata.firuploader.Model.UploadToken;
import ro.catalin.prata.firuploader.utils.UploadToRio;
import ro.catalin.prata.firuploader.view.main;

/**
 * 上传服务
 */
public class UploadService implements CustomMultiPartEntity.ProgressListener {

    /**
     * The test flight api url, see doc page https://testflightapp.com/api/doc/
     */
    public static final String TEST_FLIGHT_API_URL = "http://testflightapp.com/api/builds.json";
    public static final String WS_PARAM_API_TOKEN = "api_token";
    public static final String WS_PARAM_TEAM_TOKEN = "team_token";
    public static final String WS_PARAM_NOTES = "notes";
    public static final String WS_PARAM_NOTIFY = "notify";
    public static final String WS_PARAM_FILE = "file";
    public static final String WS_PARAM_DISTRIBUTION_LISTS = "distribution_lists";

    /**
     * Used to notify the status of the upload action
     */
    private UploadServiceDelegate uploadServiceDelegate;

    /**
     * 向FIR上传文件
     * @param url
     * @param filePath
     * @param apiToken
     * @param appVersion
     * @param appVersionCode
     * @param appId
     * @param appName
     * @param appChanglog
     * @param delegate
     */
    public void sendBuild(final String url, final String filePath, final String apiToken, final String appVersion, final String appVersionCode,
                          final String appId,final String appName,final String appChanglog, UploadServiceDelegate delegate) {

        uploadServiceDelegate = delegate;

        new Thread(new Runnable() {
            @Override
            public void run() {
                main.getInstance().setTest("开始上传....");
                UploadToRio uploadToRio = new UploadToRio(appId,apiToken,appName,appVersion,appVersionCode,appChanglog)   ;

                String url = uploadToRio.appInfo.binaryUploadUrl;
                try {
                    HttpClient client;
                    client = new DefaultHttpClient();
                    HttpPost post;
                    post = new HttpPost(url);

                    main.getInstance().setShortLink("http://fir.im/"+uploadToRio.appInfo.appShort);
                    // get the apk file
                    File fileToUpload = new File(filePath);

                    CustomMultiPartEntity multipartEntity = new CustomMultiPartEntity(UploadService.this);
                    // set the api token
                    multipartEntity.addPart("key", new StringBody(uploadToRio.appInfo.binaryKey));
                    multipartEntity.addPart("token", new StringBody(uploadToRio.appInfo.binaryToken));
                    multipartEntity.addPart("file", new FileBody(fileToUpload));
                    multipartEntity.addPart("x:name",new StringBody(uploadToRio.appName, Charset.forName("UTF-8")));
                    multipartEntity.addPart("x:version",new StringBody(uploadToRio.versionName, Charset.forName("UTF-8")));
                    multipartEntity.addPart("x:build",new StringBody(uploadToRio.versionCode));
                    multipartEntity.addPart("x:changelog",new StringBody(uploadToRio.changeLog, Charset.forName("UTF-8")));

                    if (uploadServiceDelegate != null){
                        // send the full package size
                        uploadServiceDelegate.onPackageSizeComputed(multipartEntity.getContentLength());
                    }

                    post.setEntity(multipartEntity);

                    // POST the build
                    HttpResponse response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");
                    System.out.println(responseString);
                    main.getInstance().setTest("kkkkkkkkkkkkkkkkkkk"+responseString);
                    main.getInstance().setTest("response.getStatusLine().getStatusCode()"+response.getStatusLine().getStatusCode());

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (response.getStatusLine().getStatusCode() == 200) {
                        // if the build was successfully uploaded, inform the View
//                        System.out.println("Response: " + EntityUtils.toString(response.getEntity()));
                        if (uploadServiceDelegate != null) {
                            // send success upload status
                            uploadServiceDelegate.onUploadFinished(true);
                        }

                    } else {

                        if (uploadServiceDelegate != null) {
                            // send failed upload status
                            uploadServiceDelegate.onUploadFinished(false);
                        }

                    }
                    main.getInstance().setTest("上传file完成....");



                } catch (Exception e) {
                    // Ups! error occurred
                    e.printStackTrace();
                    main.getInstance().setTest("e"+e.getMessage());
                    if (uploadServiceDelegate != null) {
                        // send failed upload status
                        uploadServiceDelegate.onUploadFinished(false);
                    }
                }

            }
        }).start();

    }

    @Override
    public void transferred(long num) {

        if (uploadServiceDelegate != null){
            uploadServiceDelegate.onProgressChanged(num);
        }

    }

    /**
     * Upload service callback interface used to notify uploading actions like status or progress
     */
    public interface UploadServiceDelegate {

        /**
         * Called when the upload is done, even if an error occurred
         *
         * @param finishedSuccessful this flag is true if the upload was made successfully, false otherwise
         */
        public void onUploadFinished(boolean finishedSuccessful);

        public void onPackageSizeComputed(long totalSize);

        public void onProgressChanged(long progress);

    }

}
