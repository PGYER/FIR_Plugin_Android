package ro.catalin.prata.firuploader.utils;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: will
 * Date: 14/12/17
 * Time: 下午9:14
 * To change this template use File | Settings | File Templates.
 */
public class ParseXML {
    public static String parseAppName(String url){
        if(url == null || url.equals("")){
            return null;
        }
        String appName = null;
        try {
            //得到DOM解析器的工厂实例
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //从DOM工厂中获得DOM解析器
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //声明为File为了识别中文名
            Document doc = null;
            doc = dbBuilder.parse(url);

            //得到文档名称为Student的元素的节点列表
            NodeList list = doc.getElementsByTagName("application");

            //遍历该集合，显示结合中的元素及其子元素的名字
            for(int i = 0; i< list.getLength() ; i ++){
                Element element = (Element)list.item(i);
                String name=element.getAttribute("android:label");
                if(name.indexOf("@")>=0){
                   appName =  getVal(url,name.substring(name.indexOf("/")+1,name.length()));
                } else{
                   appName = name;
                }

            }
        } catch (SAXException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return appName;
    }

    public static String getVal(String url,String _name){
        File file = new File(url);
        String folderPath = file.getParent();
        File cuFile = new File(folderPath);
        File[] files = cuFile.listFiles();
        String value = "" ;
        double le = files.length;
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isDirectory()){
                continue;
            }
            System.out.println("name:::"+files[i].getName());
            if(files[i].getName().equals("res")){
               File[] resFile =  files[i].listFiles();
                for (int j = 0; j < resFile.length; j++) {
                    if(!resFile[i].isDirectory()){
                        continue;
                    }
                    if(resFile[j].getName().equals("values")){
                        File[] valueFiles =  resFile[j].listFiles();
                        for (int k = 0; k < valueFiles.length; k++) {
                            if(valueFiles[k].getName().equals("strings.xml")){
                                File xmlFile = valueFiles[k];
                               value =  getAppName(xmlFile.getAbsolutePath(),_name) ;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    public static String getAppName(String url,String _name){
        String appName = null;
        try {
            //得到DOM解析器的工厂实例
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //从DOM工厂中获得DOM解析器
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            //声明为File为了识别中文名
            Document doc = null;
            doc = dbBuilder.parse(url);

            //得到文档名称为Student的元素的节点列表
            NodeList list = doc.getElementsByTagName("string");

            //遍历该集合，显示结合中的元素及其子元素的名字
            for(int i = 0; i< list.getLength() ; i ++){
                Element element = (Element)list.item(i);
                String name=element.getAttribute("name");
                if(name.equals(_name)){
                    appName = element.getFirstChild().getNodeValue();
                    break;
                }
            }
        } catch (SAXException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            Utils.postErrorNoticeTOSlack(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return appName;
    }
}
