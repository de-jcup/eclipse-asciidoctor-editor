/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipSupport {

    public void unzip(File zipFile, File targetFolder) throws IOException {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipfile may not be null");
        }

        if (targetFolder == null) {
            throw new IllegalArgumentException("target folder may not be null");
        }
        if (!zipFile.exists()) {
            throw new FileNotFoundException("file not found:" + zipFile.getAbsolutePath());
        }
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        if (!targetFolder.isDirectory()) {
            throw new IOException("Target is not a directory:" + targetFolder.getAbsolutePath());
        }
        ZipEntry zipEntry = null;
        String fileName = null;
        File newFile = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            byte[] buffer = new byte[1024];
            zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    fileName = zipEntry.getName();
                    newFile = new File(targetFolder, fileName);
                    File parentFile = newFile.getParentFile();
                    if (!parentFile.exists()) {
                        if (!parentFile.mkdirs()) {
                            throw new IOException("Was not able to create directory:" + parentFile.getAbsolutePath());
                        }
                    }
                    if (newFile.exists()) {
                        newFile.delete();
                    }
                    newFile.createNewFile();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            throw new IOException("Problems on zipentry:" + zipEntry + " - new file:" + newFile, e);
        }
    }
}
