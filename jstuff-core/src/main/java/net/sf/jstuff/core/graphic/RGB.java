/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.graphic;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RGB implements Serializable {

   private static final long serialVersionUID = 1L;

   public static RGB[] createLinearGradient(final RGB from, final RGB to, final int steps) {
      if (steps < 2) {
         Args.min("steps", steps, 2);
      }

      final RGB[] gradient = new RGB[steps];
      gradient[0] = from;

      // Calculate the difference and step increment for each color component
      final float stepR = (float) (to.red - from.red) / (steps - 1);
      final float stepG = (float) (to.green - from.green) / (steps - 1);
      final float stepB = (float) (to.blue - from.blue) / (steps - 1);

      for (int i = 1; i < steps; i++) {
         int newRed = Math.round(from.red + stepR * i);
         int newGreen = Math.round(from.green + stepG * i);
         int newBlue = Math.round(from.blue + stepB * i);

         // Clamp color values to the valid range [0, 255]
         newRed = Math.max(0, Math.min(255, newRed));
         newGreen = Math.max(0, Math.min(255, newGreen));
         newBlue = Math.max(0, Math.min(255, newBlue));

         gradient[i] = new RGB(newRed, newGreen, newBlue);
      }

      return gradient;
   }

   /**
    * Creates an RGB instance from a hexadecimal color code.
    *
    * @param hex the hexadecimal color code, e.g., "#FFAABB", "FFAABB", "#FAB", or "FAB", "0xFFAABB", "xFFAABB"
    * @return an RGB object representing the color
    * @throws IllegalArgumentException if the hex string is invalid
    */
   public static RGB fromHex(final String hex) {
      Args.notNull("hex", hex);

      // Detect prefix and determine offset
      final int offset;
      if (hex.startsWith("#") || hex.startsWith("x")) {
         offset = 1;
      } else if (hex.startsWith("0x")) {
         offset = 2;
      } else {
         offset = 0;
      }

      final int length = hex.length() - offset;
      if (length != 3 && length != 6)
         throw new IllegalArgumentException("Hex color code must be 3 or 6 characters long (excluding prefix), but was [" + hex + "]");

      try {
         final int red, green, blue;

         if (length == 3) {
            red = Integer.parseInt("" + hex.charAt(offset) + hex.charAt(offset), 16);
            green = Integer.parseInt("" + hex.charAt(offset + 1) + hex.charAt(offset + 1), 16);
            blue = Integer.parseInt("" + hex.charAt(offset + 2) + hex.charAt(offset + 2), 16);
         } else {
            red = Integer.parseInt(hex.substring(offset, offset + 2), 16);
            green = Integer.parseInt(hex.substring(offset + 2, offset + 4), 16);
            blue = Integer.parseInt(hex.substring(offset + 4, offset + 6), 16);
         }

         return new RGB(red, green, blue);
      } catch (final NumberFormatException e) {
         throw new IllegalArgumentException("Invalid hex color code [" + hex + "]", e);
      }
   }

   public final int red;
   public final int green;
   public final int blue;

   /**
    * @param red 0-255
    * @param green 0-255
    * @param blue 0-255
    */
   public RGB(final int red, final int green, final int blue) {
      Args.inRange("red", red, 0, 255);
      Args.inRange("green", green, 0, 255);
      Args.inRange("blue", blue, 0, 255);

      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public int getBlue() {
      return blue;
   }

   public int getGreen() {
      return green;
   }

   public int getRed() {
      return red;
   }

   public RGB withBlue(final int blue) {
      return new RGB(red, green, blue);
   }

   public RGB withGreen(final int blue) {
      return new RGB(red, green, blue);
   }

   public RGB withRed(final int red) {
      return new RGB(red, green, blue);
   }

   /**
    * @see <a href="https://www.w3.org/TR/AERT/#color-contrast">w3.org/TR/AERT/#color-contrast</a>
    */
   public int getBrightnessFast() {
      return (int) (0.299 * red + 0.587 * green + 0.114 * blue);
   }

   /**
    * @return 0.0-100.0
    *
    * @see <a href="https://stackoverflow.com/a/56678483">
    *      stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color</a>
    */
   public double getBrightnessPrecise() {
      // step one: convert 8 bit ints to 0.0-1.0
      final double vR = red / 255.0;
      final double vG = green / 255.0;
      final double vB = blue / 255.0;

      // step two:
      final Function<Double, Double> sRGBtoLin = colorChannel -> colorChannel <= 0.04045 //
            ? colorChannel / 12.92
            : Math.pow((colorChannel + 0.055) / 1.055, 2.4);

      // step three: find Luminance (Y)
      final double Y = 0.2126 * sRGBtoLin.apply(vR) // CHECKSTYLE:IGNORE .*
            + 0.7152 * sRGBtoLin.apply(vG) //
            + 0.0722 * sRGBtoLin.apply(vB);

      // step four: YtoLstar
      return Y <= 216 / 24389.0 //
            ? Y * (24389.0 / 27.0)
            : Math.pow(Y, 1.0 / 3.0) * 116 - 16;
   }

   @Override
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final RGB other = (RGB) obj;
      return blue == other.blue && green == other.green && red == other.red;
   }

   @Override
   public int hashCode() {
      return Objects.hash(blue, green, red);
   }

   @Override
   public String toString() {
      return "RGB[" + red + "," + green + "," + blue + "]";
   }

   /**
    * Converts this RGB color to its hexadecimal color code representation.
    *
    * @return a string representing the hexadecimal color code, e.g., "#FFAABB"
    */
   public String toHex() {
      return toHex("#");
   }

   /**
    * Converts this RGB color to its hexadecimal color code representation.
    *
    * @param prefix the prefix to use (e.g., "#", "0x", "x", or "")
    * @return a string representing the hexadecimal color code, e.g., "#FFAABB"
    */
   public String toHex(final String prefix) {
      return String.format("%s%02X%02X%02X", prefix, red, green, blue);
   }
}
