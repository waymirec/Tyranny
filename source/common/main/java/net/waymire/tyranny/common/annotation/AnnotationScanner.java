package net.waymire.tyranny.common.annotation;

import java.util.List;
import java.util.Map;

public interface AnnotationScanner<T> {
	public List<AnnotationScanResult> scan(List<T> files,String annotation);
	public Map<String,List<AnnotationScanResult>> scan(List<T> files);
}
