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

//https://courses.cs.washington.edu/courses/cse473/06sp/GeneticAlgDemo/gaintro.html

package cnrs.i3s.papareto.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.Evolver;
import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.MutationOperator;
import cnrs.i3s.papareto.Population;

public class BasicEvolver<E, R> extends Evolver<E, R>
{
	private double crossoverProbability = 0.8;
	private double mutationProbability = 0.1;

	public double getCrossoverProbability()
	{
		return crossoverProbability;
	}

	public void setCrossoverProbability(double crossoverProbability)
	{
		this.crossoverProbability = crossoverProbability;
	}

	public double getMutationProbability()
	{
		return mutationProbability;
	}

	public void setMutationProbability(double mutationProbability)
	{
		this.mutationProbability = mutationProbability;
	}

	@Override
	public void iterate(Population<E, R> p, Random random)
	{
		if (getOffspring() == - 1)
			setOffspring(p.size());

		final List<R> children = new ArrayList<>();

		while (children.size() < getOffspring())
		{

			if (random.nextDouble() < crossoverProbability)
			{
				R child = getCrossoverOperator(p, random).createNewChild(p, random);
				children.add(child);
			}
			else
			{
				Individual<E> i1 = p.binaryTournament(random);
				R ri1 = p.getRepresentation().fromObject(i1.object);

				Individual<E> i2 = p.binaryTournament(random);
				R ri2 = p.getRepresentation().fromObject(i2.object);

				children.add(ri1);
				children.add(ri2);
			}
		}

		for (R c : children)
		{
			MutationOperator<R> m = getMutationOperator(p, c, random);

			if (random.nextDouble() < mutationProbability)
			{
				m.mutate(c, random);
			}
		}

		p.clear();

		for (R c : children)
		{
			p.add(p.getRepresentation().toObject(c));
		}
	}

}
