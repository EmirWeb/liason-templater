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

    @ColumnDefinitions
    public static class Columns {
        % for field in fields:
        @ColumnDefinition
        public static final ModelColumn ${inflection.underscore(field["key"]).upper()} = new ModelColumn(${inflection.camelize(schema)}Model.NAME, ${inflection.camelize(schema)}Json.Fields.ID, Type.${toSqlType(field["type"])});
        % endfor
    }

    @PathDefinitions
    public static class Paths {
        @PathDefinition
        public static final Path PATH = new Path(NAME);
    }
}
