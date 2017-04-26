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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.gui.MonitorPanel;
import toools.collections.Lists;
import toools.gui.Utilities;
import toools.io.file.RegularFile;
import toools.math.MathsUtilities;
import toools.thread.MultiThreadProcessing;

public class Population<E, R> implements Serializable
{
	private final List<Individual<E>> individualList = new ArrayList<>();
	private final List<Evaluator<E, R>> evaluators = new ArrayList<>();
	private Representation<E, R> representation;

	// parameters of the evolutionary algorithm
	private double offSpringRatio = 1;
	private boolean allowDuplicates = true;
	private Random random = new Random();
	private boolean allowsAsynchronousUpdates = false;

	private int nbBirths = 0;
	private int numberOfRejectedDuplicates = 0;

	// an history of how the best fitness has evolved along the iterations of
	// the algorithm
	private final FitnessHistory fitnessHistory = new FitnessHistory();

	private final List<PopulationListener<E, R>> listeners = new ArrayList<>();

	
	public List<Individual<E>> getIndividualList()
	{
		return individualList;
	}
	
	public Representation<E, R> getRepresentation()
	{
		return representation;
	}

	public void setRepresentation(Representation<E, R> representation)
	{
		this.representation = representation;
	}

	public List<Evaluator<E, R>> getEvaluators()
	{
		return evaluators;
	}

	public Individual<E> get(int i)
	{
		return individualList.get(i);
	}

	public void add(E object)
	{
		if (object == null)
			throw new NullPointerException();

		double[] f = computeFitnessValues(object);
		Individual<E> i = new Individual<E>(object, f, Collections.EMPTY_LIST);
		addIndividual(i);
	}

	public int getSize()
	{
		return individualList.size();
	}

	public boolean isEmpty()
	{
		return individualList.isEmpty();
	}

	public int addIndividual(Individual<E> e)
	{
		if (e == null)
			throw new NullPointerException();

		if (isEmpty()
				|| compareObjectiveValues(e.fitness, get(getSize() - 1).fitness) < 0)
		{
			individualList.add(e);
			return individualList.size() - 1;
		}
		else
		{
			int sz = getSize();

			for (int i = 0; i < sz; ++i)
			{
				if (compareObjectiveValues(e.fitness, get(i).fitness) >= 0)
				{
					individualList.add(i, e);
					return i;
				}
			}

			throw new IllegalStateException();
		}
	}

	public int compareObjectiveValues(double[] f1, double[] f2)
	{
		int voteForF1 = 0, voteForF2 = 0;

		for (int i = 0; i < f1.length; ++i)
		{
			if (f1[i] < f2[i])
			{
				voteForF2 += evaluators.get(i).weight;
			}
			else if (f1[i] > f2[i])
			{
				voteForF1 += evaluators.get(i).weight;
			}
		}

		return MathsUtilities.compare(voteForF1, voteForF2);
	}

	public double computeCombinedFitness(double[] fitness)
	{
		double r = 0;

		for (int i = 0; i < fitness.length; ++i)
		{
			r += fitness[i] * evaluators.get(i).weight;
		}

		return r;
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

	public void expansion(final int n)
	{
		if (n < 1)
			throw new IllegalArgumentException("n must be > 1");

		if (n < getSize())
			throw new IllegalArgumentException("can't expand to a lower size");

		final List<Individual<E>> children = new ArrayList();

		new MultiThreadProcessing()
		{

			@Override
			protected void runThread(int rank, List<Thread> threads) throws Throwable
			{
				while (getSize() + children.size() < n)
				{
					Individual<E> child = createNewChild();

					if (child != null)
					{
						if (allowDuplicates || ( ! children.contains(child)
								&& ! individualList.contains(child)))
						{
							synchronized (this)
							{
								if (allowsAsynchronousUpdates
										&& participateToAsynchronousUpdating(child))
								{
									addIndividual(child);
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
			addIndividual(c);
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
				representation.getCrossoverOperators(),
				getWeight(representation.getCrossoverOperators()), random);
		R rp1 = representation.fromObject(p1.object);
		R rp2 = representation.fromObject(p2.object);
		R rchild = crossoverOperator.crossover(rp1, rp2, random);

		if (rchild == null)
		{
			return null;
		}
		else
		{
			if (rchild == p1 || rchild == p2)
				throw new IllegalStateException(
						"a crossover operator is required to return a new instance");

			List<Operator> operators = new ArrayList<Operator>();
			operators.add(crossoverOperator);
			operators.addAll(mutate(rchild));
			++nbBirths;

			E child = representation.toObject(rchild);
			double[] fitness = computeFitnessValues(child);
			return new Individual<E>(child, fitness, operators);
		}
	}

	public static <E, R> void saveToDisk(Population<E, R> p, RegularFile outFile)
			throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(outFile.createWritingStream());
		oos.writeObject(p);
		oos.close();
	}

	public static <E, R> Population<E, R> loadFromDisk(RegularFile inFile)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(inFile.createReadingStream());
		Population<E, R> p = (Population<E, R>) ois.readObject();
		ois.close();
		return p;
	}

	private Collection<? extends Operator> mutate(R child)
	{
		List<Operator> operators = new ArrayList<Operator>();
		List<MutationOperator<R>> mutationOperators = representation
				.getMutationOperators();

		// mutation
		if ( ! mutationOperators.isEmpty())
		{
			MutationOperator<R> mutationOperator = Lists.pickRandomElement(
					mutationOperators, getWeight(mutationOperators), random);

			if (random.nextDouble() < mutationOperator.getProbability())
			{
				mutationOperator.mutate(child, random);
				operators.add(mutationOperator);
			}
		}

		return operators;
	}

	public List<E> getBestSolutions()
	{
		List<E> l = new ArrayList<E>();
		double[] f = individualList.get(0).fitness;

		for (int i = 0;; ++i)
		{
			Individual<E> e = individualList.get(i);

			if (Arrays.equals(e.fitness, f))
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
		Individual<E> i1 = get(random.nextInt(getSize()));
		Individual<E> i2 = get(random.nextInt(getSize()));
		return compareObjectiveValues(i1.fitness, i2.fitness) > 0 ? i1 : i2;
	}

	private <O extends Operator> double[] getWeight(List<O> operators)
	{
		double[] a = new double[operators.size()];

		for (int i = 0; i < a.length; ++i)
		{
			Operator o = operators.get(i);

			// if the operator was never used in the past
			if (o.numberOfFailure + o.success == 0)
			{
				// give it max opportunity
				a[i] = 1;
			}
			else
			{
				a[i] = operators.get(i).getSuccessRate();
			}
		}

		return a;
	}

	public void selection(int n)
	{
		for (Individual<E> i : individualList.subList(n, getSize()))
		{
			for (Operator o : i.operators)
			{
				o.numberOfFailure++;
			}
		}

		while (individualList.size() > n)
		{
			individualList.remove(individualList.size() - 1);
		}

		for (Individual<E> i : individualList)
		{
			for (Operator o : i.operators)
			{
				o.success++;
			}
		}
	}

	public boolean makeNewGeneration()
	{
		return makeNewGeneration(getSize());
	}

	public boolean makeNewGeneration(int size)
	{
		Individual<E> previousBest = get(0);
		expansion((int) MathsUtilities.round(size + size * offSpringRatio, 0));
		selection(size);
		Individual<E> best = get(0);
		boolean improvement = compareObjectiveValues(best.fitness,
				previousBest.fitness) > 0;
		fitnessHistory.add(best.fitness);

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
		Utilities.displayInJFrame(p, "Drwin population monitor");
	}

	public void evolve(TerminationCondition<E, R> c)
	{
		int generation = 0;

		while ( ! c.completed(this))
		{
			makeNewGeneration();
			++generation;

			if (saveToDisk(generation))
			{
				try
				{
					RegularFile f = new RegularFile(
							"population-" + generation + ".serialized");
					System.out.println("saving to " + f);
					saveToDisk(this, f);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		for (PopulationListener<E, R> l : listeners)
		{
			l.completed(this);
		}
	}

	protected boolean saveToDisk(int generation)
	{
		return false;
	}

	@Override
	public String toString()
	{
		return getNumberOfGenerations() + ": " + individualList.toString();
	}

	public boolean isAllowDuplicates()
	{
		return allowDuplicates;
	}

	public void setAllowDuplicates(boolean allowDuplicates)
	{
		this.allowDuplicates = allowDuplicates;
	}

	public boolean isAllowsAsynchronousUpdates()
	{
		return allowsAsynchronousUpdates;
	}

	public void setAllowsAsynchronousUpdates(boolean allowsAsynchronousUpdates)
	{
		this.allowsAsynchronousUpdates = allowsAsynchronousUpdates;
	}

	public double[] computeFitnessValues(E e)
	{
		if (evaluators.isEmpty())
			throw new IllegalStateException(
					"fitness cannot be computed if no evaluator have been defined");

		double[] values = new double[evaluators.size()];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = evaluators.get(i).evaluate(e, this);
		}

		return values;
	}

	public void merge(Population<E, R> p)
	{
		for (Individual<E> e : p.individualList)
		{
			addIndividual(e);
		}

		for (Evaluator<E, R> e : p.evaluators)
		{
			if ( ! evaluators.contains(e))
			{
				evaluators.add(e);
			}
		}

		representation = p.representation;
	}

	public static <E extends Serializable, R> Population<E, R> merge(
			Collection<Population<E, R>> populations, int targetSize)
	{
		Population<E, R> r = new Population<E, R>();

		for (Population<E, R> p : populations)
		{
			r.merge(p);
		}

		return r;
	}
}
