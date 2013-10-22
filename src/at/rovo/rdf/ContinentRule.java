package at.rovo.rdf;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ContinentRule implements InferenceRule
{
	@Override
	public Set<Set<String>> getQueries() 
	{
		Set<Set<String>> query = new LinkedHashSet<Set<String>>();
		Set<String> query1 = new LinkedHashSet<String>();
		query1.add("?person, nationality, ?country");
		query1.add("?country, part_of, ?continent");
		
		query.add(query1);
		return query;
	}

	@Override
	public Set<Triple> makeTriples(Map<String, String> binding) 
	{
		Triple t = new Triple(binding.get("?person") ,"on_continent", binding.get("?continent"));
		Set<Triple> ret = new LinkedHashSet<Triple>();
		ret.add(t);
		return ret;
	}

}
