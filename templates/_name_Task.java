<%
applicationPrefix = inflection.camelize(config['applicationName'])
def getItemTypeName(variable, ending = ""):
    return inflection.camelize(variable["type"]) + ending

def getSetTypeName(variable, ending = ""):
    return "ArrayList<" + inflection.camelize(variable["type"]) + ending + ">"

def getItemVariableName(variable, ending = ""):
    return inflection.camelize(variable["type"], false) + ending

def getSetVariableName(variable, ending = ""):
    return inflection.pluralize(inflection.camelize(variable["type"], false) + ending)

def getTypeName(variable, ending = ""):
    if isSet(variable) :
        return getSetTypeName(variable, ending)
    return getItemTypeName(variable, ending)

def getVariableName(variable, ending = ""):
    if isSet(variable) :
        return getSetVariableName(variable, ending)
    return getItemVariableName(variable, ending)

def getUri(variable, ending):
    variableNamePrefix = getItemVariableName(variable)
    variableTypePrefix = getItemTypeName(variable, ending)
    ret = "final Uri " + variableNamePrefix + "Uri" 
    ret += " = " + applicationPrefix + "Provider.getContentUri(context, " 
    ret += getItemTypeName(variable, ending) + ".Paths.PATH);"
    return ret

def writeToDB(variable, indentCount):
    indent = "\t" * indentCount

    variableNamePrefix = getItemVariableName(variable)
    variableTypePrefix = getItemTypeName(variable)

    ret = indent
    ret += getUri(variable, "Model") + "\n"

    if isSet(variable) :        
        ret += indent
        ret += "final ContentProviderOperation " + variableNamePrefix + "DeleteContentProviderOperation = "
        ret += "ContentProviderOperation.newDelete(" + variableNamePrefix + "ModelUri).build();\n"
        ret += indent
        ret += "contentProviderOperations.add(" + variableNamePrefix + "DeleteContentProviderOperation );\n"            

    if isSet(variable) :        
        ret += indent
        ret += "for (final " + getItemTypeName(variable, "JsonModel") + " " + getItemVariableName(variable, "JsonModel") + " : "
        ret += getVariableName(variable, "JsonModel") +") {\n"
        indent = "\t" * ( indentCount + 1)
    
    contentValuesVariableName = variableNamePrefix + "ContentValues"    
    insertVariablebName = variableNamePrefix + "ModelInsertContentProviderOperation"    
    ret += indent
    ret += "final ContentValues " + contentValuesVariableName + " = " + variableTypePrefix + "Model.getContentValues(" + getItemVariableName(variable, "JsonModel") + ");\n"    
    ret += indent
    ret += "final ContentProviderOperation " + insertVariablebName + " = ContentProviderOperation.newInsert(" + variableNamePrefix + "ModelUri).withValues(" 
    ret += contentValuesVariableName + ").build();\n"
    ret += indent
    ret += "contentProviderOperations.add(" + insertVariablebName + ");\n"

    if isSet(variable) :
        indent = "\t" * indentCount
        ret += indent
        ret += "}\n"   

    for field in schemas[variable["type"]]:
        if not isJavaType(field["type"]):
            ret += "\n" + writeToDB(field, 2)

    return ret   


responseSetType = getSetTypeName(response)
responseItemType = getItemTypeName(response)
responseSetVariable = getSetVariableName(response)
responseItemVariable = getItemVariableName(response)

modelSetType = getSetTypeName(response, "JsonModel")
modelItemType = getItemTypeName(response, "JsonModel")
modelSetVariable = getSetVariableName(response, "JsonModel")
modelItemVariable = getItemVariableName(response, "JsonModel")

responseType = getTypeName(response)
responseVariable = getVariableName(response)
modelType = getTypeName(response, "JsonModel")
modelVariable = getVariableName(response, "JsonModel")

%>
package ${config['packageName']}.tasks;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import com.google.gson.Gson;

import java.util.ArrayList;

import mobi.liason.loaders.Path;
import mobi.liason.mvvm.task.Task;
import mobi.liason.sample.R;

public class ${inflection.camelize(schema)}Task extends Task {

    /**
     * The name of task
     */
    public static final String NAME =${inflection.camelize(schema)}Task.class.getSimpleName();

    /**
     * REQUIRED - TaskService expects this constuctor, feel free to add functionality to it, but do not delete it.
     */ 
    public ProductTask(final Context context, final String authorty, final Uri uri) {
        super(context, authorty, uri);
    }

    /**
     * Background task / Network call goes here and returns the full model
     * @param Appliation Context
     * @return Java Object representing the reponse of the background task
     */
    private ${responseType} getResponse(final Context context){
        return null;
    }

    /**
     * Return the model and relationships that will be written to DB, in some cases, this will be the same as getResponse(final Context context);
     * @param Application Context
     * @return Java Object representing the model and relationships that will be written to the DB
     */
    private ${modelType} getJsonModel(final Context context, final ${responseType} ${responseVariable}){
        return null;
    }

    @Override
    protected void onExecuteTask(final Context context) throws Exception {

        final ${responseType} ${responseVariable} = getResponse(context);        

        final ${modelType} ${modelVariable} = getJsonModel(context, ${responseVariable});

        final ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
       
${writeToDB(model, 2)}

        final ContentResolver contentResolver = context.getContentResolver();
        final String authority = ${applicationPrefix}Provider.getContentAuthority(context);
        contentResolver.applyBatch(authority, contentProviderOperations);

        % for viewModel in viewModels :
        ${getUri(viewModel, "ViewModel")}        
        contentResolver.notify(${inflection.camelize(viewModel["type"], false)}Uri, null, false);
        % endfor
    }

    @PathDefinitions
    public static class Paths {
        @PathDefinition
        public static final Path PATH = new Path(NAME);
    }
}
