package cnrs.i3s.papareto.impl.pojo;

import cnrs.i3s.papareto.Representation;

public class NoRepresentation<E> extends Representation<E, E>
{

	@Override
	public E toObject(E i)
	{
		return i;
	}

	@Override
	public E fromObject(E i)
	{
		return i;
	}

}
