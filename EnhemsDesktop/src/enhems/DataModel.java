package enhems;

public interface DataModel {

	public void addListener(DataListener listener);
	public void removeListener(DataListener listener);
	public void fireAllListeners();
	
}
