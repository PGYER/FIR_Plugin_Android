package ro.catalin.prata.firuploader.utils;

import ro.catalin.prata.firuploader.controller.KeysManager;
import ro.catalin.prata.firuploader.view.main;

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


    @Override
    public void run() {
        Date date = new Date(this.scheduledExecutionTime());
        System.out.println("本次执行该线程的时间为：" + date);


        String path = null;

        if(main.getInstance().binary.filePath.isEmpty()){
            System.out.println("本次执行该线程的时间为：1" + date);
            return;
        }
        //todo: 定时计算文件的md5判断文件有没有变化
        path = main.getInstance().binary.filePath;
        System.out.println("本次执行该线程的时间为：2" + date);
        String md5 = Utils.getMd5(path);
        if(!md5.equals(KeysManager.instance().getMd5())) {
            System.out.println("本次执行该线程的时间为：3" + date);
            main.getInstance().uploadFinishNotice();
        }


    }
}
