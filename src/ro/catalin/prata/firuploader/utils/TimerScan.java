package ro.catalin.prata.firuploader.utils;

import ro.catalin.prata.firuploader.view.main;

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
    long Interval = 30000;
    long delay = 30000;

    public TimerScan(){
        timer = new Timer();
        timer.schedule(new TimerScanTask(), delay, Interval);

        //todo: 设置合理的间隔时间进行扫描
    }

}


