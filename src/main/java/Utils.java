import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstModel;
import org.clafer.collection.Pair;
import org.clafer.instance.InstanceClafer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Utils {
	
	
	
	public static ArrayList<Pair<String, String>> getGoals(String xmlFileName) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlFileName);
		
		System.out.println("XML Root element: " + doc.getDocumentElement().getNodeName());
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
//		doc.getElementsByTagName("")
		
		XPathExpression expr = xpath.compile("/Module/Declaration[@type='cl:IGoal']/ParentExp/Exp");
		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		ArrayList<Pair<String, String>> result = new ArrayList<Pair<String,String>>();

		for (int i = 0; i < nl.getLength(); i++)
		{
			Node node = nl.item(i);
			
			String builtExp = buildExp(node);
			
			String parts[] = builtExp.split(" ");
			String operation = parts[0];
			String rest = builtExp.replaceAll("[^ ]* ", "").replace(".ref", "");
			rest = rest.replaceAll("[^.]*\\.", "");
			
			Pair<String, String> current = new Pair<String, String>(operation, rest);
			result.add(current);
		}
		
		
		return result;
		
	}
	
	private static String claferFilter (String s)
	{
		return s.replaceAll("c[^_]*_", "");
	}
	
	private static String operationFilter(String s)
	{
		String result = "";
		
		if (s.equals("max"))
			result = "max" + " ";
			
		else if (s.equals("min"))
			result = "min" + " ";
		else
			result = s;
			
		return result;
	}

	private static String buildExp(Node exp)
	{
		String operation = "";
		
		ArrayList<String> renderedArgs = new ArrayList<String>();
		
		for (int i = 0; i < exp.getChildNodes().getLength(); i++)
		{
			Node current = exp.getChildNodes().item(i);
			if (current.getNodeName().equals("Argument"))
			{			
				
				for (int j = 0; j < current.getChildNodes().getLength(); j++) 
				{
					Node expNode = current.getChildNodes().item(j);
					if (expNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element expElement = (Element) expNode;
						if (expElement.getNodeName().equals("Exp")) 
						{
							renderedArgs.add(buildExp(expElement));
							break;
						}
					}
				}
			}
			else if (current.getNodeName().equals("Operation"))
			{
				operation = operationFilter(current.getFirstChild().getNodeValue());
			}
			else if (current.getNodeName().equals("Id"))
			{
				return current.getFirstChild().getNodeValue();
			}
		}
		
		if (renderedArgs.size() == 1)
			return operation + renderedArgs.get(0);
		else
			return join(renderedArgs, operation);
		
	}
	
	
	private static String join(Collection<?> col, String delim) {
	    StringBuilder sb = new StringBuilder();
	    Iterator<?> iter = col.iterator();
	    if (iter.hasNext())
	        sb.append(iter.next().toString());
	    while (iter.hasNext()) {
	        sb.append(delim);
	        sb.append(iter.next().toString());
	    }
	    return sb.toString();
	}

	public static AstClafer getModelChildByName(AstClafer root, String name) {
		// TODO Auto-generated method stub

		List<AstConcreteClafer> children = root.getChildren();
		
		for (AstConcreteClafer clafer : children)
		{
			if (clafer.getName().equals(name))
			{
				return clafer;
			}
		}

		for (AstConcreteClafer clafer : children)
		{
			if (clafer.hasChildren())
			{
				AstClafer result = getModelChildByName(clafer, name);
				if (result != null)
					return result;
			}
		}
		
		return null;
	}	
	
	
    public static void printClafer(InstanceClafer clafer, Appendable out) throws IOException {
    	printClafer(clafer, "", out);
    }

    private static void printClafer(InstanceClafer clafer, String indent, Appendable out) throws IOException {
        out.append(indent).append(clafer.getType().toString());
        
        if (clafer.getType().getSuperClafer() != null)
        {
        	out.append(" : ").append(clafer.getType().getSuperClafer().getName());
        }
        
        if(clafer.hasRef()) {
            out.append("  =  ").append(clafer.getRef().toString());
        }

        out.append(" \n");
        for (InstanceClafer child : clafer.getChildren()) {
        	printClafer(child, indent + "\t", out);
        }
    }	
	
	
}
