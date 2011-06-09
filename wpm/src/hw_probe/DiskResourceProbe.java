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


public class DiskResourceProbe extends AbstractProbe implements Probe {
	private DiskInfo monitored_disk;
	//probe_timeout in millisecond
	public DiskResourceProbe(String name,int probe_timeout){
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
        monitored_disk = new DiskInfo();
        setProbeAttributes();
	}
	private void setProbeAttributes(){
		int attributeKey = 0;
		//Disk attributes
		for(int i=0;i<monitored_disk.getNumberFileSystems();i++){
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "fs"+i+"-free", ProbeAttributeType.DOUBLE, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "fs"+i+"-used", ProbeAttributeType.DOUBLE, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "fs"+i+"-mounting_point", ProbeAttributeType.STRING, "Stringa"));
		}
	}
	
	public ProbeMeasurement collect() {
		int attributeKey = 0;
		System.out.println("Start collecting at: "+System.currentTimeMillis());
		// list of proble values
		ArrayList<ProbeValue> list = new ArrayList<ProbeValue>();
		//collect data from disk
		ArrayList<DiskValue> diskValue = null;
		diskValue = monitored_disk.getDiskValues();
		for(DiskValue disk : diskValue){
			try {
				list.add(new DefaultProbeValue(attributeKey++, disk.getFree()));
				list.add(new DefaultProbeValue(attributeKey++, disk.getUsed()));
				list.add(new DefaultProbeValue(attributeKey++, disk.getMounting_point()));
			} catch (TypeException e) {
				e.printStackTrace();
			}
		}
		//for(ProbeValue pv : list){
		//	System.out.println("["+this.getAttribute(pv.getField()).getName() +":"+pv.getField()+":"+pv.getType()+"]-["+pv.getValue()+"]");
		//}
		return new ProducerMeasurement(this, list, "Disk_Usage");	
	}
}