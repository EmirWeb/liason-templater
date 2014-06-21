package ${config['packageName']}.viewmodels;

public class ${inflection.camelize(schema)}ViewModel extends ViewModel{
   
    public static final String NAME = ${inflection.camelize(schema)}ViewModel.class.getSimpleName();

    @Override
    public String getName(final Context context) {
        return NAME;
    }

    @Override
    public List<Column> getColumns(final Context context) {
        return Arrays.asList(Columns.COLUMNS);
    }

    @Override
    public List<Path> getPaths(Context context) {
        return Lists.newArrayList(Paths.PATH);
    }

    public static class Columns {
        % for field in fields:
        public static final ViewModelColumn ${inflection.underscore(field["key"]).upper()} = new ViewModelColumn(${inflection.camelize(schema)}ViewModel.NAME, "${field["key"]}", Type.${toSqlType(field["type"])});
        % endfor
        <% 
        columns = []
        for field in fields:
            columns.append(inflection.underscore(field["key"]).upper())
        %>    
        public static final Column[] COLUMNS = new Column[]{${", ".join(columns)}};
    }

    public static class Paths {
        public static final Path PATH = new Path(NAME);
    }
}
