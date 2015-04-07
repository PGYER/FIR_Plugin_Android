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
import com.sun.javaws.jnl.XMLFormat;
import ro.catalin.prata.firuploader.controller.KeysManager;
import ro.catalin.prata.firuploader.controller.ModulesManager;
import ro.catalin.prata.firuploader.provider.UploadService;
import ro.catalin.prata.firuploader.utils.AnalysisApk;
import ro.catalin.prata.firuploader.utils.ParseXML;
import ro.catalin.prata.firuploader.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 14/12/15
 * Time: 下午7:39
 * To change this template use File | Settings | File Templates.
 */
public class main implements ToolWindowFactory , UploadService.UploadServiceDelegate{
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
    private ToolWindow toolWindow;
    private String appVersion;
    private String appVersionCode;
    private String appId;
    private String appName;
    public static main m;
    private String apkAbsolutePath;
    private Color COLOR_DARK_PURPLE = new Color(37, 172, 201);
    public main() {
//        setTokenBtn.setBackground(COLOR_DARK_PURPLE);
//        setTokenBtn.setOpaque(true);
//        //setTokenBtn.setContentAreaFilled(true);
//        settingBtn.setBackground(COLOR_DARK_PURPLE);
//        uploadBtn.setBackground(COLOR_DARK_PURPLE);
        m = main.this;
        main.getInstance().setTest("start");
        main.getInstance().setTest("end");
        progressBar.setVisible(false);
        tips.setVisible(false);
        uploadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBuildVersionFields();
                performUploadValidation();

            }
        });
        setTokenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                // open an input dialog for the api key
                String apiKey = Messages.showInputDialog(ProjectManager.getInstance().getOpenProjects()[0],
                        "<HTML>get FIR.im TOKEN <a href=\"http://fir.im/dev/api\">here</a>.</HTML>",
                        "Uploading TOKEN", null, KeysManager.instance().getApiKey(), null);

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
                if(filePath == null)
                    filePath = "";
                // the file was selected so add it to the text field
                File file = new File(filePath) ;
                if(!file.exists())    {
                    filePath = "";
                }
                apkAbsolutePath = filePath;

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
                        if(!file.exists())    {
                            filePath = "";
                        }

                        apkAbsolutePath = filePath;

                        apkPath.setText(splitPath(filePath));

                        // save the file path
                        KeysManager.instance().setApkFilePath(filePath);
                        updateBuildVersionFields();

                    }
                });            }
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
        shortLink.setText(sh);
        shortLink.repaint();
    }
    public static main getInstance(){

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
          apk =  AnalysisApk.unZip(apkAbsolutePath,"") ;
      }
      appVersion = apk[0] ;
      appVersionCode = apk[2] ;
      appId = apk[1];
      String xml = "";
      if(appName ==null || appName.length()==0)
        appName = ParseXML.parseAppName(ModulesManager.instance().getAndroidManifestPath(module)) ;
      System.out.println("appVersion---->"+appVersion);
      System.out.println("appVersionCode---->"+appVersionCode);
      System.out.println("appId---->"+appId);
      System.out.println("appName---->"+appName);
      main.getInstance().setTest("appVersion---->"+appVersion);
      main.getInstance().setTest("appVersionCode---->"+appVersionCode);
      main.getInstance().setTest("appId---->"+appId);
      main.getInstance().setTest("appName---->"+appName);
    }
    /**
     * Performs validation before uploading the build to test flight, if everything is in order, the build is sent
     */
    public void performUploadValidation() {

        String mm = apkPath.getText();
        if (KeysManager.instance().getApiKey() == null) {

            Messages.showErrorDialog("Please set FIR.im token",
                    "API TOKEN Illegal");

        } else if (apkAbsolutePath == null || apkAbsolutePath.length() < 3) {

            Messages.showErrorDialog("Project no apk.",
                    "Illegal apk");

        }  else if(appName == null){
             appName = Messages.showInputDialog(ProjectManager.getInstance().getOpenProjects()[0],
                    "<HTML>please input app name</HTML>",
                    "the name of apk", null, "", null);

        } else {


                uploadBuild();

        }


    }
    /**
     * Uploads the build to test flight, it updates also the UI
     */
    public void uploadBuild() {
        progressBar.setValue(0);
        progressBar.setVisible(true);
        uploadBtn.setEnabled(false);
        tips.setVisible(true);
        uploadBtn.setText("uploading...");
        tips.setText("uploading....");

        // upload the build
        new UploadService().sendBuild(null, apkAbsolutePath, KeysManager.instance().getApiKey(),
                appVersion,
                appVersionCode,
                appId,
                appName ,
                changeLogTa.getText(),
                main.this);

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
            if(!file.exists())    {
                filePath = "";
            }

            apkAbsolutePath = filePath;

            apkPath.setText(splitPath(filePath));

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
                if(!file.exists())    {
                    filePath = "";
                }
                apkAbsolutePath = filePath;

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

                if (!finishedSuccessful) {

                    Messages.showErrorDialog("upload error", "upload error");

                }

                progressBar.setVisible(false);
                uploadBtn.setEnabled(true);
                uploadBtn.setText("Upload");
                main.getInstance().tips.setText("Success");
                uploadFinishNotice();
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //To change body of implemented methods use File | Settings | File Templates.
                        try {
                            // thread to sleep for 1000 milliseconds
                            Thread.sleep(2000);
                            main.getInstance().tips.setVisible(false);
                            main.getInstance().tips.repaint();
                            Utils.postNoticeTOSlack("'"+KeysManager.instance().getApiKey()+"'上传了APP'"+main.getInstance().appName+"'使用 android studio plugin");
                        } catch (Exception e) {
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


        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("upload finish <a href='"+shortLink.getText()+"'>"+shortLink.getText()+"</a> 打开链接去查看.",
                        MessageType.INFO, new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {

                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            ToolWindowManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getToolWindow("FIR.im").show(null);
                        }

                    }
                })
                .setFadeoutTime(6000)
                .createBalloon()
                .show(RelativePoint.getNorthEastOf(statusBar.getComponent()),
                        Balloon.Position.above);
    }

}
