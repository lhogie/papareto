/**
 *  This file is part of Papareto.
 *	
 *  Papareto is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Papareto is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Papareto.  If not, see <http://www.gnu.org/licenses/>. *
 */

package cnrs.i3s.papareto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.gui.MonitorPanel;
import toools.StopWatch;
import toools.StopWatch.UNIT;
import toools.collections.Lists;
import toools.gui.Utilities;
import toools.io.file.RegularFile;
import toools.thread.MultiThreadProcessing;

public class Population<E, R> implements Serializable
{
	private static class IndividualList<E> extends ArrayList<Individual<E>>
	{
		public void shrinkTo(int targetSize)
		{
			removeRange(targetSize, size());
		}
	}

	private final IndividualList<E> individualList = new IndividualList<>();
	private Representation<E, R> representation;
	private final List<MutationOperator<R>> mutationOperators = new ArrayList<>();
	private final List<CrossoverOperator<R>> crossoverOperators = new ArrayList<>();
	private final List<Evaluator<E, R>> evaluators = new ArrayList<>();

	// parameters of the evolutionary algorithm
	private double offSpringRatio = 1;
	private boolean allowsDuplicates = true;
	private Random random = new Random();
	private boolean allowsAsynchronousUpdates = false;

	private int nbBirths = 0;
	private int numberOfRejectedDuplicates = 0;

	// an history of how the best fitness has evolved along the iterations
	private final FitnessHistory fitnessHistory = new FitnessHistory();

	private final List<PopulationListener<E, R>> listeners = new ArrayList<>();

	public List<MutationOperator<R>> getMutationOperators()
	{
		return mutationOperators;
	}

	public List<CrossoverOperator<R>> getCrossoverOperators()
	{
		return crossoverOperators;
	}

	public Representation<E, R> getRepresentation()
	{
		return representation;
	}

	public void setRepresentation(Representation<E, R> representation)
	{
		if (representation == null)
			throw new IllegalArgumentException(
					"you cannot use the null representation. Instead use 'new NoRepresentation<>()' to make object represent by themselves");

		this.representation = representation;
	}

	public List<Evaluator<E, R>> getEvaluators()
	{
		return evaluators;
	}

	public Individual<E> getBestIndividual()
	{
		return getIndividualAt(0);
	}

	public Individual<E> getIndividualAt(int i)
	{
		return individualList.get(i);
	}

	public Individual<E> add(E object)
	{
		if (object == null)
			throw new NullPointerException();

		Fitness f = computeFitness(object);
		Individual<E> i = new Individual<E>(object, f, Collections.EMPTY_LIST);
		insertIndividual(i);
		return i;
	}

	public int getSize()
	{
		return individualList.size();
	}

	public boolean isEmpty()
	{
		return individualList.isEmpty();
	}

	/*
	 * TODO: use binary search algorithm for a log(n) insertion instead of n
	 */
	public int insertIndividual(Individual<E> e)
	{
		if (e == null)
			throw new NullPointerException();

		// if the fitness is worse than any other individual
		if (isEmpty() || e.fitness.compareTo(getIndividualAt(getSize() - 1).fitness) < 0)
		{
			individualList.add(e);
			return individualList.size() - 1;
		}
		else
		{
			int sz = getSize();

			for (int i = 0; i < sz; ++i)
			{
				if (e.fitness.compareTo(getIndividualAt(i).fitness) >= 0)
				{
					individualList.add(i, e);
					return i;
				}
			}

			throw new IllegalStateException();
		}
	}

	public FitnessHistory getFitnessHistory()
	{
		return fitnessHistory;
	}

	public double getOffspringRatio()
	{
		return offSpringRatio;
	}

	public void setOffspringRatio(double offspringRatio)
	{
		if (offspringRatio < 0)
			throw new IllegalArgumentException("offspringSize must be >= 0");

		this.offSpringRatio = offspringRatio;
	}

	public int getNumberOfGenerations()
	{
		return fitnessHistory.size();
	}

	public int getNumberOfBirths()
	{
		return nbBirths;
	}

	public int getNumberOfRejectedDuplicates()
	{
		return numberOfRejectedDuplicates;
	}

	public void expansion(final int targetSize)
	{
		if (targetSize < getSize())
			throw new IllegalArgumentException("can't expand to a lower size");

		final List<Individual<E>> children = new ArrayList<>();

		new MultiThreadProcessing()
		{

			@Override
			protected void runThread(int rank, List<Thread> threads) throws Throwable
			{
				while (getSize() + children.size() < targetSize)
				{
					Individual<E> child = createNewChild();

					if (child != null)
					{
						if (allowsDuplicates || ( ! children.contains(child)
								&& ! individualList.contains(child)))
						{
							synchronized (this)
							{
								if (allowsAsynchronousUpdates
										&& participateToAsynchronousUpdating(child))
								{
									insertIndividual(child);
								}
								else
								{
									children.add(child);
								}
							}
						}
						else
						{
							++numberOfRejectedDuplicates;
						}
					}
				}
			}
		};

		// some children may not have been added asynchronously
		for (Individual<E> c : children)
		{
			insertIndividual(c);
		}
	}

	protected boolean participateToAsynchronousUpdating(Individual<E> i)
	{
		return true;
	}

	private Individual<E> createNewChild()
	{
		// randomly picks up 2 parents (may be the same one)
		Individual<E> p1 = binaryTournament();
		Individual<E> p2 = binaryTournament();

		// crossover
		CrossoverOperator<R> crossoverOperator = Lists.pickRandomElement(
				getCrossoverOperators(),
				Operator.getUsageProbabilities(getCrossoverOperators()), random);
		R rp1 = representation.fromObject(p1.object);
		R rp2 = representation.fromObject(p2.object);
		R rchild = crossoverOperator.crossover(rp1, rp2, random);

		if (rchild == null)
		{
			return null;
		}
		else
		{
			if (rchild == rp1 || rchild == rp2)
				throw new IllegalStateException(
						"a crossover operator is not allowed to return one of the two ancestors");

			List<Operator> operators = new ArrayList<Operator>();
			operators.add(crossoverOperator);

			if ( ! mutationOperators.isEmpty())
			{
				operators.addAll(mutate(rchild));
			}

			++nbBirths;

			E child = representation.toObject(rchild);
			Fitness fitness = computeFitness(child);
			return new Individual<E>(child, fitness, operators);
		}
	}

	private Collection<? extends Operator> mutate(R child)
	{
		List<Operator> operators = new ArrayList<Operator>();

		MutationOperator<R> mutationOperator = Lists.pickRandomElement(mutationOperators,
				Operator.getUsageProbabilities(mutationOperators), random);

		if (random.nextDouble() < mutationOperator.getProbability())
		{
			mutationOperator.mutate(child, random);
			operators.add(mutationOperator);
		}

		return operators;
	}

	public static <E, R> long saveToDisk(Population<E, R> p, RegularFile outFile)
			throws FileNotFoundException, IOException
	{
		StopWatch sw = new StopWatch(UNIT.ms);
		ObjectOutputStream oos = new ObjectOutputStream(outFile.createWritingStream());
		oos.writeObject(p);
		oos.close();
		return sw.getElapsedTime();
	}

	public static <E, R> Population<E, R> loadFromDisk(RegularFile inFile)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(inFile.createReadingStream());
		Population<E, R> p = (Population<E, R>) ois.readObject();
		ois.close();
		return p;
	}

	public void checkpoint(RegularFile file) throws FileNotFoundException, IOException
	{
		saveToDisk(this, file);
	}

	public static <E, R> Population<E, R> restore(RegularFile file)
			throws ClassNotFoundException, IOException
	{
		return loadFromDisk(file);
	}

	public List<E> getBestIndividuals()
	{
		List<E> l = new ArrayList<>();
		double bestFitness = individualList.get(0).fitness.combine();

		for (int i = 0;; ++i)
		{
			Individual<E> e = individualList.get(i);

			if (e.fitness.combine() == bestFitness)
			{
				l.add(e.object);
			}
			else
			{
				return l;
			}
		}
	}

	private Individual<E> binaryTournament()
	{
		Individual<E> i1 = getIndividualAt(random.nextInt(getSize()));
		Individual<E> i2 = getIndividualAt(random.nextInt(getSize()));
		return i1.fitness.compareTo(i2.fitness) > 0 ? i1 : i2;
	}

	public void selection(int targetSize)
	{
		for (Individual<E> i : individualList.subList(targetSize, getSize()))
		{
			for (Operator o : i.operators)
			{
				o.nbFailure++;
			}
		}

		individualList.shrinkTo(targetSize);

		for (Individual<E> i : individualList)
		{
			for (Operator o : i.operators)
			{
				o.nbSuccess++;
			}
		}
	}

	public double makeNewGeneration()
	{
		// make a new generation with the same size
		return makeNewGeneration(getSize());
	}

	public double makeNewGeneration(int targetSize)
	{
		Individual<E> initialBest = getBestIndividual();
		int sizeExpanded = (int) Math.max(getSize() + 1,
				targetSize * (1 + offSpringRatio));
		expansion(sizeExpanded);
		selection(targetSize);
		Individual<E> best = getBestIndividual();
		fitnessHistory.add(best.fitness);
		double improvement = best.fitness.combine() - initialBest.fitness.combine();

		for (PopulationListener<E, R> l : listeners)
		{
			l.newIteration(this, improvement);
		}

		return improvement;
	}

	public List<PopulationListener<E, R>> getPopulationListeners()
	{
		return listeners;
	}

	public void monitor()
	{
		MonitorPanel p = new MonitorPanel(this);
		Utilities.displayInJFrame(p, "Papareto population monitor");
	}

	public void evolve(TerminationCondition<E, R> c)
	{
		while ( ! c.completed(this))
		{
			makeNewGeneration();
		}

		for (PopulationListener<E, R> l : listeners)
		{
			l.completed(this);
		}
	}

	@Override
	public String toString()
	{
		return "NbGeneration=" + getNumberOfGenerations() + ", nbIndividuals="
				+ individualList.size() + ", best fitness="
				+ getBestIndividual().getFitness();
	}

	public boolean isAllowDuplicates()
	{
		return allowsDuplicates;
	}

	public void setAllowDuplicates(boolean allowDuplicates)
	{
		this.allowsDuplicates = allowDuplicates;
	}

	public boolean isAllowsAsynchronousUpdates()
	{
		return allowsAsynchronousUpdates;
	}

	public void setAllowsAsynchronousUpdates(boolean allowsAsynchronousUpdates)
	{
		this.allowsAsynchronousUpdates = allowsAsynchronousUpdates;
	}

	public Fitness computeFitness(E e)
	{
		if (evaluators.isEmpty())
			throw new IllegalStateException(
					"fitness cannot be computed if no evaluator have been defined");

		int nbEvaluators = evaluators.size();
		Fitness values = new Fitness(nbEvaluators);

		for (int i = 0; i < nbEvaluators; ++i)
		{
			values.elements[i] = evaluators.get(i).evaluate(e, this);
		}

		return values;
	}

	public void merge(Population<E, R> p)
	{
		for (Individual<E> e : p.individualList)
		{
			insertIndividual(e);
		}

		for (Evaluator<E, R> e : p.evaluators)
		{
			if ( ! evaluators.contains(e))
			{
				evaluators.add(e);
			}
		}

		for (MutationOperator<R> o : p.mutationOperators)
		{
			if ( ! mutationOperators.contains(o))
			{
				mutationOperators.add(o);
			}
		}

		for (CrossoverOperator<R> o : p.crossoverOperators)
		{
			if ( ! crossoverOperators.contains(o))
			{
				crossoverOperators.add(o);
			}
		}

		representation = p.representation;
	}

	public static <E extends Serializable, R> Population<E, R> merge(
			Collection<Population<E, R>> populations)
	{
		Population<E, R> r = new Population<E, R>();

		for (Population<E, R> p : populations)
		{
			r.merge(p);
		}

		return r;
	}

}
