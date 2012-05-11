package capps.gp.gppopulations; 

import java.lang.IllegalAccessException;
import java.lang.InstantiationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import capps.gp.gpcreatures.GPCreature;

import capps.gp.gpexceptions.InvalidFitnessException;

import capps.gp.gpglobal.GPConfig;

public class NonSpatialTournamentPop extends GPPopulation {
	private final int POPSIZE;
	private double[] probDistro; 
	private List<GPCreature> currentPop; 

	public NonSpatialTournamentPop() 
			throws InstantiationException, IllegalAccessException{
		super(); 

		this.POPSIZE = GPConfig.getPopSize(); 

		this.currentPop = new ArrayList<GPCreature>(); 

		for (int i = 0; i < POPSIZE; i++) {
			GPCreature c = CREATURE_TYPE.newInstance(); 
			c.setId(i); 
			currentPop.add(c); 
		}
		/**Pre-compute Prob(selection) = 2^(-RANK)*/
		probDistro = new double[GPConfig.getTournySize()]; 
		double cumProb = 0.0; 
		double curProb = 1.0; 
		for (int i = 0; i < probDistro.length; i++) {
			curProb = curProb/2.0; 
			cumProb += curProb; 
			probDistro[i] = cumProb; 
		}

		/**Normalize: last entry of cumulative distro must be 1.0*/
		probDistro[probDistro.length-1] = 1.0; 
	}

	@Override
	public void evolveNextGeneration() {
		saveGenInfo(); 
		List<GPCreature> newGen = new ArrayList<GPCreature>(); 
		for (GPCreature c: currentPop) {
			newGen.add(getReplacement(c)); 
		}
		for (GPCreature c: newGen) 
			c.invalidateFitness(); 
		this.setNewestGeneration(newGen); 	
		 
		++numGensSoFar; 
	}

	@Override
	public List<GPCreature> getNewestGeneration() {
		return currentPop;
	}

	@Override
	public void computeFitnesses() {
		for (GPCreature c: currentPop) {
			c.computeFitness(); 
		}
	}

	@Override
	protected GPCreature getReplacement(GPCreature current) {
		/**First see if crossover occurs for this creature*/
		double randDouble = RANDGEN.nextDouble(); 
		if (randDouble >= GPConfig.getProbCrossover())
			return current; //do nothing in this case

		/**Randomly select TOURNY_SIZE-1 opponents*/
		List<GPCreature> pool = new ArrayList<GPCreature>();
		pool.add(current); 
		int TOURNY_SIZE = GPConfig.getTournySize();
		for (int i = 0; i < TOURNY_SIZE-1; i++) {
			int randIndex = RANDGEN.nextInt(currentPop.size()); 
			pool.add(currentPop.get(randIndex)); 
		}

		/**Sort by fitness in descending order*/
		java.util.Collections.sort(pool);
		java.util.Collections.reverse(pool); 
		try {
			assert(pool.get(0).getFitness() >= pool.get(1).getFitness()):
				"NonSpatialTournamentPop: tournament pool not sorted."; 
		}
		catch (InvalidFitnessException e) {}


		randDouble = RANDGEN.nextDouble();
		GPCreature winner = null;

		/**Randomly select a winner*/
		for (int i = 0; i < probDistro.length; i++) {
			if (randDouble < probDistro[i]) {
				winner = pool.get(i); 
				break; 
			}
		}
		assert (winner != null) : 
			"NonSpatialTournamentPop: Failed to select a tournament winner"; 
		
		/**Randomly choose creature to mate with the winner, return offspring*/
		GPCreature randomMate = null; 
		do { //Don't allow winner to reproduce with itself
			randomMate = pool.get(RANDGEN.nextInt(pool.size())); 
		} while (randomMate.getId() == winner.getId()); 

		/**Randomly choose which 'base' creature to return*/
		//Meh seems to get better results when we keep the winner.
		/*int randChoice = RANDGEN.nextInt() % 2; 
		return (randChoice == 0 ? winner.getOffspring(randomMate) :
				randomMate.getOffspring(winner)); */

		GPCreature offspring = winner.getOffspring(randomMate); 
		offspring.setId(current.getId()); 
		return offspring; 
	}

	@Override
	public int getPopSize() {
		return POPSIZE;
	}

	@Override
	public List<String> getLongGenInfo() throws InvalidFitnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewestGeneration(List<? extends GPCreature> creatures) {
		this.currentPop = (List<GPCreature>)creatures; 
	}


}
