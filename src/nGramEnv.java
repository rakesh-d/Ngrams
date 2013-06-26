import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class nGramEnv {
	Environment ngEnv;
	EntityStore ngStore;
	
	public nGramEnv () {}
	
	public void setup (File envhome, boolean readOnly) 
	throws DatabaseException {
		EnvironmentConfig ngConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		
		ngConfig.setReadOnly(readOnly);
		storeConfig.setReadOnly(readOnly);
		
		ngConfig.setAllowCreate(!readOnly);
		ngConfig.setCachePercent(80);
		storeConfig.setAllowCreate(!readOnly);
		System.out.println("cache size = " + ngConfig.getCachePercent());
		
		ngEnv = new Environment(envhome, ngConfig);
		ngStore = new EntityStore(ngEnv, "EntityStore", storeConfig);
		
	}
	public EntityStore getEntityStore() {
		return ngStore;
	}
	
	public Environment getEnv() {
		return ngEnv;
	}
	public void close() {
		if (ngStore != null) {
			try {
				ngStore.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing ngStore: " +
						dbe.toString());
				System.exit(-1);
			}
		}
		if (ngEnv != null) {
			try {
				ngEnv.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing ngEnv: " +
						dbe.toString());
				System.exit(-1);
				
			}
		}
	}
}
