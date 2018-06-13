/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.jsonrpc;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

/**
 * A model for the JSONRPC binding.
 * 
 * @version $Rev$ $Date$
 */
public interface JSONRPCBinding extends Binding, PolicySetAttachPoint {
    /**
     * @return the registry center
     */
    String getRegistryCenter();


    /**
     * @return the registry center's resource name
     */
    String getRegistryName();

    /**
     * @return returns the jsonrpc Service Name
     */
    String getServiceName();

    /**
     * @param resourceCenter the resource center
     */
    void setRegistryCenter(String resourceCenter);

    /**
     * @param resourceName resource name
     */
    void setRegistryName(String resourceName);

    /**
     * Sets the service name for the jsonrpc Server
     * @param jsonRPCServiceName the name of the jsonrpc service
     */
    void setServiceName(String jsonRPCServiceName);

    String getHost();

    void setHost(String host);

    String getPort();

    void setPort(String port);

}
