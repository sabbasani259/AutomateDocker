package remote.wise.service.datacontract;

public class GensetTrendChartDataContract {

	protected String txnDate;
	protected int generatorPhaseACurrent;
	protected int generatorPhaseBCurrent;
	protected int generatorPhaseCCurrent;
	protected int pBlockPeriodKVAHrs;
	protected int pBlockPeriodKVArHrs;
	protected int pBlockPeriodKWHrs;

	public String getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public int getGeneratorPhaseACurrent() {
	    return generatorPhaseACurrent;
	}

	public void setGeneratorPhaseACurrent(int generatorPhaseACurrent) {
	    this.generatorPhaseACurrent = generatorPhaseACurrent;
	}

	public int getGeneratorPhaseBCurrent() {
	    return generatorPhaseBCurrent;
	}

	public void setGeneratorPhaseBCurrent(int generatorPhaseBCurrent) {
	    this.generatorPhaseBCurrent = generatorPhaseBCurrent;
	}

	public int getGeneratorPhaseCCurrent() {
	    return generatorPhaseCCurrent;
	}

	public void setGeneratorPhaseCCurrent(int generatorPhaseCCurrent) {
	    this.generatorPhaseCCurrent = generatorPhaseCCurrent;
	}

	public int getpBlockPeriodKVAHrs() {
	    return pBlockPeriodKVAHrs;
	}

	public void setpBlockPeriodKVAHrs(int pBlockPeriodKVAHrs) {
	    this.pBlockPeriodKVAHrs = pBlockPeriodKVAHrs;
	}

	public int getpBlockPeriodKVArHrs() {
	    return pBlockPeriodKVArHrs;
	}

	public void setpBlockPeriodKVArHrs(int pBlockPeriodKVArHrs) {
	    this.pBlockPeriodKVArHrs = pBlockPeriodKVArHrs;
	}

	public int getpBlockPeriodKWHrs() {
	    return pBlockPeriodKWHrs;
	}

	public void setpBlockPeriodKWHrs(int pBlockPeriodKWHrs) {
	    this.pBlockPeriodKWHrs = pBlockPeriodKWHrs;
	}
}
