import java.io.*;
import java.util.*;

import weka.core.stemmers.SnowballStemmer;
import weka.core.Stopwords;
public class DataSet {
	
	public class QueryData {
	public String queryString;
	public String[] resultURL;
	public String[] resultTitle;
	public String[] resultSnippet;
	public Map<String, Set<String>> resultBag;
	}
	public QueryData[] ds;
	public String makeValidString (String s)
	{
		String t;
		char[] cArray = s.toCharArray();
		int begin = 0, end = 0, i,j;
		for (i = 0; i < s.length();i++)
		{
			if (!Character.isLetter(s.charAt(i)))
				continue;
			else
				break;
		}
		if (i == s.length()) return null;
	
		begin = i;
		
		for (j = i; j < s.length();j++)
			if (Character.isLetter(s.charAt(j)))
			{
				end++;
				continue;
			}
			else
				break;
		t = new String(cArray,begin,end);
	//	System.out.println(t);
		return t;
	}
	public DataSet (String queryFileName, String resultFileName, int nQueries) throws IOException 
	{
/*		queryString = new String[nQueries];
		resultURL = new String[nQueries][100];
		resultTitle = new String[nQueries][100];
		resultSnippet = new String[nQueries][100];
		resultBags = new ArrayList<ArrayList<Set<String>>>();
		
		BufferedReader brQ = new BufferedReader(new FileReader(queryFileName));
		BufferedReader brR = new BufferedReader(new FileReader(resultFileName));
		SnowballStemmer wordStemmer = new SnowballStemmer();
		
		brQ.readLine();
		brR.readLine();
		String[] t;
		
		for (int i = 0; i < nQueries; i++) 
		{
			String s = brQ.readLine();
			queryString[i] = s.split("\t")[1];
			ArrayList<Set<String>> bagList = new ArrayList<Set<String>>();
			for (int j = 0; j < 100; j++)
			{
			Set<String> bag = new HashSet<String>();
				t = brR.readLine().split("\t");
				if (t.length < 4) 
				{
					bag.add("empty");
					bagList.add(bag);
					continue;

				}
				resultURL[i][j] = t[1];
				resultTitle[i][j] = t[2];
				resultSnippet[i][j] = t[3];
				for (String t1: resultSnippet[i][j].split(" "))
					if (!Stopwords.isStopword(t1))
						{
							t1 = makeValidString(t1.toLowerCase());
							if (t1 != null)
								bag.add(wordStemmer.stem(t1));
						}
				bagList.add(bag);
				
			}
			resultBags.add(bagList);
						
		} */
		ds = new QueryData[nQueries];
		for (int i = 0; i < ds.length; i++)
		{
			ds[i] = new QueryData();
		}

		BufferedReader brQ = new BufferedReader(new FileReader(queryFileName));
		BufferedReader brR = new BufferedReader(new FileReader(resultFileName));
		SnowballStemmer wordStemmer = new SnowballStemmer();
		
		brQ.readLine();
		brR.readLine();
		String[] t;
		for (int i = 0; i < nQueries; i++) 
		{
			String s = brQ.readLine();
			ds[i].queryString = s.split("\t")[1];
			ds[i].resultBag = new HashMap<String, Set<String>>();
			ds[i].resultSnippet = new String[100];
			ds[i].resultURL = new String[100];
			ds[i].resultTitle = new String[100];
			for (int j = 0; j < 100; j++)
			{
			Set<String> bag = new HashSet<String>();
				t = brR.readLine().split("\t");
				if (t.length < 4) 
				{
					ds[i].resultSnippet[j] = "";
					bag.add("empty");
					ds[i].resultBag.put(ds[i].resultSnippet[j], bag);
					continue;
				}
				ds[i].resultURL[j] = t[1];
				ds[i].resultTitle[j] = t[2];
				ds[i].resultSnippet[j] = t[3];
				for (String t1: ds[i].resultSnippet[j].split(" "))
					if (!Stopwords.isStopword(t1))
						{
							t1 = makeValidString(t1.toLowerCase());
							if (t1 != null)
								bag.add(wordStemmer.stem(t1));
						}
				ds[i].resultBag.put(ds[i].resultSnippet[j], bag);
				
			}
		}
		
		
		
		
		
		
	}
	public void printDataSet()
	{
		/*for (String[] resultSet: resultTitle)
		{
			for (String title: resultSet)
				System.out.println(title);
			System.out.println("---------------------------");
					
		}
		*/
		System.out.println(ds[2].resultBag);
	}
}
