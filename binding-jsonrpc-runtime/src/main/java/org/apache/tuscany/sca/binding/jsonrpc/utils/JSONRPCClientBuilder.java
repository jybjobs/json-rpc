package org.apache.tuscany.sca.binding.jsonrpc.utils;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import java.net.URL;
import java.util.Map;

public class JSONRPCClientBuilder{

    public static Object post(String uri, Object[] params, Map headers, String methodName) throws Throwable {
        JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(uri));
        // 添加到请求头中去
        if(headers != null && ! headers.isEmpty()) client.setHeaders(headers);
        if (params == null ) params =new Object[]{};
        return client.invoke(methodName,params, Object.class);
    }
}
