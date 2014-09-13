import json
import os
import shutil
import re
import inflection
from mako.template import Template

output = "output"

def makeTemplate(template, outputFilename):
	renderedTemplate = template.render(**templateData)
	outputFilenameWithRoot = output + "/" + outputFilename
	directory = os.path.dirname(outputFilenameWithRoot)
	if not os.path.exists(directory):
		os.makedirs(directory)

	outputFile = open(outputFilenameWithRoot, "w+")
	outputFile.write(renderedTemplate)
	outputFile.close()		

def makeTemplateFromData(templateData, templateFilename, outputFilename):
	template = Template(filename=templateFilename)
	makeTemplate(template, outputFilename)

def makeAndroidManifest(templateData):	
	templateFilename="templates/AndroidManifest.xml"
	outputFilename="src/main/AndroidManifest.xml"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makeTaskService(templateData):	
	templateFilename = "templates/_project_TaskService.java"
	outputFilename = "src/main/java/" + templateData["directory"] + "/liason/" + inflection.camelize(templateData["config"]["applicationName"]) + "TaskService.java"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makeProvider(templateData):	
	templateFilename = "templates/_project_Provider.java"
	outputFilename = "src/main/java/" + templateData["directory"] + "/liason/" + inflection.camelize(templateData["config"]["applicationName"]) + "Provider.java"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makeDatabaseHelper(templateData):	
	templateFilename = "templates/_project_DatabaseHelper.java"
	outputFilename = "src/main/java/" + templateData["directory"] + "/liason/" + inflection.camelize(templateData["config"]["applicationName"]) + "DatabaseHelper.java"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makeStrings(templateData):	
	templateFilename = "templates/strings.xml"
	outputFilename = "src/main/res/values/strings.xml"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makeListItemXML(templateData):	
	for schema in templateData["schemas"]:
		if isViewModel(templateData["mappings"], schema):
			fields = templateData["schemas"][schema]
			templateFilename = "templates/layout.xml"
			outputFilename = "src/main/res/layout/list_item_" + inflection.underscore(schema) + ".xml"
			modelTemplateData = templateData
			modelTemplateData["fields"] = fields		
			modelTemplateData["schema"] = schema
			makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeJsonModel(templateData):	
	for schema in templateData["schemas"]:
		if isModel(templateData["mappings"], schema):
			fields = templateData["schemas"][schema]
			templateFilename = "templates/_name_Json.java"
			outputFilename = "src/main/java/" + templateData["directory"] + "/models/" + inflection.camelize(schema) + "Json.java"
			modelTemplateData = templateData
			modelTemplateData["fields"] = fields		
			modelTemplateData["schema"] = schema
			makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeLiasonModel(templateData):	
	for schema in templateData["schemas"]:
		if isModel(templateData["mappings"], schema):
			fields = templateData["schemas"][schema]
			templateFilename = "templates/_name_Model.java"
			outputFilename = "src/main/java/" + templateData["directory"] + "/models/" + inflection.camelize(schema) + "Model.java"
			modelTemplateData = templateData
			
			modelTemplateData["fields"] = fields		
			modelTemplateData["schema"] = schema
			makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeLiasonViewModel(templateData):	
	for schema in templateData["schemas"]:
		if isViewModel(templateData["mappings"], schema):
			fields = templateData["schemas"][schema]
			templateFilename = "templates/_name_ViewModel.java"
			outputFilename = "src/main/java/" + templateData["directory"] + "/viewmodels/" + inflection.camelize(schema) + "ViewModel.java"
			modelTemplateData = templateData
			modelTemplateData["fields"] = fields		
			modelTemplateData["schema"] = schema
			makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeLiasonJoinModel(templateData):	
	for schema in templateData["schemas"]:
		if not isViewModel(templateData["mappings"], schema):
			fields = templateData["schemas"][schema]
			for field in fields :
				if not isJavaType(field["type"]):					
					modelTemplateData = templateData		
					modelTemplateData["join"] = field		
					modelTemplateData["fields"] = fields		
					modelTemplateData["schema"] = schema
					modelTemplateData["className"] = inflection.camelize(schema) + inflection.camelize(field["key"]) + "JoinModel"
					
					templateFilename = "templates/_name_JoinModel.java"
					outputFilename = "src/main/java/" + templateData["directory"] + "/joinmodels/" + modelTemplateData["className"] + "JoinModel.java"

					makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeTask(templateData):		
	tasks =  templateData["mappings"]["tasks"]
	for task in tasks:
		templateFilename = "templates/_name_Task.java"
		outputFilename = "src/main/java/" + templateData["directory"] + "/tasks/" + inflection.camelize(task) + "Task.java"
		modelTemplateData = templateData
		taskMappings = tasks[task]

		if "response" in taskMappings :
			modelTemplateData["response"] = taskMappings["response"]
		else :
			modelTemplateData["response"] = {}

		if "model" in taskMappings :
			modelTemplateData["model"] = taskMappings["model"]
		else :
			modelTemplateData["model"] = {}
			
		if "viewModels" in taskMappings :
			modelTemplateData["viewModels"] = taskMappings["viewModels"]
		else :
			modelTemplateData["viewModels"] = []
					
		makeTemplateFromData(modelTemplateData, templateFilename, outputFilename)

def makeBuildGradle(templateData):			
	templateFilename = "templates/build.gradle"
	outputFilename = "build.gradle"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def makePomXml(templateData):			
	templateFilename = "templates/pom.xml"
	outputFilename = "pom.xml"
	makeTemplateFromData(templateData, templateFilename, outputFilename)

def isId(field) :
	hasId = "id" in field
	if hasId :
		return field["id"]
	return False

def removeSetNotation(name):
	return name.replace("[]", "")	

def toSafeType(fieldType):
	if fieldType in javaTypes:
		return javaTypeToSafeJavaTypeMap[fieldType]
	return fieldType

safeJavaTypes = [
	"Integer",
	"Long",
	"Double",
	"Float",
	"String",
	"Boolean",
	"Byte"
];

javaTypes = [
	"int",
	"Integer",
	"long",
	"Long",
	"double",
	"Double",
	"float",
	"Float",
	"String",
	"boolean",
	"Boolean",
	"byte",
	"Byte"
];

sqlTypes = [
	"blob",
	"integer",
	"real",
	"text"
];

javaTypeToSafeJavaTypeMap = {
	javaTypes[0]: safeJavaTypes[0],
	javaTypes[1]: safeJavaTypes[0],
	javaTypes[2]: safeJavaTypes[1],
	javaTypes[3]: safeJavaTypes[1],
	javaTypes[4]: safeJavaTypes[2],
	javaTypes[5]: safeJavaTypes[2],
	javaTypes[6]: safeJavaTypes[3],
	javaTypes[7]: safeJavaTypes[3],
	javaTypes[8]: safeJavaTypes[4],
	javaTypes[9]: safeJavaTypes[5],
	javaTypes[10]: safeJavaTypes[5],
	javaTypes[11]: safeJavaTypes[6],
	javaTypes[12]: safeJavaTypes[6]
};

javaTypeToSqlTypeMap = {
	javaTypes[0]: sqlTypes[1],
	javaTypes[1]: sqlTypes[1],
	javaTypes[2]: sqlTypes[1],
	javaTypes[3]: sqlTypes[1],
	javaTypes[4]: sqlTypes[2],
	javaTypes[5]: sqlTypes[2],
	javaTypes[6]: sqlTypes[2],
	javaTypes[7]: sqlTypes[2],
	javaTypes[8]: sqlTypes[3],
	javaTypes[9]: sqlTypes[3],
	javaTypes[10]: sqlTypes[3],
	javaTypes[11]: sqlTypes[0],
	javaTypes[12]: sqlTypes[0]
};

def cleanType(type):	
	return type.replace("[]", "")

def isSet(field):
	if "isSet" in field :
		return field["isSet"]
	return "[]" in field["type"]

def toSqlType(type):	
	if type in javaTypeToSqlTypeMap:
		return javaTypeToSqlTypeMap[type]
	return sqlTypes[0]

def isJavaType(type):
	return type in javaTypes

def isTask(mappings, schema):
	return schema in mappings["tasks"]

def isModel(mappings, schema):
	return schema in mappings["models"]

def isViewModel(mappings, schema):
	return schema in mappings["viewmodels"]

def isTask(mappings, schema):
	return schema in mappings["tasks"]

def getId(schema):	
	for field in schema:
		if "id" in field:
			return field["type"]
	return None

def getField(schemas, type):
	return schemas[type]

def deleteOutputDirectory():
	try:
		shutil.rmtree(output)
	except:
		pass

def getTemplateData():
	schemasJson=open("schemas.json");
	schemas = json.load(schemasJson);
	schemasJson.close()

	mappingsJson=open("mappings.json");
	mappings = json.load(mappingsJson);
	mappingsJson.close()

	configJson=open("config.json");
	config = json.load(configJson);
	configJson.close()

	packageName = config['packageName']
	directory = re.sub('\.','/', packageName)

	templateData = {}
	templateData["directory"] = directory
	templateData["inflection"] = inflection
	templateData["mappings"] = mappings
	templateData["schemas"] = schemas
	templateData["config"] = config
	templateData["isJavaType"] = isJavaType
	templateData["isId"] = isId
	templateData["isModel"] = isModel
	templateData["isViewModel"] = isViewModel
	templateData["isTask"] = isTask
	templateData["toSqlType"] = toSqlType
	templateData["toSafeType"] = toSafeType
	templateData["isSet"] = isSet
	templateData["getId"] = getId
	
	cleanSchemas(templateData)
	cleanMappings(templateData)

	return templateData	

def cleanSchemas(templateData):
	schemas = templateData["schemas"]
	for schema in schemas:
		for field in schemas[schema]:
			cleanField(field)

def cleanField(field):
	field["isSet"] = isSet(field)
	field["type"] = cleanType(field["type"])			

def cleanMappings(templateData):
	mappings = templateData["mappings"]
	tasks = mappings["tasks"]
	for task in tasks:
		responseField = tasks[task]["response"]
		modelField = tasks[task]["model"]
		cleanField(responseField)
		cleanField(modelField)

deleteOutputDirectory()
templateData = getTemplateData()
makeAndroidManifest(templateData)
makeTaskService(templateData)
makeProvider(templateData)
makeDatabaseHelper(templateData)
makeStrings(templateData)
makeListItemXML(templateData)
makeJsonModel(templateData)
makeLiasonModel(templateData)
makeLiasonViewModel(templateData)
makeBuildGradle(templateData)
makePomXml(templateData)
makeTask(templateData)
makeLiasonJoinModel(templateData)

