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
 
 
package cnrs.i3s.papareto.distributed_computing;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.TerminationCondition;
import octojus.ComputationRequest;
import octojus.OctojusCluster;
import octojus.OctojusNode;
import octojus.OneNodeOneRequest;

public class Islands
{
	public static <E, R> void evolveInIslands(Population<E, R> p, OctojusCluster c,
			TerminationCondition<E, R> localTerminationCondition,
			TerminationCondition<E, R> globalTerminationCondition,
			IslandsListener<E, R> listener, Random prng)
	{
		int initialSize = p.size();

		while ( ! globalTerminationCondition.completed(p))
		{
			List<Population<E, R>> parts = p.split(c.getNodes().size());

			Map<OctojusNode, List<Individual<E>>> r = new OneNodeOneRequest<List<Individual<E>>>()
			{
				// synchronized to make sure that remove(0) will be done
				// sequentially, for accurate note assignation
				@Override
				synchronized protected ComputationRequest<List<Individual<E>>> createComputationRequestForNode(
						OctojusNode n)
				{
					RemotePopulationEvolver<E, R> e = new RemotePopulationEvolver<E, R>();
					e.population = parts.remove(0);
					e.terminationCondition = localTerminationCondition;
					e.prng = prng;

					if (listener != null)
						listener.sendingPopulations(e.population, n);
					
					return e;
				}
			}.execute(c.getNodes());

			// merge all population evolved on nodes
			for (Entry<OctojusNode, List<Individual<E>>> entry : r.entrySet())
			{
				if (listener != null)
					listener.receivingIndividuals(entry.getKey(), entry.getValue());

				for (Individual<E> i : entry.getValue())
				{
					p.add(i);
				}
			}

			p.shrinkTo(initialSize);
		}
	}
}
