/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.graphic;

import java.io.Serializable;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RGB implements Serializable {

   private static final long serialVersionUID = 1L;

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
    * @see <a href=
    *      "https://stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color/56678483#56678483">stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color</a>
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
      final int prime = 31;
      int result = 1;
      result = prime * result + blue;
      result = prime * result + green;
      result = prime * result + red;
      return result;
   }

   @Override
   public String toString() {
      return "RGB[" + red + "," + green + "," + blue + "]";
   }
}
