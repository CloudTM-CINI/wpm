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


public class CpuResourceProbe extends AbstractProbe implements Probe {
	private CpuInfo monitored_cpu;
	//probe_timeout in millisecond
	public CpuResourceProbe(String name,int probe_timeout){
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
        monitored_cpu = new CpuInfo();
        setProbeAttributes();
	}
	private void setProbeAttributes(){
		int attributeKey = 0;
		//Cpu Attributes
		for(int i=0;i<monitored_cpu.getNumberOfCpu();i++){
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "cpu"+i+"-sys", ProbeAttributeType.DOUBLE, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "cpu"+i+"-user", ProbeAttributeType.DOUBLE, "percent"));
			//addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "cpu"+i+"-nice", ProbeAttributeType.DOUBLE, "percent"));
			addProbeAttribute(new DefaultProbeAttribute(attributeKey++, "cpu"+i+"-idle", ProbeAttributeType.DOUBLE, "percent"));
		}
	}
	
	public ProbeMeasurement collect() {
		System.out.println("Start collecting at: "+System.currentTimeMillis());
		// list of proble values
		ArrayList<ProbeValue> list = new ArrayList<ProbeValue>();
		ArrayList<CpuValue> cpuValues = null;
		int attributeKey = 0;
		//collect data from cpu
		cpuValues = monitored_cpu.getCpuValues();
		for(CpuValue cpu : cpuValues){
			try {
				list.add(new DefaultProbeValue(attributeKey++, cpu.getSystem()));
				list.add(new DefaultProbeValue(attributeKey++, cpu.getUser()));
				//list.add(new DefaultProbeValue(attributeKey++, cpu.getNice()));
				list.add(new DefaultProbeValue(attributeKey++, cpu.getIdle()));
			} catch (TypeException e) {
				e.printStackTrace();
			}
		}
		//for(ProbeValue pv : list){
		//	System.out.println("["+this.getAttribute(pv.getField()).getName() +":"+pv.getField()+":"+pv.getType()+"]-["+pv.getValue()+"]");
		//}
		return new ProducerMeasurement(this, list, "Cpu_Usage");	
	}
}