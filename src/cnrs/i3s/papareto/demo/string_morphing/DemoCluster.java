package cnrs.i3s.papareto.demo.string_morphing;

import java.net.UnknownHostException;

import cnrs.i3s.papareto.Evaluator;
import cnrs.i3s.papareto.NoRepresentation;
import cnrs.i3s.papareto.Population;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharAlterationMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.CharDeletionMutation;
import cnrs.i3s.papareto.demo.string_morphing.operator.HalfHalfCrossover;
import cnrs.i3s.papareto.demo.string_morphing.operator.PrefixSuffixCrossover;
import cnrs.i3s.papareto.distributed_computing.RemotePopulationEvolver;
import octojus.OctojusCluster;
import toools.text.TextUtilities;

public class DemoCluster
{
	public static void main(String[] args) throws UnknownHostException
	{
		Population<StringBuilder, StringBuilder> p = new Population<StringBuilder, StringBuilder>();

		p.getEvaluators().add(new Evaluator<StringBuilder, StringBuilder>()
		{

			@Override
			public double evaluate(StringBuilder i,
					Population<StringBuilder, StringBuilder> p)
			{
				return - TextUtilities.computeLevenshteinDistance(i.toString(),
						"alors ?");
			}
		});

		p.setRepresentation(new NoRepresentation<StringBuilder>());
		p.getCrossoverOperators().add(new PrefixSuffixCrossover());
		p.getCrossoverOperators().add(new HalfHalfCrossover());
		p.getMutationOperators().add(new CharAdditionMutation());
		p.getMutationOperators().add(new CharAlterationMutation());
		p.getMutationOperators().add(new CharDeletionMutation());
		p.add(new StringBuilder("coucou"));

		RemotePopulationEvolver<StringBuilder, StringBuilder> e = new RemotePopulationEvolver<StringBuilder, StringBuilder>()
		{

			@Override
			protected void evolve(Population<StringBuilder, StringBuilder> population)
			{
				while (population.getNumberOfGenerations() < 100)
				{
					population.makeNewGeneration();
				}
			}
		};

		OctojusCluster cluster = OctojusCluster.localhostCluster(3, true);
		cluster.start();

		// e.runOn(cluster.get)

	}
}
