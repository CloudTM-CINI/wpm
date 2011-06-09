package logService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class LogServiceAnalyzer implements Runnable{
	static final int filesize = 2048;
	private Cache<String,String> cache;
	private String cacheName;
	private String infinispanConfigFile;
	private long timeout;
	
	public LogServiceAnalyzer(){
		loadParametersFromRegistry();
		try {
			EmbeddedCacheManager ecm = new DefaultCacheManager(infinispanConfigFile);
			System.out.println("Log Service cache created");
	        cache = ecm.getCache(cacheName);
	        //TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
			Thread analyzer = new Thread(this,"Log Service Analyzer");
			analyzer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Running Log Service Analyzer Thread!!");
				File active_folder = new File("log/ls_processed");
				if(active_folder.isDirectory()){
					for(File activeFile : active_folder.listFiles()){
						if(!activeFile.getName().endsWith(".zip"))
							continue;
						try {
					        FileInputStream is = new FileInputStream(activeFile);
						    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
						    ZipEntry entry = null;
						    FileOutputStream fos = null;
						    String nameFileToStore = "";
						    if((entry = zis.getNextEntry()) != null){
						    	byte [] logFileByteArray  = new byte [filesize];
						    	int count = 0;
						    	nameFileToStore = "log/ls_worked/"+entry.getName();
						    	fos = new FileOutputStream(nameFileToStore);
						    	BufferedOutputStream dest = new BufferedOutputStream(fos, filesize);
								System.out.println("Extracting: " +entry.getName());
								while ((count = zis.read(logFileByteArray, 0, filesize)) != -1) {
									dest.write(logFileByteArray, 0, count);
								}
								dest.flush();
						    }
						    fos.close();
						    zis.close();
						    System.out.println("File decompressed stored");
						    //Create ack file
						    File ackFile = new File("log/ls_worked/"+activeFile.getName().substring(0,activeFile.getName().lastIndexOf(".zip"))+".ack");
				            if(!ackFile.createNewFile())
				            	System.out.println("Error while creating ack file");
				            System.out.println ("Ack file stored: "+ackFile.getPath());
						    String strLine = "";
						    FileInputStream fstream = new FileInputStream(nameFileToStore);
						    DataInputStream in = new DataInputStream(fstream);
						    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						    while ((strLine = br.readLine()) != null){
						    	StringTokenizer lineParser = new StringTokenizer(strLine,":");
						    	lineParser.nextToken();//Name
						    	String identification_key = lineParser.nextToken()+":"+lineParser.nextToken()+":"+lineParser.nextToken()+":"+lineParser.nextToken()+":"+lineParser.nextToken();
						    	String payload = "";
						    	while(lineParser.hasMoreTokens()){
						    		payload += lineParser.nextToken()+":";
						    	}
						    	payload = payload.substring(0, payload.lastIndexOf("]"));
						    	String rest = identification_key.substring(identification_key.lastIndexOf(" ")+1,identification_key.length());
						    	identification_key = identification_key.substring(0,identification_key.lastIndexOf(" "));
						    	identification_key = identification_key.replaceAll(" ", "");
						    	payload = rest+":"+payload;
						    	//System.out.println("Key: "+identification_key);
						    	//System.out.println("Value: "+payload);
						    	//tm.begin();
								cache.put(identification_key,payload); 
								//tm.commit();
						    }
						    System.out.println("Dataitem in cache: "+cache.size());
						    activeFile.delete();
						    System.out.println("Deleted file: "+activeFile.getName());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadParametersFromRegistry(){
    	String propsFile = "config/log_service.config";
    	Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		cacheName = props.getProperty("Cache_name");
		infinispanConfigFile = props.getProperty("InfinispanConfigFile");
		timeout = Long.parseLong(props.getProperty("AnalyzerThreadTimeout"));
    }
}
