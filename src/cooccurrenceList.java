
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import java.util.*;
@Entity
public class cooccurrenceList {
	
	@PrimaryKey
	String indexWord;
	
	public Map<String, Double> coList;
	
	public cooccurrenceList () {
		
		coList = new HashMap <String, Double>();
		
	}
	
	public void printcoList() {
		System.out.println(indexWord + ":" + coList);
		
	}
}
