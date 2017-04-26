/**
 *  This file is part of Papareto.
 *	
 *  Papareto is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Papareto is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Papareto.  If not, see <http://www.gnu.org/licenses/>. *
 */

package cnrs.i3s.papareto.demo.string_morphing;

import java.io.FileNotFoundException;
import java.io.IOException;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.FitnessElement;
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharAdditionMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharAlterationMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharDeletionMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.HalfHalfCrossover;
import cnrs.i3s.papareto.demo.string_morphing.operator.PrefixSuffixCrossover;
import toools.io.file.RegularFile;
import toools.text.TextUtilities;

public class Demo
{
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		StringBuilder source = new StringBuilder("Salut ca va?");
		String target = "Hello how are you doing?";

		RegularFile f = new RegularFile("$HOME/population.serialized");

		Population<StringBuilder, StringBuilder> population;

		if (f.exists())
		{
			population = Population.restore(f);
		}
		else
		{
			population = create(source, target);
		}

		System.out.println(population);

		// while the target string has not been reached
		while (population.getBestIndividual().getFitness().combine() < 0)
		{
			population.makeNewGeneration(100);

			System.out.println(population + "\t" + population.getBestIndividual());

			if (Math.random() < 0.2)
				population.checkpoint(f);
		}
	}

	private static Population<StringBuilder, StringBuilder> create(StringBuilder src,
			String target)
	{
		Population<StringBuilder, StringBuilder> population = new Population<StringBuilder, StringBuilder>();

		population.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{

			@Override
			public FitnessElement evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				int d = TextUtilities.computeLevenshteinDistance(i.toString(), target);

				return new FitnessElement( - d, 1);
			}
		});

		population.setRepresentation(new NoRepresentation<StringBuilder>());
		population.getCrossoverOperators().add(new PrefixSuffixCrossover());
		population.getCrossoverOperators().add(new HalfHalfCrossover());
		population.getMutationOperators().add(new CharAdditionMutation());
		population.getMutationOperators().add(new CharAlterationMutation());
		population.getMutationOperators().add(new CharDeletionMutation());
		population.add(src);
		return population;
	}
}
