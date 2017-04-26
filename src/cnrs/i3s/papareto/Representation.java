package cnrs.i3s.papareto;

import java.io.Serializable;

public abstract class Representation<E, R> implements Serializable
{
	public abstract E toObject(R r);

	public abstract R fromObject(E e);
}
