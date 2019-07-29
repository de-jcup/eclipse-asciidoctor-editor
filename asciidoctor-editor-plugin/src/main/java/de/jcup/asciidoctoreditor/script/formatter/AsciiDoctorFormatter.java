package de.jcup.asciidoctoreditor.script.formatter;

import java.util.List;

public class AsciiDoctorFormatter {
    private AsciidocBlockScanner blockScanner = new AsciidocBlockScanner();
    private AsciiDoctorSimpleTextFormatter simpleTextFormatter = new AsciiDoctorSimpleTextFormatter();

    public String format(String origin, AsciiDoctorFormatterConfig config) {
        if (origin == null) {
            return "";
        }
        List<AsciidocFormatBlock> blocks = blockScanner.scan(origin);
        StringBuilder sb = new StringBuilder();
        for (AsciidocFormatBlock block : blocks) {
            handleBlock(block,config);
            sb.append(block.source);
        }
        return sb.toString();
    }

    private void handleBlock(AsciidocFormatBlock block, AsciiDoctorFormatterConfig config) {
        if (block.blockType == AsciidocBlockType.TEXT) {
            block.source=new StringBuilder().append(simpleTextFormatter.format(block.source.toString(), config));
        }
    }

}
