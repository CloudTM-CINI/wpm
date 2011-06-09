package main;

import consumer.ResourceConsumer;
import logService.LogService;

public class Main {

	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("Bad Parameter, use only [logService][producer][consumer]...");
			System.exit(0);
		}
		String parameter = args[0];
		Component component_to_run = null;
		if(parameter.equalsIgnoreCase("logService"))
			component_to_run = Component.LOG_SERVICE;
		else if(parameter.equalsIgnoreCase("producer"))
			component_to_run = Component.PRODUCER;
		else if(parameter.equalsIgnoreCase("consumer"))
			component_to_run = Component.CONSUMER;
		else{
			System.out.println("Bad Parameter, use only [logService][producer][consumer]...");
			System.exit(0);
		}
		switch (component_to_run) {
			case LOG_SERVICE : LogService.main(null); break;
			case CONSUMER : ResourceConsumer.main(null); break;
			case PRODUCER : HwResourceMain.main(null);break;
			default:{System.out.println("Bad Parameter, use only [logService][producer][consumer]...");System.exit(0);}
		}
	}
}

enum Component{
	PRODUCER,
	CONSUMER,
	LOG_SERVICE;
}