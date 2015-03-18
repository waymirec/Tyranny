package net.waymire.tyranny.common;

import java.nio.ByteOrder;

public final class Environment {
	private final static String javaHome = JavaEnvironment.getProperty("java.home");
	private final static String fileEncoding = JavaEnvironment.getProperty("file.encoding");
	private final static String fileSeparator = JavaEnvironment.getProperty("file.separator");
	private final static String pathSeparator = JavaEnvironment.getProperty("path.separator");
	private final static String javaClasspath = JavaEnvironment.getProperty("java.class.path");
	private final static String architecture = JavaEnvironment.getProperty("os.arch"); //x86
	private final static String dataModel = JavaEnvironment.getProperty("sun.arch.data.model"); //32
	private final static String operatingSystem = JavaEnvironment.getProperty("os.name"); //Windows XP
	private final static String operatingSystemVersion = JavaEnvironment.getProperty("os.version"); //5.1
	private final static String operatingSystemPatchLevel = JavaEnvironment.getProperty("sun.os.patch.level"); //Service Pack 2
	private final static String owner = JavaEnvironment.getProperty("user.name"); //John Doe
	private final static String language = JavaEnvironment.getProperty("user.language"); //en
	private final static String country = JavaEnvironment.getProperty("user.country"); //US
	private final static String timezone = JavaEnvironment.getProperty("user.timezone");
	private final static String userDir = JavaEnvironment.getProperty("user.dir"); //C:\path\to\files
	private final static String tempDir = JavaEnvironment.getProperty("java.io.tmpdir"); //C:\temp
	private final static String runtimeVersion = JavaEnvironment.getProperty("java.runtime.version"); //1.6.0_14-b08
	private final static String javaVersion = JavaEnvironment.getProperty("java.version"); //1.6.0_14
	private final static String endian = ByteOrder.nativeOrder().toString();
	private final static String runtimeName = JavaEnvironment.getProperty("java.runtime.name"); //Java(TM) SE Runtime Environment
	private final static String virtualMachineVersion = JavaEnvironment.getProperty("java.vm.version"); //14.0-b16
	private final static String virtualMachineVendor = JavaEnvironment.getProperty("java.vm.vendor"); //Sun Microsystems Inc.
	private final static String virtualMachineName = JavaEnvironment.getProperty("java.vm.name"); //Java HotSpot(TM) Client VM
	private final static String specificationName = JavaEnvironment.getProperty("java.specification.name"); //Java Platform API Specification
	
	private Environment(String path) { }

	public static String getJavaHome() {
		return javaHome;
	}

	public static String getFileEncoding() {
		return fileEncoding;
	}

	public static String getFileSeparator() {
		return fileSeparator;
	}

	public static String getPathSeparator() {
		return pathSeparator;
	}

	public static String getJavaClasspath() {
		return javaClasspath;
	}

	public static String getArchitecture() {
		return architecture;
	}

	public static String getDataModel() {
		return dataModel;
	}

	public static String getOperatingSystem() {
		return operatingSystem;
	}

	public static String getOperatingSystemVersion() {
		return operatingSystemVersion;
	}

	public static String getOperatingSystemPatchLevel() {
		return operatingSystemPatchLevel;
	}

	public static String getOwner() {
		return owner;
	}

	public static String getLanguage() {
		return language;
	}

	public static String getCountry() {
		return country;
	}

	public static String getTimezone() {
		return timezone;
	}

	public static String getUserDir() {
		return userDir;
	}

	public static String getTempDir() {
		return tempDir;
	}

	public static String getRuntimeVersion() {
		return runtimeVersion;
	}

	public static String getJavaVersion() {
		return javaVersion;
	}

	public static String getEndian() {
		return endian;
	}

	public static String getRuntimeName() {
		return runtimeName;
	}

	public static String getVirtualMachineVersion() {
		return virtualMachineVersion;
	}

	public static String getVirtualMachineVendor() {
		return virtualMachineVendor;
	}

	public static String getVirtualMachineName() {
		return virtualMachineName;
	}

	public static String getSpecificationName() {
		return specificationName;
	}
}
