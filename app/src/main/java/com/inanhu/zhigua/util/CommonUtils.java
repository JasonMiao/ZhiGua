package com.inanhu.zhigua.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jason on 2016/10/26.
 */
public class CommonUtils {

    public static Map<String, String> parseXML(String xml) throws IOException, DocumentException {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        if (xml == null) {
            return null;
        } else {
            InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            // 读取输入流
            SAXReader saxReader = new SAXReader();

            Document document = saxReader.read(in);

            // 获取根节点
            Element root = document.getRootElement();
            root=(Element) root.elements().get(0);
            root=(Element) root.elements().get(0);
            root=(Element) root.elements().get(0);

            List<Element> elements = root.elements();

            // 遍历节点集
            for (Element element : elements) {
                map.put(element.getName(), element.getText());
            }

            // 释放资源
            in.close();
            in = null;

            return map;
        }
    }
}
