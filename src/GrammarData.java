import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GrammarData {

	String name;

	String parentClass = null;

	Set<String> interfaceName = new HashSet<String>();

	List<String> methodNames = new ArrayList<String>();
	List<String> methodNamesOnly = new ArrayList<String>();

	public List<String> getMethodNamesOnly() {
		return methodNamesOnly;
	}

	public void setMethodNamesOnly(List<String> methodNamesOnly) {
		this.methodNamesOnly = methodNamesOnly;
	}

	List<String> fieldName;

	List<String> classObj = new ArrayList<String>();
	
	Set<String> dependencies = new HashSet<String>();
	
	Map<String,String> fieldObj;
	
	
	public Set<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<String> dependencies) {
		this.dependencies = dependencies;
	}

	public Map<String, String> getFieldObj() {
		return fieldObj;
	}

	public void setFieldObj(Map<String, String> fieldObj) {
		this.fieldObj = fieldObj;
	}

	boolean classType = false;

	public boolean isClassType() {
		return classType;
	}

	public void setClassType(boolean classType) {
		this.classType = classType;
	}

	public List<String> getClassObj() {
		return classObj;
	}

	public void setClassObj(List<String> classObj) {
		this.classObj = classObj;
	}

	public List<String> getFieldName() {
		return fieldName;
	}

	public void setFieldName(List<String> fieldName) {
		this.fieldName = fieldName;
	}

	public List<String> getMethodNames() {
		return methodNames;
	}

	public void setMethodNames(List<String> methodNames) {
		this.methodNames = methodNames;
	}

	String primitiveVariables;

	public String getName() {
		return name;
	}

	public void setName(String className) {
		this.name = className;
	}

	public String getParentClass() {
		return parentClass;
	}

	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}

	public Set<String> getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(Set<String> interfaceName) {
		this.interfaceName = interfaceName;
	}

	// public List<String> getCollectionClasses() {
	// return collectionClassObj;
	// }
	//
	// public void setCollectionClasses(List<String> collectionClasses) {
	// this.collectionClassObj = collectionClasses;
	// }
	//
	// public String[] getClassObject() {
	// return classObject;
	// }
	//
	// public void setClassObject(String[] classObject) {
	// this.classObject = classObject;
	// }

	public String getPrimitiveVariables() {
		return primitiveVariables;
	}

	public void setPrimitiveVariables(String primitiveVariables) {
		this.primitiveVariables = primitiveVariables;
	}

	@Override
	public String toString() {
		StringBuffer grammar = new StringBuffer();
		grammar.append("class name: " + getName());
		grammar.append("\n");
		if(getParentClass()!=null){
			grammar.append("Parent class: ");
			grammar.append(getParentClass());
			grammar.append("\n");
		}
		if (!getInterfaceName().isEmpty()) {
			grammar.append("Interfaces implemented: ");
			for (String interfaceName : getInterfaceName()) {
				grammar.append(interfaceName + "\n");
			}
		}
		if (getMethodNames() != null) {
			grammar.append("Methods in class: ");
			for (String method : getMethodNames()) {
				grammar.append(getMethodNames() + "\n");
			}
		}
		grammar.append("\n");
		grammar.append("Fields: ");
		for (String field : getFieldName()) {
			grammar.append(field + "\n");
		}
		return grammar.toString();
	}

}
