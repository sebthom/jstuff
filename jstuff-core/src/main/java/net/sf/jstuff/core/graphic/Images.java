/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.graphic;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
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
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Images {
   private static final class LazyInitialized {
      private static final GraphicsConfiguration GC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
         .getDefaultConfiguration();
   }

   public static BufferedImage resize(final Image image, final int newWidth, final int newHeight) {
      Args.notNull("image", image);
      return resize(toBufferedImage(image), newWidth, newHeight);
   }

   public static BufferedImage resize(final BufferedImage image, final int newWidth, final int newHeight) {
      Args.notNull("image", image);

      final var resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
      final Graphics2D g2d = resizedImage.createGraphics();

      // use RenderingHints to improve image quality
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
      g2d.dispose();

      return resizedImage;
   }

   public static BufferedImage toBufferedImage(final Image image) throws HeadlessException {
      Args.notNull("image", image);

      return toBufferedImage(image, LazyInitialized.GC);
   }

   public static BufferedImage toBufferedImage(final Image image, final GraphicsConfiguration gc) {
      Args.notNull("image", image);
      Args.notNull("gc", gc);

      if (image instanceof final BufferedImage buffImg)
         return buffImg;

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

      if (image instanceof final BufferedImage buffImg)
         return buffImg;

      final var keys = image.getPropertyNames();
      final Hashtable<String, Object> props;
      if (keys == null) {
         props = null;
      } else {
         props = new Hashtable<>();
         for (final String key : keys) {
            props.put(key, image.getProperty(key));
         }
      }

      final ColorModel cm = image.getColorModel();
      final WritableRaster raster = cm.createCompatibleWritableRaster(image.getWidth(), image.getHeight());
      final var ret = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), props);
      image.copyData(raster);
      return ret;
   }

   @SuppressWarnings("resource")
   public static byte[] toJPEG(final BufferedImage image) throws IOException {
      Args.notNull("image", image);

      final var bos = new FastByteArrayOutputStream();
      ImageIO.write(image, "jpg", bos);
      return bos.toByteArray();
   }
}
