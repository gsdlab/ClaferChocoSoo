import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
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
import org.clafer.compiler.*;
import org.clafer.scope.*;
import org.clafer.instance.InstanceClafer;
import org.clafer.instance.InstanceModel;
import org.clafer.javascript.Javascript;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main
{

	public static void main(String[] args) throws Exception {
		
//		String currentDir = System.getProperty("user.dir");
		
//		System.out.println("Current Directory: " + currentDir);
		
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
		
		// OK, we have a JS file. But we need an XML file to be able to extract goals. 
		// Goals are not compiled into a choco solver yet.
		
		System.out.println("Compiling with Clafer Compiler's Choco Branch to produce XML...");
		
		
		ExecuteProcess(new String[]{compilerPath, fileName, compilerArgXML});
		String xmlFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".xml";		
		File xmlFile = new File(xmlFileName);
		if (!xmlFile.exists())
		{
			throw new Exception("The XML file does not exist");
		}
		
		System.out.println("Extracting Goals...");
		
		// getting goals from the file
		ArrayList<Pair<String, String>> goals = Utils.getGoals(xmlFileName);
		
		System.out.println("Goals:");
		System.out.println(goals);

		Pair<String, String> goal = goals.get(0); // focusing on the first goal only
		
		//----------------------------------------
		// Running the model itself(instantiating) 
		//----------------------------------------
		System.out.println("Running Model...");
		
		Pair<AstModel, Scope> modelPair = Javascript.readModel(jsFile);

		AstModel model = modelPair.getFst();
        
        AstConcreteClafer goalClafer = null;
        
        System.out.println(model.getAbstracts());
        
        for (AstAbstractClafer ac : model.getAbstracts())
        {
        	goalClafer = Utils.getModelChildByName(ac, goal.getSnd());
        	
        	if (goalClafer != null)
        		break;
        }
        
        System.out.println(goalClafer.getName());
  
        ClaferObjective solver;
        
        if (goal.getFst().equals("max"))
        {
        	System.out.println("Maximize");
            solver = ClaferCompiler.compileMaximize(model, 
            		Scope.defaultScope(20), 
            	    goalClafer.getRef());        	
        }
        else
        {
        	System.out.println("Minimize");
            solver = ClaferCompiler.compileMinimize(model, 
            		Scope.defaultScope(20), 
            	    goalClafer.getRef());        	
        }
        
        // The optimal instance
        Pair<Integer, InstanceModel> optimalSolution = solver.optimal();
    	System.out.println(optimalSolution);
        InstanceModel instance = optimalSolution.getSnd();

        System.out.println(solver.getObjective());
        System.out.println(solver.getInternalSolver().getName());        
        
        System.out.println("INSTANCE:");        
        System.out.println(instance);

        System.out.println("CLAFER:");        
        InstanceClafer instantiatedTotalClafer = Utils.getInstanceValueByName(instance.getTopClafers(),  goalClafer.getName());
        
        goalClafer.addConstraint(equal(joinRef($this()), constant(instantiatedTotalClafer.getRef().getValue())));
        ClaferSolver solverForNormalInstances = ClaferCompiler.compile( modelPair.getFst(), Scope.defaultScope(20));        
        System.out.println("=====");

        while (solverForNormalInstances.find()) 
    	{
        	instance = solverForNormalInstances.instance();
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
