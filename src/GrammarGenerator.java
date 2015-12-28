import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sonika Arora
 *
 */
public class GrammarGenerator {

	public String createGrammar(List<GrammarData> grammarDataObjList) {
		StringBuffer grammarString = new StringBuffer();
		grammarString.append("@startuml" + "\n");
		grammarString.append("skinparam classAttributeIconSize 0 ");
		for (GrammarData grammarDataObj : grammarDataObjList) {

			// Parent class
			if (grammarDataObj.getParentClass() != null) {
				grammarString.append(grammarDataObj.getParentClass() + " <|-- " + grammarDataObj.getName());
			}
			grammarString.append("\n");

			// Interfaces
			if (!grammarDataObj.getInterfaceName().isEmpty()) {
				for (String interfaceName : grammarDataObj.getInterfaceName()) {
					grammarString.append(interfaceName + " <|.. " + grammarDataObj.getName());
					grammarString.append("\n");
				}
			}
			// For class it will add "class {" to grammar and for interface it
			// will add "interface {" to the grammar.
			if (grammarDataObj.isClassType())
				grammarString.append("class " + grammarDataObj.getName() + " {\n");
			else
				grammarString.append("interface " + grammarDataObj.getName() + " {\n");
			// Method Names
			if (grammarDataObj.getMethodNames() != null) {
				for (String method : grammarDataObj.getMethodNames()) {
					grammarString.append(method);
					grammarString.append("\n");
				}
			}
			// Field Names
			if (!grammarDataObj.getFieldName().isEmpty()) {
				for (String field : grammarDataObj.getFieldName()) {
					grammarString.append(field);
					grammarString.append("\n");
				}

			}
			grammarString.append(" }\n");

			// classObject Names
			if (!grammarDataObj.getClassObj().isEmpty()) {
				for (String classObj : grammarDataObj.getClassObj()) {
					grammarString.append(classObj);
					grammarString.append("\n");
				}
			}

			if (!grammarDataObj.getDependencies().isEmpty()) {
				for(String dependency : grammarDataObj.getDependencies()) {
					grammarString.append(grammarDataObj.getName()+ " ..> \"uses\" "+ dependency+"\n");
				}
			}

		}
		Map<String, String> fieldMap = new ConcurrentHashMap<String, String>();

		for (GrammarData grammarDataObj : grammarDataObjList) {
			if (!grammarDataObj.getFieldObj().isEmpty()) {
				fieldMap.putAll(grammarDataObj.getFieldObj());
			}

		}
		Set<String> keySet = fieldMap.keySet();
		String reverseKey = null;

		for (String key : keySet) {
			if(key.equals(reverseKey))
			{
				continue;
			}
			String[] keyString = key.split("\\ : ");
			reverseKey = keyString[1] + " : " + keyString[0];
			if (keySet.contains(reverseKey)) {
				grammarString.append(keyString[0] + " \"" + fieldMap.get(reverseKey) + "\"-- \"" + fieldMap.get(key)
						+ "\" " + keyString[1] + "\n");
				System.out.println("grammarstring........"+keyString[0] + " \"" + fieldMap.get(reverseKey) + "\"-- \"" + fieldMap.get(key)
				+ "\" " + keyString[1] + "\n");
				fieldMap.remove(key);
				fieldMap.remove(reverseKey);
				keySet.remove(key);
				keySet.remove(reverseKey);

			} else {
				grammarString.append(keyString[0] + " -- " + keyString[1] + "\n");
				System.out.println("inside else block...."+keyString[0] + " -- " + keyString[1] + "\n");
			}
		}

		grammarString.append("@enduml");
		System.out.println("grammar generated: " + grammarString);
		return grammarString.toString();

	}

}
