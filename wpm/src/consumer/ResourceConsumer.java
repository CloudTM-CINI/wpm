package consumer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import eu.reservoir.monitoring.appl.BasicConsumer;
import eu.reservoir.monitoring.core.plane.InfoPlane;
import eu.reservoir.monitoring.distribution.udp.UDPDataPlaneConsumer;
import eu.reservoir.monitoring.im.dht.DHTInfoPlaneRoot;

	public class ResourceConsumer {
		static String Local_DP_IP_Address;
		static int Local_DP_port_number;
		static String Local_IP_IP_Address;
		static int Local_IP_port_number;
		static int Local_ack_port_number;
		static int LogService_Transmission_Period;
		static String LogService_IP_Address;
		static int LogService_port_number;
		static long timeout;
		static String ID;
		
		// The Basic consumer
	    private BasicConsumer consumer;
	    private String id;
	
	    public ResourceConsumer(String identf,String DP_addr, int DP_port,String IP_addr,int IP_port,String logService_addr,int logService_port,int trans_period) {
		// set up a BasicConsumer
		id = identf;
	    consumer = new BasicConsumer();
		// set up an IP address for data
		InetSocketAddress address = new InetSocketAddress(DP_addr,DP_port);
		// set up data plane
		consumer.setDataPlane(new UDPDataPlaneConsumer(address));
		InfoPlane inf_pl = null;
		try{
			inf_pl = new DHTInfoPlaneRoot(IP_addr, IP_port);
			consumer.setInfoPlane(inf_pl);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		consumer.setReporter(new ResourceReporter(inf_pl,id,trans_period));
		consumer.connect();
	}
	    
    public static void main(String [] args) {
    	loadParametersFromRegistry();
    	new SenderConsumer(LogService_IP_Address,LogService_port_number,timeout);
    	new AckConsumer(Local_ack_port_number);
    	new ResourceConsumer(ID,Local_DP_IP_Address,Local_DP_port_number,Local_IP_IP_Address,Local_IP_port_number,LogService_IP_Address,LogService_port_number,LogService_Transmission_Period);
	    System.err.println("Consumer UDP listening on "+Local_DP_IP_Address+"/"+Local_DP_port_number);
    }
    
    private static void loadParametersFromRegistry(){
    	String propsFile = "config/resource_consumer.config";
    	Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Local_DP_IP_Address = props.getProperty("Local_DP_IP_Address");
		Local_DP_port_number = Integer.parseInt(props.getProperty("Local_DP_port_number"));
		Local_IP_IP_Address = props.getProperty("Local_IP_IP_Address");
		Local_IP_port_number = Integer.parseInt(props.getProperty("Local_IP_port_number"));
		Local_ack_port_number = Integer.parseInt(props.getProperty("Local_ack_port_number"));
		LogService_Transmission_Period = Integer.parseInt(props.getProperty("LogService_Transmission_Period"));
		LogService_IP_Address = props.getProperty("LogService_IP_Address");
		LogService_port_number = Integer.parseInt(props.getProperty("LogService_port_number"));
		timeout = Long.parseLong(props.getProperty("SenderTimeout"));
		ID = props.getProperty("ID");
    }
}

