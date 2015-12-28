import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.plantuml.SourceStringReader;

/**
 * @author Sonika Arora
 *
 */
public class ImageGenerator {
	
	void generateImage(String source, String fileName) {
		SourceStringReader reader = new SourceStringReader(source);
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			reader.generateImage(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
