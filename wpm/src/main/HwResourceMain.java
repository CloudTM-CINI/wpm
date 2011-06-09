package main;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import producer.ResourcesController;

public class HwResourceMain {
	static String hostName;
	static String Consumer_DP_IP_Address;
	static int Consumer_DP_port_number;
	static String Consumer_IP_IP_Address;
	static int Consumer_IP_local_port_number;
	static int Consumer_IP_remote_port_number;
	public static void main(String[] args) {
		loadParametersFromRegistry();
		// allocate a Controller 
		ResourcesController controller = new ResourcesController(Consumer_DP_IP_Address,Consumer_DP_port_number,Consumer_DP_IP_Address,Consumer_IP_local_port_number,Consumer_IP_remote_port_number,hostName);
		// activate the controller
		do{
			System.out.println("Ciclo while pre activate");
			controller.activateControl();
			System.out.println("Ciclo while post activate "+controller.isRunning());
		}while(!controller.isRunning());
		
		
	}
	private static void loadParametersFromRegistry(){
    	String propsFile = "config/resource_controller.config";
    	Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		hostName = props.getProperty("hostName");
		Consumer_DP_IP_Address = props.getProperty("Consumer_DP_IP_Address");
		Consumer_DP_port_number = Integer.parseInt(props.getProperty("Consumer_DP_port_number"));
		Consumer_IP_IP_Address = props.getProperty("Consumer_IP_IP_Address");
		Consumer_IP_remote_port_number = Integer.parseInt(props.getProperty("Consumer_IP_remote_port_number"));
		Consumer_IP_local_port_number = Integer.parseInt(props.getProperty("Consumer_IP_local_port_number"));
    }
}
