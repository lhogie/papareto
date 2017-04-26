package cnrs.i3s.papareto;

import java.util.ArrayList;
import java.util.List;

public abstract class Representation<E, R>
{
	private final List<MutationOperator<R>> mutationOperators = new ArrayList<>();
	private final List<CrossoverOperator<R>> crossoverOperators = new ArrayList<>();

	public List<MutationOperator<R>> getMutationOperators()
	{
		return mutationOperators;
	}

	public List<CrossoverOperator<R>> getCrossoverOperators()
	{
		return crossoverOperators;
	}

	public abstract E toObject(R r);

	public abstract R fromObject(E e);

}
