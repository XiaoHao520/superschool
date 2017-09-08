package com.superschool.entity;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by XIAOHAO on 2017/5/1.
 */

public class XmlToListMap {
    private String xml;
    private List<Map<String,String>>listMap;

    public XmlToListMap(String xml) {
        this.xml = xml;
    }
    public List<Map<String,String>> toListMap() throws DocumentException {

        listMap=new ArrayList<Map<String, String>>();

        Map<String,String>map;
        Document xmlDocument= DocumentHelper.parseText(xml);
        Element root=xmlDocument.getRootElement();

        Iterator<Element>nodes=root.elementIterator();//根节点首层子节点
        while (nodes.hasNext()){      //遍历首层子节点
            Element node1=nodes.next();
            Iterator<Element> node=node1.elementIterator(); //第三层节点
            map=new HashMap<String,String>();
            while (node.hasNext()){//遍历第三层节点
                Element element=node.next();
                   map.put(element.getName(),element.getStringValue());  //采集数据
            }
            listMap.add(map);

        }
        return listMap;
    }




}
