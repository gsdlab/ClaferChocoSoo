import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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

import joptsimple.OptionParser;
import joptsimple.OptionSet;

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
		
        OptionParser parser = new OptionParser() {
            {
                accepts( "file", "input file name in Javascript format" ).withRequiredArg().ofType( File.class )
                    .describedAs( "Javascript file" );
                
                accepts( "testaadl", "test the AADL to Clafer model" );
                accepts( "version", "display the tool version" );
                accepts( "maxint", "specify maximum integer value" ).withRequiredArg().ofType( Integer.class );
                accepts( "scope", "override global scope value" ).withRequiredArg().ofType( Integer.class );

                accepts( "help", "show help").forHelp();                
            }
        };

        OptionSet options = parser.parse(args);

		if (options.has("version"))
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

		
		if (!options.has( "file" ) ){
			throw new Exception("Input file must be given using --file argument");		
		}
		
//		String fileName = 
		File inputFile = (File) options.valueOf("file");
		
		if (!inputFile.exists())
		{
			throw new Exception("File does not exist: " + inputFile.getPath());
		}
		
		//----------------------------------------
		// Running the model itself(instantiating) 
		//----------------------------------------
		System.out.println("Running Model...");
		
		Triple<AstModel, Scope, Objective[]> modelTriple = Javascript.readModel(inputFile);

		AstModel model = modelTriple.getFst();
        
        Objective[] goals = modelTriple.getThd();
        if (goals.length == 0) {
            throw new Exception("No goals.");
        } else if (goals.length > 1) {
            throw new Exception("Multiple goals not currently supported.");
        }
        Objective goal = goals[0];
        
        System.out.println(goal.isMaximize() ? "Maximize" : "Minimize");       	
        
        Scope scope = modelTriple.getSnd(); 
        
        if (options.has("testaadl")) // testing scopes for AADL model
		{
			Properties configFile = new Properties();
			try {
				configFile.load(Main.class.getClassLoader().getResourceAsStream("aadl.scopes"));
				
				for (Enumeration<Object> e = configFile.keys(); e.hasMoreElements();)
				{
					String key = (e.nextElement()).toString();					
					int value = Integer.parseInt(configFile.getProperty(key));
					System.out.println(key + " = " + value);
					
					if (key.equals("defaultScope"))
					{
						scope = scope.toBuilder().defaultScope(value).toScope();
						System.out.println("Set default scope: " + value);
					}
					else if (key.equals("maxInt"))
					{
						int scopeHigh = value;
						int scopeLow = -(scopeHigh + 1);
						scope = scope.toBuilder().intLow(scopeLow).intHigh(scopeHigh).toScope();
						System.out.println("Set maxInt: " + value);
					}
					else
					{
						AstClafer clafer = Utils.getModelChildByName(model, key);
						if (clafer == null)
						{
							System.out.println("The clafer is not found: '" + key + "'");
							continue;
						}
							
						scope = scope.toBuilder().setScope(clafer, value).toScope();					
						System.out.println("Set clafer scope: '" + key + "' = " + value);
					}
				}
				
			} catch (IOException e) {
	 
				e.printStackTrace();
			}						
		}
        else
        {
        	if (options.has("scope"))
        	{
            	scope = scope.toBuilder().defaultScope((int) options.valueOf("scope")).toScope();
        	}

        	if (options.has("maxint"))
        	{
				int scopeHigh = (int)options.valueOf("maxint");
				int scopeLow = -(scopeHigh + 1);
				scope = scope.toBuilder().intLow(scopeLow).intHigh(scopeHigh).toScope();
        	}
        }
        
        ClaferOptimizer solver = ClaferCompiler.compile(model, 
        		scope, 
        	    goal);         
        
        System.out.println("Generating instances...");        
        
    	int index = 0; // optimal instance id
        while (solver.find()) 
    	{
            System.out.println("=== Instance " + (++index) + " ===\n");                    
            InstanceModel instance = solver.instance();
            for (InstanceClafer c : instance.getTopClafers())
            {
            	Utils.printClafer(c, System.out);
            }
            System.out.println("--- instance " + (index) + " ends ---\n");                    
    	}
    	
	}
}
