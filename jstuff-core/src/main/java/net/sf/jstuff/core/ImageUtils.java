/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ImageUtils
{
	private static GraphicsConfiguration _gc;

	private static synchronized GraphicsConfiguration getDefaultConfiguration()
	{
		if (_gc == null)
		{
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final GraphicsDevice gd = ge.getDefaultScreenDevice(); // not supported in headless mode
			_gc = gd.getDefaultConfiguration();
		}
		return _gc;
	}

	public static byte[] getJPEG(final BufferedImage image) throws IOException
	{
		Args.notNull("image", image);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", bos);
		return bos.toByteArray();
	}

	public static BufferedImage getScaledImage(final Image image, final int width, final int height)
	{
		Args.notNull("image", image);

		final Image scaled = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
		final BufferedImage scaledBuffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = scaledBuffered.createGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return scaledBuffered;
	}

	public static BufferedImage toBufferedImage(final Image image)
	{
		Args.notNull("image", image);

		return toBufferedImage(image, getDefaultConfiguration());
	}

	public static BufferedImage toBufferedImage(final Image image, final GraphicsConfiguration gc)
	{
		Args.notNull("image", image);
		Args.notNull("gc", gc);

		if (image instanceof BufferedImage) return (BufferedImage) image;

		final int w = image.getWidth(null);
		final int h = image.getHeight(null);

		final BufferedImage bimage = gc.createCompatibleImage(w, h, 1);
		final Graphics2D g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public static BufferedImage toBufferedImage(final RenderedImage image)
	{
		Args.notNull("image", image);

		if (image instanceof BufferedImage) return (BufferedImage) image;

		final ColorModel cm = image.getColorModel();
		final int w = image.getWidth();
		final int h = image.getHeight();

		final WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
		final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		final Hashtable<String, Object> props = new Hashtable<String, Object>();
		final String[] keys = image.getPropertyNames();

		if (keys != null) for (final String key : keys)
			props.put(key, image.getProperty(key));
		final BufferedImage ret = new BufferedImage(cm, raster, isAlphaPremultiplied, props);
		image.copyData(raster);
		return ret;
	}

	protected ImageUtils()
	{
		super();
	}
}
