
/**
 * 
 * This class is starting point for UMLGenerator, it requires classpath of java
 * source code to be provided as command line argument. It follows the below
 * sequence to generate UML diagram 1. Initiates grammar creation by calling
 * createGrammar method of GrammarGenerator. 2. Provide the grammar generated to
 * generateImage method of ImageGenerator.
 * 
 * @author Sonika Arora
 *
 */
public class UMLParser {

	public static void main(String[] args) throws Exception {

		Parser parserObj = Parser.getInstance();
		if (args.length < 2) {
			System.out.println("Please provide classpath for java source code and output file name.");
			return;
		}
		try {
			parserObj.readClasses(args[0]);
			GrammarGenerator grammarGenerator = new GrammarGenerator();
			String grammar = grammarGenerator.createGrammar(parserObj.getGrammarDataObjList());
			ImageGenerator imageGenerator = new ImageGenerator();
			imageGenerator.generateImage(grammar,args[1].toString());
		} catch (NoJavaFilesException exception) {
			System.out.println(exception);
		}

	}

}
