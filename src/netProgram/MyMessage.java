package netProgram;

import java.io.Serializable;
import java.util.ArrayList;

public class MyMessage implements Serializable {// 序列化&&反序列化
	public static final long serialVersionUID = 1l;
	public static final int MES_TYPE_PLAIN = 1;
	public static final int MES_TYPE_UPDATE_CLIENTLIST = 2;

	private String content;
	private ArrayList<String> clientList;
	private int mesType = -1;
	private boolean ifMass;
private String sendFrom,sendTo;
	
	public MyMessage(int mesType, boolean ifMass) {
		this.mesType = mesType;
		this.ifMass=ifMass;
	}

	public int getMesType() {
		return mesType;
	}

	public void setMesType(int mesType) {
		this.mesType = mesType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<String> getClientList() {
		return clientList;
	}

	public void setClientList(ArrayList<String> clientList) {
		this.clientList = clientList;
	}

	public boolean isIfMass() {
		return ifMass;
	}

	public void setIfMass(boolean ifMass) {
		this.ifMass = ifMass;
	}

	public String getSendFrom() {
		return sendFrom;
	}

	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
}
