
package org.apache.tuscany.sca.binding.jsonrpc.provider;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.tuscany.sca.binding.jsonrpc.utils.JSON2JavaBean;
import org.apache.tuscany.sca.binding.jsonrpc.utils.JSONRPCClientBuilder;
import org.apache.tuscany.sca.binding.jsonrpc.utils.NameServiceBuilder;
import org.apache.tuscany.sca.binding.jsonrpc.utils.PropertiesHelper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.json.JSONObject;
import org.osoa.sca.ServiceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scallop.nameservice.ns.NameService;
import scallop.nameservice.ns.NameServiceException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Invoker for the JSONRPC Binding
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCReferenceZKInvoker implements Invoker {
    private final static Logger logger = LoggerFactory.getLogger(JSONRPCReferenceZKInvoker.class);

    private final static String COLON = ":";
    private static final String POINT = ".";
    Operation operation;
    String uri;
    private String registryName;
    private String svcName;
    private NameService nameService = NameServiceBuilder.getInstance().getNameService();

    public JSONRPCReferenceZKInvoker(Operation operation, String uri,String svcName,String registryName) {
        this.operation = operation;
        this.uri = uri;
        this.svcName = svcName;
        this.registryName = registryName;
    }


    private static Map<String, Long> methodExecuteMap = new ConcurrentHashMap<String, Long>();

    private void statMethodExecute(String srvDetail, String method) {
        String key = srvDetail + POINT + method;
        Long counter = methodExecuteMap.get(key);
        if (counter == null) {
            counter = 1L;
        } else {
            counter++;
        }
        methodExecuteMap.put(key, counter);
    }

    private final static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        if (logger.isInfoEnabled()) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, Long> entry : methodExecuteMap.entrySet()) {
                        logger.info("method invoke stat: " + entry.getKey() + " count: " + entry.getValue() + " interval: " + PropertiesHelper.getInstance().getRmiExecuteTimesPrintInterval() +
                                " rate: " + entry.getValue() / (PropertiesHelper.getInstance().getRmiExecuteTimesPrintInterval() / 60) + " t/m");
                    }
                    methodExecuteMap.clear();
                }
            }, PropertiesHelper.getInstance().getRmiExecuteTimesPrintInterval(), PropertiesHelper.getInstance().getRmiExecuteTimesPrintInterval(), TimeUnit.SECONDS);
        }
    }


    public Message invoke(Message msg) {
        String hostPortProxy = null;
        try {
            hostPortProxy = hostPortProxy();
            Object[] args = msg.getBody();
            Class clazz = this.operation.getOutputType().getPhysical();
            Object resp = JSONRPCClientBuilder.post(getURI(hostPortProxy), args, null, operation.getName());
//            client = new JsonRpcHttpClient(new URL(getURI(hostPortProxy)));
//            Object resp = invokeTarget(args, hostPortProxy);
            //对结果进行反序列化
            ObjectMapper mapper = new ObjectMapper();
          //  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonStr = mapper.writeValueAsString(resp);
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSON2JavaBean ob = new JSON2JavaBean();
            resp = ob.transform(jsonObject, clazz);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ServiceRuntimeException) {
                msg.setFaultBody(e.getCause().getCause());
            } else {
                msg.setFaultBody(e.getCause());
            }
            String srvDetail = getServiceDetail(hostPortProxy);
            logger.error("invoking target error, srvDetail: {}, e: {}", srvDetail, e);
        } catch (Throwable e) {

            msg.setFaultBody(e.getCause());
            String srvDetail = getServiceDetail(hostPortProxy);
            logger.error("invoking target error, srvDetail: {}, e: {}", srvDetail, e);
        }
        return msg;
    }

    /**
     * 返回proxy的hostPort
     *
     * @return normal host:port, else null nameServiceException
     * @throws NameServiceException
     */
    private String hostPortProxy() throws NameServiceException {
        String hostPort = nameService.getHost(registryName);
        if (null == hostPort || hostPort.trim().isEmpty()) {
            String errMsg = "hostPortProxy() registryName : " + registryName + " hostPort null";
            logger.error(errMsg);
            throw new NameServiceException(errMsg);
        }
        return hostPort;
    }

    private String getServiceDetail(String hostPortProxy) {
        if (null == hostPortProxy) {
            return registryName + " hostPort error";
        }
        return registryName + "#" + svcName + "(" + hostPortProxy + ")";
    }

    /**
     * 根据hostPortProxy 信息，拼接请求的uri
     * @param hostPortProxy
     * @return
     * @throws NameServiceException
     */
    private String getURI(String hostPortProxy) throws NameServiceException {
        if (hostPortProxy != null) {
            String[] ipAndPort = hostPortProxy.split(COLON);
            if (ipAndPort.length < 2) {
                String errMsg = "getHostAndPort() registryName :" + registryName + " has a wrong configuration";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            } else {

                String path = svcName;
                if (path != null) {
                    path = "/" + path;
                }
                String port = ipAndPort[1];
                int p = -1;
                if (port != null && port.length() > 0) {
                    p = Integer.decode(port);
                }
                try {
                    return new URI("http", null, ipAndPort[0], p, path, null, null).toString();
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        } else {
            String errMsg = "getHostAndPort() registryName : " + registryName + " hostPortProxy null";
            logger.error(errMsg);
            throw new NameServiceException(errMsg);
        }
    }
}
