package ro.catalin.prata.firuploader.utils;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/12
 * Time: 下午11:40
 * To change this template use File | Settings | File Templates.
 */
public class TimerScanTask  extends TimerTask {

    private String path;
    public TimerScanTask(String path){
       this.path = path;
    }
    @Override
    public void run() {
        Date date = new Date(this.scheduledExecutionTime());
        System.out.println("本次执行该线程的时间为：" + date);

        //todo: 定时计算文件的md5判断文件有没有变化

        Tips.showMD5ChangedUploadTips();
    }
}
