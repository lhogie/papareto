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

package cnrs.i3s.papareto;

import toools.io.IORuntimeException;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.ser.JavaSerializer;

public class FitnessCache<E>
{
	private final Directory directory;

	public FitnessCache(Directory directory)
	{
		this.directory = directory;
	}

	public void add(E r, FitnessMeasure m)
	{
		RegularFile f = getFileFor(r);
		f.getParent().mkdirs();

		try
		{
			f.setContent(JavaSerializer.getDefaultSerializer().toBytes(m));
		}
		catch (IORuntimeException e)
		{
			throw new CacheException(e);
		}
	}

	public FitnessMeasure get(E r)
	{

		return (FitnessMeasure) JavaSerializer.getDefaultSerializer()
				.fromBytes(getFileFor(r).getContent());

	}

	public RegularFile getFileFor(E r)
	{
		return new RegularFile("" + r.hashCode());
	}
}
