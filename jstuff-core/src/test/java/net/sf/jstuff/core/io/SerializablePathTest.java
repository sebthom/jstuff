/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SerializablePathTest {

   @Test
   @SuppressWarnings("resource")
   public void testSerializablePath() throws Exception {
      final var originalPath = Path.of("some/path/to/test.txt");
      final var serializablePath = SerializablePath.of(originalPath);

      /*
       * serialize
       */
      final var baos = new ByteArrayOutputStream();
      final var oos = new ObjectOutputStream(baos);
      oos.writeObject(serializablePath);
      oos.close();

      /*
       * deserialize
       */
      final var bais = new ByteArrayInputStream(baos.toByteArray());
      final var ois = new ObjectInputStream(bais);
      final var deserializedPath = (SerializablePath) ois.readObject();

      assertThat(deserializedPath).isEqualTo(serializablePath);
      assertThat(deserializedPath).isEqualTo(serializablePath.getWrapped());
      assertThat(deserializedPath.toString()).isEqualTo(originalPath.toString());
      assertThat(deserializedPath.getFileSystem()).isEqualTo(serializablePath.getFileSystem());
   }
}
