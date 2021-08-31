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

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public abstract class Evolver<E, R> implements Serializable
{
	private int offSpring = - 1;
	protected int nbBirths = 0;

	public int getOffspring()
	{
		return offSpring;
	}

	public void setOffspring(int offSpring)
	{
		if (offSpring <= 0)
			throw new IllegalArgumentException("offspringSize must be >= 0");

		this.offSpring = offSpring;
	}

	public abstract void iterate(Population<E, R> p, Random random);

	public  NewChildOperator<E, R> getRandomGenerator(Population<E, R> p, Random random)
	{
		List<NewChildOperator<E, R>> rg = p.getRepresentation().getRandomIndividualGenerators();
		
		if (rg.size() == 1)
		{
			return rg.get(0);
		}
		else
		{
			throw new IllegalStateException("There are multiple random individual generators. Class " + getClass().getName() + " must implement getRandomGenerator() to decide which one to choose");
		}
	}

	public  NewChildOperator<E, R> getCrossoverOperator(Population<E, R> p, Random random)
	{
		List<NewChildOperator<E, R>> rg = p.getRepresentation().getCrossoverOperators();
		
		if (rg.size() == 1)
		{
			return rg.get(0);
		}
		else
		{
			throw new IllegalStateException("There are multiple crossover operators. Class " + getClass().getName() + " must implement getCrossoverOperator() to decide which one to choose");
		}
	}

	public  MutationOperator<R> getMutationOperator(Population<E, R> p, R children, Random random)
	{
		{
			List<MutationOperator<R>> rg = p.getRepresentation().getMutationOperators();
			
			if (rg.size() == 1)
			{
				return rg.get(0);
			}
			else
			{
				throw new IllegalStateException("There are multiple mutation generators. Class " + getClass().getName() + " must implement getMutationOperator() to decide which one to choose");
			}
		}
	}

}
