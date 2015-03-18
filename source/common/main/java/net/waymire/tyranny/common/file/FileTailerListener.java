package net.waymire.tyranny.common.file;

import java.util.List;

public interface FileTailerListener
{
	public void onFileTailRead(List<String> lines);
}
