package com.renjie;

public class Node {
	private static final String TAG = "Node";

	private String name;
	private String code;
	private String id;
	private int level;
	private String param1;
	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	private boolean isParent;
	private boolean opened;
	private boolean loadChildren;

	public boolean isLoadChildren() {
		return loadChildren;
	}

	public void setLoadChildren(boolean loadChildren) {
		this.loadChildren = loadChildren;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String toString(){
		return "code="+this.code+",name="+this.name+",id="+this.id+",level="+level
				+",isParent="+isParent+",opened="+opened+",loadChildren="+loadChildren;
	}
}
