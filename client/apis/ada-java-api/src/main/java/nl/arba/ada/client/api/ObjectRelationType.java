package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class ObjectRelationType {
    private String strName;
    private ObjectRelationTypeObjectType object1Type;
    private ObjectRelationTypeObjectType object2Type;

    public void setName(String name) {
        strName = name;
    }

    public String getName() {
        return strName;
    }

    public void setObject1type(String type) {
        object1Type = ObjectRelationTypeObjectType.fromString(type);
    }

    public ObjectRelationTypeObjectType getObject1Type() {
        return object1Type;
    }

    public void setObject2type(String type) {
        object2Type = ObjectRelationTypeObjectType.fromString(type);
    }

    public ObjectRelationTypeObjectType getObject2Type() {
        return object2Type;
    }

    public String toJson() {
        try {
            HashMap<String, String> data = new HashMap<>();
            data.put("name", getName());
            data.put("object1type", object1Type == null ? ObjectRelationTypeObjectType.ANY.toString() : object1Type.toString());
            data.put("object2type", object2Type == null ? ObjectRelationTypeObjectType.ANY.toString() : object2Type.toString());
            return (new ObjectMapper()).writeValueAsString(data);
        }
        catch (Exception err) {
            return "{}";
        }
    }
}
