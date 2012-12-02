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

		String inputFile = args[0];
		String outputFile = args[1];

		File f = new File(inputFile);
		Map<String, List<SentenceData>> jbehaveSentenceMap = new HashMap<String, List<SentenceData>>();
		CompilationUnit jbhaveSentenceJavaCompilationUnit = readCompilationUnitFromFile(
				f, jbehaveSentenceMap);
		new JbehaveSentenceVisitor().visit(jbhaveSentenceJavaCompilationUnit,
				jbehaveSentenceMap);
		writeOutputToFile(outputFile, jbehaveSentenceMap);
	}

	public static CompilationUnit readCompilationUnitFromFile(File inputFile,
			Map<String, List<SentenceData>> jbehaveSentenceMap)
			throws FileNotFoundException, ParseException, IOException {
		CompilationUnit jbehaveCompilationUnit;
		FileInputStream in = new FileInputStream(inputFile);
		try {
			jbehaveCompilationUnit = JavaParser.parse(in);
		} finally {
			in.close();
		}
		return jbehaveCompilationUnit;
	}

	private static void writeOutputToFile(String outputFilePath,
			Map<String, List<SentenceData>> jbehavesentenceMap)
			throws IOException {
		File output = new File(outputFilePath);
		FileWriter fileWriter = new FileWriter(output);
		for (String curkey : jbehavesentenceMap.keySet()) {
			fileWriter.write(curkey);
			for (SentenceData cursentence : jbehavesentenceMap.get(curkey)) {
				fileWriter.write("\n\n");
				if (cursentence.getCommentText() != null) {
					fileWriter.write(cursentence.getCommentText() + "\n");
				}
				fileWriter.write(curkey + " " + cursentence.getsentenceText()
						+ "\n");

			}
			fileWriter.write("\n\n");
		}

		fileWriter.flush();
		fileWriter.close();
	}

	/**
	 * Simple visitor for reading JBehave Annotations.
	 */
	private static class JbehaveSentenceVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration candidateJbehaveSentenceMethod, Object arg) {
			Map<String, List<SentenceData>> jbehavesentenceMap = (Map<String, List<SentenceData>>) arg;

			String comment = null;
			if (candidateJbehaveSentenceMethod.getJavaDoc() != null) {
				comment = candidateJbehaveSentenceMethod.getJavaDoc().getContent();
			}

			if (candidateJbehaveSentenceMethod.getAnnotations() != null) {
				String sentenceType = null;
				for (AnnotationExpr annotation : candidateJbehaveSentenceMethod.getAnnotations()) {

					if (sentenceType == null)
						sentenceType = annotation.getName().toString();

					List<SentenceData> sentenceList = jbehavesentenceMap
							.get(sentenceType);
					if (sentenceList == null) {
						sentenceList = new ArrayList<SentenceData>();
						jbehavesentenceMap.put(sentenceType, sentenceList);
					}
					SingleMemberAnnotationExpr sma = (SingleMemberAnnotationExpr) annotation;
					String restOfsentence = sma.getMemberValue().toString();
					restOfsentence = restOfsentence.substring(1,
							restOfsentence.length() - 1);
					restOfsentence = restOfsentence.replaceAll("\\\\\"", "\"");

					sentenceList.add(new SentenceData(restOfsentence, comment));

				}
			}
		}
	}
}
