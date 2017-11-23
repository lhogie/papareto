/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 

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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.algo.LucEvolver;
import cnrs.i3s.papareto.gui.MonitorPanel;
import toools.Clazz;
import toools.StopWatch;
import toools.StopWatch.UNIT;
import toools.collections.RoundRobinIterator;
import toools.gui.Utilities;
import toools.io.file.RegularFile;

public class Population<E, R> implements Serializable, Iterable<Individual<E>>
{
	private static class RemoveRangeArrayList<T> extends ArrayList<T>
	{
		// augment the visibility to make it usable here
		@Override
		public void removeRange(int a, int b)
		{
			super.removeRange(a, b);
		}
	}

	// the sorted list (by combined fitness) of individuals in the population
	private final RemoveRangeArrayList<Individual<E>> individualList = new RemoveRangeArrayList<>();

	// the class of objects that is used to represent the elements in the
	// population
	private Representation<E, R> representation;

	private final List<Evaluator<E, R>> evaluators = new ArrayList<>();
	private Combiner combination = new LinearCombination();

	private Evolver<E, R> evolver = new LucEvolver<>();

	// an history of how the best fitness has evolved along the iterations
	private final FitnessMeasureHistory fitnessHistory = new FitnessMeasureHistory();

	// private final List<PopulationListener<E, R>> listeners = new
	// ArrayList<>();
	private transient FitnessCache<E> fitnessCache = null;

	public int size()
	{
		return individualList.size();
	}

	public void shrinkTo(int targetSize)
	{
		individualList.removeRange(targetSize, individualList.size());
	}

	/*
	 * TODO: use binary search algorithm for a log(n) insertion instead of n
	 */
	public synchronized int add(Individual<E> e)
	{
		if (e == null)
			throw new NullPointerException();

		int index = 0;

		while (index < individualList.size()
				&& e.fitness.getCombinedFitnessValue() < individualList.get(index).fitness
						.getCombinedFitnessValue())
		{
			++index;
		}

		individualList.add(index, e);
		return index;
	}

	public synchronized void removeIndividualAt(int i)
	{
		individualList.remove(i);
	}

	@Override
	public Iterator<Individual<E>> iterator()
	{
		return individualList.iterator();
	}

	public Evolver<E, R> getEvolver()
	{
		return evolver;
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

		FitnessMeasure f = computeFitness(object);
		Individual<E> i = new Individual<E>(object, f, Collections.EMPTY_LIST);
		add(i);
		return i;
	}

	public boolean isEmpty()
	{
		return individualList.size() == 0;
	}

	public FitnessMeasureHistory getFitnessHistory()
	{
		return fitnessHistory;
	}

	public int getNumberOfGenerations()
	{
		return fitnessHistory.size();
	}

	public long saveToDisk(RegularFile outFile) throws FileNotFoundException, IOException
	{
		StopWatch sw = new StopWatch(UNIT.ms);
		ObjectOutputStream oos = new ObjectOutputStream(outFile.createWritingStream());
		oos.writeObject(this);
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
		saveToDisk(file);
	}

	public static <E, R> Population<E, R> restore(RegularFile file)
			throws ClassNotFoundException, IOException
	{
		return loadFromDisk(file);
	}

	public List<Individual<E>> getBestIndividuals()
	{
		List<Individual<E>> r = new ArrayList<>();
		double bestFitness = individualList.get(0).fitness.getCombinedFitnessValue();

		for (int i = 0;; ++i)
		{
			Individual<E> e = individualList.get(i);

			if (e.fitness.getCombinedFitnessValue() == bestFitness)
			{
				r.add(e);
			}
			else
			{
				return r;
			}
		}
	}

	public void monitor()
	{
		MonitorPanel p = new MonitorPanel();
		Utilities.displayInJFrame(p, "Papareto population monitor");
	}

	public double iterate(Random prng)
	{
		Individual<E> initialBest = getBestIndividual();
		evolver.iterate(this, prng);
		Individual<E> best = getBestIndividual();
		getFitnessHistory().add(best.fitness);
		double improvement = best.fitness.getCombinedFitnessValue()
				- initialBest.fitness.getCombinedFitnessValue();
		return improvement;
	}

	public void evolve(Random prng, TerminationCondition<E, R> c)
	{
		while ( ! c.completed(this))
		{
			evolver.iterate(this, prng);
		}
	}

	public void evolveInTheBackground(Random prng, TerminationCondition<E, R> c,
			PopulationListener<E, R> listener)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while ( ! c.completed(Population.this))
				{
					double improvement = iterate(prng);

					if (listener != null)
						listener.newIteration(Population.this, improvement);
				}
			}

		}).start();
	}

	@Override
	public String toString()
	{
		return "NbGeneration=" + getNumberOfGenerations() + ", nbIndividuals="
				+ individualList.size() + (size() > 0
						? ", best fitness=" + getBestIndividual().getFitness() : "");
	}

	public String toStringFull()
	{
		return individualList.toString();
	}

	public FitnessMeasure computeFitness(E e)
	{
		if (fitnessCache != null)
		{
			FitnessMeasure m = fitnessCache.get(e);

			if (m != null)
			{
				return m;
			}
		}

		if (evaluators.isEmpty())
			throw new IllegalStateException(
					"fitness cannot be computed if no evaluator have been defined");

		int nbEvaluators = evaluators.size();
		double[] values = new double[nbEvaluators];

		for (int i = 0; i < nbEvaluators; ++i)
		{
			values[i] = evaluators.get(i).evaluate(e, this);
		}

		FitnessMeasure f = new FitnessMeasure(values, combination);

		if (fitnessCache != null)
		{
			fitnessCache.add(e, f);
		}

		return f;
	}

	public void merge(Population<E, R> p)
	{
		for (Individual<E> e : p.individualList)
		{
			individualList.add(e);
		}

		for (Evaluator<E, R> e : p.evaluators)
		{
			if ( ! evaluators.contains(e))
			{
				evaluators.add(e);
			}
		}

		evolver = p.evolver;
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

	public Population<E, R> clone()
	{
		Population<E, R> c = Clazz.makeInstance(getClass());
		c.representation = representation;
		c.evolver = evolver;
		c.evaluators.addAll(evaluators);
		c.combination = combination;
		return c;
	}

	public List<Population<E, R>> split(int nbSubPopulations)
	{
		List<Population<E, R>> r = new ArrayList<>();

		// create nbSubPopulations empty subpopulations
		for (int i = 0; i < nbSubPopulations; ++i)
		{
			r.add(clone());
		}

		int pi = 0;

		for (Individual<E> i : individualList)
		{
			r.get(pi).individualList.add(i);

			// cycle to the next subpopulation
			pi = (pi + 1) % r.size();
		}

		return r;
	}

	public void setEvolver(Evolver<E, R> evolver)
	{
		this.evolver = evolver;
	}

	public int indexOf(Individual<E> individual)
	{
		int index = Collections.binarySearch(individualList, individual);
		return index < 0 ? - 1 : index;
	}

	public void ensureCapacity(int n)
	{
		individualList.ensureCapacity(n);
	}

	protected Individual<E> tournament(int n, Random r)
	{
		Individual<E> i1 = pickRandomIndividual(r);

		while (--n > 0)
		{
			Individual<E> i2 = pickRandomIndividual(r);

			// if i2 has higher fitness
			if (i1.fitness.compareTo(i2.fitness) < 0)
			{
				i1 = i2;
			}
		}

		return i1;
	}

	public Individual<E> binaryTournament(Random r)
	{
		return tournament(2, r);
	}

	public Individual<E> pickRandomIndividual(Random r)
	{
		return getIndividualAt(r.nextInt(size()));
	}

	public void clear()
	{
		individualList.clear();
	}

	public void set(List<E> children)
	{
		clear();

		for (E e : children)
		{
			add(e);
		}
	}

	public void fillRandomly(int targetSize, Random r)
	{
		while (size() < targetSize)
		{
			add(getRepresentation().toObject(getRepresentation().getRandomIndividualGenerators().get(0).createNewChild(this, r)));
		}
	}
}
