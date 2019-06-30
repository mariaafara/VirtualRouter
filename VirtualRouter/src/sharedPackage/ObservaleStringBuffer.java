/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedPackage;

import javafx.beans.binding.StringBinding;

/**
 *
 * @author maria afara
 */
public class ObservaleStringBuffer extends StringBinding {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    protected String computeValue() {
        return buffer.toString();
    }

    public void set(String content) {
        buffer.replace(0, buffer.length(), content);
        invalidate();
    }

    public void append(String text) {
        buffer.append(text);
        invalidate();
    }

    // wrap other StringBuffer methods as needed...
}