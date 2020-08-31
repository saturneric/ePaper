package org.codedream.epaper.exception.notfound;


public class TagNotFoundException extends NotFoundException {
    String tagName;

    public TagNotFoundException(String tagName){
        super(tagName);
        this.tagName = tagName;
    }
}
