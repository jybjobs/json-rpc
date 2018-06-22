package org.apache.tuscany.sca.binding.jsonrpc.utils;

import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.SerializerState;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;

public class JSON2JavaBean {
    private JSONSerializer serializer = new JSONSerializer();

    public JSON2JavaBean() {
        try {
            this.serializer.registerDefaultSerializers();
        } catch (Exception var2) {
            var2.getStackTrace();
        }

        this.serializer.setMarshallClassHints(true);
        this.serializer.setMarshallNullAttributes(true);
    }

    public Object transform(Object source,Class clazz) {
        if (source == null) {
            return null;
        } else {
            try {
                SerializerState state = new SerializerState();
                return this.serializer.unmarshall(state, clazz , source);
            } catch (Exception var4) {
                throw new TransformationException(var4);
            }
        }
    }

    public String getSourceDataBinding() {
        return "JSON";
    }

    public String getTargetDataBinding() {
        return "java:complexType";
    }

    public int getWeight() {
        return 5000;
    }
}