package at.rovo.rdf;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class TestTriplestore
{

	/**
	 * Test-Entrypoint
	 * @param args Application parameters passed by the console
	 */
	public static void main(String[] args)
	{
		TestTriplestore test = new TestTriplestore();
		Graph graph = new Graph();

		///////////////////////////////////////////////////////////////////////
		/// Simple Test: adds some triples to the store and queries it
		///////////////////////////////////////////////////////////////////////
		
//		test.test1(graph);
		
		///////////////////////////////////////////////////////////////////////
		/// List all movies directed by Steven Spielberg and Harrison Ford 
		/// played in
		///////////////////////////////////////////////////////////////////////
		
//		graph.load("movies.csv");
//		test.test2(graph);
		
		///////////////////////////////////////////////////////////////////////
		/// Complex query with variable-binding - single result
		///
		/// Query all investment banks in New York that have given money to Utah 
		/// senator Orrin Hatch
		///////////////////////////////////////////////////////////////////////
		
		graph.load("business_triples.csv");
		test.test3(graph);
		
		///////////////////////////////////////////////////////////////////////
		/// Complex query with variable-binding - multiple result
		///
		/// Which person started a new relationship in the same year that their
		/// relationship with Britney Spears ended?
		///////////////////////////////////////////////////////////////////////
		
		graph.load("celeb_triples.csv");
		test.test4(graph);
		
		///////////////////////////////////////////////////////////////////////
		/// Complex query with variable-binding and feed-forward inference
		///
		///////////////////////////////////////////////////////////////////////
		
//		graph.load("business_triples.csv");
//		test.test5(graph);
		
//		graph.load("celeb_triples.csv");
//		test.test6(graph);
		
		graph = new Graph();
		
		System.out.println();
		
		graph.add("Peter","nationality","Germany");
		graph.add("Sandra", "nationality", "Austria");
		graph.add("Paul", "nationality", "USA");
		graph.add("Germany", "part_of", "Europe");
		graph.add("Austria", "part_of", "Europe");
		graph.add("USA", "part_of", "North America");
		test.test7(graph);
	}
	
	public void test1(Graph graph)
	{
		graph.add("blade_runner", "name", "Blade Runner");
		graph.add("blade_runner", "directed_by", "ridley_scott");
		graph.add("ridley_scott", "name", "Ridley Scott");
		
		list(graph.triples("blade_runner", "directed_by", ""));
		list(graph.triples("", "name", ""));
		System.out.println(graph.value("blade_runner", "directed_by", ""));
		
		list(graph.triples("", "", ""));
		graph.remove("blade_runner", "", "ridley_scott");
		list(graph.triples("", "", ""));
		
		graph.save("test.csv");
	}
	
	public void test2(Graph graph)
	{
		System.out.println("All actors in the movie \"Blade Runner\": ");
		String bladerunnerId = graph.value("", "name", "Blade Runner");
		Set<Triple> actorIds = graph.triples(bladerunnerId, "starring", "");

		for (Triple t : actorIds)
			System.out.println("\t"+graph.value(t.getObject(), "name", ""));
		System.out.println();
		
		System.out.println("Movies Harrison Ford played in: ");
		String harrisonfordId = graph.value("", "name", "Harrison Ford");
		Set<Triple> harrisonfordMovieId = graph.triples("", "starring", harrisonfordId);
		List<String> moviesHarrisonFordPlayedIn = new ArrayList<String>();
		for (Triple t : harrisonfordMovieId)
		{
			moviesHarrisonFordPlayedIn.add(graph.value(t.getSubject(), "name", ""));
			System.out.println("\t"+graph.value(t.getSubject(), "name", ""));
		}
		System.out.println();
		
		String spielbergId = graph.value("", "name", "Steven Spielberg");
		Set<Triple> spielbergMovieIds = graph.triples("", "directed_by", spielbergId);
		List<String> moviesDirectedByStevenSpielberg = new ArrayList<String>();
		for (Triple t : spielbergMovieIds)
			moviesDirectedByStevenSpielberg.add(graph.value(t.getSubject(), "name",""));	
		
		System.out.println("Movies Harrison Ford played in and Steven Spielberg directed: ");
		List<String> intersection = new ArrayList<String>(moviesDirectedByStevenSpielberg);
		intersection.retainAll(moviesHarrisonFordPlayedIn);
		for (String s : intersection)
			System.out.println("\t"+s);
	}
	
	public void test3(Graph graph)
	{
		Set<String> query = new LinkedHashSet<String>();
		query.add("?company, headquarters, New_York_New_York");
		query.add("?company, industry, Investment banking");
		query.add("?cont, contributor, ?company");
		query.add("?cont, recipient, Orrin Hatch");
		query.add("?cont, amount, ?dollars");
		
		System.out.println("Query: "+query);
		System.out.println("Result: ");
		
		queryResultHelper(graph.query(query));

	}
	
	public void test4(Graph graph)
	{
		Set<String> query = new LinkedHashSet<String>();
		query.add("?rel1, with, ?person");
		query.add("?rel1, with, Britney Spears");
		query.add("?rel1, end, ?year1");
		query.add("?rel2, with, ?person");
		query.add("?rel2, start, ?year1");
		
		System.out.println("Query: "+query);
		System.out.println("Result: ");
		
		queryResultHelper(graph.query(query));
	}
	
	public void test5(Graph graph)
	{
		InferenceRule wcr = new WestCoastRule();
		graph.applyInference(wcr);
		list(graph.triples("", "on_coast", ""));
	}
	
	public void test6(Graph graph)
	{
		EnemyRule er = new EnemyRule();
		System.out.println("Triplestore before applying inference rule: ");
		list(graph.triples("", "enemy", ""));
		System.out.println("Applying inference rule!");
		graph.applyInference(er);
		System.out.println("Triplestore after inference rule has been applied: ");
		list(graph.triples("", "enemy", ""));
	}
	
	public void test7(Graph graph)
	{
		ContinentRule cr = new ContinentRule();
		System.out.println("Triplestore before applying inference rule: ");
		list(graph.triples("", "", ""));
		System.out.println("Applying inference rule!");
		graph.applyInference(cr);
		System.out.println("Triplesotre after inference rule has been applied: ");
		list(graph.triples("", "", ""));
	}
	
	/**
	 * <p>Helper-method to print variable-names and their bindings</p>
	 * 
	 * @param results A {@link Set} which contains the mappings of variables and their bindings
	 */
	public static void queryResultHelper(Set<Map<String, String>> results)
	{
		for (Map<String, String> result : results)
		{
			for (Map.Entry<String, String> keys : result.entrySet())
				System.out.println("Key: "+keys.getKey()+ " Value: "+keys.getValue());
		}
		System.out.println();
	}
	
	/**
	 * <p>Helper-method to print a {@link Set} of {@link Triple}s conveniently</p>
	 * 
	 * @param list A {@link Set} of {@link Triple}s that should get printed out
	 */
	private void list(Set<Triple> list)
	{
		String output = "[";
		if (!list.isEmpty())
		{
			for (Triple t : list)
				output += "(" + t.getSubject() + ", " + t.getPredicate() + ", "
						+ t.getObject() + "), ";
			output = output.substring(0, output.lastIndexOf(", "));
		}
		output += "]";
		System.out.println(output);
	}
}
