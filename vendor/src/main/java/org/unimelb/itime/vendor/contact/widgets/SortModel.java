package org.unimelb.itime.vendor.contact.widgets;

public class SortModel {

	private String name;   //显示的数
	private String sortLetters;  //显示数据拼音的首字母
	private String id;// uid

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
