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

package cnrs.i3s.papareto.demo.function;

import java.util.Random;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.Representation;
import cnrs.i3s.papareto.algo.RandomEvolver;

public class Main
{
	public static void main(String[] args)
	{
		Representation<Point, Point> r = new NoRepresentation<>();

		r.getRandomIndividualGenerators().add(new RandomGenerator(2));
		r.getCrossoverOperators().add(new Barycenter());
		r.getMutationOperators().add(new PointMutation(100));
		Population<Point, Point> pp = new Population<>();
		pp.setRepresentation(r);
		// pp.setEvolver(new LucEvolver<>());
		pp.setEvolver(new RandomEvolver<>());
		pp.getEvaluators().add(new Evaluator<Point, Point>()
		{
			@Override
			public double evaluate(Point p, Population<Point, Point> pp)
			{// http://fooplot.com/#W3sidHlwZSI6MCwiZXEiOiJ4XjIqKDErY29zKHgpKSthYnMoeCkiLCJjb2xvciI6IiMwMDAwMDAifSx7InR5cGUiOjEwMDAsIndpbmRvdyI6WyItMTE4LjIzNDMxMTIzMDQ4MDM3IiwiMTE4LjIzNDMxMTIzMDQ4MDM3IiwiLTQxLjEwOTE2MDUyMDEzNjI3IiwiMTA0LjQwOTk5MTc2MzUzMTg1Il19XQ--
				return (p.v[0] * p.v[0]) * (1 + Math.cos(p.v[0])) + Math.abs(p.v[0]);
			}
		});

		System.out.println(
				pp.getEvaluators().get(0).evaluate(new Point(512, 404.2319), pp));

		Random prng = new Random();
		pp.fillRandomly(100, prng);

		while (pp.getFitnessHistory()
				.getNumberOfGenerationsDuringWhichTheBestFitnessHasNotChanged() < 100)
		{
			pp.iterate(prng);
			System.out.println(pp.getBestIndividual().object + " => "
					+ pp.getBestIndividual().fitness);
		}

		System.out.println("gen=" + pp.getNumberOfGenerations());
	}

}
