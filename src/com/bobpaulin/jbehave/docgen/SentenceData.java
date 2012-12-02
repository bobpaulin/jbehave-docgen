package com.bobpaulin.jbehave.docgen;

public class SentenceData {
	private String sentenceText;
	private String commentText;
	
	public SentenceData(String sentenceText, String commentText)
	{
		this.sentenceText = sentenceText;
		this.commentText = commentText;
	}
	
	public String getsentenceText() {
		return sentenceText;
	}
	public void setsentenceText(String sentenceText) {
		this.sentenceText = sentenceText;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	
	
	
}
