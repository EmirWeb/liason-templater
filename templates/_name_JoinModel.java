package ${config['packageName']}.joinmodels;

public class ${className} extends Model{
    
    public static final String NAME = ${inflection.camelize(schema)}Model.class.getSimpleName();
   
    public static ContentValues getContentValues(Long ${inflection.camelize(schema, False)}Id, ${join["type"]} ${inflection.camelize(join["key"], False)}Id) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(Columns.${inflection.underscore(schema).upper()}.getName(), ${inflection.camelize(schema, False)}Id);    
        contentValues.put(Columns.${inflection.underscore(join["key"]).upper()}.getName(), ${inflection.camelize(join["key"], False)}Id);    

        return contentValues;
    }

    @Override
    public String getName(final Context context) {
        return NAME;
    }

    @ColumnDefinitions
    public static class Columns {
        
        @ColumnDefinition
        public static final ModelColumn ${inflection.underscore(schema).upper()} = new ModelColumn(${className}.NAME, "${inflection.camelize(schema, False)}Id", Type.${toSqlType(join["type"])});

        @ColumnDefinition
        public static final ModelColumn ${inflection.underscore(join["key"]).upper()} = new ModelColumn(${className}.NAME, "${inflection.camelize(join["key"], False)}Id", Type.${toSqlType(join["type"])});
        
    }

    @PathDefinitions
    public static class Paths {
        @PathDefinition
        public static final Path PATH = new Path(NAME);
    }
}
