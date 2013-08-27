package com.ais.sand.radiofm91;

public class News {
	private String tag = getClass().getSimpleName();
	String id;
	String type;
	String primarySource;
	String secondarySource;
	String startTime;
	String endTime;
	String mediaType;
	String path;
	String title;
	String description;
	String locationType;
	String roadName;
	String startPoint;
	String startPointLat, startPointLong;
	String endPoint;
	String endPointLat, endPointLong;

	public News(String id, String type, String primarySource,
			String secondarySource, String startTime, String endTime,
			String mediaType, String path, String title, String description,
			String locationType, String roadName, String startPoint,
			String startPointLat, String startPointLong, String endPoint,
			String endPointLat, String endPointLong) {
		this.id = id;
		this.type = type;
		this.primarySource = primarySource;
		this.secondarySource = secondarySource;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mediaType = mediaType;
		this.path = path;
		this.title = title;
		this.description = description;
		this.locationType = locationType;
		this.roadName = roadName;
		this.startPoint = startPoint;
		this.startPointLat = startPointLat;
		this.startPointLong = startPointLong;
		this.endPoint = endPoint;
		this.endPointLat = endPointLat;
		this.endPointLong = endPointLong;

	}

	@Override
	public String toString() {
		return id+","+type+","+primarySource+","+secondarySource+","+startTime+","+
				endTime+","+mediaType+","+path+","+title+","+description+","+locationType+","+
				roadName+","+startPoint+","+startPointLat+","+startPointLong+","+endPoint+endPointLat+","+endPointLong;
	}
}
