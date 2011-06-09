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


public class MemoryResourceProbe extends AbstractProbe implements Probe {
	private MemoryInfo monitored_memory;
	//probe_timeout in millisecond
	public MemoryResourceProbe(String name,int probe_timeout){
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
        //this.identification = identification;
        monitored_memory = new MemoryInfo();
        setProbeAttributes();
	}
	private void setProbeAttributes(){
		int attributeKey = 0;
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "mem-free", ProbeAttributeType.LONG, "Byte"));
		addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "mem-used", ProbeAttributeType.LONG, "Byte"));
	}
	
	public ProbeMeasurement collect() {
		System.out.println("Start collecting at: "+System.currentTimeMillis());
		int attributeKey = 0;
		// list of proble values
		ArrayList<ProbeValue> list = new ArrayList<ProbeValue>();
		MemoryValue memValue = null;
		//collect data from ram
		memValue = monitored_memory.getMemoryValues();
		try {
			list.add(new DefaultProbeValue(attributeKey++, memValue.getFree()));
			list.add(new DefaultProbeValue(attributeKey++, memValue.getUsed()));
		} catch (TypeException e) {
			e.printStackTrace();
		}
		//for(ProbeValue pv : list){
		//	System.out.println("["+this.getAttribute(pv.getField()).getName() +":"+pv.getField()+":"+pv.getType()+"]-["+pv.getValue()+"]");
		//}
		return new ProducerMeasurement(this, list, "Memory_Usage");	
	}
}