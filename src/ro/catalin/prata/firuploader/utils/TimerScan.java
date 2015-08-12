package ro.catalin.prata.firuploader.utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/12
 * Time: 下午11:38
 * To change this template use File | Settings | File Templates.
 */
public class TimerScan {
    Timer timer;

    public TimerScan(String path){
        timer = new Timer();
        timer.schedule(new TimerScanTask(path), 1000,3000);

        //todo: 设置合理的间隔时间进行扫描
    }

}


