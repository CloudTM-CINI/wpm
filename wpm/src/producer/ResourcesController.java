package producer;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import eu.reservoir.monitoring.appl.DynamicControl;
import eu.reservoir.monitoring.core.Probe;
import eu.reservoir.monitoring.core.Rational;
import eu.reservoir.monitoring.distribution.udp.UDPDataPlaneProducer;
import eu.reservoir.monitoring.im.dht.DHTInfoPlane;

public class ResourcesController extends DynamicControl {
	// the start time
    private long startTime = 0;
    private String VM_Identification;
    private String IP_Address;

    // the DataSource
    private ResourcesDataSource dataSource;

    public ResourcesController(String consumer_DP_addr, int consumer_DP_port, String consumer_IP_addr, int consumer_IP_local_port, int consumer_IP_remote_port,String hostname) {
		super("Probes Controller");
		//set up counter
		//set up data source
		dataSource = new ResourcesDataSource(hostname);
		InetSocketAddress address = new InetSocketAddress(consumer_DP_addr, consumer_DP_port);
		
		// set up multicast addresses
		//MulticastAddress dataGroup = new MulticastAddress(addr, port);
		//dataSource.setDataPlane(new MulticastDataPlaneProducer(dataGroup));
		//without attributes
		//dataSource.setDataPlane(new UDPDataPlaneProducerNoNames(address));
		//with attriutes
		dataSource.setDataPlane(new UDPDataPlaneProducer(address));
		try{
			dataSource.setInfoPlane(new DHTInfoPlane(consumer_IP_addr, consumer_IP_remote_port, consumer_IP_local_port));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		dataSource.connect();
    }

    /**
     * Initialize.
     */
    protected void controlInitialize() {
    	System.out.println("Inizializing control plane");
    	startTime = System.currentTimeMillis();
    	loadParametersFromRegistry();
    }

    /**
     * Actually evaluate something.
     */
    protected void controlEvaluate() {
		long now = System.currentTimeMillis();
		long diff = (now - startTime) / 1000;
		System.out.println(diff + ": " + this + " seen " + dataSource.getProbes().size());
		if(dataSource.getProbes().size() == 0){
			dataSource.addProbe("VM_"+VM_Identification+":"+IP_Address+":CPU",15000,MonitorableResources.CPU);
			dataSource.addProbe("VM_"+VM_Identification+":"+IP_Address+":MEM",30000,MonitorableResources.MEMORY);
			dataSource.addProbe("VM_"+VM_Identification+":"+IP_Address+":NET",50000,MonitorableResources.NETWORK);
			dataSource.addProbe("VM_"+VM_Identification+":"+IP_Address+":DISK",50000,MonitorableResources.DISK);
			dataSource.addProbe("VM_"+VM_Identification+":"+IP_Address+":INFINISPAN",50000,MonitorableResources.JMX);
		}
    }

    /**
     * Cleanup
     */
    protected void controlCleanup() {
    	System.out.println("Clean Control");
    	//this.activateControl();
    }
    
    public void setProbeDataRate(String name, int probe_timeout) {
    	Probe probe = dataSource.getProbeByName(name);
    	if(probe == null)
    		return;
    	int milliseconds_each_hour = 3600000;
		Rational probe_rate = new Rational(milliseconds_each_hour,1000);
		try{
			probe_rate = new Rational(milliseconds_each_hour, probe_timeout);
		}catch(Exception e){
			e.printStackTrace();
		}
		probe.setDataRate(probe_rate);
    }
    private void loadParametersFromRegistry(){
    	String propsFile = "config/resource_controller.config";
    	Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		VM_Identification = props.getProperty("VM_Identification");
		IP_Address = props.getProperty("Producer_IP_Address");
    }
    
}
