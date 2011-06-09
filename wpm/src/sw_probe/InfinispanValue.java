package sw_probe;


class InfinispanValue{
	private long numPut;
	private long numCommits;
	private long commitTime;
	private long numPrepares;
	private long numRollbacks;
	private long writeTxCommitted;
	private long writeTxPrepared;
	private long writeTxStarted;
	private long avgLocalROTxExTime;
	private long avgLocalWRTxExTime;
	public long getNumPut() {
		return numPut;
	}
	public void setNumPut(long numPut) {
		this.numPut = numPut;
	}
	public long getNumCommits() {
		return numCommits;
	}
	public void setNumCommits(long numCommits) {
		this.numCommits = numCommits;
	}
	public long getCommitTime() {
		return commitTime;
	}
	public void setCommitTime(long commitTime) {
		this.commitTime = commitTime;
	}
	public long getNumPrepares() {
		return numPrepares;
	}
	public void setNumPrepares(long numPrepares) {
		this.numPrepares = numPrepares;
	}
	public long getNumRollbacks() {
		return numRollbacks;
	}
	public void setNumRollbacks(long numRollbacks) {
		this.numRollbacks = numRollbacks;
	}
	public long getWriteTxCommitted() {
		return writeTxCommitted;
	}
	public void setWriteTxCommitted(long writeTxCommitted) {
		this.writeTxCommitted = writeTxCommitted;
	}
	public long getWriteTxPrepared() {
		return writeTxPrepared;
	}
	public void setWriteTxPrepared(long writeTxPrepared) {
		this.writeTxPrepared = writeTxPrepared;
	}
	public long getWriteTxStarted() {
		return writeTxStarted;
	}
	public void setWriteTxStarted(long writeTxStarted) {
		this.writeTxStarted = writeTxStarted;
	}
	public long getAvgLocalROTxExTime() {
		return avgLocalROTxExTime;
	}
	public void setAvgLocalROTxExTime(long avgLocalROTxExTime) {
		this.avgLocalROTxExTime = avgLocalROTxExTime;
	}
	public long getAvgLocalWRTxExTime() {
		return avgLocalWRTxExTime;
	}
	public void setAvgLocalWRTxExTime(long avgLocalWRTxExTime) {
		this.avgLocalWRTxExTime = avgLocalWRTxExTime;
	}
	
	
}