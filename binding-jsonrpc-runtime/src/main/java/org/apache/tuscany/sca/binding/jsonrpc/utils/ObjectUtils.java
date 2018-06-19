package org.apache.tuscany.sca.binding.jsonrpc.utils;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

    public static JSONObject addEnumName(JSONObject jsonObject)  {
        Class clazz = Enum.class;
        Method[] methods = clazz.getDeclaredMethods();
//        while(clazz.getSuperclass() != Object.class){
//            methods = (Method[]) ArrayUtils.addAll(clazz.getSuperclass().getDeclaredMethods() ,methods);
//            clazz = clazz.getSuperclass();
//        }
        try {
        //    if(jsonObject != null && jsonObject.length()>-1)
                // sb.append("\"name\":\""+clazz.getSuperclass().getMethod("name").invoke(object)+"\"}");
            //    jsonObject.put("name", clazz.getSuperclass().getMethod("name").invoke(object));
        }catch (Exception e){
            e.getStackTrace();
        }
        return jsonObject;
    }

    Object newIn(Class<?> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator<?> appType = objenesis.getInstantiatorOf(clazz);
        Object at = appType.newInstance();
        return at;
    }

}
