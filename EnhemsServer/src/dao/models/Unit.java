package dao.models;

public class Unit {

	private Integer id;
	private String name;
	
	public Unit() {
	}
	
	public Unit(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode()+id.hashCode();
	}
	
	@Override
	public String toString() {
		return id+" "+name;
	}
}
