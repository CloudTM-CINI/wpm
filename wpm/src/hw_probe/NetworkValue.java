package hw_probe;

class NetworkValue{
	private long tx_bytes;
	private long rx_bytes;
	private double tx_brandwidth;
	private double rx_brandwidth;
	
	public long getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(long txBytes) {
		tx_bytes = txBytes;
	}
	public long getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(long rxBytes) {
		rx_bytes = rxBytes;
	}
	public double getTx_brandwidth() {
		return tx_brandwidth;
	}
	public void setTx_brandwidth(double txBrandwidth) {
		tx_brandwidth = txBrandwidth;
	}
	public double getRx_brandwidth() {
		return rx_brandwidth;
	}
	public void setRx_brandwidth(double rxBrandwidth) {
		rx_brandwidth = rxBrandwidth;
	}
	
	
}