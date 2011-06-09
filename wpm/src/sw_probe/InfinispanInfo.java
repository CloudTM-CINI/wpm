package sw_probe;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Display amount of free and used memory in the system.
 */
public class InfinispanInfo {
	
	private MBeanServerConnection mbsc = null;
	private String address;
	private int port;
	private String cacheName;
	private String jmxDomainName;
	private String replicationType;
	private String cacheManager;
    
	public InfinispanInfo(String addr, int port_num,String cache_name,String jmxDomain,String repl,String manager) {
    	address = addr;
    	port = port_num;
    	cacheName = cache_name;
    	jmxDomainName = jmxDomain;
    	replicationType = repl;
    	cacheManager = manager; 
    	connectToJMXServer();
    }

    public InfinispanValue getInfinispanValues(){
    	if(mbsc == null)
    		connectToJMXServer();
    	InfinispanValue inf_value = new InfinispanValue();
    	try{
    		ObjectName transactionComponent = new ObjectName(jmxDomainName+":type=Cache,name="+ObjectName.quote(cacheName+"("+replicationType+")")+",manager="+ObjectName.quote(cacheManager)+",component=Transactions");
    		inf_value.setNumPut((Long)mbsc.getAttribute(transactionComponent,"NumPuts"));
    		inf_value.setNumCommits((Long)mbsc.getAttribute(transactionComponent,"Commits"));
    		inf_value.setCommitTime((Long)mbsc.getAttribute(transactionComponent,"CommitTime"));
    		inf_value.setNumPrepares((Long)mbsc.getAttribute(transactionComponent,"Prepares"));
    		inf_value.setNumRollbacks((Long)mbsc.getAttribute(transactionComponent,"Rollbacks"));
    		inf_value.setWriteTxCommitted((Long)mbsc.getAttribute(transactionComponent,"WriteTxCommits"));
    		inf_value.setWriteTxPrepared((Long)mbsc.getAttribute(transactionComponent,"WriteTxInPrepare"));
    		inf_value.setWriteTxStarted((Long)mbsc.getAttribute(transactionComponent,"WriteTxStarted"));
    		inf_value.setAvgLocalROTxExTime((Long)mbsc.getAttribute(transactionComponent,"AvgLocalReadOnlyExecutionTime"));
    		inf_value.setAvgLocalWRTxExTime((Long)mbsc.getAttribute(transactionComponent,"AvgLocalWriteTxExecutionTime"));
    	}catch (IOException e) {
    		e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
    	
		return inf_value;
    }
    
    private void connectToJMXServer(){
    	JMXServiceURL url = null;
    	JMXConnector jmxc = null;
    	try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+address+":"+port+"/jmxrmi");
			jmxc = JMXConnectorFactory.connect(url, null);
			mbsc = jmxc.getMBeanServerConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
}

