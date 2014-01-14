import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.clafer.ast.*;

import static org.clafer.ast.Asts.*;

import org.clafer.collection.Pair;
import org.clafer.collection.Triple;
import org.clafer.compiler.*;
import org.clafer.scope.*;
import org.clafer.instance.InstanceClafer;
import org.clafer.instance.InstanceModel;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main
{

	public static void main(String[] args) throws Exception {
		
		if (args.length < 1)
		{
			throw new Exception("Not Enough Arguments. Must be at least one");		
		}
		
		if (args[0].equals("--version"))
		{
			Properties configFile = new Properties();
			try {
				configFile.load(Main.class.getClassLoader().getResourceAsStream("version.properties"));
				String name = configFile.getProperty("name");
				String releaseDate = configFile.getProperty("releasedate");
				String version = configFile.getProperty("version");
				System.out.println(name + " v" + version + "." + releaseDate);
			} catch (IOException e) {
	 
				e.printStackTrace();
			}
						
			return;
		}
		
		if (args.length < 2)
		{
			throw new Exception("Not Enough Arguments. Need File Name and Compiler Path");
		}
		
		String fileName = args[0];
		String compilerPath = args[1];
		compilerPath = compilerPath.replaceAll("//", "/"); // remove double slashes may be caused by the last concatenation
				
		String compilerArgChoco = "--mode=choco";
		String compilerArgXML = "--mode=XML";
		
		File inputFile = new File(fileName);
		
		if (!inputFile.exists())
		{
			throw new Exception("File Does Not Exist");
		}
		
		// now we are sure that the file exists.
		// now executing the Clafer Compiler (but, the Choco branch!)
		

		System.out.println("Compiling with Clafer Compiler's Choco Branch to produce JS...");
		
		ExecuteProcess(new String[]{compilerPath, fileName, compilerArgChoco});
		String jsFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".js";		
		File jsFile = new File(jsFileName);
		if (!jsFile.exists())
		{
			throw new Exception("The JS file does not exist");
		}
		
		//----------------------------------------
		// Running the model itself(instantiating) 
		//----------------------------------------
		System.out.println("Running Model...");
		
		Triple<AstModel, Scope, Objective[]> modelPair = Javascript.readModel(jsFile);

		AstModel model = modelPair.getFst();
        
        Objective[] goals = modelPair.getThd();
        if (goals.length == 0) {
            throw new Exception("No goals.");
        } else if (goals.length > 1) {
            throw new Exception("Multiple goals not currently supported.");
        }
        Objective goal = goals[0];
        
        	System.out.println(goal.isMaximize() ? "Maximize" : "Minimize");
        ClaferOptimizer solver = ClaferCompiler.compile(model, 
            		Scope.defaultScope(20), 
            	    goal);        	
        
        System.out.println("=====");
        // The optimal instance
        while (solver.find()) 
    	{
        	Pair<Integer, InstanceModel> solution = solver.instance();
                // Not used:
                //   int optimalValue = solution.getFst();
                InstanceModel instance = solution.getSnd();
            for (InstanceClafer c : instance.getTopClafers())
            {
            	Utils.printClafer(c, System.out);
            }
    	}
	}

	private static void ExecuteProcess(String[] strings) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(strings);
		pb.redirectErrorStream(true);
		Process compilerChoco = pb.start();

		InputStream is = compilerChoco.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		int exit = -1;
		
		while ((line = br.readLine()) != null) 
		{
		    // Outputs your process execution
		    System.out.println(line);
		    try {
		        exit = compilerChoco.exitValue();
		        if (exit == 0)  
		        {
		        	// Process finished
		        	// So, the output file is a JS file of the compiled model 
		        }
		        else
		        	throw new Exception("The return code of the compiler is non-zero");
		        
		    } catch (IllegalThreadStateException t) {
		        // The process has not yet finished. 
		        // Should we stop it?
//		        proc.destroy();
		    }
		}		
		
	}

}
