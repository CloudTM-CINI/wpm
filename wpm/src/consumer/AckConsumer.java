package consumer;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class AckConsumer implements Runnable{
	private int port_num;
	private static final int filesize = 1024;
	
	public AckConsumer(int consumerAckPort){
		port_num = consumerAckPort;
		Thread consumer_ack = new Thread(this,"Consumer Ack Thread");
		consumer_ack.start();
	}

	@Override
	public void run() {
		ServerSocket servsock = null;
		try {
			servsock = getServer();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		DataOutputStream dos = null;
		while(true){
			try {
				System.out.println("Consumer Ack Thread Waiting on port..."+port_num);
				Socket sock = servsock.accept();
				System.out.println("Consumer Ack Thread accepted connection...");
				OutputStream os = sock.getOutputStream();
				InputStream is = sock.getInputStream();
				dos = new DataOutputStream (os);
				DataInputStream dis = new DataInputStream(is);
				//receive ack file
				File ackFile = receiveFile(dis);
				System.out.println("Ack File received "+ackFile.getName());
				File readyFile = new File("log/active/"+ackFile.getName().substring(0,ackFile.getName().indexOf(".ack"))+".ready");
				if(readyFile.isFile()){
					//readyFile.renameTo(new File("log/active/"+readyFile.getName().substring(0,readyFile.getName().indexOf(".ready"))+".del"));
					readyFile.delete();
				}
				File checkFile = new File("log/active/"+ackFile.getName().substring(0,ackFile.getName().indexOf(".ack"))+".check");
				if(checkFile.isFile()){
					checkFile.delete();
				}
				File zipFile = new File("log/active/"+ackFile.getName().substring(0,ackFile.getName().indexOf(".ack"))+".zip");
				if(zipFile.isFile()){
					zipFile.delete();
				}
				sendMsg("ACK",dos);
				dis.close();
				is.close();
				sock.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				sendMsg("NACK",dos);
			}
		}
	}
	private void sendMsg(String strMsg, DataOutputStream dos){
		try{
		    dos.writeInt(strMsg.getBytes().length);
		    dos.flush();
		    dos.write(strMsg.getBytes());
			dos.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static File receiveFile(DataInputStream dis){
		try{
			byte [] fileInByte = new byte [filesize];
		    int count = 0;
		    int sizeOfName = dis.readInt();
		    count = dis.read(fileInByte, 0, sizeOfName);
		    String filename = new String(fileInByte,0,count);
		    File nameFileToStore = new File("log/ack/"+filename);
		    FileOutputStream fos = new FileOutputStream(nameFileToStore);
			BufferedOutputStream dest = new BufferedOutputStream(fos, filesize);
		    int sizeOfFile = dis.readInt();
		    int totalSize = 0;
		    while((totalSize != sizeOfFile) && ((count = dis.read(fileInByte, 0, filesize)) != -1)) {
				dest.write(fileInByte, 0, count);
				totalSize+=count;
				if(totalSize == sizeOfFile)
					break;
			}
		    dest.flush();
		    dest.close();
		    fos.close();
			System.out.println("File "+filename+" written!!");
			return nameFileToStore;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public ServerSocket getServer() throws Exception {
		SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket serversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port_num);
		return serversocket;
	}
}
