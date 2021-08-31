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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.Representation;
import cnrs.i3s.papareto.algo.LucEvolver;
import cnrs.i3s.papareto.operator.string.CharAdditionMutation;
import cnrs.i3s.papareto.operator.string.CharAlterationMutation;
import cnrs.i3s.papareto.operator.string.CharDeletionMutation;
import cnrs.i3s.papareto.operator.string.HalfHalfCrossover;
import cnrs.i3s.papareto.operator.string.PrefixSuffixCrossover;
import cnrs.i3s.papareto.operator.string.RandomStringGenerator;
import toools.text.TextUtilities;

public class DemoCheckpoints
{
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		Random random = new Random(5);

		Population<StringBuilder, StringBuilder> population = new Population<>();

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
			public double evaluate(StringBuilder s,
					Population<StringBuilder, StringBuilder> p)
			{
				return - TextUtilities.computeLevenshteinDistance(s.toString(),
						"Hello! How are you doing?");
			}
		});

		population.setEvolver(new LucEvolver<>());

		population.fillRandomly(50, random);

		// while the target string has not been reached
		while (population.getBestIndividual().getFitness().getCombinedFitnessValue() < 0)
		{
			population.iterate(random);
			System.out.println(population + "\t" + population.getBestIndividual());
		}
	}
}
