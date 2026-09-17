package com.abdullghani.sheetflow.callbacks;

import java.util.List;

public interface OnMultiChoiceListener {
    void onSelectionChanged(List<Integer> selectedIndices);
}