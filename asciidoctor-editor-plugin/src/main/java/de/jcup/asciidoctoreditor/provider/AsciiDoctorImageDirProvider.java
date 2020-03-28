package de.jcup.asciidoctoreditor.provider;

import static de.jcup.asciidoctoreditor.provider.AttributeSearchParameter.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;

/**
 * This provider will have cached information about which image directory is
 * used for an editor file. The data will be lazily created and cached
 * 
 * @author albert
 *
 */
public class AsciiDoctorImageDirProvider extends AbstractAsciiDoctorProvider {

    private Map<File, ImageDirData> map = new HashMap<>();
    private AttributeSearch search = new AttributeSearch();

    AsciiDoctorImageDirProvider(AsciiDoctorProjectProviderContext context) {
        super(context);
    }

    public String getImagesDirAbsolutePathOrNull() {
        Path imageDir = getImagesDirAbsoluteFileOrNull();
        if (imageDir == null) {
            if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                getContext().getLogAdapter().logInfo("image dir is null");
            }
            return null;
        }
        if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
            getContext().getLogAdapter().logInfo("image dir is: "+imageDir.toString());
        }
        return imageDir.toString();
    }

    public Path getImagesDirAbsoluteFileOrNull() {
        ImageDirData data;
        try {
            data = getCachedSourceImagesPath();
        } catch (Exception e) {
            getContext().getLogAdapter().logError("Was not able to resolve image dir", e);
            return null;
        }
        if (data == null) {
            return null;
        }
        return data.imageDirAbsolutePath;
    }

    private ImageDirData getCachedSourceImagesPath() throws IOException {
        File editorFileOrNull = getContext().getEditorFileOrNull();
        if (editorFileOrNull == null) {
            return null;
        }
        /* file match contains the raw (trimmed) value found inside files */
        ImageDirData matchForEditorFile = map.get(editorFileOrNull);
        if (matchForEditorFile != null) {
            return matchForEditorFile;
        }
        /* nothing found - not even a "not found", so recreate */
        FileMatch match = null;
        try {
            match = resolveFileMatchOrNull(editorFileOrNull);
        } catch (IOException e) {
            getContext().getLogAdapter().logError("Cannot resolve source image path for editor file" + editorFileOrNull, e);
        }
        if (match == null) {
            match = new FileMatch(editorFileOrNull, null);
        }
        ImageDirData data = new ImageDirData();
        data.match = match;
        createImageDirAbsoluteForMatch(data);

        map.put(editorFileOrNull, data);
        return data;
    }

    private void createImageDirAbsoluteForMatch(ImageDirData data) throws IOException {
        if (data.match == null) {
            return;
        }
        if (data.match.getFile() == null) {
            return;
        }
        if (data.match.getValue() == null) {
            return;
        }
        Path path = data.match.getFile().getParentFile().toPath();
        Path path2 = path.resolve(data.match.getValue());

        Path absolutePath = path2.toRealPath();
        data.imageDirAbsolutePath = absolutePath;
    }

    private FileMatch resolveFileMatchOrNull(File file) throws IOException {
        FileMatch match = search.resolveFirstAttributeFoundTopDown(IMAGES_DIR_ATTRIBUTE, file);
        if (match != null) {
            return match;
        }
        /* not inside this file, so we use top images dir */
        File rootDirectory = getContext().getRootDirectoryProvider().getRootDirectory();
        match = search.resolveFirstAttributeFoundTopDown(IMAGES_DIR_ATTRIBUTE, rootDirectory);

        return match;
    }

    @Override
    protected void reset() {
        map.clear();
    }

    private class ImageDirData {
        FileMatch match;
        Path imageDirAbsolutePath;
    }

}
