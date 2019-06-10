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
package de.jcup.asciidoctoreditor.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.asciidoctor.ast.DocumentRuby;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;

public class FileIncludeIncludeProcessor extends IncludeProcessor { 

  public FileIncludeIncludeProcessor(Map<String, Object> config) {
    super(config);
  }

  @Override
  public boolean handles(String target) {
    return target.startsWith("file://"); 
  }

  @Override
  public void process(DocumentRuby document, PreprocessorReader reader, String target,
            Map<String, Object> attributes) {

    StringBuilder content = readContent(target);
    reader.push_include(content.toString(), target, target, 1, attributes); 

  }

  private StringBuilder readContent(String target) {

    StringBuilder content = new StringBuilder();

    try {

      URL url = new URL(target);
      InputStream openStream = url.openStream();

      BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(openStream));

      String line = null;
			while ((line = bufferedReader.readLine()) != null) {
        content.append(line);
      }

      bufferedReader.close();

      } catch (MalformedURLException e) {
          throw new IllegalArgumentException(e);
      } catch (IOException e) {
          throw new IllegalArgumentException(e);
      }
      return content;
  }

}