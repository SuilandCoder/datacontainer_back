package njnu.opengms.container.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

/**
 * @ClassName XmlUtils
 * @Description todo
 * @Author sun_liber
 * @Date 2019/1/10
 * @Version 1.0.0
 */
public class XmlUtils {

    public static JSONObject xmltoJson(String xml) throws Exception {
        JSONObject jsonObject = new JSONObject();
        Document document = DocumentHelper.parseText(xml);
        // 获取根节点元素对象
        Element root = document.getRootElement();
        iterateNodes(root, jsonObject);
        return jsonObject;
    }

    public static void iterateNodes(Element node, JSONObject json) {
        // 获取当前元素的名称
        String nodeName = node.getName();
        // 判断已遍历的JSON中是否已经有了该元素的名称
        if (json.containsKey(nodeName)) {
            // 该元素在同级下有多个
            Object Object = json.get(nodeName);
            JSONArray array = null;
            if (Object instanceof JSONArray) {
                array = (JSONArray) Object;
            } else {
                array = new JSONArray();
                array.add(Object);
            }
            // 获取该元素下所有子元素
            List<Element> listElement = node.elements();
            if (listElement.isEmpty()) {
                // 该元素无子元素，获取元素的值
                String nodeValue = node.getTextTrim();
                array.add(nodeValue);
                json.put(nodeName, array);
                return;
            }
            // 有子元素
            JSONObject newJson = new JSONObject();
            // 遍历所有子元素
            for (Element e : listElement) {
                // 递归
                iterateNodes(e, newJson);
            }
            array.add(newJson);
            json.put(nodeName, array);
            return;
        }
        // 该元素同级下第一次遍历
        // 获取该元素下所有子元素
        List<Element> listElement = node.elements();
        if (listElement.isEmpty()) {
            // 该元素无子元素，获取元素的值
            String nodeValue = node.getTextTrim();
            json.put(nodeName, nodeValue);
            return;
        }
        // 有子节点，新建一个JSONObject来存储该节点下子节点的值
        JSONObject object = new JSONObject();
        // 遍历所有一级子节点
        for (Element e : listElement) {
            // 递归
            iterateNodes(e, object);
        }
        json.put(nodeName, object);
        return;
    }
}
