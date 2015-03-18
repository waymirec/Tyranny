package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelingSalesman 
{
	private final Map<String,Site> sites = new HashMap<>();
	
	public void addSite(Site site)
	{
		sites.put(site.getName(),site);
	}
	
	public void route()
	{
		List<String> visited = new ArrayList<>();
		
		String source = "A";
		visited.add(source);
		System.out.printf("%s", source);
		for(int i=0; i<sites.get(source).getNeighbors().size(); )
		{
			Neighbor n = sites.get(source).getNeighbors().get(i);
			if(!visited.contains(n.getName()))
			{
				visited.add(n.getName());
				source = n.getName();
				i = 0;
				System.out.printf(" => %s", n.getName());
				continue;
			}
			else
			{
				i++;
			}
		}		
	}
	
	private class Site
	{
		private final String name;
		private final List<Neighbor> neighbors;
		
		public Site(String name)
		{
			this.name = name;
			this.neighbors = new ArrayList<Neighbor>();
		}
		
		public String getName()
		{
			return name;
		}
		
		public List<Neighbor> getNeighbors()
		{
			return neighbors;
		}
	}
	
	private class Neighbor
	{
		private final String name;
		private final int distance;
		
		public Neighbor(String name, int distance)
		{
			this.name = name;
			this.distance = distance;
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getDistance()
		{
			return distance;
		}
	}
	
	public static class NeighborComparator implements Comparator<Neighbor>
	{

		@Override
		public int compare(Neighbor n1, Neighbor n2) 
		{
			return Integer.valueOf(n1.getDistance()).compareTo(n2.getDistance());
		}
		
	}
	
	public static void main(String[] args)
	{
		TravelingSalesman ts = new TravelingSalesman();
		
		Site s1 = ts.new Site("A");
		s1.getNeighbors().add(ts.new Neighbor("B",40));
		s1.getNeighbors().add(ts.new Neighbor("D",35));
		s1.getNeighbors().add(ts.new Neighbor("C",42));
		Collections.sort(s1.getNeighbors(), new NeighborComparator());
		ts.addSite(s1);
		
		Site s2 = ts.new Site("B");
		s2.getNeighbors().add(ts.new Neighbor("A",20));
		s2.getNeighbors().add(ts.new Neighbor("C",30));
		s2.getNeighbors().add(ts.new Neighbor("D",34));
		Collections.sort(s2.getNeighbors(), new NeighborComparator());
		ts.addSite(s2);

		Site s3 = ts.new Site("C");
		s3.getNeighbors().add(ts.new Neighbor("A",42));
		s3.getNeighbors().add(ts.new Neighbor("B",30));
		s3.getNeighbors().add(ts.new Neighbor("D",12));
		Collections.sort(s3.getNeighbors(), new NeighborComparator());
		ts.addSite(s3);

		Site s4 = ts.new Site("D");
		s4.getNeighbors().add(ts.new Neighbor("A",35));
		s4.getNeighbors().add(ts.new Neighbor("B",34));
		s4.getNeighbors().add(ts.new Neighbor("C",12));
		Collections.sort(s4.getNeighbors(), new NeighborComparator());
		ts.addSite(s4);

		ts.route();
	}
}
