package net.waymire.tyranny.common.util;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class ResourceUtil {
	
	public static Image createImage(Class<?> clazz,String path,String description)
	{
		URL imageURL = clazz.getResource(path);
		if(imageURL == null)
		{
			return null;
		}
		return new ImageIcon(imageURL,description).getImage();
	}
}
