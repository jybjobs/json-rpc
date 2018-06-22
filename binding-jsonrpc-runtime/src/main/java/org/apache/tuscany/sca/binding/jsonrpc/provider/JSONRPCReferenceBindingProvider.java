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

package org.apache.tuscany.sca.binding.jsonrpc.provider;

import org.apache.tuscany.sca.binding.jsonrpc.JSONRPCBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.ServiceRuntimeException;

import java.lang.reflect.Method;

/**
 * Implementation of the JSONRPC Binding Provider for References
 * 
 * @version $Rev$ $Date$
 */
public class JSONRPCReferenceBindingProvider implements ReferenceBindingProvider {

    private RuntimeComponentReference reference;
    private JSONRPCBinding binding;
    
    public JSONRPCReferenceBindingProvider(RuntimeComponent component,
                                           RuntimeComponentReference reference,
                                           JSONRPCBinding binding) {
           this.reference = reference;
           this.binding = binding;
        
    }

    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }
    
//    public Invoker createInvoker(Operation operation) {
//        return new JSONRPCBindingInvoker(operation, binding.getURI());
//    }
    public Invoker createInvoker(Operation operation) {
        //    Class<?> iface = ((JavaInterface) reference.getInterfaceContract().getInterface()).getJavaClass();
//            Method remoteMethod = JavaInterfaceUtil.findMethod(iface, operation);
        //name isAlive
        if (binding.getRegistryName() != null && binding.getRegistryName().trim().length() > 0 ) {
            return new JSONRPCReferenceZKInvoker(operation, binding.getURI(), binding.getServiceName(),
                    binding.getRegistryName());
        }else{
            return new JSONRPCBindingInvoker(operation, binding.getURI());
        }
    }

    public void start() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
