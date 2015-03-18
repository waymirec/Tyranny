package net.waymire.tyranny.client.util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

public class MaterialUtils 
{
	public MaterialUtils()
    {
    }
 
 
    public static Material makeMaterial(AssetManager am, String name, ColorRGBA color)
    {
        Material mat = new Material(am, name);
        mat.setColor("Color", color);
        return mat;
    }
}
