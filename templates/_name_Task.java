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

    public static final String NAME = ${inflection.camelize(schema)}ViewModel.class.getSimpleName();

    public ProductTask(final Context context, final String authorty, final Uri uri) {
        super(context, authorty, uri);
    }

    % if isSet(response) :
    private ArrayList<${inflection.camelize(response["type"])}> getResponse(final Context context){
    % else :
    private ${inflection.camelize(response["type"])} getResponse(final Context context){
    % endif
        return null;
    }

    % if isSet(model) :
        % if isSet(response) :
    private ArrayList<${inflection.camelize(model["type"])}JsonModel> getJsonModel(final Context context, final ArrayList<${inflection.camelize(response["type"])}> ${inflection.camelize(inflection.pluralize(response["type"]), false)}){
        % else :
    private ArrayList<${inflection.camelize(model["type"])}JsonModel> getJsonModel(final Context context, final ${inflection.camelize(response["type"])} ${inflection.camelize(response["type"], false)}){
        % endif
    % else :
        % if isSet(response) :
    private ${inflection.camelize(model["type"])}JsonModel getJsonModel(final Context context, final ArrayList<${inflection.camelize(response["type"])}> ${inflection.camelize(inflection.pluralize(response["type"]), false)}){
        % else :
    private ${inflection.camelize(model["type"])}JsonModel getJsonModel(final Context context, final ${inflection.camelize(response["type"])} ${inflection.camelize(response["type"], false)}){
        % endif
    % endif
        return null;
    }

    @Override
    protected void onExecuteTask(final Context context) throws Exception {
        % if isSet(response) :
        final ArrayList<${inflection.camelize(response["type"])}> ${inflection.camelize(inflection.pluralize(response["type"]), false)} = getResponse(context);
        % else :
        final ${inflection.camelize(response["type"])} ${inflection.camelize(response["type"], false)} = getResponse(context);
        % endif

        % if isSet(model) :
        final ArrayList<${inflection.camelize(model["type"])}JsonModel> ${inflection.pluralize(inflection.camelize(model["type"], false) + "JsonModel")} = getJsonModel(context, ${inflection.camelize(response["type"], false)});
        % else :
        final ${inflection.camelize(model["type"])}JsonModel ${inflection.camelize(model["type"], false)}JsonModel = getJsonModel(context, ${inflection.camelize(response["type"], false)});
        % endif

        final ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();

        final Uri ${inflection.camelize(model["type"], false)}ModelUri = ${inflection.camelize(config['applicationName'])}Provider.getContentUri(context, ${inflection.camelize(model["type"])}Model.Paths.PATH);        
        % if isSet(model) :
        final ContentProviderOperation ${inflection.camelize(model["type"], false)}DeleteContentProviderOperation = ContentProviderOperation.newDelete(${inflection.camelize(model["type"], false)}ModelUri).build();

        contentProviderOperations.add(${inflection.camelize(model["type"], false)}DeleteContentProviderOperation );
        for (final ${inflection.camelize(model["type"])}JsonModel ${inflection.camelize(model["type"], false)}JsonModel : ${inflection.pluralize(inflection.camelize(model["type"], false) + "JsonModel")}) {
            final ContentValues ${inflection.camelize(model["type"],false)}ContentValues = ${inflection.camelize(model["type"])}Model.getContentValues(${inflection.camelize(model["type"], false)}JsonModel);
            final ContentProviderOperation ${inflection.camelize(model["type"],false)}ModelInsertContentProviderOperation = ContentProviderOperation.newInsert(${inflection.camelize(model["type"], false)}ModelUri).withValues(${inflection.camelize(model["type"],false)}ContentValues).build();
            contentProviderOperations.add(${inflection.camelize(model["type"],false)}ModelInsertContentProviderOperation);
        }
        % else :
        
        final ContentValues ${inflection.camelize(model["type"],false)}ContentValues = ${inflection.camelize(model["type"])}Model.getContentValues(${inflection.camelize(model["type"], false)}JsonModel);
        final ContentProviderOperation ${inflection.camelize(model["type"],false)}ModelInsertContentProviderOperation = ContentProviderOperation.newInsert(${inflection.camelize(model["type"], false)}ModelUri).withValues(${inflection.camelize(model["type"],false)}ContentValues).build();
        contentProviderOperations.add(${inflection.camelize(model["type"],false)}ModelInsertContentProviderOperation);
        % endif

        % for field in schemas[model["type"]]:
            % if not isJavaType(field["type"]):
        final Uri ${inflection.camelize(field["type"], false)}ModelUri = ${inflection.camelize(config['applicationName'])}Provider.getContentUri(context, ${inflection.camelize(field["type"])}Model.Paths.PATH);
                % if isSet(field) :
                
                % else :

                % endif
            % endif
        % endfor

        final ContentResolver contentResolver = context.getContentResolver();
        final String authority = ${inflection.camelize(config['applicationName'])}Provider.getContentAuthority(context);
        contentResolver.applyBatch(authority);

        % for viewModel in viewModels :
        final Uri ${inflection.camelize(viewModel["type"], false)}Uri = ${inflection.camelize(config['applicationName'])}Provider.getContentUri(context, ${inflection.camelize(viewModel["type"])}ViewModel.Paths.PATH);
        contentResolver.notify(${inflection.camelize(viewModel["type"], false)}Uri, null, false);
        % endfor
    }

    public static class Paths {
        public static final Path PATH = new Path(NAME);
    }
}
