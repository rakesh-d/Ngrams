/**
 * 
 */

/**
 * @author rakesh
 *
 */
import java.io.File;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import weka.core.stemmers.SnowballStemmer;
import weka.core.Stopwords;

public class WordGraph {

	/**
	 * @param args
	 */
	SimpleWeightedGraph <String, DefaultWeightedEdge> wGraph; 
	List<Set<String>> topicClusters;
	String query;
	double meanSigma;
	double maxSigma;
	double minSigma;
	double medianSigma;
	ArrayList<Double> arraySigma;
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
	public void addByDice (String indWord, Map<String, Double> tempMap)
	{
		wGraph.addVertex(indWord);
		DefaultWeightedEdge t;
		for (String other: tempMap.keySet())
		{
			if (indWord.equals(other) || wGraph.containsEdge(indWord, other))
				continue;
			double dice;
			dice = tempMap.get(other);
			
			if (dice > 0.0033)
			{
				wGraph.addVertex(other);
				//System.out.println(indWord + " " + other + " " + dice);
				t = wGraph.addEdge(indWord, other);
				wGraph.setEdgeWeight(t, dice);
				
			}
		}
		//System.out.println("here");
		
		
	}
			
	public WordGraph (String query,  ngDataAccessor ng, String[] snippets) {
		
	
	wGraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);	
	this.query = query;
	topicClusters = new ArrayList<Set<String>>();
	
	// Adding the snippet words to the V0
	SnowballStemmer wordStemmer = new SnowballStemmer();
	
	for (String s: snippets)
	{
		if (s == null)
			continue;
		for (String t: s.split(" "))
			if (!Stopwords.isStopword(t))
				{
					t = makeValidString(t.toLowerCase());
					if (t != null)
						wGraph.addVertex(wordStemmer.stem(t));
				}
	}
	
	// Adding the words co-occurring with the whole of the queryString
	
	String[] queryWords = query.split(" ");
	Map<String, Double> tempMap;
	ArrayList<String> queryList = new ArrayList<String>();
	for (String s: queryWords)
	{
		if (Stopwords.isStopword(s))
			continue;
		else
			queryList.add(wordStemmer.stem(s));
	}
	for (String s: queryList)
	{
		wGraph.addVertex(s);
	}
	
	String keyWord = wordStemmer.stem(queryList.get(0)).toLowerCase();
	tempMap = ng.coListByWord.get(keyWord).coList;
	if (tempMap == null) return;
	//cList.printcoList();
	boolean state = true;
	for (int i = 1; i < queryList.size();i++)
	{
		if (!tempMap.containsKey(wordStemmer.stem(queryList.get(i))))
		{
			state = false;
			break;
		}
		
	}

	if (state == true)
	{
		
		for (String s: tempMap.keySet())
		{
			double dice = tempMap.get(s);
			if (dice > 0.0033)
				wGraph.addVertex(s);
		}
		
	}
	
	//System.out.println("initial Graph: " + wGraph);
	//System.out.println("initial vertex Set: " +wGraph.vertexSet());
	ArrayList<String> v0 = new ArrayList<String>(wGraph.vertexSet());
	for (String s: v0)
	{
		if (ng.coListByWord.get(s) != null)
			tempMap = ng.coListByWord.get(s).coList;
		if (tempMap != null)
			addByDice(s,tempMap);
		
	}
//	System.out.println("here2");
	// Removing isolated vertices
	ConnectivityInspector<String, DefaultWeightedEdge> wcInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(wGraph);
	List<Set<String>> conSets = wcInsp.connectedSets();
	for (Set<String> s: conSets)
	{
		if (s.size() == 1)
		{
			for (String t: s)
				wGraph.removeVertex(t);
		}
	}
	
	// Computing Sqr(e) for each edge E
	
//	Map <DefaultWeightedEdge, Double> sqrValue = new HashMap<DefaultWeightedEdge, Double>();
	NeighborIndex<String, DefaultWeightedEdge> ni = new NeighborIndex<String, DefaultWeightedEdge>(wGraph);
	Set<DefaultWeightedEdge> tempSet = new HashSet<DefaultWeightedEdge>(wGraph.edgeSet());
	System.out.println("no. of edges before clustering: " + tempSet.size());
	System.out.println("no. of conn comps before: " + wcInsp.connectedSets().size());
	

	arraySigma = new ArrayList<Double>();
	for (DefaultWeightedEdge e: tempSet)
	{
//		iter++;
//		if (iter % 100 == 0)
//			System.out.println(iter);
		String s = wGraph.getEdgeSource(e);
		String t = wGraph.getEdgeTarget(e);
		Set<String> sNeigh = new HashSet<String>(ni.neighborsOf(s));
		Set<String> tNeigh = new HashSet<String>(ni.neighborsOf(t));
		sNeigh.remove(t);
		tNeigh.remove(s);
		Set<String> inters = new HashSet<String>(sNeigh);
		inters.retainAll(tNeigh);
		int potential, m, n1, n2;
		m = inters.size();
		n1 = sNeigh.size() - m;
		n2 = tNeigh.size() - m;
		potential = m*n2 + n1*(m + n2);
		if (sNeigh == null || tNeigh == null || potential == 0) 
		{
			wGraph.removeEdge(e);
			continue;
		}
		int sqEdges = 0;
		for (String p: sNeigh)
		{
			for (String q: tNeigh)
				if (wGraph.containsEdge(p, q))
					sqEdges++;
		}
		double sigma = sqEdges*1.0/(potential);
		arraySigma.add(sigma);
		if (sigma < 0.45)
		{
			//System.out.println(e);
			wGraph.removeEdge(e);
		}
	}
	System.out.println("no. of edges after clustering: " + wGraph.edgeSet().size());
//	System.out.println("here3");
	ConnectivityInspector<String, DefaultWeightedEdge> newcInsp = new ConnectivityInspector<String, DefaultWeightedEdge>(wGraph);
//
	conSets = newcInsp.connectedSets();
	System.out.println("No. of conn comps after: " + conSets.size());
	for (Set<String> s: conSets) {

		if (s.size() > 2)
		{
			topicClusters.add(s);

			//System.out.println(s);

		}
	}
	getStats();
						
	}
	public void getStats() {
		
		Collections.sort(arraySigma);
		medianSigma = arraySigma.get(arraySigma.size()/2);
		maxSigma = Collections.max(arraySigma);
		minSigma = Collections.min(arraySigma);
		double arraySum = 0.0;
		for (int i = 0; i < arraySigma.size();i++) {
			
			arraySum += arraySigma.get(i);
		}
		meanSigma = arraySum / arraySigma.size();
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
