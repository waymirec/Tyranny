package net.waymire.tyranny.instrumentation;

import java.lang.instrument.Instrumentation;

public class Agent
{
	public static void premain(String agentArgs, Instrumentation inst)
	{
		inst.addTransformer(new TraceTransformer());
		inst.addTransformer(new LockingTransformer());
	}
}
