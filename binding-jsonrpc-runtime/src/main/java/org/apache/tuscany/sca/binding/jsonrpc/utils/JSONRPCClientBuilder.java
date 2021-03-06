package org.apache.tuscany.sca.binding.jsonrpc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.osoa.sca.ServiceRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JSONRPCClientBuilder{

    public static Object post(String uri, Object[] params, Map headers, String methodName) throws InvocationTargetException {
        if(headers == null) headers = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX,true);

        try {
            JsonRpcHttpClient client = new JsonRpcHttpClient(mapper,new URL(uri),headers);
            // 添加到请求头中去
            // if(headers != null && ! headers.isEmpty()) client.setHeaders(headers);
            if (params == null ) params =new Object[]{};
            return client.invoke(methodName,params, Object.class);
        } catch (Throwable e) {
            //@todo 暂时简单处理
            throw new InvocationTargetException(e);
        }
    }
    public static Object post(String uri, Object[] params, Map headers, String methodName,Class clazz) throws Throwable {
        if(headers == null) headers = new HashMap();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX,true);
        JsonRpcHttpClient client = new JsonRpcHttpClient(mapper,new URL(uri),headers);
        // 添加到请求头中去
        // if(headers != null && ! headers.isEmpty()) client.setHeaders(headers);
        if (params == null ) params =new Object[]{};
        if(clazz == null) clazz = Object.class;
        return client.invoke(methodName,params, clazz);
    }
}
