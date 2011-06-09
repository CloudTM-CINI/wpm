package hw_probe;

import java.util.ArrayList;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class DiskInfo {
	private Sigar handle_sigar;
	private int num_fs;
	
    public DiskInfo() {
    	handle_sigar = new Sigar();
    	try {
			num_fs = handle_sigar.getFileSystemList().length;
		} catch (SigarException e) {
			e.printStackTrace();
		}
    }

    public ArrayList<DiskValue> getDiskValues(){
    	FileSystem[] fs_list = null;
    	try {
    		fs_list = handle_sigar.getFileSystemList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<DiskValue> fs_values = new ArrayList<DiskValue>(fs_list.length);
		for(FileSystem fs : fs_list){
			FileSystemUsage fs_usage = null;
			try {
				fs_usage = handle_sigar.getFileSystemUsage(fs.getDirName());
			} catch (SigarException e) {
				e.printStackTrace();
			}
			DiskValue disk_value = new DiskValue();
			disk_value.setFree((new Double(1))-fs_usage.getUsePercent());
			disk_value.setUsed(fs_usage.getUsePercent());
			disk_value.setMounting_point(fs.getDirName());
			fs_values.add(disk_value);
		}
		return fs_values;
    }
    public ArrayList<DiskValue> getDefaultValues(){
		ArrayList<DiskValue> fs_values = new ArrayList<DiskValue>(getNumberFileSystems());
		for(int i=0;i<getNumberFileSystems();i++){
			DiskValue disk_value = new DiskValue();
			disk_value.setFree(-1);
			disk_value.setUsed(-1);
			disk_value.setMounting_point("");
			fs_values.add(disk_value);
		}
		return fs_values;
    }
    
    public int getNumberFileSystems(){
		return num_fs;
    }
}
