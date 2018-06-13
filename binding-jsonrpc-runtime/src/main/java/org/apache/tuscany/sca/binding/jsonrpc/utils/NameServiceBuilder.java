package org.apache.tuscany.sca.binding.jsonrpc.utils;

import scallop.nameservice.base.Byte2StringFunction;
import scallop.nameservice.base.Function;
import scallop.nameservice.ns.*;
import scallop.nameservice.zookeeper.ZooKeeperClient;

/**
 * User: jacky
 * singlon nameservice factory
 */
public class NameServiceBuilder {

    private final static String DEFAULT_ROOT = "/rkhd";
    private final static String SYSTEM_PROPERTY = "zk_root";
    private final static int SESSION_TIMEOUT_MS = 30000;
    private final static String SERVER_ROOT = "/servers";
    /**
     * single factory
     */
    private static final NameServiceBuilder INSTANCE = new NameServiceBuilder();

    /**
     * single nameservice object
     */
    private final NameService nameService;

    private NameServiceBuilder(){
        String zkRoot = System.getProperty(SYSTEM_PROPERTY, DEFAULT_ROOT);
        Function<byte[],String> function = new Byte2StringFunction();
        String zkHosts = PropertiesHelper.getInstance().getZooKeeperHosts();
        if(zkHosts == null){
            throw new RuntimeException("zookeeper hosts must not null");
        }
        ZooKeeperClient zkClient = new ZooKeeperClient(SESSION_TIMEOUT_MS,zkHosts);
        LoadBalanceAlgorithm loadBalanceAlgorithm = new DefaultLoadBalanceAlgorithm();
        NameServiceConfig config = new ZooKeeperNameServiceConfig(zkRoot + SERVER_ROOT ,zkClient,function);
        nameService = new ServersNameService(config,loadBalanceAlgorithm);
    }

    public static NameServiceBuilder getInstance(){
        return INSTANCE;
    }

    public NameService getNameService(){
        return this.nameService;
    }
}
