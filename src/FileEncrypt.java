import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
 
public class FileEncrypt {
	static BufferedReader br = null;
	boolean exists = false;
	
	public FileEncrypt(String filename) {
		try {
			br = new BufferedReader(new FileReader(filename));
			exists = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getLines() {
		ArrayList<String> s = new ArrayList<String>();
 		try { 
			String sCurrentLine;
  
			while ((sCurrentLine = br.readLine()) != null) {
				s.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
 		return s;
	}
}