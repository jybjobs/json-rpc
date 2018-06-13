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

package org.apache.tuscany.sca.binding.jsonrpc.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;

/**
 * A model for the JSONRPC binding.
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingImpl implements JSONRPCBinding {
    private String name;
    private String uri;
    private String registryCenter;
    private String registryName;
    private String serviceName;
    private String host;
    private String port;

    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private IntentAttachPointType intentAttachPointType;
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();

    public String getName() {
        return name;
    }

    public String getURI() {
        compose();
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
        parse(uri);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnresolved() {
        // The binding is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The binding is always resolved
    }

    //Policy related getters/setters

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return intentAttachPointType;
    }

    public void setType(IntentAttachPointType intentAttachPointType) {
        this.intentAttachPointType = intentAttachPointType;
    }

    public void setPolicySets(List<PolicySet> policySets) {
        this.policySets = policySets;
    }

    public void setRequiredIntents(List<Intent> intents) {
        this.requiredIntents = intents;
    }

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    /*
    http://[host][:port][/[object]]
    http:[/][object]
    */
    private void parse(String uriStr) {
        if (uriStr == null) {
            return;
        }
        URI uri = URI.create(uriStr);
        if (this.host == null) {
            this.host = uri.getHost();
        }
        if (port == null) {
            this.port = String.valueOf(uri.getPort());
        }
        if (serviceName == null) {
            String path = uri.getPath();
            if (path != null && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            this.serviceName = path;
        }
    }

    private void compose() {
        if (uri == null) {
            int p = -1;
            if (port != null && port.length() > 0) {
                p = Integer.decode(port);
            }
            String path = serviceName;
            if (path != null) {
                path = "/" + path;
            }
            try {
                uri = new URI("http", null, host, p, path, null, null).toString();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String getRegistryCenter() {
        return registryCenter;
    }

    @Override
    public void setRegistryCenter(String registryCenter) {
        this.registryCenter = registryCenter;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }
}
