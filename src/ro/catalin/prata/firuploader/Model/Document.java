package ro.catalin.prata.firuploader.Model;

import ro.catalin.prata.firuploader.utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/8
 * Time: 上午12:21
 * To change this template use File | Settings | File Templates.
 */
public class Document {
    public String formHeader;
    public String formToken;
    public String formProject;
    public String formPath;
    public String formLog;
    public String formLink;
    public String setTokenBtn;
    public String settingBtn;
    public String uploadBtn;
    public Document(){
        if(Utils.isZh()){
            formHeader = "fir.im 一键上传";
            formToken = "api_token";
            formProject = "项目名称";
            formPath = "文件路径";
            formLog = "更新日志";
            formLink = "应用地址";
            setTokenBtn = "设置";
            settingBtn = "选择文件";
            uploadBtn = "上传" ;
        } else{
            formHeader = "fir.im upload";
            formToken = "api_token";
            formProject = "Project";
            formPath = "file path";
            formLog = "changelog";
            formLink = "short";
            setTokenBtn = "setting";
            settingBtn = "choose path";
            uploadBtn = "upload" ;
        }

    }
}
