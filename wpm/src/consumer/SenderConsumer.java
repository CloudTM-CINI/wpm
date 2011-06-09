package consumer;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SenderConsumer implements Runnable{
	private String logService_addr;
	private int logService_port;
	private long timeout;
	
	public SenderConsumer(String logServiceAddr, int logServicePort,long period){
		logService_addr = logServiceAddr;
		logService_port = logServicePort;
		timeout = period;
		Thread sender = new Thread(this,"Consumer Sender");
		sender.start();
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
				System.out.println("Consumer Sender Thread active!!");
				File active_folder = new File("log/active");
				if(active_folder.isDirectory()){
					for(File activeFile : active_folder.listFiles()){
						if(!activeFile.getName().endsWith(".ready"))
							continue;
						try{
							//now check is exist .zip and .check files
							File zipFile = new File("log/active/"+activeFile.getName().substring(0,activeFile.getName().indexOf(".ready"))+".zip");
							//System.out.println(zipFile.getName()+" is a file: "+zipFile.isFile());
							File checkFile = new File("log/active/"+activeFile.getName().substring(0,activeFile.getName().indexOf(".ready"))+".check");
							//System.out.println(checkFile.getName()+" is a file: "+checkFile.isFile());
							if(zipFile != null && zipFile.isFile() && checkFile != null && checkFile.isFile()){
								//send zip and check file to log Service
								SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
								SSLSocket sock = (SSLSocket) factory.createSocket(logService_addr, logService_port);
								OutputStream os = sock.getOutputStream();
								DataOutputStream dos = new DataOutputStream (os);
								sendFile(zipFile,dos);
								sendFile(checkFile,dos);
								dos.close();
					            os.close();
					            sock.close();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void sendFile(File file,DataOutputStream dos){
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte [] fileInByte = new byte [(int)file.length()];
			System.out.println("FileName sending..."+file.getName()+" bytes "+file.getName().getBytes().length);
			dos.writeInt(file.getName().getBytes().length);
			dos.flush();
			dos.write(file.getName().getBytes());
			dos.flush();
			//System.out.println("FileName written");
			dos.writeInt((int)file.length());
			dos.flush();
			bis.read(fileInByte, 0, fileInByte.length);
			dos.write(fileInByte);
			dos.flush();
            bis.close();
            fis.close();
            System.out.println("File "+file.getName()+" sent!!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
