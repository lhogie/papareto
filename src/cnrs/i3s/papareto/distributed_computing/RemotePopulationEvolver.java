package cnrs.i3s.papareto.distributed_computing;

import cnrs.i3s.papareto.Population;
import octojus.ComputationRequest;

public abstract class RemotePopulationEvolver<E, R> extends ComputationRequest<Population<E, R>>
{
	Population<E, R> population;
	
	@Override
	protected Population<E, R> compute() throws Throwable
	{
		evolve(population);
		
		return population;
	}

	protected abstract void evolve(Population<E, R> population);

}
