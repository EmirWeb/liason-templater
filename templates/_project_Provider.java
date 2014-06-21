package ${config['packageName']}.liason;

import android.content.Context;
import android.content.res.Resources;

import mobi.liason.loaders.DatabaseHelper;
import mobi.liason.loaders.Provider;

public class ${inflection.camelize(config['applicationName'])}Provider extends Provider {
    
    @Override
    public String getAuthority(final Context context) {
        return getContentAuthority(contex)
    }

    @Override
    protected DatabaseHelper onCreateDatabaseHelper(final Context context) {
        return new ${inflection.camelize(config['applicationName'])}DatabaseHelper(context);
    }

    public static String getContentAuthority(final Context context) {
        final Resources resources = context.getResources();
        return resources.getString(R.string.authority_${inflection.underscore(config['applicationName'])});        
    }

    public static Uri getContentUri(final Context context, final Path path, final Object... objects) {        
        final String authority = getContentAuthority(context)
        final String scheme = ContentResolver.SCHEME_CONTENT;
        return getUri(scheme, authority, path, objects);
    }
}
