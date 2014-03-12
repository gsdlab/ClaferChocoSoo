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
import org.clafer.instance.InstanceModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Utils {
		
	private static String claferFilter (String s)
	{
		return s.replaceAll("c0_*_", "");
	}
	
	public static InstanceClafer getInstanceValueByName(InstanceClafer[] topClafers, String name) {
		// TODO Auto-generated method stub

		for (int i = 0; i < topClafers.length; i++)
		{
			if (topClafers[i].getType().getName().equals(name))
			{
				return topClafers[i];
			}			
		}

		for (int i = 0; i < topClafers.length; i++)
		{
			if (topClafers[i].hasChildren())
			{
				InstanceClafer result = getInstanceValueByName(topClafers[i].getChildren(), name);
				if (result != null)
					return result;
			}
		}
		
		return null;
	}	

	public static AstConcreteClafer getConcreteClaferChildByName(AstClafer root, String name) {
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
				AstConcreteClafer result = getConcreteClaferChildByName(clafer, name);
				if (result != null)
					return result;
			}
		}
		
		return null;
	}	    
	
	public static AstClafer getModelChildByName(AstModel model, String name) {
		// TODO Auto-generated method stub
		
		List<AstAbstractClafer> abstractChildren = model.getAbstracts();
		
		for (AstAbstractClafer clafer : abstractChildren)
		{
			if (clafer.getName().equals(name))
			{
				return clafer;
			}
		}

		for (AstAbstractClafer clafer : abstractChildren)
		{
			AstClafer foundchild = getConcreteClaferChildByName(clafer, name);
			if (foundchild != null)
			{
				return foundchild;
			}
		}		

		
		List<AstConcreteClafer> concreteChildren = model.getChildren();
		
		for (AstConcreteClafer clafer : concreteChildren)
		{
			if (clafer.getName().equals(name))
			{
				return clafer;
			}
		}

		for (AstConcreteClafer clafer : concreteChildren)
		{
			AstClafer foundchild = getConcreteClaferChildByName(clafer, name);
			if (foundchild != null)
			{
				return foundchild;
			}
		}		
		
		return null;
	}	
	
    public static void printClafer(InstanceClafer clafer, Appendable out) throws IOException {
    	printClafer(clafer, "", out);
    }

    private static void printClafer(InstanceClafer clafer, String indent, Appendable out) throws IOException {
        out.append(indent).append(clafer.getType().toString()).append("$").append(Integer.toString(clafer.getId()));
        
        if (clafer.getType().getSuperClafer() != null)
        {
        	String name = clafer.getType().getSuperClafer().getName();
        	if (name.equals("#clafer#"))
        		name = "clafer";
        	
        	out.append(" : ").append(name);
        }
        
        if(clafer.hasRef()) {
            out.append("  =  ").append(clafer.getRef().getType().isPrimitive()
                    ? clafer.getRef().getValue().toString()
                    : clafer.getRef().getType().getName() + "$" + clafer.getRef().getValue());
        }

        out.append(" \n");
        for (InstanceClafer child : clafer.getChildren()) {
        	printClafer(child, indent + "\t", out);
        }
    }	
	
	
}
