/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 

*/
 
 
package cnrs.i3s.papareto.impl.bit;

import java.util.BitSet;
import java.util.Random;

import cnrs.i3s.papareto.NewChildOperator;
import cnrs.i3s.papareto.Population;

public class BitCrossover<E> extends NewChildOperator<E, BitSet>
{

	@Override
	public BitSet createNewChild(Population<E, BitSet> p, Random r)
	{
		BitSet i1 = p.getRepresentation().fromObject(p.binaryTournament(r).object);
		BitSet i2 = p.getRepresentation().fromObject(p.binaryTournament(r).object);
		BitSet b = new BitSet();
		int splitIndex = r.nextInt(Math.min(i1.length(), i2.length()));

		// copy the begining of i1
		System.arraycopy(i1, 0, b, 0, splitIndex);

		// copy the end of i2
		System.arraycopy(i2, splitIndex, b, splitIndex, i2.length() - splitIndex);

		return b;
	}
}
