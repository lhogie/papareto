package cnrs.i3s.papareto.impl.bytes;

import java.io.Serializable;

import cnrs.i3s.papareto.Individual;
import cnrs.i3s.papareto.Representation;
import toools.io.serialization.JavaSerializer;

public class SerializingRepresentation<E extends Serializable>
		extends Representation<Individual<E>, byte[]>
{

	@Override
	public Individual<E> toObject(byte[] bytes)
	{
		return (Individual<E>) JavaSerializer.getDefaultSerializer().fromBytes(bytes);
	}

	@Override
	public byte[] fromObject(Individual<E> e)
	{
		return JavaSerializer.getDefaultSerializer().toBytes(e);
	}
}
