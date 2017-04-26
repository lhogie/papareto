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
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharAdditionMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharAlterationMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharDeletionMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.HalfHalfCrossover;
import cnrs.i3s.papareto.demo.string_morphing.operator.PrefixSuffixCrossover;
import toools.io.file.RegularFile;
import toools.text.TextUtilities;

public class DemoCheckpoints
{
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ClassNotFoundException
	{
		StringBuilder source = new StringBuilder("Salut ca va?");
		final String target = "Hello how are you doing?";

		Population<StringBuilder, StringBuilder> p = new Population<StringBuilder, StringBuilder>();

		p.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{

			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - TextUtilities.computeLevenshteinDistance(i.toString(), target);
			}
		});

		p.setRepresentation(new NoRepresentation<StringBuilder>());
		p.getCrossoverOperators().add(new PrefixSuffixCrossover());
		p.getCrossoverOperators().add(new HalfHalfCrossover());
		p.getMutationOperators().add(new CharAdditionMutation());
		p.getMutationOperators().add(new CharAlterationMutation());
		p.getMutationOperators().add(new CharDeletionMutation());
		p.add(source);

		RegularFile f = new RegularFile("$HOME/a.serialized");

		if (f.exists())
		{
			p = Population.restore(f);
		}

		while (true)
		{
			if (p.makeNewGeneration(100))
			{
				if (p.getNumberOfGenerations() % 5 == 0)
					p.checkpoint(f);

				System.out.println(
						"Generation: " + p.getNumberOfGenerations() + ": " + p.getIndividualAt(0));

				if (p.getIndividualAt(0).fitness[0] == 0)
				{
					break;
				}
			}
		}
	}
}
