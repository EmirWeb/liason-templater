package ${config['packageName']}.liason;

import android.content.Context;

public class ${inflection.camelize(config['applicationName'])}DatabaseHelper extends DatabaseHelper {
    
    private static final String NAME = "${inflection.camelize(config['applicationName'])}Database";
    private static final int VERSION = 1;

    public SampleDatabaseHelper(Context context) {
        super(context, NAME, VERSION);
    }

    @Override
    public List<Content> getContent(final Context context) {
        final List<Content> contentList = new ArrayList<Content>();

        % for model in mappings['models']:
        contentList(new ${model}Model());
        % endfor

        % for viewmodel in mappings['viewmodels']:
        contentList(new ${viewmodel}ViewModel());
        % endfor

        return contentList;
    }

}
