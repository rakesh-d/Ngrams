import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ngDataAccessor {
	
	PrimaryIndex<String, cooccurrenceList> coListByWord;
	public ngDataAccessor (EntityStore ngStore)
	throws DatabaseException {
		coListByWord = ngStore.getPrimaryIndex(
				String.class, cooccurrenceList.class);
		
	}
	
}