package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.Filter;
import it.cnr.isti.hpc.io.reader.RecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * SpotReader class iterates over a list of spots given two files:
 * <ul>
 * <li>a <strong>spotSrcTargetFile</strong> containing a list of spots extracted
 * from the anchors in the wikipedia dump. The format of each line in the file
 * is: <br>
 * <code>
 * spot <tab> source-id <tab> target-id 
 * </code> <br>
 * where <code>source-id</code> is the integer id of the entity containing the
 * anchor, while <code>target-id</code> is the target of the anchor. The file is
 * lexicographically sorted by the spot, source-id and then target-id.</li>
 * <li>a <strong>spotDocFreqFile </strong> containing the document frequency
 * (how many articles contain the spot as pure text) for each spot (each line
 * containing <code> spot <tab> doc frequency </code>). The file is sorted by
 * the spot.</li>
 * </ul>
 * 
 * The SpotReader merges the informations in the two files and provides an
 * iterator over a list of Spot objects.
 * 
 * @see Spot
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 22, 2013
 */
public class SpotReader implements Iterator<Spot> {
	BufferedReader br = null;
	SpotSrcTarget currentSST;
	SpotFrequency currentSF;
	Iterator<SpotSrcTarget> spotSrcTargetIterator;
	Iterator<SpotFrequency> spotFrequencyIterator;

	List<Filter<Spot>> filters = new ArrayList<Filter<Spot>>();
	boolean init = false;
	Spot next = null;

	public SpotReader(String spotSrcTargetFile, String spotDocFreqFile) {
		RecordReader<SpotSrcTarget> reader = new RecordReader<SpotSrcTarget>(
				spotSrcTargetFile, new SpotSrcTargetParser());
		;
		spotSrcTargetIterator = reader.iterator();
		currentSST = spotSrcTargetIterator.next();
		RecordReader<SpotFrequency> reader2 = new RecordReader<SpotFrequency>(
				spotDocFreqFile, new SpotFrequencyParser());
		spotFrequencyIterator = reader2.iterator();
		currentSF = spotFrequencyIterator.next();

	}

	/**
	 * Add a filter to the reader. The Iterator will return only Spot objects
	 * that are not filtered by the filters.
	 * 
	 * @param filter
	 *            - the filter to apply
	 * 
	 * @see Filter
	 */
	public void addFilter(Filter<Spot> filter) {
		filters.add(filter);
	}

	private Spot getNextSpot() {
		if (!spotSrcTargetIterator.hasNext())
			return null;
		List<SpotSrcTarget> sstf = new ArrayList<SpotSrcTarget>();
		sstf.add(currentSST);
		SpotSrcTarget next = spotSrcTargetIterator.next();
		Spot nextSpot = null;
		do {
			while (spotSrcTargetIterator.hasNext()
					&& next.hasSameSpot(currentSST)) {
				// equals returns true if next has the same entity target
				if (next.equals(currentSST))
					currentSST.incrementEntityFrequency();

				else {
					currentSST = next;
					sstf.add(currentSST);
				}
				next = spotSrcTargetIterator.next();
			}
			currentSST = next;
			nextSpot = generateSpot(sstf);
		} while (nextSpot != null && isFilter(nextSpot));
		return nextSpot;

	}

	private Spot generateSpot(List<SpotSrcTarget> sstf) {
		int link = 0;
		String spot = sstf.get(0).getSpot();

		List<Entity> entities = new ArrayList<Entity>();

		for (SpotSrcTarget e : sstf) {
			link += e.getEntityFrequency();
			if (!Entity.isDisambiguation(e.getTarget())) {
				// adding only if it is not a disambiguation
				Entity entity = new Entity(e.getTarget(),
						e.getEntityFrequency());

				entities.add(entity);
			}
		}
		int freq = currentSF.getFreq();
		assert (currentSF.getSpot().equals(spot));
		currentSF = spotFrequencyIterator.next();
		Spot s = new Spot(spot, entities, link, freq);
		return s;
	}

	public boolean hasNext() {
		if (!init)
			init();
		return (next != null);
	}

	public void init() {
		init = true;
		// entities = new HashSet<Integer>();
		// String[] elems = getNextLine();
		// if (elems == null) {
		// next = null;
		// return;
		// }
		// currentSpot = elems[0];
		// Integer eId = Integer.parseInt(elems[2]);
		// if (!Entity.isDisambiguation(eId) && eId != 0) {
		// entities.add(eId);
		// }
		// docFreq = Integer.parseInt(elems[3]);
		// try {
		next = getNextSpot();
		// } catch (Exception e) {
		// Spot.logger.error("reading data for spot {} ({})", currentSpot,
		// e.toString());
		// e.printStackTrace();
		// }
	}

	private boolean isFilter(Spot spot) {
		for (Filter<Spot> f : filters) {
			if (f.isFilter(spot))
				return true;
		}
		return false;
	}

	public Spot next() {
		if (!init)
			init();
		Spot t = next;
		// boolean finish = false;
		// while (!finish & next != null) {
		// try {
		next = getNextSpot();
		// finish = true;
		// } catch (Exception e) {
		// Spot.logger.error("reading data for spot {} ({})", currentSpot,
		// e.toString());
		// e.printStackTrace();
		// finish = false;
		// }
		// }
		//
		return t;
	}

	public void remove() {
		throw new UnsupportedOperationException();

	}

	public static class SpotSrcTarget {
		String spot;
		int src;
		int target;

		// entityFrequency
		int entityFrequency = 1;

		public SpotSrcTarget() {

		}

		public boolean hasSameSpot(SpotSrcTarget sstf) {
			return spot.equals(sstf.getSpot());
		}

		public void incrementEntityFrequency() {
			entityFrequency++;
		}

		public int getEntityFrequency() {
			return entityFrequency;
		}

		/**
		 * @return the spot
		 */
		public String getSpot() {
			return spot;
		}

		/**
		 * @param spot
		 *            the spot to set
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
		 * @param src
		 *            the src to set
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
		 * @param target
		 *            the target to set
		 */
		public void setTarget(int target) {
			this.target = target;
		}

		/*
		 * (non-Javadoc)
		 * 
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

		/*
		 * (non-Javadoc)
		 * 
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return spot + "\t" + src + " -> " + target + " \t(entity freq = "
					+ entityFrequency + " )";
		}

	}

	public static class SpotSrcTargetParser implements
			RecordParser<SpotSrcTarget> {

		public SpotSrcTarget decode(String record) {
			SpotSrcTarget rec = new SpotSrcTarget();
			Scanner scanner = new Scanner(record).useDelimiter("\t");
			rec.setSpot(scanner.next());
			rec.setSrc(scanner.nextInt());
			rec.setTarget(scanner.nextInt());
			return rec;
		}

		public String encode(SpotSrcTarget r) {
			return r.spot + "\t" + r.src + "\t" + r.target;
		}

	}

	/**
	 * Contains the text of a spot and its document frequency in the collection.
	 * 
	 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
	 * 
	 *         Created on Apr 29, 2013
	 */
	public static class SpotFrequency {
		String spot;
		int freq;

		public SpotFrequency() {

		}

		public SpotFrequency(String spot, int freq) {
			super();
			this.spot = spot;
			this.freq = freq;
		}

		/**
		 * @return the spot
		 */
		public String getSpot() {
			return spot;
		}

		/**
		 * @param spot
		 *            the spot to set
		 */
		public void setSpot(String spot) {
			this.spot = spot;
		}

		/**
		 * @return the freq
		 */
		public int getFreq() {
			return freq;
		}

		/**
		 * @param freq
		 *            the freq to set
		 */
		public void setFreq(int freq) {
			this.freq = freq;
		}

	}

	/**
	 * Parse a line containing the encoded version of a SpotFrequency object.
	 * 
	 * @see RecordParser
	 */
	public static class SpotFrequencyParser implements
			RecordParser<SpotFrequency> {

		public SpotFrequency decode(String record) {
			SpotFrequency rec = new SpotFrequency();
			Scanner scanner = new Scanner(record).useDelimiter("\t");
			rec.setSpot(scanner.next());
			rec.setFreq(scanner.nextInt());
			return rec;
		}

		public String encode(SpotFrequency r) {
			return r.spot + "\t" + r.freq;
		}
	}

}