/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.graphic;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Images {
   private static final class LazyInitialized {
      private static final GraphicsConfiguration GC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
   }

   public static BufferedImage toBufferedImage(final Image image) throws HeadlessException {
      Args.notNull("image", image);

      return toBufferedImage(image, LazyInitialized.GC);
   }

   public static BufferedImage toBufferedImage(final Image image, final GraphicsConfiguration gc) {
      Args.notNull("image", image);
      Args.notNull("gc", gc);

      if (image instanceof BufferedImage)
         return (BufferedImage) image;

      final int w = image.getWidth(null);
      final int h = image.getHeight(null);

      final BufferedImage bimage = gc.createCompatibleImage(w, h, 1);
      final Graphics2D g = bimage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();

      return bimage;
   }

   public static BufferedImage toBufferedImage(final RenderedImage image) {
      Args.notNull("image", image);

      if (image instanceof BufferedImage)
         return (BufferedImage) image;

      final String[] keys = image.getPropertyNames();
      final Hashtable<String, Object> props;
      if (keys == null) {
         props = null;
      } else {
         props = new Hashtable<String, Object>();
         for (final String key : keys) {
            props.put(key, image.getProperty(key));
         }
      }

      final ColorModel cm = image.getColorModel();
      final WritableRaster raster = cm.createCompatibleWritableRaster(image.getWidth(), image.getHeight());
      final BufferedImage ret = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), props);
      image.copyData(raster);
      return ret;
   }

   @SuppressWarnings("resource")
   public static byte[] toJPEG(final BufferedImage image) throws IOException {
      Args.notNull("image", image);

      final FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
      ImageIO.write(image, "jpg", bos);
      return bos.toByteArray();
   }

   public static BufferedImage toScaledImage(final Image image, final int width, final int height) {
      Args.notNull("image", image);

      final Image scaled = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
      final BufferedImage scaledBuffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final Graphics2D g = scaledBuffered.createGraphics();
      g.drawImage(scaled, 0, 0, null);
      g.dispose();
      return scaledBuffered;
   }
}
