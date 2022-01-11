package egovframework.cmmn;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class PropertyUtil {
	public static Properties getProperties(String sPropertyFile) throws IOException {
//		InputStream is = new FileInputStream(sPropertyFile);
		Reader reader = new InputStreamReader(new FileInputStream(sPropertyFile), "UTF-8");
		Properties properties = new Properties();
		properties.load(reader);
		
		return properties;
	}
}
