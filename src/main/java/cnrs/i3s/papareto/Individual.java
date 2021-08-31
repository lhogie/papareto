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

/**
 * A Bucket is an object with associate to an individual: - a fitness - the
 * operators utilized to generate it
 * 
 * @author lhogie
 * 
 * @param <E>
 */
public class Individual<E> implements Serializable, Comparable<Individual<E>>
{
	public final E object;
	public final FitnessMeasure fitness;

	// the operators used to created this individual
	public final List<Operator> operators;

	public Individual(E o, FitnessMeasure fitness, List<Operator> operators)
	{
		if (o.getClass() == String.class)
			throw new IllegalArgumentException("does not support String objects");

		this.object = o;
		this.fitness = fitness;
		this.operators = operators;

	}

	@Override
	public String toString()
	{
		return "['" + object + "', f=" + fitness + "]";
	}

	@Override
	public boolean equals(Object i)
	{
		return i instanceof Individual && ((Individual) i).object.equals(object);
	}

	public FitnessMeasure getFitness()
	{
		return fitness;
	}

	@Override
	public int compareTo(Individual<E> o)
	{
		// used to sort the individual list by descending value of the fitness
		return -Double.compare(fitness.getCombinedFitnessValue(), o.fitness.getCombinedFitnessValue());
	}
}