//$Id$
package core.immutable;

import java.io.Serializable;

public class ContractVariation implements Serializable {
	
	private int version;
	private Contract contract;
	private String text;

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ContractVariation() {
		super();
	}

	public ContractVariation(int version, Contract contract) {
		this.contract = contract;
		this.version = version;
		contract.getVariations().add(this);
	}
}
