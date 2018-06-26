package org.apache.tuscany.sca.binding.jsonrpc.utils;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectUtils {

    /**
     *　重写toString方法，
     * 为枚举类添加name
     * @param object
     * @return
     */
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
    /**
     *　重写toString方法，
     * 为枚举类添加name
     * @param object
     * @return
     */
    public static JSONObject enumToJsonObject(Class clazz,Object object)  {
      //  Class clazz = object.getClass();
        if(!isBaseClass(object)) return addEnumName(clazz,(JSONObject) object);
        JSONObject jo = new JSONObject();
        Method[] methods = clazz.getDeclaredMethods();
        try {
            for (Method method : methods) {
                String modifier = Modifier.toString(method.getModifiers());
                if (modifier.contains("public") && method.getName().indexOf("get")>-1) {//public getter method
                    //         sb.append("\""+method.getName().split("get")[1].toLowerCase()+"\":"+method.invoke(object));
                    jo.put(method.getName().split("get")[1].toLowerCase(),method.invoke(object));
                }
            }
            if(jo.length()==0){//无ｇｅｔ
                if(clazz.getSuperclass()==Enum.class) jo.put("name", object);
            } else if(clazz.getSuperclass()==Enum.class)
                // sb.append("\"name\":\""+clazz.getSuperclass().getMethod("name").invoke(object)+"\"}");
                jo.put("name", clazz.getSuperclass().getMethod("name").invoke(object));
        }catch (Exception e){
            //@todo  枚举类是内部类没有get set 问题
            //e.getStackTrace();
            e.printStackTrace();
            return null;
        }
        return jo;
    }
    /**
     *  为jsonArray 中对象添加类型说明：
     *  e.g. "trailItemList":{"javaClass":"java.util.ArrayList",
     *  "list":[{"javaClass":"com.rkhd.platform.log.dto.XAuditTrail","createdAt":1529403348143,...}]}
     * @param clazz
     * @param object
     * @return
     */
    @Deprecated
    public static JSONObject arrayToJsonObject(Class clazz,JSONObject object)  {
        JSONObject jo = new JSONObject();
        Field[] fields = clazz.getDeclaredFields();
        while(clazz.getSuperclass() != Object.class){
            fields = (Field[]) ArrayUtils.addAll(clazz.getSuperclass().getDeclaredFields(),fields);
            clazz = clazz.getSuperclass();
        }
       try {
           for (Field field : fields) {
               field.setAccessible(true);
               if(List.class.isAssignableFrom(field.getType())){
                   String name = field.getName();
                   if(object.has(name)){
                       JSONArray ja = (JSONArray) object.get(name);
                       jo.put("javaClass",field.getType().getCanonicalName());
                       if(ja != null && ja.length()>0){
                           String inType = null;
                           Type t = field.getGenericType();//
                           if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
                               Type[] types = ((ParameterizedType) t).getActualTypeArguments();//返回此类型的实际类型参数的对象数组
                               if(types.length>0){
                                   inType = (types[0].toString().split("class "))[1];//去掉前面的"class "
                               }

                           }
                           for(int i=0;i<ja.length();i++){
                               if(inType != null) ((JSONObject) ja.get(i)).put("javaClass",inType);
                           }
                           jo.put("list", ja);
                       }else {
                           jo.put("list",new JSONArray());
                       }
                       object.put(name,jo);//覆盖原json value
                   }
               }

           }
       }catch (Exception e){
           e.getStackTrace();
       }
        return object;
    }

    /**
     * 为枚举类jsonObject添加　name 值
     * @param clazz
     * @param jsonObject
     * @return
     */
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

    /**
     * if field type for list
     */
    public static JSONObject addListType(Type t,Class c,JSONArray object){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String inType;
        if (t != null) {
            if(t != null) jsonObject.put("javaClass",c.getName());
            Type type = getType(t);
            if(type != null) {
                String[] strs = type.toString().split("class ");// class java.lang.String
                if (strs.length==1){//need next
                    inType = type.toString().split("<")[0]; //java.util.List<java.lang.String>
                } else {
                    inType = strs[1];
                }
                try {
                    Class clazz = Class.forName(inType);
                        if(!isBaseType(inType)){
                        for(int i=0;i<object.length();i++){
                            //if(inType != null) ((JSONObject) ja.get(i)).put("javaClass",inType);
                            JSONObject j;
                            Object o = object.get(i);
                            if (o== null) continue;
//                            if(List.class.isAssignableFrom(clazz)) j=addListType(type,clazz,(JSONArray) o);
//                            else if(Map.class.isAssignableFrom(clazz)) j=addMapType(type,clazz,(JSONObject)o);
//                            else if(Set.class.isAssignableFrom(clazz)) j=addSetType(type,clazz,(JSONObject)o);
//                            else if(Enum.class.isAssignableFrom(clazz)) j=enumToJsonObject(clazz,o);
//                            else j=addObjectType(clazz,(JSONObject)o);
                            else j = addTypes(type,clazz,o);
                            if(j!=null) jsonArray.put(j);
                        }
                        jsonObject.put("list", jsonArray);
                    }else{
                        jsonObject.put("list",object);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        }
        return null;
    }


    public static JSONObject addTypes(Type type, Class clazz, Object o){
        JSONObject j ;
        if(List.class.isAssignableFrom(clazz)) j=ObjectUtils.addListType(type,clazz,(JSONArray) o);
        else if(Map.class.isAssignableFrom(clazz)) j=ObjectUtils.addMapType(type,clazz,(JSONObject)o);
        else if(Set.class.isAssignableFrom(clazz)) j=ObjectUtils.addSetType(type,clazz,(JSONObject)o);
        else if(Enum.class.isAssignableFrom(clazz)) j=ObjectUtils.enumToJsonObject(clazz,o);
        else j=ObjectUtils.addObjectType(clazz,(JSONObject)o);
        return j;
    }
    /**
     * if field type for object
     */
    public static JSONObject addObjectType(Class clazz, JSONObject object){
        object.put("javaClass",clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        while(clazz.getSuperclass() != Object.class){
            fields = (Field[]) ArrayUtils.addAll(clazz.getSuperclass().getDeclaredFields(),fields);
            clazz = clazz.getSuperclass();
        }
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                JSONObject j;
                String name = field.getName();
                String typrStr = field.getType().getCanonicalName();
                Class c = field.getType();
                Type type = field.getGenericType();
                if(isBaseType(typrStr))
                    continue;
                if (object.has(name)) {
                    Object ja = object.get(name);
                    if(ja== JSONObject.NULL) {
                        object.put(name,null);
                        continue;
                    }
                    if(ja== null) continue;
//                    if (List.class.isAssignableFrom(field.getType())) {
//                        j = addListType(type,c, (JSONArray)ja);
//                    } else if (Map.class.isAssignableFrom(field.getType())) {
//                        j = addMapType(type,c,(JSONObject) ja);
//                    } else if (Set.class.isAssignableFrom(field.getType())) {
//                        j = addSetType(type, c,(JSONObject) ja);
//                    }else if (Enum.class.isAssignableFrom(field.getType())) {
//                        j = enumToJsonObject(c,ja);
//                    }else {//javabean
//                        j = addObjectType(c, (JSONObject) ja);
//                    }
                    else j = addTypes(type,c,ja);
                    if(j != null) object.put(name,j);
                    //else object.put(name,ja);
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return object;
    }


    /**
     * if field type for map
     */
    public static JSONObject addMapType(Type t,Class c,JSONObject object){
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        String inType = null;
        if (t != null) {
            if(t != null) jsonObject.put("javaClass",c.getName());
            Type type = getType(t);
            if(type != null) {
                String[] strs = type.toString().split("class ");// class java.lang.String
                if (strs.length==1){//need next
                    inType = type.toString().split("<")[0]; //java.util.List<java.lang.String>
                } else {
                    inType = strs[1];
                }
                try {
                    Class clazz = Class.forName(inType);
                    if(!isBaseType(inType)){
                        Iterator its = object.keys();
                        while (its.hasNext()) {
                            JSONObject j;
                            String key = (String) its.next();
                            Object o = object.get(key);
                            if (o== null) continue;
//                            if(List.class.isAssignableFrom(clazz)) j=addListType(type,clazz,(JSONArray) o);
//                            else if(Map.class.isAssignableFrom(clazz)) j=addMapType(type,clazz,(JSONObject)o);
//                            else if(Set.class.isAssignableFrom(clazz)) j=addSetType(type,clazz,(JSONObject)o);
//                            else if(Enum.class.isAssignableFrom(clazz)) j=enumToJsonObject(clazz,o);
//                            else j=addObjectType(clazz,(JSONObject)o);
                            else j = addTypes(type,clazz,o);
                            if(j!=null) jo.put(key,j);
                        }
                        jsonObject.put("map", jo);
                    }else{
                        jsonObject.put("map",object);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        }
        return null;
    }

    /**
     * if field type for set
     */
    public static JSONObject addSetType(Type t, Class c, JSONObject object){
        JSONObject jsonObject = new JSONObject();
        JSONObject jo = new JSONObject();
        String inType;
        if (t != null) {
            if(t != null) jsonObject.put("javaClass",c.getName());
            Type type = getType(t);
            if(type != null) {
                String[] strs = type.toString().split("class ");
                if (strs.length==1){//need next
                    inType = type.toString().split("<")[0];
                } else {
                    inType = strs[1];
                }
                try {
                    Class clazz = Class.forName(inType);
                    if(!isBaseType(inType)){
                        Iterator its = object.keys();
                        while (its.hasNext()) {
                            JSONObject j;
                            String key = (String) its.next();
                            Object o = object.get(key);
                            if (o== null) continue;
//                            if(List.class.isAssignableFrom(clazz)) j=addListType(type,clazz,(JSONArray) o);
//                            else if(Map.class.isAssignableFrom(clazz)) j=addMapType(type,clazz,(JSONObject)o);
//                            else if(Set.class.isAssignableFrom(clazz)) j=addSetType(type,clazz,(JSONObject)o);
//                            else if(Enum.class.isAssignableFrom(clazz)) j=enumToJsonObject(clazz,o);
//                            else j=addObjectType(clazz,(JSONObject)o);
                            else j = addTypes(type,clazz,o);
                            if(j!=null) jo.put(key,j);
                        }
                        jsonObject.put("set", jo);
                    }else{
                        jsonObject.put("set",object);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        }
        return null;
    }

    /**
     * 查询泛型field的对象类型
     * @param t
     * @return
     */
    public static Type getType(Type t){
        if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
            Type[] types = ((ParameterizedType)t).getActualTypeArguments();//返回此类型的实际类型参数的对象数组
            if(types == null || types.length==0) return null;
            else return types[types.length-1];// 通常取最后一个　list: length=1 ,map: length=2
        }
        return null;
    }


    /**
     * 首字母转大写
     * @param str
     * @return
     */
    public static  String upperCase(String str) {//根据属性名生成get method
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }


    /**
     * 基本类型、包装类型、String类型
     */
    public  static  boolean isBaseType(String ctype){
        String[] types = {"java.lang.Integer",
                "java.lang.Double",
                "java.lang.Float",
                "java.lang.Long",
                "java.lang.Short",
                "java.lang.Byte",
                "java.lang.Boolean",
                "java.lang.Character",
                "java.lang.String",
                "int","double","long","short","byte","boolean","char","float"};
        for (String s:types) {
            if(s.equals(ctype)) return true;
        }
        return false;
    }

    public  static  boolean isBaseClass(Object ctype){
        if((ctype instanceof Integer)||(ctype instanceof Integer)||(ctype instanceof String)
                ||(ctype instanceof Boolean)||(ctype instanceof Double)
                ||(ctype instanceof Float)||(ctype instanceof Long)||(ctype instanceof Short)||
                (ctype instanceof Byte)||(ctype instanceof Character)) return  true;
        return false;
    }

    //
//    Object newIn(Class<?> clazz) {
//        Objenesis objenesis = new ObjenesisStd();
//        ObjectInstantiator<?> appType = objenesis.getInstantiatorOf(clazz);
//        Object at = appType.newInstance();
//        return at;
//    }



    //    public static String getClassName(Class clazz,String fieldName){
//
//        Field[] fields = clazz.getDeclaredFields();
//        while(clazz.getSuperclass() != null){
//            fields = (Field[]) ArrayUtils.addAll(clazz.getSuperclass().getDeclaredFields() ,fields);
//            clazz = clazz.getSuperclass();
//        }
//        for (Field field : fields) {
//
//            field.setAccessible(true);
//            if (field.getName().equals(fieldName)) {
//                Type t = field.getGenericType();//
//                if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
//                    Type[] types = ((ParameterizedType) t).getActualTypeArguments();//返回此类型的实际类型参数的对象数组
//                    if(types.length>0)
//                        return types[0].toString();
//                }
//            }
//        }
//        return null;
//    }

    public static void main(String[] args) {
//        String str = upperCase("type");
//        System.out.println("get"+str);

        JSONObject o = null;
        System.out.println("sss:"+ (o==null));
        System.out.println("sss:"+JSONObject.NULL.equals("null"));
    }

}
