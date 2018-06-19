package org.apache.tuscany.sca.binding.jsonrpc.utils;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class ObjectUtils {
    public static JSONObject toJsonObject(Object object)  {
        Class clazz = object.getClass();
        JSONObject jo = new JSONObject();
        Method[] methods = clazz.getDeclaredMethods();
//        while(clazz.getSuperclass() != Object.class){
//            methods = (Method[]) ArrayUtils.addAll(clazz.getSuperclass().getDeclaredMethods() ,methods);
//            clazz = clazz.getSuperclass();
//        }
       try {
           for (Method method : methods) {
               String modifier = Modifier.toString(method.getModifiers());
               if (modifier.contains("public") && method.getName().indexOf("get")>-1) {//public getter method
          //         sb.append("\""+method.getName().split("get")[1].toLowerCase()+"\":"+method.invoke(object));
                   jo.put(method.getName().split("get")[1].toLowerCase(),method.invoke(object));
               }
           }
           if(clazz.getSuperclass()==Enum.class)
              // sb.append("\"name\":\""+clazz.getSuperclass().getMethod("name").invoke(object)+"\"}");
               jo.put("name", clazz.getSuperclass().getMethod("name").invoke(object));
       }catch (Exception e){
           e.getStackTrace();
       }
        return jo;
    }

    public static JSONObject addEnumName(Class clazz,JSONObject jsonObject)  {
        Object[] objs = clazz.getEnumConstants();
        Iterator<String> its = jsonObject.keys();
        while (its.hasNext()) {
            String key = its.next();
            Object value = jsonObject.get(key);
            if (value== null || "".equals(value)) continue;
            for (Object o:objs) {
                try {
                   Object val = clazz.getMethod("get"+upperCase(key)).invoke(o);
                   if(val != null && val.equals(value)){
                       jsonObject.put("name",clazz.getMethod("name").invoke(o));
                       break;
                   }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

        }
        return jsonObject;
    }

    Object newIn(Class<?> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator<?> appType = objenesis.getInstantiatorOf(clazz);
        Object at = appType.newInstance();
        return at;
    }

    public static  String upperCase(String str) {//根据属性名生成get method
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static void main(String[] args) {
        String str = upperCase("type");
        System.out.println("get"+str);
    }

}
