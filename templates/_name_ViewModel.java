package ${config['packageName']}.viewmodels;

public class ${inflection.camelize(schema)}ViewModel extends ViewModel{
   
    public static final String NAME = ${inflection.camelize(schema)}ViewModel.class.getSimpleName();

    @Override
    public String getName(final Context context) {
        return NAME;
    }

     @ColumnDefinitions
    public static class Columns {
        % for field in fields:
        @ColumnDefinition
        public static final ViewModelColumn ${inflection.underscore(field["key"]).upper()} = new ViewModelColumn(${inflection.camelize(schema)}ViewModel.NAME, "${field["key"]}", Type.${toSqlType(field["type"])});
        % endfor
    }

    @PathDefinitions
    public static class Paths {
        @PathDefinition
        public static final Path PATH = new Path(NAME);
    }
}
