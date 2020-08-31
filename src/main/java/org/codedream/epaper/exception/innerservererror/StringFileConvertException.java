package org.codedream.epaper.exception.innerservererror;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StringFileConvertException extends HandlingErrorsException {
    public StringFileConvertException(String msg){
        super(msg);
    }
}
