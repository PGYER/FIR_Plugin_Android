package ro.catalin.prata.firuploader.utils;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/13
 * Time: 下午6:23
 * To change this template use File | Settings | File Templates.
 */
public class Tips {

    public static void showCustomTips(){

    }

    public static void showBuildFinishUploadTips(){
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(ProjectManager.getInstance().getOpenProjects()[0]);


        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("编译完毕发送到FIR.im, <a href='open'>点击</a> 打开FIR.im uploader 并上传.",
                        MessageType.INFO, new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {

                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            ToolWindowManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getToolWindow("FIR.im").show(null);
                        }

                    }
                })
                .setFadeoutTime(4000)
                .createBalloon()
                .show(RelativePoint.getNorthEastOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

    public static void  showMD5ChangedUploadTips(){
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(ProjectManager.getInstance().getOpenProjects()[0]);


        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("检测到apk文件改变了还没有上传, <a href='open'>点击</a> 打开FIR.im uploader 并上传.",
                        MessageType.INFO, new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent e) {

                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            ToolWindowManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getToolWindow("FIR.im").show(null);
                        }

                    }
                })
                .setFadeoutTime(4000)
                .createBalloon()
                .show(RelativePoint.getNorthEastOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }
}
