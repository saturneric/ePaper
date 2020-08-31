package org.codedream.epaper.exception.notfound;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){
        super(msg);
    }

    public  NotFoundException(){
        super();
    }
}
