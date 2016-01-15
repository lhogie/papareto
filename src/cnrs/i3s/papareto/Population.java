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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import toools.collections.Lists;
import toools.gui.Utilities;
import toools.math.MathsUtilities;
import toools.thread.MultiThreadProcessing;
import cnrs.i3s.papareto.gui.MonitorPanel;

public class Population<E>
{
	private final ArrayList<Individual<E>> individualList = new ArrayList<Individual<E>>();
	private final List<MutationOperator<E>> mutationOperators = new ArrayList();
	private final List<CrossoverOperator<E>> crossoverOperators = new ArrayList();
	private final Method[] fitnessFunctions;
	private final double[] objectiveWeights;

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

	private final List<PopulationListener<E>> listeners = new ArrayList();

	public Population(E... initialIndividuals)
	{
		this(Arrays.asList(initialIndividuals));
	}

	public Population(Collection<E> initialIndividuals)
	{
		if (initialIndividuals.isEmpty())
			throw new IllegalArgumentException("the initial population must contain at least 1 element");

		{
			List<Method> methods = new ArrayList<Method>();

			for (Method m : getClass().getDeclaredMethods())
			{
				if (m.getName().startsWith("computeFitness"))
				{
					m.setAccessible(true);
					methods.add(m);
				}
			}

			fitnessFunctions = methods.toArray(new Method[0]);
			objectiveWeights = new double[fitnessFunctions.length];
			Arrays.fill(objectiveWeights, 1);

			if (fitnessFunctions.length == 0)
				throw new IllegalStateException(
						"no fitness function defined. Please define at least one method with the following signature in your Population class: double computeFitness(E)");
		}

		for (E m : initialIndividuals)
		{
			add(m);
		}

		fitnessHistory.add(get(0).fitness);

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
		add(i);
	}

	public int getSize()
	{
		return individualList.size();
	}

	public boolean isEmpty()
	{
		return individualList.isEmpty();
	}

	public int add(Individual<E> e)
	{
		if (e == null)
			throw new NullPointerException();

		if (isEmpty() || compareObjectiveValues(e.fitness, get(getSize() - 1).fitness) < 0)
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
				voteForF2 += objectiveWeights[i];
			}
			else if (f1[i] > f2[i])
			{
				voteForF1 += objectiveWeights[i];
			}
		}

		return MathsUtilities.compare(voteForF1, voteForF2);
	}

	public FitnessHistory getFitnessHistory()
	{
		return fitnessHistory;
	}

	public List<MutationOperator<E>> getMutationOperators()
	{
		return mutationOperators;
	}

	public List<CrossoverOperator<E>> getCrossoverOperators()
	{
		return crossoverOperators;
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

		new MultiThreadProcessing() {

			@Override
			protected void runThread(int rank, List<Thread> threads) throws Throwable
			{
				while (getSize() + children.size() < n)
				{
					Individual<E> child = createNewChild();

					if (child != null)
					{
						if (allowDuplicates || (!children.contains(child) && !individualList.contains(child)))
						{
							synchronized (this)
							{
								if (allowsAsynchronousUpdates && participateToAsynchronousUpdating(child))
								{
									add(child);
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
			add(c);
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
		CrossoverOperator<E> crossoverOperator = Lists.pickRandomElement(crossoverOperators, getWeight(crossoverOperators), random);
		E child = crossoverOperator.crossover(p1, p2, this, random);

		if (child == null)
		{
			return null;
		}
		else
		{
			if (child == p1 || child == p2)
				throw new IllegalStateException("a crossover operator is required to return a new instance");

			List<Operator> operators = new ArrayList<Operator>();
			operators.add(crossoverOperator);
			operators.addAll(mutate(child));
			++nbBirths;

			double[] fitness = computeFitnessValues(child);
			return new Individual<E>(child, fitness, operators);
		}
	}

	private Collection<? extends Operator> mutate(E child)
	{
		List<Operator> operators = new ArrayList<Operator>();

		// mutation
		if (!mutationOperators.isEmpty())
		{
			MutationOperator<E> mutationOperator = Lists.pickRandomElement(mutationOperators, getWeight(mutationOperators), random);

			if (random.nextDouble() < mutationOperator.getProbability())
			{
				mutationOperator.mutate(child, Population.this, random);
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
		boolean improvement = compareObjectiveValues(best.fitness, previousBest.fitness) > 0;
		fitnessHistory.add(best.fitness);

		for (PopulationListener<E> l : listeners)
		{
			l.newIteration(this, improvement);
		}

		return improvement;
	}

	public List<PopulationListener<E>> getPopulationListeners()
	{
		return listeners;
	}

	public double[] getObjectiveWeights()
	{
		return objectiveWeights;
	}

	public void monitor()
	{
		MonitorPanel p = new MonitorPanel(this);
		Utilities.displayInJFrame(p, "Drwin population monitor");
	}

	public void evolve(TerminationCondition<E> c)
	{
		while (!c.completed(this))
		{
			makeNewGeneration();
		}

		for (PopulationListener<E> l : listeners)
		{
			l.completed(this);
		}
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
		double[] values = new double[fitnessFunctions.length];

		for (int i = 0; i < fitnessFunctions.length; ++i)
		{
			try
			{
				values[i] = (Double) fitnessFunctions[i].invoke(this, e);
			}
			catch (Throwable error)
			{
				throw new IllegalStateException(error);
			}
		}

		return values;
	}
}
