package hw_probe;

class DiskValue{
	private double free;
	private double used;
	private String mounting_point;
	
	public double getFree() {
		return free;
	}
	public void setFree(double free) {
		this.free = free;
	}
	public double getUsed() {
		return used;
	}
	public void setUsed(double used) {
		this.used = used;
	}
	public String getMounting_point() {
		return mounting_point;
	}
	public void setMounting_point(String mountingPoint) {
		mounting_point = mountingPoint;
	}
	
}