package it.cnr.isti.hpc.dexter.spot;

import java.util.Scanner;

import it.cnr.isti.hpc.io.reader.RecordParser;


public class SpotSrcTarget {
	String spot;
	int src;
	int target;

	// entityFrequency
	int entityFrequency = 1;
		
	public SpotSrcTarget(){
		
	}
	
	public boolean hasSameSpot(SpotSrcTarget sstf){
		return spot.equals(sstf.getSpot());
	}
	
	public void incrementEntityFrequency(){
		entityFrequency++;
	}
	
	public int getEntityFrequency(){
		return entityFrequency;
	}

	/**
	 * @return the spot
	 */
	public String getSpot() {
		return spot;
	}

	/**
	 * @param spot the spot to set
	 */
	public void setSpot(String spot) {
		this.spot = spot;
	}

	/**
	 * @return the src
	 */
	public int getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(int src) {
		this.src = src;
	}

	/**
	 * @return the target
	 */
	public int getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(int target) {
		this.target = target;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spot == null) ? 0 : spot.hashCode());
		result = prime * result + target;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpotSrcTarget other = (SpotSrcTarget) obj;
		if (spot == null) {
			if (other.spot != null)
				return false;
		} else if (!spot.equals(other.spot))
			return false;
		if (target != other.target)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return spot + "\t" +src + " -> "
				+ target + " \t(entity freq = "+entityFrequency + " )";
	}
	
	public static class Parser implements RecordParser<SpotSrcTarget>{

		
		public SpotSrcTarget decode(String record) {
			SpotSrcTarget rec = new SpotSrcTarget();
			Scanner scanner = new Scanner(record).useDelimiter("\t");
			rec.setSpot(scanner.next());
			rec.setSrc(scanner.nextInt());
			rec.setTarget(scanner.nextInt());
			return rec;
		}

		
		public String encode(SpotSrcTarget r) {
			return r.spot+"\t"+r.src+"\t"+r.target;
		}
		
	}
	

	
	
	
	
	

}
