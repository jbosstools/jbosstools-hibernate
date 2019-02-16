package pack;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Emmanuel Bernard
 */
public class A {
	private Integer id;
	private B prop;
	private B[] bs;
	private List<B> list;
	private Set<B> set;

	private Map<String, B> mapValue;	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public B getProp() {
		return prop;
	}

	public void setProp(B b) {
		this.prop = b;
	}
	
	public List<B> getList() {
		return list;
	}

	public void setList(List<B> list) {
		this.list = list;
	}

	public Set<B> getSet() {
		return set;
	}

	public void setSet(Set<B> set) {
		this.set = set;
	}

	public Map<String, B> getMapValue() {
		return mapValue;
	}

	public void setMapValue(Map<String, B> mapValue) {
		this.mapValue = mapValue;
	}	

	public B[] getBs() {
		return bs;
	}

	public void setBs(B[] bs) {
		this.bs = bs;
	}
}
