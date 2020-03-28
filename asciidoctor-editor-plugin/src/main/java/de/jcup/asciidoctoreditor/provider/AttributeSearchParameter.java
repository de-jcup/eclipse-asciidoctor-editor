package de.jcup.asciidoctoreditor.provider;

class AttributeSearchParameter{
    
    static final AttributeSearchParameter IMAGES_DIR_ATTRIBUTE = new AttributeSearchParameter("imagesdir");
    
    private String searchString;
    private String name;

    AttributeSearchParameter(String name) {
        this.searchString=":"+name+":";
        this.name=name;
    }
    
    public String getSearchString() {
        return searchString;
    }
    
    public String getName() {
        return name;
    }
}