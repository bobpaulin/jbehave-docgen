package com.bobpaulin.jbehave.docgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;


public class GenerateSentenceDocs {
	public static void main(String[] args) throws Exception {
        File f = new File("C:\\Users\\bpaulin\\git\\retireup-autotesting\\retireup-jbehave\\src\\main\\java\\com\\retireup\\jbehave\\steps\\RetireUpSteps.java");
        inspectJavaFile(f);
    }

    public static void inspectJavaFile(File pFile) 
    throws FileNotFoundException, ParseException, IOException {
        CompilationUnit cu;
        FileInputStream in = new FileInputStream(pFile);
        try {
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        Map<String, List<SentenceData>> jbehavesentenceMap = new HashMap<String, List<SentenceData>>();
        
        new MethodVisitor().visit(cu, jbehavesentenceMap);
        
        System.out.println(jbehavesentenceMap);
        
        File output = new File("output.txt");
        FileWriter fileWriter = new FileWriter(output);
        for(String curkey:jbehavesentenceMap.keySet())
        {
        	fileWriter.write(curkey);
        	for(SentenceData cursentence:jbehavesentenceMap.get(curkey))
        	{
        		fileWriter.write("\n\n");
        		if(cursentence.getCommentText() != null)
        		{
        			fileWriter.write(cursentence.getCommentText() +"\n");
        		}
        		fileWriter.write(curkey + " " + cursentence.getsentenceText() + "\n");
        		
        	}
        	fileWriter.write("\n\n");
        }
        
        fileWriter.flush();
        fileWriter.close();
        
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            Map<String, List<SentenceData>> jbehavesentenceMap = (Map<String, List<SentenceData>>) arg;
            
            String comment = null;
            if(n.getJavaDoc() != null)	
            {
            	comment = n.getJavaDoc().getContent();
            }
            
            if (n.getAnnotations() != null) {
            	String sentenceType = null;
                for (AnnotationExpr annotation : n.getAnnotations()) {
                    
                	if(sentenceType== null)
                		sentenceType = annotation.getName().toString();
                	
                	List<SentenceData> sentenceList = jbehavesentenceMap.get(sentenceType);
                	if(sentenceList == null)
                	{
                		sentenceList = new ArrayList<SentenceData>();
                		jbehavesentenceMap.put(sentenceType, sentenceList);
                	}
                	 SingleMemberAnnotationExpr sma = (SingleMemberAnnotationExpr) annotation;
                	 String restOfsentence = sma.getMemberValue().toString();
                	 restOfsentence = restOfsentence.substring(1, restOfsentence.length() -1);
                	 restOfsentence  = restOfsentence.replaceAll("\\\\\"", "\"");
                	
                	sentenceList.add(new SentenceData(restOfsentence, comment));
                   
                }
            }
        }
    }
}
