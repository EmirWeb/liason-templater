package ${config['packageName']}.models;

import com.google.gson.annotations.SerializedName;

public class ${inflection.camelize(schema)}Json {

    % for field in fields:
    @SerializedName(Fields.${inflection.underscore(field["key"]).upper()})
    private final ${field["type"]} m${inflection.camelize(field["key"])};    

    % endfor
    <% 
    constructor = []
    for field in fields:
        constructor.append("final " + field["type"] + " " + inflection.camelize(field["key"], False))
    %>
    public Product(${", ".join(constructor)}) {
    % for field in fields:
        m${inflection.camelize(field["key"])} = ${inflection.camelize(field["key"], False)};        
    % endfor
    }

    % for field in fields:
    public ${field["type"]} get${inflection.camelize(field["key"])}() {
        return m${inflection.camelize(field["key"])};
    }       
     
    % endfor
    public static class Fields {
        % for field in fields:
        public static final String ${inflection.underscore(field["key"]).upper()} = "${field["key"]}";
        % endfor
    }
}
