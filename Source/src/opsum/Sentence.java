package opsum;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.IDF;
import util.SentiWordNet;
import util.Tokenize;

/**
* 
* Implementation for sentence object 
* 
* @author  Jayaprakash Sundararaj
* @version 1.0
* @since   2014-03-31 
*/

public class Sentence {
	private int sentID;
	private String sent;
	public  String[] wordList;
	public  Map<String, Double> wordMap;
	//public boolean isSummSentence = false;
	public Double budgetScore = null; // Coverage
	public Double dAspRatio[] = null; // Soft clustering prob
	public Aspect aspect = null;
	public Double dSubjScore = null; // Senti Score
	public Double dPolScore = null; // Senti Score
	public Double dPosScore = null; // Senti Score
	public Double dNegScore = null; // Senti Score
	
	
	public Sentence(){
		sentID = -1;
		sent = null;
		wordList = null;
		wordMap = null;
		//isSummSentence = false;
	}

	public Sentence(String str){
		sentID = -1;
		sent = null;
		wordList = null;
		wordMap = null;
		setSentence(str);
		dSubjScore = SentiWordNet.SubjSentiScore(str);
		dPolScore = SentiWordNet.PolSentiScore(str);
		dPosScore = SentiWordNet.PosSentiScore(str);
		dNegScore = SentiWordNet.NegSentiScore(str);
	}
	
	public void setSentID(int id){ sentID = id; }
	public int getSentID(){ return sentID; }
	
	public String getSentString(){
		return sent;
	}

	public void setSentence(String str){
		this.sent = str;
		this.wordList = Tokenize.run(this.sent);
		this.wordMap = new HashMap<String, Double>();
		for(String s: this.wordList){
			if (wordMap.containsKey(s) == false){
				wordMap.put(s, 0.0);
			}
			Double frq = wordMap.get(s);
			wordMap.put(s, frq+1.0);
		}
	}
		
	
	public Double score(Sentence sent){
		//return scoreTFIDF(sent);
		return scoreCosine(sent);
	}
	
	/**
	* Cosine similariy between current sentence and sent.
	* @param sent  
	* @return cosine similarity between <b> this </b> and sent. 
	*/

	public Double scoreCosine(Sentence sent){
		Double res  = 0.0;
		Double res1 = 0.0, res2 = 0.0;
		Set<String> ks = new TreeSet<String>();
		ks.addAll(this.wordMap.keySet());
		ks.addAll(sent.wordMap.keySet());
		for(String word : ks){
			try{
				res += this.getTF(word)  * this.getIDF(word) * sent.getTF(word) * sent.getIDF(word) ;
				res1 += Math.pow( this.getTF(word) *  this.getIDF(word), 2.0) ;
				res2 += Math.pow( sent.getTF(word) *  sent.getIDF(word), 2.0) ;
			}catch(Exception e){
				System.err.println( word );
				System.err.println( this.getTF(word) );
				System.err.println( sent.getTF(word) );
				System.err.println( this.getIDF(word) );
				System.err.println( sent.getIDF(word) );
			}
		}
		Double numerator	= 	res;
		Double denominator	=	(Math.sqrt(res1) * Math.sqrt(res2));
		/* Debug
		System.err.println( "+" + this );
		System.err.println( "+" + sent );
		System.err.println( "+" + res + "\t" + denominator +"\t" + res1 +"\t" + res2 );
		*/
		Double result = 0.0;
		if(denominator > 0.0)
			result = numerator / denominator;
		return result;
	}

	
	/**
	* Term frequency of word
	* @param word 
	* @return Term Frequency as Double 
	*/
	
	public Double getTF(String word){
		if( wordMap.containsKey(word) )
			return wordMap.get(word);
		else
			return 0.0;
	}
 
	/**
	* Inverse Document Frequency of a word
	* @param word 
	* @return Inverse Document Frequency as Double 
	*/
	public Double getIDF(String word){
		return IDF.getIDF(word);
	}
	
	public String printDetails(){
		String res = "[ " ;
		res += getSentString();
		res += this.wordList;
		res += "] ";
		return res;
	}
	
	public String toString(){
		String res = "[ " ;
		res += ""+sentID+","+ dSubjScore  +"=";
		res += getSentString();
		res += "{";
		for(String w: this.wordList)
			res += "["+w + ","+ getTF(w)+","+ getIDF(w)+","+ IDF.getIDFCnt(w)+ "]_";
		res += "}";

		res += "] ";
		return res;
	}

	public Double getBudgetScore() {
		try{
			return Summarize.tradeCBudget * budgetScore;
		}catch(Exception e){
			System.err.println(Summarize.tradeCBudget);
			System.err.println(budgetScore);
		}
		return null;
	}
}
