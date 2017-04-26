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

package cnrs.i3s.papareto.demo.string;

import cnrs.i3s.papareto.impl.pojo.POJOEvaluator;
import cnrs.i3s.papareto.impl.pojo.POJOPopulation;
import toools.text.TextUtilities;

public class Demo2
{
	public static void main(String[] args)
	{
		final String target = "Hello how are you doing?";
		StringBuilder initialInidividual = new StringBuilder("Salut ca va?");

		POJOPopulation<StringBuilder> p = new POJOPopulation<StringBuilder>();
		p.add(initialInidividual);

		p.getEvaluators().add(new POJOEvaluator<StringBuilder>()
		{

			@Override
			public double evaluate(StringBuilder i, POJOPopulation<StringBuilder> p)
			{
				return - TextUtilities.computeLevenshteinDistance(i.toString(), target);
			}
		});

		p.getRepresentation().getCrossoverOperators().add(new PrefixSuffixCrossover());
		p.getRepresentation().getCrossoverOperators().add(new HalfHalfCrossover());
		p.getRepresentation().getMutationOperators().add(new CharAdditionMutation());
		p.getRepresentation().getMutationOperators().add(new CharAlterationMutation());
		p.getRepresentation().getMutationOperators().add(new CharDeletionMutation());

		while (true)
		{
			if (p.makeNewGeneration(100))
			{
				System.out.println(
						"Generation: " + p.getNumberOfGenerations() + ": " + p.get(0));

				if (p.get(0).fitness[0] == 0)
				{
					break;
				}
			}
		}

	}
}
