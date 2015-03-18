package net.waymire.tyranny.common;

public class SerialNumberManager {
	/*
	private final DatabaseManager dbManager;
	private final String prefix;
	private final List<Object> groupLocks = new ArrayList<Object>();
	private int groupCount = 0;
	private int lastGroupIndex = 0;
	List<Integer> groups;
	
	@SuppressWarnings("unchecked")
	public SerialNumberManager(String prefix,DatabaseManager dbManager)
	{
		this.dbManager = dbManager;
		this.groups = (List<Integer>)dbManager.getConfigAccessor().get("SNGROUPS").getValue();
		this.groupCount = groups.size();
		this.prefix = prefix;
		for(int i=0;i<groupCount;i++)
		{
			groupLocks.add(new Object());
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getNextSerialNumber()
	{
		String group = getNextGroup();
		synchronized(groupLocks.get(lastGroupIndex))
		{
			String key = "SNGROUP" + group;
			Transaction transaction = dbManager.beginTransaction();
			ConfigObject obj = dbManager.getConfigAccessor().get(key);
			Integer serial = ((List<Integer>)obj.getValue()).remove(0);
			dbManager.getConfigAccessor().put(obj,transaction);
			transaction.commit();
			return prefix + "-" + group + "-" + serial;
		}
	}
	
	private synchronized String getNextGroup()
	{
		lastGroupIndex = (lastGroupIndex == (groupCount-1)) ? 0 : ++lastGroupIndex;
		return String.format("%02d", groups.get(lastGroupIndex));
	}
	*/
}
