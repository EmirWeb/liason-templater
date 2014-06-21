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

def isId(field) :
	hasId = "id" in field
	if hasId :
		return field["id"]
	return False

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
	"Boolean"
];

def isJavaType(type):
	return type in javaTypes

def isModel(mappings, schema):
	return schema in mappings["models"]

def isViewModel(mappings, schema):
	return schema in mappings["viewmodels"]

def isTask(mappings, schema):
	return schema in mappings["tasks"]

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
	
	return templateData

deleteOutputDirectory()
templateData = getTemplateData()
makeAndroidManifest(templateData)
makeProvider(templateData)
makeDatabaseHelper(templateData)
makeStrings(templateData)
makeListItemXML(templateData)
makeJsonModel(templateData)
makeLiasonModel(templateData)
makeLiasonViewModel(templateData)