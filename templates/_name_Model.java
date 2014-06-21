package ${config['packageName']}.models;

public class ${inflection.camelize(schema)}Model extends Model{
   
    public static final String NAME = ${inflection.camelize(schema)}Model.class.getSimpleName();

    public static ContentValues getContentValues(final ${inflection.camelize(schema)}Json ${inflection.camelize(schema, False)}Json) {
        final ContentValues contentValues = new ContentValues();

    % for field in fields:
        contentValues.put(Columns.${inflection.underscore(field["key"]).upper()}.getName(), ${inflection.camelize(field["key"], False)}Json.get${inflection.camelize(field["key"], False)}());
    % endfor

        return contentValues;
    }

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
        public static final ModelColumn ${inflection.underscore(field["key"]).upper()} = new ModelColumn(${inflection.camelize(schema)}Model.NAME, ${inflection.camelize(schema)}Json.Fields.ID, ModelColumn.Type.integer);
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
