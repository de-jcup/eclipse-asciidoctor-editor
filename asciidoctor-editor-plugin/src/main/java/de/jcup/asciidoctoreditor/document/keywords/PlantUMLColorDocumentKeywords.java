/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.document.keywords;

import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

/**
 * 
 * PlantUMLColorDocumentKeywords is a generated java class. Please look into
 * PlantUMLKeywordsGenerator.java
 *
 */
public enum PlantUMLColorDocumentKeywords implements DocumentKeyWord {

    APPLICATION("APPLICATION"), ALICEBLUE("AliceBlue"), ANTIQUEWHITE("AntiqueWhite"), AQUA("Aqua"), AQUAMARINE("Aquamarine"), AZURE("Azure"), BUSINESS("BUSINESS"), BEIGE("Beige"), BISQUE("Bisque"),
    BLACK("Black"), BLANCHEDALMOND("BlanchedAlmond"), BLUE("Blue"), BLUEVIOLET("BlueViolet"), BROWN("Brown"), BURLYWOOD("BurlyWood"), CADETBLUE("CadetBlue"), CHARTREUSE("Chartreuse"),
    CHOCOLATE("Chocolate"), CORAL("Coral"), CORNFLOWERBLUE("CornflowerBlue"), CORNSILK("Cornsilk"), CRIMSON("Crimson"), CYAN("Cyan"), DARKBLUE("DarkBlue"), DARKCYAN("DarkCyan"),
    DARKGOLDENROD("DarkGoldenRod"), DARKGRAY("DarkGray"), DARKGREEN("DarkGreen"), DARKGREY("DarkGrey"), DARKKHAKI("DarkKhaki"), DARKMAGENTA("DarkMagenta"), DARKOLIVEGREEN("DarkOliveGreen"),
    DARKORCHID("DarkOrchid"), DARKRED("DarkRed"), DARKSALMON("DarkSalmon"), DARKSEAGREEN("DarkSeaGreen"), DARKSLATEBLUE("DarkSlateBlue"), DARKSLATEGRAY("DarkSlateGray"),
    DARKSLATEGREY("DarkSlateGrey"), DARKTURQUOISE("DarkTurquoise"), DARKVIOLET("DarkViolet"), DARKORANGE("Darkorange"), DEEPPINK("DeepPink"), DEEPSKYBLUE("DeepSkyBlue"), DIMGRAY("DimGray"),
    DIMGREY("DimGrey"), DODGERBLUE("DodgerBlue"), FIREBRICK("FireBrick"), FLORALWHITE("FloralWhite"), FORESTGREEN("ForestGreen"), FUCHSIA("Fuchsia"), GAINSBORO("Gainsboro"), GHOSTWHITE("GhostWhite"),
    GOLD("Gold"), GOLDENROD("GoldenRod"), GRAY("Gray"), GREEN("Green"), GREENYELLOW("GreenYellow"), GREY("Grey"), HONEYDEW("HoneyDew"), HOTPINK("HotPink"), IMPLEMENTATION("IMPLEMENTATION"),
    INDIANRED("IndianRed"), INDIGO("Indigo"), IVORY("Ivory"), KHAKI("Khaki"), LAVENDER("Lavender"), LAVENDERBLUSH("LavenderBlush"), LAWNGREEN("LawnGreen"), LEMONCHIFFON("LemonChiffon"),
    LIGHTBLUE("LightBlue"), LIGHTCORAL("LightCoral"), LIGHTCYAN("LightCyan"), LIGHTGOLDENRODYELLOW("LightGoldenRodYellow"), LIGHTGRAY("LightGray"), LIGHTGREEN("LightGreen"), LIGHTGREY("LightGrey"),
    LIGHTPINK("LightPink"), LIGHTSALMON("LightSalmon"), LIGHTSEAGREEN("LightSeaGreen"), LIGHTSKYBLUE("LightSkyBlue"), LIGHTSLATEGRAY("LightSlateGray"), LIGHTSLATEGREY("LightSlateGrey"),
    LIGHTSTEELBLUE("LightSteelBlue"), LIGHTYELLOW("LightYellow"), LIME("Lime"), LIMEGREEN("LimeGreen"), LINEN("Linen"), MOTIVATION("MOTIVATION"), MAGENTA("Magenta"), MAROON("Maroon"),
    MEDIUMAQUAMARINE("MediumAquaMarine"), MEDIUMBLUE("MediumBlue"), MEDIUMORCHID("MediumOrchid"), MEDIUMPURPLE("MediumPurple"), MEDIUMSEAGREEN("MediumSeaGreen"), MEDIUMSLATEBLUE("MediumSlateBlue"),
    MEDIUMSPRINGGREEN("MediumSpringGreen"), MEDIUMTURQUOISE("MediumTurquoise"), MEDIUMVIOLETRED("MediumVioletRed"), MIDNIGHTBLUE("MidnightBlue"), MINTCREAM("MintCream"), MISTYROSE("MistyRose"),
    MOCCASIN("Moccasin"), NAVAJOWHITE("NavajoWhite"), NAVY("Navy"), OLDLACE("OldLace"), OLIVE("Olive"), OLIVEDRAB("OliveDrab"), ORANGE("Orange"), ORANGERED("OrangeRed"), ORCHID("Orchid"),
    PHYSICAL("PHYSICAL"), PALEGOLDENROD("PaleGoldenRod"), PALEGREEN("PaleGreen"), PALETURQUOISE("PaleTurquoise"), PALEVIOLETRED("PaleVioletRed"), PAPAYAWHIP("PapayaWhip"), PEACHPUFF("PeachPuff"),
    PERU("Peru"), PINK("Pink"), PLUM("Plum"), POWDERBLUE("PowderBlue"), PURPLE("Purple"), RED("Red"), ROSYBROWN("RosyBrown"), ROYALBLUE("RoyalBlue"), STRATEGY("STRATEGY"), SADDLEBROWN("SaddleBrown"),
    SALMON("Salmon"), SANDYBROWN("SandyBrown"), SEAGREEN("SeaGreen"), SEASHELL("SeaShell"), SIENNA("Sienna"), SILVER("Silver"), SKYBLUE("SkyBlue"), SLATEBLUE("SlateBlue"), SLATEGRAY("SlateGray"),
    SLATEGREY("SlateGrey"), SNOW("Snow"), SPRINGGREEN("SpringGreen"), STEELBLUE("SteelBlue"), TECHNOLOGY("TECHNOLOGY"), TAN("Tan"), TEAL("Teal"), THISTLE("Thistle"), TOMATO("Tomato"),
    TURQUOISE("Turquoise"), VIOLET("Violet"), WHEAT("Wheat"), WHITE("White"), WHITESMOKE("WhiteSmoke"), YELLOW("Yellow"), YELLOWGREEN("YellowGreen"),;

    private String text;

    private PlantUMLColorDocumentKeywords(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isBreakingOnEof() {
        return true;
    }

    @Override
    public String getTooltip() {
        return "This is a keyword representing a 'color' in plantuml. Please refer to online documentation for more information";
    }

    @Override
    public String getLinkToDocumentation() {
        return "http://plantuml.com";
    }

}
