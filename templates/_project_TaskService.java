<%
applicationPrefix = inflection.camelize(config['applicationName'])
%>
package ${config['packageName']}.liason;

import android.content.Context;

public class ${applicationPrefix}TaskService extends TaskService {

    public static void startTask(final Context context, final Uri uri) {
        startTask(context, uri, ${applicationPrefix}TaskService.class);
    }

    public static void forceStartTask(final Context context, final Uri uri) {
        forceStartTask(context, uri, ${applicationPrefix}TaskService.class);
    }

    @Override
    public String getAuthority(final Context context) {
        return ${applicationPrefix}Provider.getProviderAuthority(context);
    }

    @Override
    public Set<Class> getTasks(Context context) {
        final Set<Class> tasks = new HashSet<Class>();

        % for task in mappings['tasks']:
        tasks.add(${inflection.camelize(task)}.class);
        % endfor

        return tasks;
    }
}