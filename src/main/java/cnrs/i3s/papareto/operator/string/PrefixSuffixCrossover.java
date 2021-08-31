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
 
 
package cnrs.i3s.papareto.operator.string;

import java.util.Random;

import cnrs.i3s.papareto.NewChildOperator;
import cnrs.i3s.papareto.Population;

public class PrefixSuffixCrossover extends NewChildOperator<StringBuilder, StringBuilder>
{

	@Override
	public StringBuilder createNewChild(Population<StringBuilder, StringBuilder> p,
			Random r)
	{
		if (p.size() < 2)
			return null;

		StringBuilder a = p.binaryTournament(r).object;
		StringBuilder b = p.binaryTournament(r).object;

		if (r.nextDouble() < 0.5)
		{
			StringBuilder tmp = a;
			a = b;
			b = tmp;
		}

		// take a prefix from a
		String aPrefix = a.length() > 0 ? a.substring(0, r.nextInt(a.length())) : "";

		// take a suffix from b
		String bSuffix = b.length() > 0 ? b.substring(r.nextInt(b.length())) : "";

		// append them
		return new StringBuilder(aPrefix + bSuffix);
	}

	@Override
	public String getFriendlyName()
	{
		return "random crossover";
	}
}
