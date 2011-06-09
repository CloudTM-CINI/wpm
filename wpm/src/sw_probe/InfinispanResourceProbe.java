package sw_probe;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import eu.reservoir.monitoring.core.AbstractProbe;
import eu.reservoir.monitoring.core.DefaultProbeAttribute;
import eu.reservoir.monitoring.core.DefaultProbeValue;
import eu.reservoir.monitoring.core.Probe;
import eu.reservoir.monitoring.core.ProbeAttributeType;
import eu.reservoir.monitoring.core.ProbeMeasurement;
import eu.reservoir.monitoring.core.ProbeValue;
import eu.reservoir.monitoring.core.ProducerMeasurement;
import eu.reservoir.monitoring.core.Rational;
import eu.reservoir.monitoring.core.TypeException;


public class InfinispanResourceProbe extends AbstractProbe implements Probe {
	static InfinispanInfo monitored_infinispan;
	static String addr;
	static int port_num; 
	static String cache_name;
	static String jmxDomain;
	static String replicationType;
	static String cacheManager;
	
	//probe_timeout in millisecond
	public InfinispanResourceProbe(String name,int probe_timeout){
		setName(name);
		//Logical group of VM
		//ID gr_id = new ID(group_id);
		//setGroupID(gr_id);
		//Specified in measurements per hour
		int milliseconds_each_hour = 3600000;
		Rational probe_rate = new Rational(milliseconds_each_hour,1000);
		try{
			probe_rate = new Rational(milliseconds_each_hour, probe_timeout);
		}catch(Exception e){
			e.printStackTrace();
		}
        setDataRate(probe_rate);
        //retrive JMX and infinispan parameters
        loadParametersFromRegistry();
        monitored_infinispan = new InfinispanInfo(addr, port_num, cache_name, jmxDomain,replicationType,cacheManager);
        setProbeAttributes(cache_name);
	}
	
	private void setProbeAttributes(String cache_name){
		int attributeKey = 0;
		//Cpu Attributes
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-numPut", ProbeAttributeType.LONG, ""));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-numCommits", ProbeAttributeType.LONG, "transactions"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-commitTime", ProbeAttributeType.LONG, "millisecond"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-numPrepares", ProbeAttributeType.LONG, ""));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-numRollbacks", ProbeAttributeType.LONG, "transactions"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-writeTxCommitted", ProbeAttributeType.LONG, "transactions"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-writeTxPrepared", ProbeAttributeType.LONG, "transactions"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-writeTxStarted", ProbeAttributeType.LONG, "transactions"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-avgLocalROTxExTime", ProbeAttributeType.LONG, "milliseconds"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, cache_name+"-avgLocalWRTxExTime", ProbeAttributeType.LONG, "milliseconds"));
			
	}
	
	public ProbeMeasurement collect() {
		System.out.println("Start collecting at: "+System.currentTimeMillis());
		// list of proble values
		ArrayList<ProbeValue> list = new ArrayList<ProbeValue>();
		int attributeKey = 0;
		//collect data from infinispan using JMX
		InfinispanValue inf_value = monitored_infinispan.getInfinispanValues();
		try {
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getNumPut()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getNumCommits()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getCommitTime()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getNumPrepares()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getNumRollbacks()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getWriteTxCommitted()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getWriteTxPrepared()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getWriteTxStarted()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getAvgLocalROTxExTime()));
			list.add(new DefaultProbeValue(attributeKey++, inf_value.getAvgLocalWRTxExTime()));
		} catch (TypeException e) {
			e.printStackTrace();
		}
		//for(ProbeValue pv : list){
		//	System.out.println("["+this.getAttribute(pv.getField()).getName() +":"+pv.getField()+":"+pv.getType()+"]-["+pv.getValue()+"]");
		//}
		
		return new ProducerMeasurement(this, list, "Infinispan_Parameters");
	}

	private static void loadParametersFromRegistry(){
    	String propsFile = "config/infinispan_probe.config";
    	Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		addr = props.getProperty("Infinispan_IP_Address");
		port_num = Integer.parseInt(props.getProperty("Infinispan_JMX_port_number")); 
		cache_name = props.getProperty("Cache_name");
		jmxDomain = props.getProperty("JMXDomain");
		replicationType = props.getProperty("Replication_type");
		cacheManager = props.getProperty("Cache_Manager");
    }
}