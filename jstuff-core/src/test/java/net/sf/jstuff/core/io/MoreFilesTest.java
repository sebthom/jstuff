/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MoreFilesTest {

   private void _testFind(final String rootPath, final String globPattern, final int exceptedFiles, final int exceptedFolders) throws IOException {
      assertThat(MoreFiles.findFiles(Paths.get(rootPath), globPattern)).hasSize(exceptedFiles);
      assertThat(MoreFiles.findDirectories(Paths.get(rootPath), globPattern)).hasSize(exceptedFolders);
      assertThat(MoreFiles.find(Paths.get(rootPath), globPattern, true, true)).hasSize(exceptedFiles + exceptedFolders);
   }

   @Test
   public void testFind() throws IOException {
      _testFind("", "**/FileUtils.java", 1, 0);
      _testFind(".", "**/FileUtils.java", 1, 0);
      _testFind("./", "**/FileUtils.java", 1, 0);

      // match children of rootfolder
      _testFind("", "src/test/resources/rootfolder/*", 0, 2);
      _testFind("src/test", "resources/rootfolder/*", 0, 2);
      _testFind("src/test/resources/rootfolder", "*", 0, 2);

      // match children of folder1
      _testFind("", "src/test/resources/rootfolder/folder1/*", 3, 3);
      _testFind("src/test", "resources/rootfolder/folder1/*", 3, 3);
      _testFind("src/test/resources/rootfolder/folder1", "*", 3, 3);

      _testFind("", "src/test/resources/rootfolder/folder1/file*.txt", 2, 0);
      _testFind("src/test", "resources/rootfolder/folder1/file*.txt", 2, 0);
      _testFind("src/test/resources/rootfolder/folder1", "file*.txt", 2, 0);

      // ends with "/", thus matches nothing
      _testFind("", "src/test/resources/rootfolder/*/", 0, 0);
      _testFind("src/test", "resources/rootfolder/*/", 0, 0);
      _testFind("src/test/resources/rootfolder", "*/", 0, 0);

      _testFind("", "src/test/resources/rootfolder/**", 24, 8); // match (sub-)children of current folder
      _testFind("", "src/test/resources/rootfolder/**/", 0, 0); // ends with "/", thus matches nothing
      _testFind("", "src/test/resources/rootfolder/**/*", 24, 8); // match (sub-)children of rootfolder
      _testFind("", "src/test/resources/rootfolder/*/**", 24, 6); // match (sub-)children of first-level sub folders
      _testFind("", "src/test/resources/rootfolder/*/*", 6, 6); // match children of first-level sub folders
      _testFind("", "src/test/resources/rootfolder/*/*/*", 18, 0); // match children of two-level sub folders
      _testFind("", "src/test/resources/rootfolder/*A*", 0, 0); // match children of rootfolder containing "A"
      _testFind("", "src/test/resources/rootfolder/folder1/**/*A*", 8, 2); // match (sub-)children of folder1 ending with "A"
      _testFind("", "src/test/resources/rootfolder/folder1/**/?ileA.txt", 4, 0);
      _testFind("", "src/test/resources/rootfolder/folder1/**/fileA.txt", 4, 0);
      _testFind("", "src/test/resources/rootfolder/folder1/*/**/fileA.txt", 3, 0);
      _testFind("", "src/test/resources/rootfolder/folder1/**fileA.txt", 8, 0); // this also matches otherfileA.txt
      _testFind("", "src/test/resources/rootfolder/folder1/**/*fileA.txt", 8, 0); // this also matches otherfileA.txt
      _testFind("", "src/test/resources/rootfolder/**/folder?", 0, 6);
      _testFind("", "src/test/resources/rootfolder/**folder?", 0, 8); // this also matches otherfolderA
      _testFind("", "src/test/resources/rootfolder/**/*folder?", 0, 8); // this also matches otherfolderA
      _testFind("", "src/test/resources/rootfolder/**/folderA", 0, 2);
      _testFind("", "src/test/resources/rootfolder/**folderA", 0, 4); // this also matches otherfolderA
      _testFind("", "src/test/resources/rootfolder/**/*folderA", 0, 4); // this also matches otherfolderA

      _testFind("", "src/test/resources/rootfolder/**/*(*", 8, 2); // match (sub-)children of rootfolder containing "("
      _testFind("", "src/test/resources/rootfolder/**/*[AB]", 0, 6); // match (sub-)children of rootfolder ending with "A" or "B"
      _testFind("", "src/test/resources/rootfolder/**/{other,fileA}*", 16, 2); // match (sub-)children of rootfolder containing "other" or "fileA"
      _testFind("", "src/test/resources/rootfolder/**/*\\[*", 8, 2); // match (sub-)children of rootfolder containing "["
      _testFind("", "src/test/resources/rootfolder/**/*\\{*", 8, 2); // match (sub-)children of rootfolder containing "{"
   }
}
