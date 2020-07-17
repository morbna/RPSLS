package acs.boundaries.sub;

public class Location {

	private Double lat;
	private Double lng;

	public Location() {
	}

	public Location(Double lat, Double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public Double getlng() {
		return this.lng;
	}

	public Double getlat() {
		return this.lat;
	}

	public void setlng(Double lng) {
		this.lng = lng;
	}

	public void setlat(Double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}

}
