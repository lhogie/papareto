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

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.PopulationListener;
import cnrs.i3s.papareto.TerminationCondition;
import cnrs.i3s.papareto.impl.pojo.POJOPopulation;

public class Demo
{
	public static void main(String[] args)
	{
		StringBuilder initialIndividual = new StringBuilder("Salut ca va?");

		POJOPopulation<StringBuilder> p = new POJOPopulation<StringBuilder>()
		{
			public boolean saveToDisk(int g)
			{
				return false;
			}
		};

		p.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{
			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - Math.abs(10 - i.length());
			}
		});

		p.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{
			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - Math.abs(5 - i.toString().split(" ").length);
			}
		});

		p.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{
			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - i.indexOf("  ");
			}
		});

		p.add(initialIndividual);

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

		
		p.getIndividualList().clear();
		p.add(initialIndividual);

		
		System.out.println("once again");
		p.getPopulationListeners()
				.add(new PopulationListener<StringBuilder, StringBuilder>()
				{

					@Override
					public void newIteration(Population<StringBuilder, StringBuilder> p,
							boolean improve)
					{
						System.out.println("Generation: " + p.getNumberOfGenerations()
								+ ": " + p.get(0));
					}

					@Override
					public void completed(Population<StringBuilder, StringBuilder> p)
					{
						System.out.println("done");
					}
				});

		p.evolve(new TerminationCondition<StringBuilder, StringBuilder>()
		{

			@Override
			public boolean completed(Population<StringBuilder, StringBuilder> p)
			{
				return p.get(0).fitness[0] == 0;
			}
		});

	}
}
