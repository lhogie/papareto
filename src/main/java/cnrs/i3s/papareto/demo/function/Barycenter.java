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
 
 
package cnrs.i3s.papareto.demo.function;

import java.util.Random;

import cnrs.i3s.papareto.NewChildOperator;
import cnrs.i3s.papareto.Population;

public class Barycenter extends NewChildOperator<Point, Point>
{

	@Override
	public Point createNewChild(Population<Point, Point> p, Random prng)
	{
		Point p1 = p.binaryTournament(prng).object;
		Point p2 = p.binaryTournament(prng).object;

		if (p1.v.length != p2.v.length)
			throw new IllegalStateException();

		Point r = new Point(p1.v.length);

		for (int i = 0; i < r.v.length; ++i)
		{
			r.v[i] = (p1.v[i] + p2.v[i]) / 2;
		}

		return r;
	}

}
