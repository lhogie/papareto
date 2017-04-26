package cnrs.i3s.papareto;

public class NoRepresentation<E> extends Representation<E, E>
{
	@Override
	public E toObject(E representation)
	{
		return representation;
	}

	@Override
	public E fromObject(E object)
	{
		return object;
	}
}
