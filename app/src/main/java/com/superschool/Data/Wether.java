package com.superschool.Data;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/3/8.
 */

public class Wether {
    String city;
    HttpURLConnection conn;
    URL url;
    Map <String,String> map;
    public Wether(String city) {
        this.city = city;
    }
    public  Map <String,String> getWether()throws Exception {
        city = URLEncoder.encode(city, "GB2312");
        String urlStr = "http://php.weather.sina.com.cn/xml.php?city=" +city+ "&password=DJOYnieT8234jlsK&day=0";
        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset","utf-8");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        InputStreamReader isr=new InputStreamReader(conn.getInputStream());
        BufferedReader br=new BufferedReader(isr);
        String line=null;
        StringBuilder str=new StringBuilder();
        while((line=br.readLine())!=null){
            str.append(line);
        }
        Document xmlDocument= DocumentHelper.parseText(str.toString());
        Element root=xmlDocument.getRootElement();//取得根节点
        Element root1= (Element) root.node(0);
        Iterator<Element> node=root1.elementIterator();
        map=new HashMap<String,String>();
        while (node.hasNext()){
            Element element=node.next();
          map.put(element.getName(),element.getStringValue());

        }

        isr.close();
        conn.disconnect();
        return map;
    }
}
