package com.superschool.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by XIAOHAO on 2017/5/1.
 */

public class NewsXmlToListMap2 {
    private String xml;
    private ArrayList<Map<String, Object>> listNewsMap;

    public NewsXmlToListMap2(String xml) {
        this.xml = xml;
    }

    public List<Map<String, Object>> toListMap() throws DocumentException {

        listNewsMap = new ArrayList<Map<String, Object>>();
        System.out.println(xml);
        Map<String, Object> map;
        Document xmlDocument = DocumentHelper.parseText(xml);
        Element root = xmlDocument.getRootElement();

        Iterator<Element> nodes = root.elementIterator();//根节点首层子节点
        while (nodes.hasNext()) {      //遍历首层子节点
            Element node1 = nodes.next();
            Iterator<Element> node = node1.elementIterator(); //第三层节点
            map = new HashMap<String, Object>();
            int i = 0;
            while (node.hasNext()) {//遍历第三层节点
                i++;
                Element element = node.next();
               map.put(element.getName(), element.getStringValue());  //采集数据
                if (element.getName().contains("img")) {
                   /* MakeImg makeImg = new MakeImg();
                    try {
                        if (makeImg.generateImage(element.getStringValue(), Environment.getExternalStorageDirectory() + "/" + i + ".jpg")) {

                            File img=new File(Environment.getExternalStorageDirectory(), "0.jpg");
                            if(img.exists()){
                                System.out.println("图片生成");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(), i + ".jpg");
                        FileInputStream fis = null;
                        fis = new FileInputStream(file);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        map.put(element.getName(), bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    map.put(element.getName(), element.getStringValue());
                }
            }
            listNewsMap.add(map);

        }
        return listNewsMap;
    }
}
