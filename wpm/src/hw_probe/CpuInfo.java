package hw_probe;

import java.util.ArrayList;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Display cpu information for each cpu found on the system.
 */
public class CpuInfo{
	private Sigar handle_sigar;
	private int cpu_number;
	
    public CpuInfo() {
    	handle_sigar = new Sigar();
    	try {
			cpu_number = handle_sigar.getCpuPercList().length;
		} catch (SigarException e) {
			e.printStackTrace();
		}
    }

    public ArrayList<CpuValue> getCpuValues(){
    	CpuPerc[] cpus = null;
    	try {
    		cpus = handle_sigar.getCpuPercList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("IDLEEEE:"+cpus[0].getIdle());
    	ArrayList<CpuValue> cpu_values = new ArrayList<CpuValue>(cpus.length);
    	for (int i=0; i<cpus.length; i++) {
    		CpuValue cpu = new CpuValue();
    		cpu.setNumber(i);
    		cpu.setIdle(cpus[i].getIdle());
    		//cpu.setNice(cpus[i].getNice());
    		cpu.setSystem(cpus[i].getSys());
    		cpu.setUser(cpus[i].getUser());
    		cpu_values.add(cpu);
    		//System.out.println(cpu.getSystem());
    		//if(cpu.getSystem() == Double.NaN)
    		//	System.out.println("NONONONON");
    		//else
    		//	System.out.println("SISIIS");
    		
        }
    	return cpu_values;
    }
    public ArrayList<CpuValue> getDefaultValues(){
    	CpuPerc[] cpus = null;
    	try {
    		cpus = handle_sigar.getCpuPercList();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ArrayList<CpuValue> cpu_values = new ArrayList<CpuValue>(cpus.length);
    	for (int i=0; i<cpus.length; i++) {
    		CpuValue cpu = new CpuValue();
    		cpu.setNumber(i);
    		cpu.setIdle(-1);
    		//cpu.setNice(-1);
    		cpu.setSystem(-1);
    		cpu.setUser(-1);
    		cpu_values.add(cpu);
        }
    	return cpu_values;
    }
    
    public int getNumberOfCpu(){
		return cpu_number;
    }
    
    //Ausiliar Funcions
    /*
    private void output(CpuPerc cpu) {
    	throw new RuntimeException("Do Not Call this method");
    }
    public void output(String[] args) throws SigarException {
    	throw new RuntimeException("Do Not Call this method");
    }
    
    public String getUsageShort() {
        return "Display cpu information";
    }

    private void output(CpuPerc cpu) {
        println("User Time....." + CpuPerc.format(cpu.getUser()));
        println("Sys Time......" + CpuPerc.format(cpu.getSys()));
        println("Idle Time....." + CpuPerc.format(cpu.getIdle()));
        println("Wait Time....." + CpuPerc.format(cpu.getWait()));
        println("Nice Time....." + CpuPerc.format(cpu.getNice()));
        println("Combined......" + CpuPerc.format(cpu.getCombined()));
        println("Irq Time......" + CpuPerc.format(cpu.getIrq()));
        if (SigarLoader.IS_LINUX) {
            println("SoftIrq Time.." + CpuPerc.format(cpu.getSoftIrq()));
            println("Stolen Time...." + CpuPerc.format(cpu.getStolen()));
        }
        println("");
    }

    public void output(String[] args) throws SigarException {
        org.hyperic.sigar.CpuInfo[] infos =
            this.sigar.getCpuInfoList();

        CpuPerc[] cpus =
            this.sigar.getCpuPercList();

        org.hyperic.sigar.CpuInfo info = infos[0];
        long cacheSize = info.getCacheSize();
        println("Vendor........." + info.getVendor());
        println("Model.........." + info.getModel());
        println("Mhz............" + info.getMhz());
        println("Total CPUs....." + info.getTotalCores());
        if ((info.getTotalCores() != info.getTotalSockets()) ||
            (info.getCoresPerSocket() > info.getTotalCores()))
        {
            println("Physical CPUs.." + info.getTotalSockets());
            println("Cores per CPU.." + info.getCoresPerSocket());
        }

        if (cacheSize != Sigar.FIELD_NOTIMPL) {
            println("Cache size...." + cacheSize);
        }
        println("");

        if (!this.displayTimes) {
            return;
        }

        for (int i=0; i<cpus.length; i++) {
            println("CPU " + i + ".........");
            output(cpus[i]);
        }

        println("Totals........");
        output(this.sigar.getCpuPerc());
    }
	*/
    public static void main(String[] args) throws Exception {
        System.out.println(new CpuInfo().getCpuValues().size());
    }
}