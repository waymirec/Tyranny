package net.waymire.tyranny.common.annotation;

public class TraceTest {
	public static void main(String[] args) {
		TraceTest test = new TraceTest();
		test.test1();
	}
	
	@Trace
	public void test1() {
		System.out.println("Test1");
		System.out.println("Creating exception...");
		try {
			test2();
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void test2() throws Exception {
		throw new Exception();
	}
}
