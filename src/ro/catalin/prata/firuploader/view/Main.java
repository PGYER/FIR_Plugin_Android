package ro.catalin.prata.firuploader.view;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.*;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.catalin.prata.firuploader.Model.Binary;
import ro.catalin.prata.firuploader.controller.KeysManager;
import ro.catalin.prata.firuploader.controller.ModulesManager;
import ro.catalin.prata.firuploader.provider.UploadService;
import ro.catalin.prata.firuploader.utils.TimerScan;
import ro.catalin.prata.firuploader.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 14/12/15
 * Time: 下午7:39
 * To change this template use File | Settings | File Templates.
 */
 public class Main implements ToolWindowFactory , UploadService.UploadServiceDelegate{
    private JPanel panel1;
    private JButton setTokenBtn;
    private JComboBox projectCombo;
    private JLabel apkPath;
    private JLabel shortLink;
    private JTextArea changeLogTa;
    private JButton uploadBtn;
    private JProgressBar progressBar;
    private JButton settingBtn;
    private JLabel tips;
    private JLabel formHeader;
    private JLabel formToken;
    private JLabel formProject;
    private JLabel formPath;
    private JLabel formLink;
    private JLabel formLog;
    private JLabel formHelp;
    private JLabel formTip;
    private JCheckBox formTipCB;
    private JLabel formUpload;
    private JCheckBox formUploadCB;
    private JLabel qrCodeLabel;
    private JButton cancelUploadButton;
    private JLabel languageLabel;
    private JButton chinese;
    private JButton english;
    private ToolWindow toolWindow;
    private String appVersion;
    private String appVersionCode;
    private String appId;
    private String appName;
    private String appShort;
    public static Main m;
    private String apkAbsolutePath;
    private QrCodeJDialog qrCode;
    private UploadService uploadService;
    public Binary binary;
    public ro.catalin.prata.firuploader.Model.Document document;
    private Color COLOR_DARK_PURPLE = new Color(37, 172, 201);
    private TimerScan timerScan;
    public Main() {
        initComponent();
        if(!"yes".equals(KeysManager.instance().getUploadFlag()) || !"cancel".equals(KeysManager.instance().getUploadFlag())){
            KeysManager.instance().setUploadFlag("cancel");
        }
        m = Main.this;
        qrCode = new QrCodeJDialog();
        binary = new Binary();
        Main.getInstance().setTest("start");
        Main.getInstance().setTest("end");
        progressBar.setVisible(false);
        tips.setVisible(false);
        uploadService = new UploadService();
        uploadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBuildVersionFields();
                performUploadValidation();

            }
        });
        chinese.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                KeysManager.instance().setLanguage("chinese");
                initComponent();
            }
        });

        english.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                KeysManager.instance().setLanguage("english");
                initComponent();
            }
        });
        cancelUploadButton.setVisible(false);
        cancelUploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                uploadService.post.abort();
                uploadService.uploadServiceDelegate = null;
                uploadService = null;  //销毁对象暂停上传
                uploadService = new UploadService();
                progressBar.setVisible(false);
                uploadBtn.setEnabled(true);
                uploadBtn.setText(document.uploadBtn);
                changeLogTa.setText("");
                Main.getInstance().tips.setVisible(false);
                Main.getInstance().tips.repaint();
                cancelUploadButton.setVisible(false);
            }
        });
        setTokenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                // open an input dialog for the api key
                String apiKey = Messages.showInputDialog(ProjectManager.getInstance().getOpenProjects()[0],
                        "<HTML>获取api token <a href=\"http://fir.im/user/info\">here</a>.</HTML>",
                        "api token", null, KeysManager.instance().getApiKey(), null);

                // save the api key after a minor validation
                if (apiKey != null && apiKey.length() > 3) {
                    KeysManager.instance().setApiKey(apiKey);
                }
            }
        });
        projectCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                // if a module is selected, save the module
                KeysManager.instance().setSelectedModuleName((String) projectCombo.getSelectedItem());

                // update the apk path
                Module module = ModulesManager.instance().getModuleByName((String) projectCombo.getSelectedItem());
                String filePath = ModulesManager.instance().getAndroidApkPath(module);
                if (filePath == null)
                    filePath = "";
                // the file was selected so add it to the text field
                File file = new File(filePath);
                if (!file.exists() || filePath.toLowerCase().indexOf(".apk") < 0) {
                    filePath = "";
                }
                apkAbsolutePath = filePath;
                binary.initPath(apkAbsolutePath);

                apkPath.setText(splitPath(filePath));

                // update the build version fields too
                updateBuildVersionFields();

            }
        });

        setupValuesOnUI();
        settingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // create a new file type with the apk extension to be used for file type filtering
                FileType type = FileTypeManager.getInstance().getFileTypeByExtension("apk");

                // create a descriptor for the file chooser
                FileChooserDescriptor descriptor = Utils.createSingleFileDescriptor(type);
                descriptor.setTitle("APK File");
                descriptor.setDescription("choose apk to upload FIR.im");

                // by default open the first opened project root directory
                VirtualFile fileToSelect = ProjectManager.getInstance().getOpenProjects()[0].getBaseDir();

                // open the file chooser
                FileChooser.chooseFiles(descriptor, null, fileToSelect, new FileChooser.FileChooserConsumer() {
                    @Override
                    public void cancelled() {

                        // do nothing for now...

                    }

                    @Override
                    public void consume(List<VirtualFile> virtualFiles) {

                        String filePath = virtualFiles.get(0).getPath();

                        // the file was selected so add it to the text field
                        File file = new File(filePath) ;
                        if(!file.exists() || filePath.toLowerCase().indexOf(".apk")<0 )    {
                            filePath = "";
                        }

                        apkAbsolutePath = filePath;

                        apkPath.setText(splitPath(filePath));
                        binary.initPath(apkAbsolutePath);

                        // save the file path
                        KeysManager.instance().setApkFilePath(filePath);
                        updateBuildVersionFields();

                    }
                });            }
        });
        shortLink.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                browserUrl(Main.getInstance().shortLink.getText());
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        qrCodeLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                if (!shortLink.getText().isEmpty()){
                    qrCode.show();
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        formHelp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                try {
                    Desktop desktop = Desktop.getDesktop();
                    String message = "mailto:yh@fir.im?subject=firuploader";
                    URI uri = URI.create(message);
                    desktop.mail(uri);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    Utils.postErrorNoticeTOSlack(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timerScan = new TimerScan();

        this.formTipCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                if(formTipCB.isSelected()){
                    KeysManager.instance().setFlag("");
                }else{
                    KeysManager.instance().setFlag("cancel");
                }
            }
        });

        this.formUploadCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                if(formUploadCB.isSelected()){
                    KeysManager.instance().setUploadFlag("yes");
                }else{
                    KeysManager.instance().setUploadFlag("cancel");
                }
            }
        });
    }

    public String splitPath(String filep){
        String rt = "";
        if(filep == null){
            filep = "";
        }
        if(filep.length()>30){
            filep ="..."+filep.substring(filep.length()-30,filep.length());
        }
        return filep  ;
    }
    public void setShortLink (String sh){
        this.appShort = sh;
        shortLink.setText(sh);
        shortLink.repaint();
        qrCode.setUri(sh);
    }
    public static Main getInstance(){

           return m;
    }
    /**
     * Updates the build version(code and name) fields
     */
    public void updateBuildVersionFields() {
        Module module = ModulesManager.instance().getModuleByName((String) projectCombo.getSelectedItem());
        // update the code and name text fields manifest build version code and name values


      String[] apk = new String[3];
      if(apkAbsolutePath != null){
           binary.initPath(apkAbsolutePath);
      }

    }

    public void setTips(String content){
        Main.getInstance().tips.setText(content);
    }
    /**
     * Performs validation before uploading the build to test flight, if everything is in order, the build is sent
     */
    public void performUploadValidation() {

        String mm = apkPath.getText();
        if (KeysManager.instance().getApiKey() == null) {

            Messages.showErrorDialog("请设置fir.im的api token",
                    "api token 不合法");

        } else if (apkAbsolutePath == null || apkAbsolutePath.length() < 3) {

            Messages.showErrorDialog("工程没有发现apk文件请单击设置来设置apk路径",
                    "apk文件不合法");

        }  else if(binary.name == null){
             binary.name = Messages.showInputDialog(ProjectManager.getInstance().getOpenProjects()[0],
                    "<HTML>请设置apk应用名称</HTML>",
                    "apk的名称", null, "", null);

        } else {


                uploadBuild();

        }


    }
    /**
     * Uploads the build to test flight, it updates also the UI
     */
    public void uploadBuild() {
        Main.getInstance().binary.initPath(apkAbsolutePath);
        progressBar.setValue(0);
        progressBar.setVisible(true);
        uploadBtn.setEnabled(false);
        tips.setVisible(true);
        uploadBtn.setText("uploading...");
        tips.setText("uploading....");

        cancelUploadButton.setVisible(true);
        // upload the build
        uploadService.sendBuild(null, apkAbsolutePath, KeysManager.instance().getApiKey(),
                binary,
                changeLogTa.getText(),
                Main.this);

    }

   public void setTest(String text){

       //logContent.setText(text+"\n"+logContent.getText() );
   }
    /**
     * Set the default or previously saved values on the UI components
     */
    public void setupValuesOnUI() {

        Module previouslySelectedModule;

        // if the apk file path was not saved previously by the user, set the saved module apk file path or the best matching module
        previouslySelectedModule = ModulesManager.instance().getModuleByName(KeysManager.instance().getSelectedModuleName());

        if (previouslySelectedModule != null) {
            String filePath = ModulesManager.instance().getAndroidApkPath(previouslySelectedModule);

            if(filePath == null)
                filePath = "";
            // the file was selected so add it to the text field
            File file = new File(filePath) ;
            if(!file.exists() || filePath.toLowerCase().indexOf(".apk") < 0)    {
                filePath = "";
            }

            apkAbsolutePath = filePath;

            apkPath.setText(splitPath(filePath));
            binary.initPath(apkAbsolutePath);

        } else {

            // get the best matching module for this project and set it's file path
            previouslySelectedModule = ModulesManager.instance().getMostImportantModule();
            String filePath = ModulesManager.instance().getAndroidApkPath(previouslySelectedModule);

            // the file was selected so add it to the text field
            if(filePath == null)
                filePath = "";
            File file = new File(filePath) ;
            if(!file.exists())    {
                filePath = "";
            }

            apkAbsolutePath = filePath;

            apkPath.setText(splitPath(filePath));
            binary.initPath(apkAbsolutePath);
        }

        // set the model of the modules
        projectCombo.setModel(new DefaultComboBoxModel(ModulesManager.instance().getAllModuleNames()));



        // set the selection
        projectCombo.setSelectedIndex(ModulesManager.instance().getSelectedModuleIndex(previouslySelectedModule.getName()));

        updateBuildVersionFields();
    }
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        //To change body of implemented methods use File | Settings | File Templates.
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "", false);
        toolWindow.getContentManager().addContent(content);

        this.toolWindow = toolWindow;
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

                // get the best matching module for this project and set it's file path
                Module previouslySelectedModule = ModulesManager.instance().getMostImportantModule();
                String filePath = previouslySelectedModule.getModuleFilePath();
                if(filePath == null)
                    filePath = "";
                File file = new File(filePath) ;
                if(!file.exists() || filePath.toLowerCase().indexOf(".apk")< 0)    {
                    filePath = "";
                }
                apkAbsolutePath = filePath;
                binary.initPath(apkAbsolutePath);

                apkPath.setText(splitPath(filePath));

                KeysManager.instance().setSelectedModuleName(previouslySelectedModule.getName());

                String[] modules = ModulesManager.instance().getAllModuleNames();
                if (modules != null) {
                    // set the model of the modules
                    projectCombo.setModel(new DefaultComboBoxModel(ModulesManager.instance().getAllModuleNames()));
                }

                // set the selection
                projectCombo.setSelectedIndex(ModulesManager.instance().getSelectedModuleIndex(previouslySelectedModule.getName()));

            }

            @Override
            public boolean canCloseProject(Project project) {
                return true;
            }

            @Override
            public void projectClosed(Project project) {


            }

            @Override
            public void projectClosing(Project project) {

            }
        });
    }

    @Override
    public void onUploadFinished(final boolean finishedSuccessful) {

        // upload is now finished, run some UI updates on the UI thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                cancelUploadButton.setVisible(false);
                if (!finishedSuccessful) {

                    Messages.showErrorDialog("上传失败！有问题请联系dev@fir.im", "上传失败！有问题请联系dev@fir.im");
                    progressBar.setVisible(false);
                    uploadBtn.setEnabled(true);
                    uploadBtn.setText(document.uploadBtn);
                    changeLogTa.setText("");
                    Main.getInstance().tips.setVisible(false);
                    Main.getInstance().tips.repaint();
                    return;

                }

                progressBar.setVisible(false);
                uploadBtn.setEnabled(true);
                uploadBtn.setText(document.uploadBtn);
                Main.getInstance().tips.setText("File upload success");
                KeysManager.instance().setMd5(Utils.getMd5(Main.getInstance().binary.filePath));
                changeLogTa.setText("");
                uploadFinishNotice();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //To change body of implemented methods use File | Settings | File Templates.
                        try {
                            // thread to sleep for 1000 milliseconds
                            Thread.sleep(2000);
                            Main.getInstance().tips.setVisible(false);
                            Main.getInstance().tips.repaint();
                            Utils.postSuccessNoticeToSlack("#" + Main.getInstance().binary.name + "#" + Main.getInstance().appShort);
                        } catch (Exception e) {
                            Utils.postErrorNoticeTOSlack(e);
                            System.out.println(e);
                        }
                    }
                });
                th.start();

            }
        });

    }

    @Override
    public void onPackageSizeComputed(final long totalSize) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                progressBar.setMaximum((int) totalSize);

            }
        });

    }

    @Override
    public void onProgressChanged(final long progress) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                progressBar.setValue((int) progress);

            }
        });

    }


    public void uploadFinishNotice(){
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(ProjectManager.getInstance().getOpenProjects()[0]);
        JComponent component = statusBar.getComponent();
        final Rectangle rect = component.getVisibleRect();
        final Point p = new Point(rect.x + rect.width - 30, rect.y - 30);
        final RelativePoint point = new RelativePoint(component, p);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("<p style='font-size:12px;color:black'>fir.im上传成功Y(^_^)Y</p></br><p style='font-size:12px;'> <a style='font-size:12px' href='"+shortLink.getText()+"'>"+shortLink.getText()+"</a> 打开链接去查看.</p>",
                        MessageType.INFO, new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {

                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//                            ToolWindowManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getToolWindow("FIR.im").show(null);
                            browserUrl(Main.getInstance().shortLink.getText())  ;
                        }

                    }
                })
                .setFadeoutTime(10000)
                .setCloseButtonEnabled(true)
                .createBalloon()
                .show(point,
                        Balloon.Position.atRight);
    }
    public void browserUrl(String url){
        try {
            Desktop.getDesktop().browse(new URI( url));
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Utils.postErrorNoticeTOSlack(e1);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Utils.postErrorNoticeTOSlack(e1);
        }
    }

    public void initComponent(){
        document = new ro.catalin.prata.firuploader.Model.Document();
        this.formHeader.setText(document.formHeader);
        this.formLink.setText(document.formLink);
        this.formLog.setText(document.formLog);
        this.formPath.setText(document.formPath);
        this.formToken.setText(document.formToken);
        this.formProject.setText(document.formProject);
        this.settingBtn.setText(document.settingBtn);
        this.setTokenBtn.setText(document.setTokenBtn);
        this.uploadBtn.setText(document.uploadBtn);
        this.formTip.setText(document.formTip);
        this.languageLabel.setText(document.languageLabel);
        this.cancelUploadButton.setText(document.cancelUpload);
        this.chinese.setText(document.chineseBtn);
        this.english.setText(document.englishBtn);
        if("cancel".equals(KeysManager.instance().getFlag())){
            this.formTipCB.setSelected(false);
        }else{
            this.formTipCB.setSelected(true);
        }

        if("cancel".equals(KeysManager.instance().getUploadFlag())) {
           this.formUploadCB.setSelected(false);
        }else if("yes".equals(KeysManager.instance().getUploadFlag())){
            this.formUploadCB.setSelected(true);
        }else{
            this.formUploadCB.setSelected(false);
            KeysManager.instance().setUploadFlag("cancel");
        }
        this.formUpload.setText(document.formUpload);
    }

}
