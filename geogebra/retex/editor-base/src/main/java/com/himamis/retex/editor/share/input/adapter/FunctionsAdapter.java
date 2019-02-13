package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

import java.util.Arrays;
import java.util.List;

public class FunctionsAdapter extends StringInput {

    private static final List<String> fnList = Arrays.asList("sin", "cos", "tan", "ln", "log",
            "sinh", "cosh", "tanh", "asin", "acos", "atan", "arcsin", "arccos", "arctan", "sen",
            "arcsen", "tg", "tgh", "senh");

    @Override
    public void commit(MathFieldInternal mfi, String input) {
        commitFunction(mfi, input);
    }

    @Override
    public boolean test(String input) {
        return fnList.contains(input);
    }
}
