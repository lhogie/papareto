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

package cnrs.i3s.papareto.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.Evolver;
import cnrs.i3s.papareto.FitnessMeasure;
import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.MutationOperator;
import cnrs.i3s.papareto.NewChildOperator;
import cnrs.i3s.papareto.Operator;
import cnrs.i3s.papareto.Population;
import toools.collections.Lists;
import toools.thread.MultiThreadPolicy;
import toools.thread.MultiThreadProcessing;
import toools.thread.NThreadsPolicy;

public class LucEvolver<E, R> extends Evolver<E, R>
{

	// parameters of the evolutionary algorithm
	private boolean allowsDuplicates = true;

	private boolean allowsAsynchronousUpdates = false;

	private int numberOfRejectedDuplicates = 0;

	// private MultiThreadPolicy multiThreadPolicy = new
	// NCoresNThreadsPolicy(1);
	private MultiThreadPolicy multiThreadPolicy = new NThreadsPolicy(1);

	private double mutationProbability = 0.1;

	private int nbThreads = MultiThreadProcessing.NB_THREADS_TO_USE;

	public int getNumberOfRejectedDuplicates()
	{
		return numberOfRejectedDuplicates;
	}

	@Override
	public void iterate(Population<E, R> p, Random random)
	{
		// if the offspring has not been explicitly set by the user, the
		// population
		// will double its size during evolution, before bring shrinked back to
		// its initial size
		if (getOffspring() == - 1)
			setOffspring(p.size());

		p.ensureCapacity(p.size() + getOffspring());
		final int initSize = p.size();

		final List<Individual<E>> children = new ArrayList<>();

		new MultiThreadProcessing(nbThreads, "evolving", null)
		{
			@Override
			protected void runInParallel(ThreadSpecifics t)
			{
				while (p.size() + children.size() < initSize + getOffspring())
				{
					Individual<E> child = createNewChild(p, random);

					if (child != null)
					{
						if (allowsDuplicates
								|| ( ! children.contains(child) && p.indexOf(child) >= 0))
						{
							synchronized (this)
							{
								if (allowsAsynchronousUpdates
										&& participateToAsynchronousUpdating(child))
								{
									p.add(child);
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
		}.execute();

		// some children may not have been added asynchronously
		for (Individual<E> c : children)
		{
			p.add(c);
		}

		selection(p, initSize);
	}

	public void selection(Population<E, R> p, int targetSize)
	{
		for (int i = 0; i < targetSize; ++i)
		{
			for (Operator o : p.getIndividualAt(i).operators)
			{
				o.nbSuccess++;
			}
		}

		p.shrinkTo(targetSize);

		for (int i = 0; i < p.size(); ++i)
		{
			for (Operator o : p.getIndividualAt(i).operators)
			{
				o.nbFailure++;
			}
		}
	}

	protected boolean participateToAsynchronousUpdating(Individual<E> i)
	{
		return true;
	}

	private Individual<E> createNewChild(Population<E, R> p, Random random)
	{
		List<Operator> operators = new ArrayList<Operator>();

		NewChildOperator<E, R> crossoverOperator = getCrossoverOperator(p, random);
		R rchild = crossoverOperator.createNewChild(p, random);
		++nbBirths;

		operators.add(crossoverOperator);

		MutationOperator<R> m = getMutationOperator(p, rchild, random);

		if (random.nextDouble() < mutationProbability)
		{
			m.mutate(rchild, random);
			operators.add(m);
		}

		E child = p.getRepresentation().toObject(rchild);
		FitnessMeasure fitness = p.computeFitness(child);
		return new Individual<E>(child, fitness, operators);
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

	public MultiThreadPolicy getMultiThreadPolicy()
	{
		return multiThreadPolicy;
	}

	public void setMultiThreadPolicy(MultiThreadPolicy multiThreadPolicy)
	{
		this.multiThreadPolicy = multiThreadPolicy;
	}

	@Override
	public NewChildOperator<E, R> getRandomGenerator(Population<E, R> p, Random random)
	{
		return pick(p.getRepresentation().getRandomIndividualGenerators(), random);
	}

	@Override
	public NewChildOperator<E, R> getCrossoverOperator(Population<E, R> p, Random random)
	{
		return pick(p.getRepresentation().getCrossoverOperators(), random);
	}

	@Override
	public MutationOperator<R> getMutationOperator(Population<E, R> p, R children,
			Random random)
	{
		return pick(p.getRepresentation().getMutationOperators(), random);
	}

	private static <A extends Operator> A pick(List<A> operators, Random random)
	{
		return Lists.pickRandomElement(operators, computeUsageProbabilities(operators),
				random);
	}

	private static <A extends Operator> double[] computeUsageProbabilities(
			List<A> operators)
	{
		double[] a = new double[operators.size()];

		for (int i = 0; i < a.length; ++i)
		{
			Operator o = operators.get(i);

			// if the operator was never used in the past
			if (o.nbFailure + o.nbSuccess == 0)
			{
				// give it max opportunity by assuming it would have top
				// performance
				a[i] = 1;
			}
			else
			{
				a[i] = operators.get(i).getSuccessRate();
			}
		}

		return a;
	}

}
