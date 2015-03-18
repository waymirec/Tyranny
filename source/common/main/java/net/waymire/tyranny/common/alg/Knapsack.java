package net.waymire.tyranny.common.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Knapsack 
{
	public static void main(String[] args)
	{
		Random random = new Random();
		List<KnapsackItem> items = new ArrayList<KnapsackItem>();
		for(int i=0; i<20; i++)
		{
			items.add(new KnapsackItem(random.nextFloat() * 10,random.nextFloat() * 10));
		}
		
		Collections.sort(items,new KnapsackValueToWeightComparator());
		Collections.reverse(items);
		
		for(KnapsackItem item : items)
		{
			float ratio = item.getValue() / item.getWeight();
			System.out.printf("%s -- %s -- %s\n",  item.getValue(), item.getWeight(), ratio);
		}
	}

	private static class KnapsackItem
	{
		private final float weight;
		private final float value;
		
		public KnapsackItem(float weight, float value)
		{
			this.weight = weight;
			this.value = value;
		}
		
		public float getWeight()
		{
			return weight;
		}
		
		public float getValue()
		{
			return value;
		}
	}
	
	private static class KnapsackValueToWeightComparator implements Comparator<KnapsackItem>
	{
		@Override
		public int compare(KnapsackItem item1, KnapsackItem item2)
		{
			Float ratio1 = item1.getValue() / item1.getWeight();
			Float ratio2 = item2.getValue() / item2.getWeight();
			return ratio1.compareTo(ratio2); 
		}		
	}
}
