/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.graphic;

import static java.lang.Integer.parseInt;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RGBA implements Serializable {
   private static final long serialVersionUID = 1L;

   public static RGBA[] createLinearGradient(final RGBA from, final RGBA to, final int steps) {
      if (steps < 2) {
         Args.min("steps", steps, 2);
      }

      final var gradient = new RGBA[steps];
      gradient[0] = from;

      // Calculate the difference and step increment for each color component
      final var stepR = (float) (to.red - from.red) / (steps - 1);
      final var stepG = (float) (to.green - from.green) / (steps - 1);
      final var stepB = (float) (to.blue - from.blue) / (steps - 1);
      final var stepA = (float) (to.alpha - from.alpha) / (steps - 1);

      for (int i = 1; i < steps; i++) {
         int newRed = Math.round(from.red + stepR * i);
         int newGreen = Math.round(from.green + stepG * i);
         int newBlue = Math.round(from.blue + stepB * i);
         int newAlpha = Math.round(from.alpha + stepA * i);

         // Clamp color values to the valid range [0, 255]
         newRed = Math.max(0, Math.min(255, newRed));
         newGreen = Math.max(0, Math.min(255, newGreen));
         newBlue = Math.max(0, Math.min(255, newBlue));
         newAlpha = Math.max(0, Math.min(255, newAlpha));

         gradient[i] = new RGBA(newRed, newGreen, newBlue, newAlpha);
      }

      return gradient;
   }

   /**
    * Creates an RGBA instance from a hexadecimal color code. If the alpha value is missing, "FF" is assumed.
    *
    * @param hex the hexadecimal color code, e.g., "#FFAABB88", "FFAABB88", "#FAB8", or "FAB8", "0xFFAABB88", "xFFAABB88"
    * @return an RGBA object representing the color
    * @throws IllegalArgumentException if the hex string is invalid
    */
   public static RGBA fromHex(final String hex) {
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
      if (length != 3 && length != 4 && length != 6 && length != 8)
         throw new IllegalArgumentException("Hex color code must be 3, 4, 6 or 8 characters long (excluding prefix), but was [" + hex
               + "]");

      try {
         final int red, green, blue, alpha;

         if (length < 5) {
            red = parseInt("" + hex.charAt(offset) + hex.charAt(offset), 16);
            green = parseInt("" + hex.charAt(offset + 1) + hex.charAt(offset + 1), 16);
            blue = parseInt("" + hex.charAt(offset + 2) + hex.charAt(offset + 2), 16);
            alpha = length == 3 ? 255 : parseInt("" + hex.charAt(offset + 3) + hex.charAt(offset + 3), 16);
         } else {
            red = parseInt(hex.substring(offset, offset + 2), 16);
            green = parseInt(hex.substring(offset + 2, offset + 4), 16);
            blue = parseInt(hex.substring(offset + 4, offset + 6), 16);
            alpha = length == 6 ? 255 : parseInt(hex.substring(offset + 6, offset + 8), 16);
         }

         return new RGBA(red, green, blue, alpha);
      } catch (final NumberFormatException e) {
         throw new IllegalArgumentException("Invalid hex color code [" + hex + "]", e);
      }
   }

   public final int red;
   public final int green;
   public final int blue;
   public final int alpha;

   /**
    * @param red 0-255
    * @param green 0-255
    * @param blue 0-255
    * @param alpha 0-255
    */
   public RGBA(final int red, final int green, final int blue, final int alpha) {
      Args.inRange("red", red, 0, 255);
      Args.inRange("green", green, 0, 255);
      Args.inRange("blue", blue, 0, 255);
      Args.inRange("alpha", alpha, 0, 255);

      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   public int getAlpha() {
      return alpha;
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

   public RGBA withBlue(final int blue) {
      return new RGBA(red, green, blue, alpha);
   }

   public RGBA withGreen(final int blue) {
      return new RGBA(red, green, blue, alpha);
   }

   public RGBA withRed(final int red) {
      return new RGBA(red, green, blue, alpha);
   }

   @Override
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final RGBA other = (RGBA) obj;
      return blue == other.blue && green == other.green && red == other.red && alpha == other.alpha;
   }

   @Override
   public int hashCode() {
      return Objects.hash(blue, green, red, alpha);
   }

   public RGB toRGB() {
      return new RGB(red, green, blue);
   }

   /**
    * Converts this RGBA color to its hexadecimal color code representation.
    *
    * @return a string representing the hexadecimal color code, e.g., "#FFAABB88"
    */
   public String toHex() {
      return toHex("#");
   }

   /**
    * Converts this RGBA color to its hexadecimal color code representation.
    *
    * @param prefix the prefix to use (e.g., "#", "0x", "x", or "")
    * @return a string representing the hexadecimal color code, e.g., "#FFAABB88"
    */
   public String toHex(final String prefix) {
      return String.format("%s%02X%02X%02X%02X", prefix, red, green, blue, alpha);
   }

   @Override
   public String toString() {
      return "RGBA[" + red + "," + green + "," + blue + "," + alpha + "]";
   }
}
