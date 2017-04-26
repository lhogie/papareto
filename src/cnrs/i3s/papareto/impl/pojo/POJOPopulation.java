package cnrs.i3s.papareto.impl.pojo;

import cnrs.i3s.papareto.Population;

public class POJOPopulation<E> extends Population<E, E>
{
	public POJOPopulation()
	{
		setRepresentation(new NoRepresentation<E>());
	}


}
