package de.jcup.asciidoctoreditor.tools;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.asciidoctoreditor.SystemOutLogAdapter;
import de.jcup.asciidoctoreditor.globalmodel.AsciidocFile;
import de.jcup.asciidoctoreditor.globalmodel.AsciidocImageNode;
import de.jcup.asciidoctoreditor.globalmodel.AsciidocIncludeNode;
import de.jcup.asciidoctoreditor.globalmodel.GlobalAsciidocModel;
import de.jcup.asciidoctoreditor.globalmodel.GlobalAsciidocModelRecursionFinder;

public class DocumentPlantUMLMindMapGenerator {

    private static final int MAX_NODE_DEPTH = 64*2; // like asciidoc max include depth (64) * 2 because of additional "include" node

    private static GlobalAsciidocModelRecursionFinder recursionFinder = new GlobalAsciidocModelRecursionFinder();
    
    public String generate(File startFile, GlobalAsciidocModel model) {
        AsciidocFileGeneratorContext context = new AsciidocFileGeneratorContext();

        AsciidocFile file = model.getAsciidocFileOrNull(startFile, new SystemOutLogAdapter());
        if (file == null) {
            return "";
        }

        context.append("@startmindmap\n");

        generate(file, context, 1,model);

        context.append("@endmindmap\n");

        return context.getContent();
    }

    private void generate(AsciidocFile file, AsciidocFileGeneratorContext context, int depth, GlobalAsciidocModel model) {
        /* fuse */
        if (depth > MAX_NODE_DEPTH) {
            context.append(mindMapPrefix(depth, false));
            context.append(" STOP - reached max depth level of ");
            context.append(MAX_NODE_DEPTH+"\n");

            context.append("' PROBLEM found!\n");
            context.append("' ----------------------------");
            context.append("' Maximum depth reached: " + depth + " - will stop here!\n");
            context.append("' This is a fuse to prevent endless processing.\n");
            return;
        }
        boolean recursive = recursionFinder.isRecursive(file);
        boolean alreadyGenerated = context.isAlreadyGenerated(file);
        
        boolean fileExists = file.getFile().exists();

        boolean problem = (recursive) || !fileExists;

        /* generate content */
        context.append(mindMapPrefix(depth, problem));
        if (!fileExists) {
            context.append("[#darkorange]");
        }
        if (recursive) {
            context.append("[#crimson]");
        }
        context.append(" ");

        appendFileNameWithShortPathAndHandleNotExisting(file, context, fileExists);

        if (recursive && alreadyGenerated) { // the first call will be generated
            context.append(warnMessage("recursion/loop detected!"));
        }
        if (alreadyGenerated && !recursive) {
            context.append(" <i>[used again:"+context.getAmountOfGenerations(file)+"]</i>");
        }
        context.append("\n");
        
        if (recursive && alreadyGenerated) {
            return;
        }
        context.markAsGenerated(file);
        
        /* handle images */
        List<AsciidocImageNode> imageNodes = file.getImageNodes();
        if (! imageNodes.isEmpty()) {
            context.append(mindMapPrefix(depth+1, true));
            context.append("[#palegreen] images\n");
            for (AsciidocImageNode imageNode: imageNodes ) {
                File imageFile = imageNode.getImageFile();
                boolean imageProblem = imageFile.exists();
                
                context.append(mindMapPrefix(depth+2, imageProblem));
                if (!fileExists) {
                    context.append("[#darkorange]");
                }
                context.append(" ");
                appendFileNameWithShortPathAndHandleNotExisting(imageFile, context, imageFile.exists());
                context.append("\n");
            }
        }

        /* handle includes */
        List<AsciidocIncludeNode> included = file.getIncludeNodes();
        
        if (included.size()>0) {
            context.append(mindMapPrefix(depth+1, true));
            context.append("[#white] includes");
            context.append("\n");
            for (AsciidocIncludeNode include : included) {
                AsciidocFile child = include.getIncludedAsciidocFile();
                generate(child, context, depth + 2, model);
            }
        }
        
    }

    private void appendFileNameWithShortPathAndHandleNotExisting(AsciidocFile file, AsciidocFileGeneratorContext context, boolean fileExists) {
        if (!fileExists) {
            appendOpenTag(context, "s");
        }
        context.append(resolveShownPath(file));
        context.append("/");

        appendOpenTag(context, "b");
        context.append(resolveName(file));
        appendCloseTag(context, "b");

        if (!fileExists) {
            appendCloseTag(context, "s");

            context.append(warnMessage("The file does not exist!"));
        }
    }
    private void appendFileNameWithShortPathAndHandleNotExisting(File file, AsciidocFileGeneratorContext context, boolean fileExists) {
        if (!fileExists) {
            appendOpenTag(context, "s");
        }
        context.append(resolveShownPath(file));
        context.append("/");
        
        appendOpenTag(context, "b");
        context.append(resolveName(file));
        appendCloseTag(context, "b");
        
        if (!fileExists) {
            appendCloseTag(context, "s");
            
            context.append(warnMessage("The file does not exist!"));
        }
    }

    private void appendCloseTag(AsciidocFileGeneratorContext context, String tag) {
        context.append("</");
        context.append(tag);
        context.append(">");
    }

    private void appendOpenTag(AsciidocFileGeneratorContext context, String tag) {
        context.append("<");
        context.append(tag);
        context.append(">");
    }

    private String warnMessage(String message) {
        return "<b> >> "+message + "</b>";
    }

    private String resolveName(AsciidocFile asciidocFile) {
        File file = asciidocFile.getFile();
        return resolveName(file);
    }

    private String resolveName(File file) {
        StringBuilder sb = new StringBuilder();
        if (file == null) {
            sb.append("null");
        } else {
            sb.append(file.getName());
        }
        return sb.toString();
    }

    private String resolveShownPath(AsciidocFile asciidocFile) {
        File file = asciidocFile.getFile();
        return resolveShownPath(file);
    }

    private String resolveShownPath(File file) {
        return file.getParentFile().getName();
    }

    private static Map<Integer, String> mindMapPrefixCacheWithBorders = new TreeMap<>();
    private static Map<Integer, String> mindMapPrefixCacheWithoutBorders = new TreeMap<>();

    private static String mindMapPrefix(int depth, boolean showBorders) {
        Map<Integer, String> cacheToUse;
        if (showBorders) {
            cacheToUse = mindMapPrefixCacheWithBorders;
        } else {
            cacheToUse = mindMapPrefixCacheWithoutBorders;
        }
        String result = cacheToUse.get(depth);

        if (result == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append("*");
            }
            if (!showBorders) {
                sb.append("_");
            }
            result = sb.toString();

            cacheToUse.put(depth, result);
        }
        return result;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: $baseFolder $fileToInspect");
            System.exit(2);
        }
        File baseFolder = new File(args[0]);
        File fileToInspect = new File(args[1]);
        if (!fileToInspect.exists()) {
            throw new IllegalArgumentException("Does not exist:" + fileToInspect);
        }

        GlobalAsciidocModel model = GlobalAsciidocModel.builder().from(baseFolder).logWith(new SystemOutLogAdapter()).build();

        DocumentPlantUMLMindMapGenerator generator = new DocumentPlantUMLMindMapGenerator();
        String generated = generator.generate(fileToInspect, model);

        System.out.println("Generated:");
        System.out.println(generated);
    }

}
