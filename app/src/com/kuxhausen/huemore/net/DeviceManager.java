package com.kuxhausen.huemore.net;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.RequestQueue;
import com.kuxhausen.huemore.net.hue.HubConnection;
import com.kuxhausen.huemore.network.ConnectionMonitor;
import com.kuxhausen.huemore.network.NetworkMethods;
import com.kuxhausen.huemore.network.OnConnectionStatusChangedListener;
import com.kuxhausen.huemore.state.Group;
import com.kuxhausen.huemore.state.api.BulbState;

import android.content.Context;
import android.util.Pair;

public class DeviceManager{
	
	private ArrayList<Connection> mConnections;
	private Context mContext;
	private Group selectedGroup;
	private String selectedGroupName;
	private ArrayList<OnConnectionStatusChangedListener> connectionListeners = new ArrayList<OnConnectionStatusChangedListener>();
	public ArrayList<OnStateChangedListener> brightnessListeners = new ArrayList<OnStateChangedListener>();
	private HashMap<String, NetworkBulb> bulbMap = new HashMap<String, NetworkBulb>();
	
	public DeviceManager(Context c){
		mContext = c;
		
		//load all connections from the database 
		mConnections = new ArrayList<Connection>();
		mConnections.addAll(HubConnection.loadHubConnections(c, this));
		
		onBulbsListChanged();
		
	}
	
	public void onDestroy() {
		for(Connection c : mConnections)
			c.onDestroy();
	}
	
	public Group getSelectedGroup(){
		return selectedGroup;
	}
	public String getSelectedGroupName(){
		return selectedGroupName;
	}
	
	public void onGroupSelected(Group group, Integer optionalBri, String groupName){
		selectedGroup = group;
		selectedGroupName = groupName;
		
		((HubConnection)mConnections.get(0)).onGroupSelected(selectedGroup,optionalBri,groupName);
	}
	
	public void addOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l){
		connectionListeners.add(l);
	}
	
	public void removeOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l){
		connectionListeners.remove(l);
	}
	
	public void onConnectionChanged(){
			for(OnConnectionStatusChangedListener l : connectionListeners)
				l.onConnectionStatusChanged();	
	}
	

	public interface OnStateChangedListener {
		public void onStateChanged();
	}
	
	/** announce brightness to any listeners **/
	public void onStateChanged(){
		for(OnStateChangedListener l : brightnessListeners){
			l.onStateChanged();
		}
	}
	public void registerBrightnessListener(OnStateChangedListener l){
		brightnessListeners.add(l);
	}
	
	public void removeBrightnessListener(OnStateChangedListener l){
		brightnessListeners.remove(l);
	}
	
	public Integer getBrightness(Group g){
		//TODO	
		return null;
	}
	
	public Integer getMaxBrightness(Group g){
		//TODO
		return ((HubConnection)mConnections.get(0)).getMaxBrightness();
	}
	
	/** doesn't notify listeners **/
	public void setBrightness(Group g, int brightness){
		//TODO
		((HubConnection)mConnections.get(0)).setBrightness(brightness, selectedGroup);
	}
	
	public void onBulbsListChanged(){
		for(Connection con : mConnections){
			ArrayList<NetworkBulb> conBulbs = con.getBulbs();
			for(NetworkBulb bulb: conBulbs){
				bulbMap.put(bulb.getUniqueId(), bulb);
			}
		}
	}
	
	public NetworkBulb getNetworkBulb(String bulbDeviceId) {
		return bulbMap.get(bulbDeviceId);
	}
	
	//TODO clean up/remove everything below this line
	
	public void transmit(int bulb, BulbState bs){
		((HubConnection)mConnections.get(0)).queue.add(new Pair<Integer,BulbState>(bulb,bs));
	}
	
	public RequestQueue getRequestQueue() {
		return ((HubConnection)mConnections.get(0)).getRequestQueue();
	}
}
