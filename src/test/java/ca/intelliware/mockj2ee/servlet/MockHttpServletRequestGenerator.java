package ca.intelliware.mockj2ee.servlet;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
/**
 * TestHttpRequestGenerator (c) 1998 Intelliware Development Inc.
 * 
 * HttpRequest Generator: Reads <Servlet>tags from HTML flavoured files to
 * generate TestHttpRequests. Used to help test Servlet applications.
 * 
 * @version 1.0, 10/22/1998
 * @author Leo Sun
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
public class MockHttpServletRequestGenerator {
	//Vector of httpRequest objects read in from a given text file.
	public static Vector httpRequestVector;
	//file name
	public static String fileName;
	//servlet starting and ending indicators.
	public static final String SERVLET_START_IND = "<SERVLET";
	public static final String SERVLET_END_IND = "</SERVLET>";
	//servlet parameter starting indicator
	public static final String SERVLET_PARAM_IND = "<PARAM";
	//servlet param name.
	public static final String SERVLET_PARAM_NAME = "NAME";
	//servlet param value
	public static final String SERVLET_PARAM_VALUE = "VALUE";
	//result file director
	public static final String FILE_DIR = "D:\\WWW\\html\\test_site\\";
	//no. of files
	public static final int MAX_FILE_NO = 100;
	//file extention
	public static final String FILE_EXT = ".doc";
	/**
	 * @param s
	 *            java.lang.String
	 */
	public MockHttpServletRequestGenerator() {
		super();
	}
	/**
	 * Read a text file and seach for "servlet" inclusion and turn each servlet
	 * tag instance into a coressponding httpRequest object and store them in
	 * httpRequestVector.
	 * 
	 * @param fileName,
	 *            a text file path name.
	 * @param if
	 *            openFileDialog is on, a file dialog is up for choosing a file
	 *            from the FS.
	 * 
	 * @return Vector of httpRequests.
	 */
	public static Vector GetHttpRequestsFromFile() throws IOException,
			FileNotFoundException {
		Frame frame = new java.awt.Frame();
		FileDialog fileDialog = new FileDialog(frame, "Open <Servlet> File");
		fileDialog.show();
		//get the fileName
		fileName = fileDialog.getDirectory() + fileDialog.getFile();
		httpRequestVector = GetHttpRequestsFromFile(fileName);
		return httpRequestVector;
	}
	/**
	 * Read a text file and seach for "servlet" inclusion and turn each servlet
	 * tag instance into a corresponding httpRequest object and store them in
	 * httpRequestVector.
	 * 
	 * @param fileName,
	 *            a text file path name.
	 * @param if
	 *            openFileDialog is on, a file dialog is up for choosing a file
	 *            from the FS.
	 * 
	 * @return Vector of httpRequests.
	 */
	public static Vector GetHttpRequestsFromFile(String filePath)
			throws IOException, FileNotFoundException {
		//get the file name.
		FileInputStream fileInputStm = null;
		//DataInputStream dataStm = null;
		BufferedReader dataStm = null;
		httpRequestVector = new Vector();
		fileName = filePath;
		Hashtable params;
		MockHttpServletRequest req;
		try {
			File file = new File(fileName);
			//grab the file input stream
			fileInputStm = new FileInputStream(file);
			//chain with data input stream
			dataStm = new BufferedReader(new InputStreamReader(fileInputStm));
			//dataStm = new DataInputStream(fileInputStm);
			//main loop, read one line a time until end of the file
			String line;
			while (true) {
				line = dataStm.readLine();
				if (line == null) {
					break; //done.
				}
				if (line.length() >= SERVLET_START_IND.length()
						&& isServletStart(line)) { //beginnig of a servlet
												   // inclusion.
					req = new MockHttpServletRequest(); //instantiate a req
														// object.
					params = new Hashtable(); //instantiate a hash table
					line = dataStm.readLine(); //read in next line.
					while (!isServletEnd(line)) { //still inside the servlet
						if (isParamLine(line)) { //a line start with "<PARAM"?
							params.put(getParamName(line), getParamValue(line));
						}
						//read in next line
						line = dataStm.readLine();
					}
					req.setParametersDictionary(params);
					//insert req into the vector.
					httpRequestVector.addElement(req);
				}
			}
		} finally {
			if (!(fileInputStm == null))
				try {
					fileInputStm.close();
				} catch (IOException e) {
				}
			if (!(dataStm == null))
				try {
					dataStm.close();
				} catch (IOException e) {
				}
		}
		return httpRequestVector;
	}
	/**
	 * @return java.lang.String
	 * @param line
	 *            java.lang.String
	 */
	private static String getParamName(String line) {
		//get the substring between "NAME" and "VALUE"
		String value = line.substring(line.indexOf(SERVLET_PARAM_NAME)
				+ SERVLET_PARAM_NAME.length(), line
				.indexOf(SERVLET_PARAM_VALUE));
		//get rid of "="
		int index = value.indexOf("=");
		value = value.substring(index + 1);
		//trim off spaces.
		value = value.trim();
		value = value.substring(1, value.length() - 1);
		return value;
	}
	/**
	 * Parse the given line, return the value of the given param.
	 * 
	 * @return java.lang.String
	 * @param line
	 *            java.lang.String
	 * 
	 * @return java.lang.String
	 */
	private static String getParamValue(String line) {
		//get the substring between "NAME" and "VALUE"
		String value = line.substring(line.indexOf(SERVLET_PARAM_VALUE)
				+ SERVLET_PARAM_VALUE.length());
		//get rid of "="
		int index = value.indexOf("=");
		value = value.substring(index + 1);
		//get rid of ">"
		index = value.indexOf(">");
		value = value.substring(0, index);
		//trim off spaces.
		value = value.trim();
		value = value.substring(1, value.length() - 1);
		return value;
	}
	/**
	 * Check if the give string is a line of starting with PARAM_IND.
	 * 
	 * <pre>
	 * 
	 *    SERVLET_PARAM_IND is the starting String of the give line.
	 *  
	 * </pre>
	 * 
	 * @return boolean
	 * @param line
	 *            java.lang.String
	 */
	private static boolean isParamLine(String line) {
		//if SERVLET_PARAM_IND is a substring of the line
		if (line.indexOf(SERVLET_PARAM_IND) != -1) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Given a line, check if SERVLET_START_END is a substring of the given
	 * line. If it is, return true indicating the end of the Servlet inclusion;
	 * o.s, return false.
	 * 
	 * <pre>
	 * 
	 *    &lt;/Servlet tag is allways starts a new line. Is it true?
	 *  
	 * </pre>
	 * 
	 * @return boolean
	 * @param line
	 *            java.lang.String
	 */
	private static boolean isServletEnd(String line) {
		//if the line contains the SERVLET_END_IND substring.
		if (line.indexOf(SERVLET_END_IND) != -1) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Given a line, check if SERVLET_START_IND is a substring of the given
	 * line. If it is, return true indicating the beginning of the Servlet
	 * inclusion; o.s, return false.
	 * 
	 * <pre>
	 * 
	 *    Servlet tag is allways starts a new line. Is it true?
	 *  
	 * </pre>
	 * 
	 * @return boolean
	 * @param line
	 *            java.lang.String
	 */
	private static boolean isServletStart(String line) {
		//if the line contain the SERVLET_START_IND
		if (line.indexOf(SERVLET_START_IND) != -1) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String args[]) throws IOException,
			FileNotFoundException {
		//GetHttpRequestsFromFile(FILE_DIR + "home.html" ,true);
		GetHttpRequestsFromFile();
		printVector(httpRequestVector);
		return;
	}
	/**
	 * Inspect each item of the vector.
	 * 
	 * @param httpReqVector
	 *            java.util.Vector
	 */
	public static void printVector(Vector httpReqVector) throws IOException {
		Random randomIntGenerator = new Random();
		int fileNo = randomIntGenerator.nextInt() % MAX_FILE_NO;
		FileOutputStream outStr = null;
		PrintWriter filePrinter = null;
		try {
			File outFile = new File(fileName + fileNo + ".wri");
			outStr = new FileOutputStream(outFile);
			filePrinter = new PrintWriter(outStr);
			MockHttpServletRequest req;
			//no of httpRequest inside the Vector
			int noOfItems = httpReqVector.size();
			if (noOfItems == 0) {
				System.out.println("Empty Vector.");
			} else {
				for (int i = 0; i < noOfItems; i++) {
					req = new MockHttpServletRequest();
					req = (MockHttpServletRequest) httpReqVector.elementAt(i);
					Enumeration e = req.getParameterNames();
					String name = "";
					filePrinter.println();
					filePrinter.println("***** SERVLET ***** " + (i + 1)
							+ " Starts *****.");
					int count = 0; //no of params.
					while (e.hasMoreElements()) {
						name = (String) e.nextElement();
						filePrinter.println("PARAM NAME =" + name + " "
								+ "VALUE = " + req.getParameter(name));
						count++;
					}
					filePrinter.println("There are " + count
							+ " params in servlet " + (i + 1));
					filePrinter.println("***** SERVLET ***** " + (i + 1)
							+ " End ******");
				}
			}
		} finally {
			if (!(outStr == null))
				try {
					outStr.close();
				} catch (IOException e) {
				}
			if (!(filePrinter == null))
				try {
					filePrinter.close();
				} catch (Exception e) {
				}
		}
	}
}