package org.jcobol.core;

import org.jcobol.annotation.CobolField;

import java.lang.reflect.Field;

/**
 * Class to store field information during binary parsing
 */
public class FieldInfo {
    private final Field field;
    private final CobolField cobolField;
    private int startPos;
    private int endPos;
    
    public FieldInfo(Field field, CobolField cobolField) {
        this.field = field;
        this.cobolField = cobolField;
        this.startPos = -1;
        this.endPos = -1;
    }
    
    public Field getField() {
        return field;
    }
    
    public CobolField getCobolField() {
        return cobolField;
    }
    
    public int getStartPos() {
        return startPos;
    }
    
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }
    
    public int getEndPos() {
        return endPos;
    }
    
    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}