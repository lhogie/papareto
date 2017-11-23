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
import java.util.ArrayList;
import java.util.List;

public abstract class Representation<E, R> implements Serializable
{
	private final List<NewChildOperator<E, R>> randomGenerators = new ArrayList<>();
	private final List<NewChildOperator<E, R>> crossoverOperators = new ArrayList<>();
	private final List<MutationOperator<R>> mutationOperators = new ArrayList<>();

	
	public List<NewChildOperator<E, R>> getRandomIndividualGenerators()
	{
		return randomGenerators;
	}

	public List<NewChildOperator<E, R>> getCrossoverOperators()
	{
		return crossoverOperators;
	}

	public List<MutationOperator<R>> getMutationOperators()
	{
		return mutationOperators;
	}

	public abstract E toObject(R r);

	public abstract R fromObject(E e);
}
