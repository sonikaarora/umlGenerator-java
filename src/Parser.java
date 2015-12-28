import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Sonika Arora
 *
 */
public class Parser {

	static private List<GrammarData> grammarDataObjList = new ArrayList<GrammarData>();

	GrammarData grammarDataObj;
	static Set<String> files = new HashSet<String>();
	// static List<String> methodName;

	/**
	 * @param folder
	 *            classpath of java source code
	 * @throws NoJavaFilesException
	 *             If classpath has no java files, exception will be thrown
	 */
	void readClasses(String folder) throws NoJavaFilesException {
		CompilationUnit cu = null;
		Parser parserObj = getInstance();
		File folderName = new File(folder);
		File[] fileNames = folderName.listFiles();
		for (File file : fileNames) {
			if (file.getName().endsWith("java")) {
				files.add(file.getName().split("\\.")[0]);
			}
		}
		if (files.isEmpty()) {
			throw new NoJavaFilesException("No java files available at the classpath provided");

		}

		for (File file : fileNames) {
			try {
				if (file.getName().endsWith("java")) {
					files.add(file.getName().split("\\.")[0]);
					FileInputStream in = new FileInputStream(file.getAbsolutePath());
					parserObj.parseCode(cu, in);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	public static Parser getInstance() {
		Parser parserObj = null;
		if (parserObj == null) {
			parserObj = new Parser();
		}
		return parserObj;
	}

	/**
	 * @param cu
	 * @param in
	 */
	void parseCode(CompilationUnit cu, FileInputStream in) {

		grammarDataObj = new GrammarData();

		try {
			// parse the file
			cu = JavaParser.parse(in);
			new MethodVisitor().visit(cu, grammarDataObj);

			List<TypeDeclaration> types = cu.getTypes();
			populateClassInterfaces(types);
			populateFields(types);

		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		grammarDataObjList.add(grammarDataObj);

	}

	/**
	 * This method will find all the instance variables of the class.
	 * 
	 * @param fields
	 */
	private void populateFields(List<TypeDeclaration> fields) {
		Map<String, String> fieldMap = new HashMap<String, String>();

		for (TypeDeclaration type : fields) {

			List<BodyDeclaration> members = type.getMembers();

			List<String> variables = new ArrayList<String>();
			List<String> classObjects = new ArrayList<String>();

			for (BodyDeclaration field : members) {
				if (field instanceof FieldDeclaration) {
					FieldDeclaration myType = (FieldDeclaration) field;
					List<VariableDeclarator> myFields = myType.getVariables();
					int modifier = myType.getModifiers();

					if (myType.getType() instanceof ReferenceType) {
						ReferenceType referenceType = (ReferenceType) myType.getType();
						// int arrayCount = referenceType.getArrayCount();
						if (referenceType.getType() instanceof PrimitiveType) {
							populatePrimitiveFields(variables, myType, myFields, modifier);

						}
						if (referenceType.getType() instanceof ClassOrInterfaceType) {
							ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) referenceType.getType();

							if (classOrInterfaceType.getTypeArgs() != null) {
								String packageName = classOrInterfaceType.getTypeArgs().getClass().getPackage()
										.getName();

								if (classOrInterfaceType.getTypeArgs().getClass().getPackage().getName()
										.equals("java.util")) {
									if (files.contains(classOrInterfaceType.getTypeArgs().get(0).toString())) {
										fieldMap.put(grammarDataObj.getName() + " : "
												+ classOrInterfaceType.getTypeArgs().get(0), "* ");

									} else {
										fieldMap.put(grammarDataObj.getName() + " : " + classOrInterfaceType.getName(),
												"1");
									}
								}

							} else if (files.contains(classOrInterfaceType.getName())) {

								fieldMap.put(grammarDataObj.getName() + " : " + classOrInterfaceType.getName(), "1");
							} else {
								populatePrimitiveFields(variables, myType, myFields, modifier);

							}

						}

					} else if (myType.getType() instanceof PrimitiveType) {
						if (myType.getType().getClass().equals(PrimitiveType.class)) {
							populatePrimitiveFields(variables, myType, myFields, modifier);
						}

					}
					String classType = myType.getType().getClass().getName().toString();
					System.out.println("className: " + classType);

				}

			}
			grammarDataObj.setFieldName(variables);
			grammarDataObj.setClassObj(classObjects);
			grammarDataObj.setFieldObj(fieldMap);
		}
	}

	private void populatePrimitiveFields(List<String> variables, FieldDeclaration myType,
			List<VariableDeclarator> myFields, int modifier) {

		String parameter = myFields.toString().substring(1, myFields.toString().length());
		String getMethod = "get" + parameter.substring(0, 1).toUpperCase()
				+ parameter.substring(1, parameter.length() - 1);
		String setMethod = "set" + parameter.substring(0, 1).toUpperCase()
				+ parameter.substring(1, parameter.length() - 1);

		if (grammarDataObj.getMethodNamesOnly().contains(getMethod)
				|| grammarDataObj.getMethodNamesOnly().contains(setMethod)) {
			modifier = 1;

			grammarDataObj.getMethodNames().remove(grammarDataObj.getMethodNamesOnly().indexOf(setMethod));
			grammarDataObj.getMethodNamesOnly().remove(setMethod);
			grammarDataObj.getMethodNames().remove(grammarDataObj.getMethodNamesOnly().indexOf(getMethod));
			grammarDataObj.getMethodNamesOnly().remove(getMethod);

		}
		// if (!grammarDataObj.isClassType()) {
		// modifier = 1;
		// }

		switch (modifier) {
		case 2:
			variables.add(
					"- " + myFields.toString().substring(1, myFields.toString().length() - 1) + " : " + myType.getType());
			break;
		case 1:
			variables.add(
					"+ " + myFields.toString().substring(1, myFields.toString().length() - 1) + " : " + myType.getType());
			break;
		
		case 1025:
			variables.add(
					"+ " + myFields.toString().substring(1, myFields.toString().length() - 1) + " : " + myType.getType());
			break;

		}
	}

	/**
	 * This method will find parent class, and implemented interfaces and will
	 * populate the grammarDataObj object.
	 * 
	 * @param types
	 */
	private void populateClassInterfaces(List<TypeDeclaration> types) {
		for (TypeDeclaration type : types) {
			if (type instanceof ClassOrInterfaceDeclaration) {
				grammarDataObj.setName(type.getName());
				// class type boolean is checked to add "interface" or "class"
				// string while creating grammar for plantUMl.
				if (!type.toString().contains(" interface ")) {
					grammarDataObj.setClassType(true);
				}

				List<ClassOrInterfaceType> extendsType = ((ClassOrInterfaceDeclaration) type).getExtends();
				List<ClassOrInterfaceType> implementsType = ((ClassOrInterfaceDeclaration) type).getImplements();
				if (extendsType != null) {
					for (ClassOrInterfaceType extendClass : extendsType) {
						grammarDataObj.setParentClass(extendClass.getName()); // Parentclass
					}
				}
				if (implementsType != null) {
					Set<String> interfaceList = new HashSet<String>();
					for (ClassOrInterfaceType interfaceName : implementsType) {

						interfaceList.add(interfaceName.getName());

					}
					grammarDataObj.setInterfaceName(interfaceList); // Interfaces
																	// implemented
				}

			}
		}
	}

	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		Parser parser = getInstance();

		List<String> methodList = new ArrayList<String>();
		List<String> methodNames = new ArrayList<String>();

		// methodName = new ArrayList<String>();
		@Override
		public void visit(ConstructorDeclaration constructor, Object arg) {
			parser.grammarDataObj = (GrammarData) arg;
			Set<String> dependeciesSet = new HashSet<String>();
			String parameterString = "";

			if (constructor.getParameters() != null) {

				List<Parameter> constructorParameterNames = constructor.getParameters();

				for (Parameter constructorParams : constructorParameterNames) {
					String constructorName[] = constructorParams.toString().split(" ");
					String param1 = constructorName[0];
					if (parser.files.contains(param1)) {
						parameterString = parameterString + constructorName[1] + " : " + param1 + ", ";
						dependeciesSet.add(param1);
					} else {
						parameterString = parameterString + constructorName[1] + " : " + param1 + ", ";
					}

				}
				if (!parameterString.isEmpty()) {
					parameterString = parameterString.substring(0, parameterString.length() - 2);
				}

				int modifiers = constructor.getModifiers();

				switch (modifiers) {

				case 1:

					methodList.add("+ " + constructor.getName() + "(" + parameterString + ") ");

					break;

				case 2:
					methodList.add("- " + constructor.getName() + "() ");
					break;

				}

			} else {

				int modifiers = constructor.getModifiers();

				switch (modifiers) {

				case 1:

					methodList.add("+ " + constructor.getName() + "() ");

					break;

				case 1025:
					methodList.add("+ " + constructor.getName() + "() ");
					break;

				}

			}

			if (!methodList.isEmpty()) {
				parser.grammarDataObj.setMethodNames(methodList);
			}
			if (!dependeciesSet.isEmpty()) {
				parser.grammarDataObj.setDependencies(dependeciesSet);
			}

		}

		@Override
		public void visit(MethodDeclaration method, Object arg) {
		
			List<String> parameterNames = new ArrayList<String>();
			Set<String> dependeciesSet = new HashSet<String>();
			if (method.getBody() != null) {
				List<Statement> statements = method.getBody().getStmts();
				if(statements != null) {
				for (Statement body : statements) {
					parameterNames.add(body.toString().split(" ")[0]);

				}
				for (String param : parameterNames) {
					if (parser.files.contains(param)) {
						dependeciesSet.add(param);
					}
				}
				}
			}

			methodNames.add(method.getName());
			parser.grammarDataObj = (GrammarData) arg;
			parser.grammarDataObj.setMethodNamesOnly(methodNames);

			String parameterString = "";

			if (method.getParameters() != null) {

				List<Parameter> methodParameterNames = method.getParameters();

				for (Parameter methodParams : methodParameterNames) {
					String methodName[] = methodParams.toString().split(" ");
					String param1 = methodName[0];
					if (parser.files.contains(param1)) {
						parameterString = parameterString + methodName[1] + " : " + param1 + ", ";
						dependeciesSet.add(param1);
					} else {
						parameterString = parameterString + methodName[1] + " : " + param1 + ", ";
					}

				}
				if (!parameterString.isEmpty()) {
					parameterString = parameterString.substring(0, parameterString.length() - 2);
				}

				int modifiers = method.getModifiers();

				switch (modifiers) {
				case 0:
					methodList.add("+ " + method.getName() + "(" + parameterString + ") : " + method.getType());

					break;

				case 1:

					methodList.add("+ " + method.getName() + "(" + parameterString + ") : " + method.getType());

					break;
				case 2:

					methodList.add("- " + method.getName() + "(" + parameterString + ") : " + method.getType());

					break;
				case 9:
					methodList.add("+ " + method.getName() + "(" + parameterString + ") : " + method.getType());

					break;

				case 1025:
					methodList.add("+ " + method.getName() + "() : " + method.getType());
					break;
				}

			} else {

				int modifiers = method.getModifiers();
				// if (!parser.grammarDataObj.isClassType()) {
				// modifiers = 1;
				// }
				switch (modifiers) {

				case 0:
					methodList.add("+ " + method.getName() + "() : " + method.getType());
					break;

				case 1:

					methodList.add("+ " + method.getName() + "() : " + method.getType());

					break;
				case 2:

					methodList.add("- " + method.getName() + "(" + parameterString + ") : " + method.getType());

					break;
				case 9:
					methodList.add("+ " + method.getName() + "() : " + method.getType());
					break;

				case 1025:
					methodList.add("+ " + method.getName() + "() : " + method.getType());
					break;

				}

			}

			if (!methodList.isEmpty()) {
				parser.grammarDataObj.setMethodNames(methodList);
			}
			if (!dependeciesSet.isEmpty()) {
				parser.grammarDataObj.setDependencies(dependeciesSet);
			}
		}

	}

	public List<GrammarData> getGrammarDataObjList() {
		return grammarDataObjList;
	}

}