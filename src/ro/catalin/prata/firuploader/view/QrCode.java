package ro.catalin.prata.firuploader.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 15/8/24
 * Time: 下午4:39
 * To change this template use File | Settings | File Templates.
 */
public class QrCode extends JDialog {
    public String uri = "";
    JPanel panel;
    int screenWidth;
    int screenHeight;
    public QrCode(){

        super();
        getScreen();
        this.setLocation((screenWidth-200)/2,(screenHeight-200)/2);
        initPanel();

    }

    public void setUri(String uri){
        this.uri = uri;
        initPanel();
    }

    public void initPanel(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                        System.out.print("............"+QrCode.this.uri);
                        QrCode.this.setSize(200,200);
                        Image image = null;
                        try {
                            URL url = new URL("http://qr.liantu.com/api.php?w=200&text="+QrCode.this.uri);
                            image = ImageIO.read(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ImageIcon im = new ImageIcon(image);
                        JLabel label = new JLabel("", im, JLabel.CENTER);
                        if(panel != null)
                            QrCode.this.remove(panel);
                        panel = new JPanel(new BorderLayout());
                        panel.add( label, BorderLayout.CENTER );
                        QrCode.this.add(panel) ;

                    }
        }).start();

    }

    public void getScreen(){
        screenWidth=((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
        System.out.println(screenWidth+""+screenHeight);
    }
}
