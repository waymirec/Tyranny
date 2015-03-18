package net.waymire.tyranny.common.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;

public class SystemUtil {

	public static final String getClipboardContents(Object requestor)
	{
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(requestor);
		if (t != null)
		{
			DataFlavor df = DataFlavor.stringFlavor;
			if (df != null)
			{
				try
				{
					Reader r = df.getReaderForText(t);
					char[] charBuf = new char[512];
					StringBuffer buf = new StringBuffer();
					int n;
					while ((n = r.read(charBuf, 0, charBuf.length)) > 0)
					{
						buf.append(charBuf, 0, n);
					}
					r.close();
					return (buf.toString());
				} catch (IOException ex)
				{
					ex.printStackTrace();
				} catch (UnsupportedFlavorException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	public static final boolean isClipboardContainingText(Object requestor)
	{
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(requestor);
		return t != null && (t.isDataFlavorSupported(DataFlavor.stringFlavor) || t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor()));
	}

	public static final void setClipboardContents(String s)
	{
		StringSelection selection = new StringSelection(s);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}
}
