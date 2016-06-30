package enhems;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import enhems.components.ImagePanel;


public class EnhemsDataModel implements DataModel {

	private List<DataListener> listeners = new ArrayList<>();
	
	private  HashMap<String, List<EnhemsGraph>> graphs = new HashMap<>();
	
	private String selectedRoom;
	private Set<String> rooms;
	private String setPoint;
	private String FCspeed;
	private String temperature;
	private String co2;
	private String humidity;
	private String medium;
	private boolean opMode;
	private boolean systemOn;
	
	private String currentMeassUnit;
	private String currentMeassPeriod;
	
	public EnhemsDataModel(String[] units) {
		rooms = new LinkedHashSet<>(Arrays.asList(units));
		listeners = new ArrayList<>();
		currentMeassUnit = GraphCodes.temperature;
		currentMeassPeriod = GraphCodes.dayInterval;
	}

	public void refreshData() {
		
		ServerService.executeRequest(new ServerRequest() {
			private String[] data;
			public void execute() {
				data = ServerService.GetCurrentValuesData(selectedRoom);
			}
			public void afterExecution() {
				//data[0] is empty, look at the server side!
				graphs.clear();
				opMode = true;
		        systemOn = true;
		        if (data[5].equals("---")) {
		            opMode = false;
		        }
		        if (data[6].equals("---")) {
		            systemOn = false;
		        }
		        temperature = data[1];
		        humidity = data[2];
		        co2 = data[3];
		        medium = data[7];
		        
		        if (opMode) {
		        	setPoint = data[4];
		        	FCspeed = data[8];

		        } else {
		        	setPoint = "--°C";
		        	FCspeed = "--";
		        }
		        
		        setPoint = data[4];
		        FCspeed = data[8];
		        
		        fireAllListeners();
		        //system status notification
//		        if (systemOn) {
//		            mSystemStatus.setText("Sustav radi");
//		            mSystemStatus.setTextColor(Color.GREEN);
//		        } else {
//		            mSystemStatus.setText("Sustav ne radi");
//		            mSystemStatus.setTextColor(Color.RED);
//		        }
				
			}
		});
	}

	
	/**
	 * Returns {@link ImagePanel} of currently selected graph on interface.
	 * This method handles caching of graphs.
	 * 
	 * @return
	 */
	public  ImagePanel getSelectedGraph() {		
		
		if(selectedRoom == null) {
			try {
				return new ImagePanel(ImageIO.read(
						Utilities.class.getResource("res/icons/enhemsBig.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!graphs.containsKey(selectedRoom)) {
			graphs.put(selectedRoom, new ArrayList<>());
		}
		
		String graphName = currentMeassUnit + " " + currentMeassPeriod;
		//if graph already exists in repo
		List<EnhemsGraph> list = graphs.get(selectedRoom);
		for(EnhemsGraph graph : list) {
			if(graph.getName().equals(graphName)) {
				return graph.getImagePanel();
			}
		}
		
		//setting loading icon 
		ImageIcon graphLoaderIcon = new ImageIcon(
				Utilities.class.getResource("res/icons/graph_loader.gif"));
		EnhemsGraph graph = new EnhemsGraph(graphName, graphLoaderIcon.getImage());
		list.add(graph);
				
		//sending request for graph
		ServerService.executeRequest(new ServerRequest() {
			Image tempImage;
			public void execute() {
				
				//used to remove flickering effect when changing images
//				try {
//					Thread.sleep(150);
//				} catch (Exception ignorable) { }
				
				String[] data = graphName.split("\\s");
				tempImage = ServerService.getGraph(data[0], data[1], selectedRoom);
			}
			public void afterExecution() {
				graph.getImagePanel().setImage(tempImage);
			}
		});	
		
		return graph.getImagePanel();
	}
	
	
	@Override
	public void addListener(DataListener listener) {
		listeners.add(listener);
	}
	@Override
	public void removeListener(DataListener listener) {
		listeners.remove(listener);
	}
	@Override
	public void fireAllListeners() {
		for(DataListener listener : listeners) {
			listener.dataChanged();
		}
	}

	public void setCurrentMeassUnit(String currentMeassUnit) {
		this.currentMeassUnit = currentMeassUnit;
		fireAllListeners();
	}
	
	public void setCurrentMeassPeriod(String currentMeassPeriod) {
		this.currentMeassPeriod = currentMeassPeriod;
		fireAllListeners();
	}

	public String getTemperature() {
		return temperature;
	}

	public String getCo2() {
		return co2;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getSetPoint() {
		return setPoint;
	}

	public void setSetPoint(String setPoint) {
		this.setPoint = setPoint;
		fireAllListeners();
	}

	public String getFCspeed() {
		return FCspeed;
	}

	public void setFCspeed(String fCspeed) {
		FCspeed = fCspeed;
		fireAllListeners();
	}

	public boolean isOpMode() {
		return opMode;
	}

	public String getSelectedRoom() {
		return selectedRoom;
	}

	public void setSelectedRoom(String selectedRoom) {
		this.selectedRoom = selectedRoom;
		refreshData();
		fireAllListeners();
	}

	public String getCurrentMeass() {
		switch (currentMeassUnit) {
		case GraphCodes.temperature:
			return temperature;
		case GraphCodes.humidity:
			return humidity;
		case GraphCodes.co2:
			return co2;
		default:
			return "---";
		}
	}

	public Set<String> getRooms() {
		return rooms;
	}

	public String getMedium() {
		return medium;
	}

}
