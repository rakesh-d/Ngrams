import java.io.*;
import java.util.*;

import weka.core.stemmers.SnowballStemmer;
import weka.core.Stopwords;

public class WsiImplement {
	
	
	
	
	public static void main (String[] args) throws IOException 
	{
		
/*
		Test syntax
		
		cooccurrenceList bList = new cooccurrenceList();
		bList.indexWord = "sachin";
		bList.coList = new HashMap<String, Integer>();
		
		bList.coList.put("sourav",3);
		bList.coList.put("rahul",20);
		bList.coList.put("laxman",10);
		
		ngDbEnv.setup(ngEnvPath, false);
		
		da = new ngDataAccessor(ngDbEnv.getEntityStore());
		
		da.coListByWord.put(bList);
		
		da.coListByWord.put(cList);
		
		boolean t = da.coListByWord.contains("sachin");
		System.out.println(t);
		t = da.coListByWord.contains("sourav");
		System.out.println(t);
		
		t = da.coListByWord.contains("rohit");
		
		System.out.println(t);		
		String nGram = null;
		String[] fiveGrams;
		int i = 0;

		Map <String, Map<String, Integer>> wcoMap = new HashMap<String,Map<String, Integer>>();
		Map <String, String> stemMap = new HashMap<String, String>();
		
		// Using the weka stemmer and stopword list respectively
		
		SnowballStemmer wordStemmer = new SnowballStemmer();
		int pre = 0;		
		while ((nGram = br.readLine()) != null)
		{
			i++;
			progress = (i*100)/nLines;
			if ((i+1) % 100000 == 0)
				System.out.println(Integer.toString(i+1) + " lines processed");
			
			if (progress != pre)
			{
				pre = progress;
				System.out.println(progress + " percent processed");
			}
			fiveGrams = nGram.split(" ");
			int count = Integer.parseInt(fiveGrams[5]);
			for (int j = 0; j < 5; j++)
			{
				String s = fiveGrams[j].toLowerCase(), w;
				w = makeValidString(s);
				if (w == null || Stopwords.isStopword(w)) continue;
				s = wordStemmer.stem(w);
				stemMap.put(s,w);
				Map <String, Integer> cList;
				if (!wcoMap.containsKey(s))
				{
					cList = new HashMap<String, Integer>();
					wcoMap.put(s,cList);	
				}
				else
					cList = wcoMap.get(s);
				for (int k = 0; k < 5; k++)
				{
					if (k == j) continue;
					String t = fiveGrams[k].toLowerCase();
					t = makeValidString(t);
					if (t == null || Stopwords.isStopword(t)) continue;
					t = wordStemmer.stem(t);
					if (cList.containsKey(t))
					{
						cList.put(t,cList.get(t) + count);
					}
					else
						cList.put(t,count);
						
				}
				
			}
			
		}

		
		Console c = System.console();

		String query, queryStem;
		while (true)
		{
			query = c.readLine("Enter the query String: ");
			queryStem = wordStemmer.stem(query);
			if (query.equalsIgnoreCase("end"))
				break;
			Map<String, Integer> t = wcoMap.get(queryStem);
		
			System.out.println(query+": {");

			for (Map.Entry<String, Integer> entry: t.entrySet())
			{
				System.out.print("("+stemMap.get(entry.getKey()) + ":" + entry.getValue() + ") ");
			}
			System.out.println(" }");

		
		}
		
		// Dumping the data into db
		
		query = c.readLine("Write to DB ? ");
		if (query.equalsIgnoreCase("yes"))
		{
			for (Map.Entry<String, Map<String, Integer>> entry: wcoMap.entrySet())
			{
				cooccurrenceList ocrList = new cooccurrenceList();
				ocrList.indexWord = entry.getKey();
				ocrList.coList = entry.getValue();
				da.coListByWord.put(ocrList);
			}
		}
		
		ngDbEnv.setup(ngEnvPath, true);
		da = new ngDataAccessor(ngDbEnv.getEntityStore());
		SnowballStemmer wordStemmer = new SnowballStemmer();
		// testing the DB
		Console c = System.console();

		String query, queryStem;
		

		while (true)
		{
			query = c.readLine("Enter the query String: ");
			queryStem = wordStemmer.stem(query);
			if (query.equalsIgnoreCase("end"))
				break;
			da.coListByWord.get(queryStem).printcoList();

		
		}

		*/
		File ngEnvPath = new File("/home/rakesh/Downloads/finalDB");
		ngDataAccessor da;
		nGramEnv ngDbEnv = new nGramEnv();
		String queryFile = "/home/rakesh/Downloads/ambient/topics.txt";
		String resultFile = "/home/rakesh/Downloads/ambient/results.txt";
		DataSet ambDS = new DataSet(queryFile,resultFile,44);
	//	ambDS.printDataSet();

		ngDbEnv.setup(ngEnvPath, true);
		da = new ngDataAccessor(ngDbEnv.getEntityStore());
	//	List<WordGraph> testGraphs = new ArrayList<WordGraph>();
		
	//	for (int i = 0; i < 44; i++)
	//		 testGraphs.add(new WordGraph(ambDS.ds[i].queryString, da, ambDS.ds[i].resultSnippet));
	
		WordGraph sampleGraph = new WordGraph(ambDS.ds[2].queryString, da, ambDS.ds[2].resultSnippet);
		System.out.println("Max Sigma : " + sampleGraph.maxSigma);
		System.out.println("Min Sigma : " + sampleGraph.minSigma);
		System.out.println("Median Sigma : " + sampleGraph.medianSigma);
		System.out.println("Mean Sigma : " + sampleGraph.meanSigma);
		
		
		System.out.println("====== Clusters corresponding to various senses =======");
		for (Set<String> s: sampleGraph.topicClusters)
			System.out.println(s);
		
		HashMap<Set<String>, ArrayList<String>> resultClusters = new HashMap<Set<String>, ArrayList<String>>();
		
		for (int i = 0; i < ambDS.ds[2].resultSnippet.length; i++)
		{
			String curSnippet = ambDS.ds[2].resultSnippet[i];
			
			Set<String> curBag = ambDS.ds[2].resultBag.get(curSnippet);
			Set<String> maxCluster = new HashSet<String>();
			for (Set<String> s: sampleGraph.topicClusters)
			{
				Set<String> temp = new HashSet<String>(s);
				temp.retainAll(curBag);
				if (temp.size() > maxCluster.size())
				{	
					maxCluster = s;
				}
			}
			if (resultClusters.get(maxCluster) == null)
			{
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(curSnippet);
				resultClusters.put(maxCluster, temp);
			}
			else
				resultClusters.get(maxCluster).add(curSnippet);
		}
		
		for (Map.Entry<Set<String>, ArrayList<String>> entry : resultClusters.entrySet())
		{
			System.out.println("========= Cluster topic ===========");
			System.out.println(entry.getKey());
			System.out.println("===== Results under this cluster: ======");
			for (String s: entry.getValue())
				System.out.println(s);
			
		}
		
		
		
		
		
		
		/*for (WordGraph wg: testGraphs)
		{
			System.out.println("======== Query String: " + wg.query + " ==========");
			System.out.println();
			for (Set<String> cluster: wg.topicClusters)
				System.out.println(cluster);
			System.out.println("-----------------------------------------");
			System.out.println();
		}*/
		// Clustering the results
		
	/*	for (int i = 0; i < 44; i++)
		{
			List<Set<String>> senseMap= new ArrayList<Set<String>>();
			System.out.println("========= Query String: " + ambDS.queryString[i] + " =========");
			if (testGraphs.get(i).wGraph == null) continue;
			List<Set<String>> bagList = ambDS.resultBags.get(i);
			System.out.println(bagList.size());
			for (int j = 0; j < bagList.size();j++)
			{
				// Finding max intersection among the sense clusters
				Set<String> bag = bagList.get(j);
				int max = 0;
				Set<String> maxSet = new HashSet<String>();
				for (Set<String> senSet: testGraphs.get(i).topicClusters)
				{
					Set<String> inters = new HashSet<String>(bag);
					inters.retainAll(senSet);
					if (max < inters.size())
					{
						max = inters.size();
						maxSet = senSet;
					}
					
					
				}
				if (maxSet == null)
					maxSet.add("empty");
				senseMap.add(maxSet);
			//	if (inters == null) continue;
			//	if (maxSet.size() > 3) System.out.println(inters);
			}
			System.out.println(senseMap.size());
			// Printing out the clusters
			List<Set<String>> clusters = testGraphs.get(i).topicClusters;
			List<ArrayList<String>> clusterLists = new ArrayList<ArrayList<String>>();
			for (int j = 0; j < clusters.size(); j++)
			{
				ArrayList<String> resultCluster = new ArrayList<String>();
				Set<String> topic = clusters.get(j);
				for (int k = 0; k < senseMap.size(); k++)
				{
					if (senseMap.get(k) == null) continue;
					if (senseMap.get(k).equals(topic))
					{
						resultCluster.add(ambDS.resultSnippet[i][k]);
					}
				}
				clusterLists.add(resultCluster);
			}
			
			
			
			
			for (int j = 0; j < clusters.size();j++)
			{
				if (clusterLists.get(j).size() == 0) continue;
				System.out.println("********* Cluster no. " + (j+1) + " *******"  );
				System.out.println(clusters.get(j));
				System.out.println("Results under this cluster: ");
				for (String result: clusterLists.get(j))
					System.out.println(result);
			}
				
		}
	*/	

//		System.out.println(testGraph.wGraph);
	
	
	}
}
