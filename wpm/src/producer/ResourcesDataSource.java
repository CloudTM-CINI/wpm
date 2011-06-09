package producer;

import sw_probe.InfinispanResourceProbe;
import hw_probe.CpuResourceProbe;
import hw_probe.DiskResourceProbe;
import hw_probe.MemoryResourceProbe;
import hw_probe.NetworkResourceProbe;
import eu.reservoir.monitoring.appl.BasicDataSource;
import eu.reservoir.monitoring.core.DataSource;
import eu.reservoir.monitoring.core.Probe;

public class ResourcesDataSource  extends BasicDataSource implements DataSource {
	
	public ResourcesDataSource(String hostname){
		setName(hostname);
	}
	
	public boolean addProbe(String name, int probe_timeout, MonitorableResources resource_type) {
		System.out.println("NEW PROBE: " + name);
		// try and get the probe
		Probe veeProbe = getProbeByName(name);
		if (veeProbe == null) {
		    // it doesn't exists, so add it
			//Probe p = new HwResourceProbe(name,cpu_timeout, memory_timeout,network_timeout, disk_timeout,probe_timeout,group_id);
			Probe p = null;
			switch(resource_type){
				case CPU : p = new CpuResourceProbe(name, probe_timeout);break;
				case MEMORY : p = new MemoryResourceProbe(name, probe_timeout);break;
				case NETWORK : p = new NetworkResourceProbe(name, probe_timeout);break;
				case DISK : p = new DiskResourceProbe(name, probe_timeout);break;
				case JMX : p = new InfinispanResourceProbe(name, probe_timeout);break;
				default : throw new RuntimeException("Unknown monitorable resource type");
			}
		    addProbe(p);
		    activateProbe(p);
		    turnOnProbe(p);
		    System.out.println("Probe: " + p + " acivated!!");
		    return true;
		} else {
			System.out.println("Probe: " + name + " already activated!!");
		    return false;
		}
	}
	
	public boolean deleteProbe(String name) {
		System.out.println("DELETE PROBE: " + name);
		// try and get the probe
		Probe veeProbe = getProbeByName(name);
		if (veeProbe != null) {
		    // it exists, so remove it
		    turnOffProbe(veeProbe);
		    deactivateProbe(veeProbe);
		    removeProbe(veeProbe);
		    System.out.println("Probe: " + veeProbe + " deleted!!");
		    return true;
		} else {
		    // it doesn't exist
			System.out.println("Probe: " + name + " already deleted!!");
		    return false;
		}
	}
}
