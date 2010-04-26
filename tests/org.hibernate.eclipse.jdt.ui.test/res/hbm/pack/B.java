package pack;

import java.io.Serializable;


public class B {	
	
	private Integer id;
	private Inner inner;
	private int[] testIntArray;
	private State state = State.AGREED;
	
	
	public static class Inner implements Serializable {

		private Long categoryId;
		private Long itemId;

	    public Inner(Long categoryId, Long itemId) {
			this.categoryId = categoryId;
			this.itemId = itemId;
		}
	}

	public int[] getTestIntArray() {
		return testIntArray;
	}

	public void setTestIntArray(int[] testIntArray) {
		this.testIntArray = testIntArray;
	}

	
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
}
