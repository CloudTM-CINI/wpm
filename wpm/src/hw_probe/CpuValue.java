package hw_probe;

class CpuValue{
	private int number;
	private double idle;
	//private double nice;
	private double system;
	private double user;
	
	public double getIdle() {
		return idle;
	}
	
	public void setIdle(double idle) {
		this.idle = idle;
	}
	/*
	public double getNice() {
		return nice;
	}
	public void setNice(double nice) {
		this.nice = nice;
	}
	*/
	public double getSystem() {
		return system;
	}
	public void setSystem(double system) {
		this.system = system;
	}
	public double getUser() {
		return user;
	}
	public void setUser(double user) {
		this.user = user;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "CPU"+number+"[idle:"+idle*100+"%|system:"+system*100+"%|user:"+user*100+"%]";
	}
	
}