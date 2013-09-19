package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.spot.filter.Filter;
import it.cnr.isti.hpc.io.reader.RecordReader;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		RecordReader<SpotSrcTarget> reader = new RecordReader<SpotSrcTarget>(spotSrcTargetFile, new SpotSrcTarget.Parser());
;		spotSrcTargetIterator = reader.iterator();
		currentSST = spotSrcTargetIterator.next();
		RecordReader<SpotFrequency> reader2 = new RecordReader<SpotFrequency>(spotDocFreqFile, new SpotFrequency.Parser());
		spotFrequencyIterator = reader2.iterator();
		currentSF = spotFrequencyIterator.next();
		
	}

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
			while (spotSrcTargetIterator.hasNext() && next.hasSameSpot(currentSST)) {
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
		assert(currentSF.getSpot().equals(spot));
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

	public boolean isFilter(Spot spot) {
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

}