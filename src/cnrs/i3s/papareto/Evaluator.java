package cnrs.i3s.papareto;

import java.io.Serializable;

public abstract class Evaluator<E, R> implements Serializable
{
	public abstract FitnessElement evaluate(E i, Population<E, R> p);

}
