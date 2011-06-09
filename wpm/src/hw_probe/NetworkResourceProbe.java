package hw_probe;

import java.util.ArrayList;

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


public class NetworkResourceProbe extends AbstractProbe implements Probe {
	private NetworkInfo monitored_network;
	//probe_timeout in millisecond
	public NetworkResourceProbe(String name,int probe_timeout){
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
        monitored_network = new NetworkInfo();
        setProbeAttributes();
	}
	private void setProbeAttributes(){
		int attributeKey = 0;
		//Network attributes
		for(int i=0;i<monitored_network.getNumberInterfaces();i++){
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "int"+i+"-in", ProbeAttributeType.LONG, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "int"+i+"-out", ProbeAttributeType.LONG, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "int"+i+"-in_width", ProbeAttributeType.DOUBLE, "Byte/sec"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "int"+i+"-out_width", ProbeAttributeType.DOUBLE, "Byte/sec"));
		}
	}
	
	public ProbeMeasurement collect() {
		System.out.println("Start collecting at: "+System.currentTimeMillis());
		int attributeKey = 0;
		// list of proble values
		ArrayList<ProbeValue> list = new ArrayList<ProbeValue>();
		//collect data from network
		ArrayList<NetworkValue> netValue = null;
		netValue = monitored_network.getNetworkValues(); 
		for(NetworkValue net : netValue){
			try {
				list.add(new DefaultProbeValue(attributeKey++, net.getRx_bytes()));
				list.add(new DefaultProbeValue(attributeKey++, net.getTx_bytes()));
				list.add(new DefaultProbeValue(attributeKey++, net.getRx_brandwidth()));
				list.add(new DefaultProbeValue(attributeKey++, net.getTx_brandwidth()));
			} catch (TypeException e) {
				e.printStackTrace();
			}
		}
		//for(ProbeValue pv : list){
		//	System.out.println("["+this.getAttribute(pv.getField()).getName() +":"+pv.getField()+":"+pv.getType()+"]-["+pv.getValue()+"]");
		//}
		return new ProducerMeasurement(this, list, "Network_Usage");	
	}
}