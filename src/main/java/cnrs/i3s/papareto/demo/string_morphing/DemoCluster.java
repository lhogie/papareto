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
 
 
package cnrs.i3s.papareto.demo.string_morphing;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.Representation;
import cnrs.i3s.papareto.TerminationCondition;
import cnrs.i3s.papareto.algo.LucEvolver;
import cnrs.i3s.papareto.distributed_computing.Islands;
import cnrs.i3s.papareto.distributed_computing.IslandsListener;
import cnrs.i3s.papareto.operator.string.CharAdditionMutation;
import cnrs.i3s.papareto.operator.string.CharAlterationMutation;
import cnrs.i3s.papareto.operator.string.CharDeletionMutation;
import cnrs.i3s.papareto.operator.string.HalfHalfCrossover;
import cnrs.i3s.papareto.operator.string.PrefixSuffixCrossover;
import cnrs.i3s.papareto.operator.string.RandomStringGenerator;
import jacaboo.NodeNameSet;
import octojus.OctojusCluster;
import octojus.OctojusNode;
import toools.text.TextUtilities;

public class DemoCluster
{
	public static void main(String[] args) throws UnknownHostException
	{
		Population<StringBuilder, StringBuilder> population = new Population<StringBuilder, StringBuilder>();

		Representation<StringBuilder, StringBuilder> r = new NoRepresentation<StringBuilder>();

		r.getRandomIndividualGenerators().add(new RandomStringGenerator());
		r.getCrossoverOperators().add(new HalfHalfCrossover());
		r.getCrossoverOperators().add(new PrefixSuffixCrossover());
		r.getMutationOperators().add(new CharAdditionMutation());
		r.getMutationOperators().add(new CharDeletionMutation());
		r.getMutationOperators().add(new CharAlterationMutation());
		population.setRepresentation(r);

		population.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{
			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - TextUtilities.computeLevenshteinDistance(i.toString(),
						"Sgt. Pepper's Lonely Hearts Club Band");
			}
		});

		population.setEvolver(new LucEvolver<>());

		Random prng = new Random();
		population.fillRandomly(50, prng);

		
		OctojusCluster cluster = new OctojusCluster("lhogie", null,
				new NodeNameSet("styx", "oops"));
		cluster.start();

		TerminationCondition<StringBuilder, StringBuilder> localTerminationCondition = new TerminationCondition<StringBuilder, StringBuilder>()
		{

			@Override
			public boolean completed(Population<StringBuilder, StringBuilder> p)
			{
				return p.getNumberOfGenerations() == 100;
			}
		};

		TerminationCondition<StringBuilder, StringBuilder> globalTerminationCondition = new TerminationCondition<StringBuilder, StringBuilder>()
		{

			@Override
			public boolean completed(Population<StringBuilder, StringBuilder> p)
			{
				return p.getBestIndividual().getFitness().getCombinedFitnessValue() == 0;
			}
		};

		IslandsListener<StringBuilder, StringBuilder> monitor = new IslandsListener<StringBuilder, StringBuilder>()
		{

			@Override
			public void sendingPopulations(
					Population<StringBuilder, StringBuilder> subPopulation,
					OctojusNode targetNode)
			{
				System.out.println("Sending " + subPopulation + " to " + targetNode);
			}

			@Override
			public void receivingIndividuals(OctojusNode senderNode,
					List<Individual<StringBuilder>> individuals)
			{
				System.out.println("Receiving " + individuals.size() + " individuls from "
						+ senderNode);
			}

			@Override
			public void terminating(Population<StringBuilder, StringBuilder> result)
			{
			}
		};

		Islands.evolveInIslands(population, cluster, localTerminationCondition,
				globalTerminationCondition, monitor, prng);

		System.out.println("Terminated with " + population.getBestIndividual());
	}
}
