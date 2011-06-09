package consumer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.reservoir.monitoring.core.Measurement;
import eu.reservoir.monitoring.core.Reporter;
import eu.reservoir.monitoring.core.plane.InfoPlane;

public class ResourceReporter implements Reporter {
	private InfoPlane infoModel;
	private String id_consumer;
	private long lastTimestamp;
	private long timeToRefresh;
	
	public ResourceReporter(InfoPlane infoPlane,String id_cons,long refresh_period) {
		infoModel = infoPlane;
		id_consumer = id_cons;
		timeToRefresh = refresh_period;
	}
	
	public void report(Measurement m) {
		String probeName = (String)infoModel.lookupProbeInfo(m.getProbeID(), "name");
		System.out.print("Received msg from probe"+probeName + " => ");
		long currentTimestamp = System.currentTimeMillis();
		File logFile = new File("log/stat_"+id_consumer+".log");
		try{
			FileWriter fstream = new FileWriter(logFile,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Name:"+probeName+"Value:"+m.toString()+"\n");
			System.out.println("File updated!!");
			out.close();
			if(currentTimestamp - lastTimestamp >= timeToRefresh){
				byte [] logFileByteArray  = new byte [(int)logFile.length()];
				FileInputStream fis = new FileInputStream(logFile);
				fis = new FileInputStream(logFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				//Create zip file to store
				File logFileZip = new File("log/active/"+(logFile.getName().substring(0,logFile.getName().lastIndexOf(".log")))+"_"+lastTimestamp+"_"+currentTimestamp+".log.zip");
				FileOutputStream dest = new FileOutputStream(logFileZip);
				CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
				ZipOutputStream outZip = new ZipOutputStream(new BufferedOutputStream(checksum));
				ZipEntry entry = new ZipEntry(logFileZip.getName().substring(0,logFileZip.getName().lastIndexOf(".zip")));
				outZip.putNextEntry(entry);
	            bis.read(logFileByteArray, 0, logFileByteArray.length);
	            outZip.write(logFileByteArray,0,logFileByteArray.length);
	            outZip.close();
	            bis.close();
	            fis.close();
	            System.out.println ("Zip file stored: "+logFile.getPath());
	            File checkFile = new File("log/active/"+logFileZip.getName().substring(0,logFileZip.getName().lastIndexOf(".zip"))+".check");
	            FileWriter fw = new FileWriter(checkFile);
	            BufferedWriter checkFile_writer = new BufferedWriter(fw);
	            checkFile_writer.write(new String(""+checksum.getChecksum().getValue()));
	            checkFile_writer.close();
	            fw.close();
	            System.out.println ("Check file stored: "+checkFile.getPath());
	            File readyFile = new File("log/active/"+logFileZip.getName().substring(0,logFileZip.getName().lastIndexOf(".zip"))+".ready");
	            if(!readyFile.createNewFile())
	            	throw new RuntimeException("Error while creating ready file");
	            System.out.println ("Ready file stored: "+readyFile.getPath());
				lastTimestamp = currentTimestamp;
				logFile.delete();
			}
			System.out.print("Fine elaborazione");
		}catch(Exception ex){
			ex.printStackTrace();
			//delete file if is bigger than 100Mb
			try{
				if(logFile.length() > 100000000)
					logFile.delete();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
}