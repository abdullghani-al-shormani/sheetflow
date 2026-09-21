package com.abdullghani.sheetflow;

import android.graphics.Color;

import com.abdullghani.sheetflow.callbacks.OnActionListener;
import com.abdullghani.sheetflow.callbacks.OnMultiChoiceListener;
import com.abdullghani.sheetflow.callbacks.OnStringClickListener;
import com.abdullghani.sheetflow.callbacks.OnViewCreatedListener;
import com.abdullghani.sheetflow.models.SheetItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal data holder class that stores all configuration parameters for {@link SheetFlow}.
 * <p>
 * This class is not part of the public API and should not be used directly by library consumers.
 * </p>
 */
class SheetParams {

   // --- Content ---
   String title;
   String message;
   int iconRes = 0;
   int customLayoutRes = 0;
   OnViewCreatedListener onViewCreatedListener;
   Integer backgroundColor = null;

   // --- Buttons ---
   String positiveBtnText;
   OnActionListener positiveListener;
   String negativeBtnText;
   OnActionListener negativeListener;

   // --- List & Adapter ---
   final List<SheetItem> itemsList = new ArrayList<>();
   OnStringClickListener simpleItemClickListener;
   OnMultiChoiceListener multiChoiceListener;
   boolean isMultiSelect = false;

   // --- Configurations & Styling ---
   int themeResId = 0;
   boolean isCancelable = true;
   boolean isDraggable = true;
   boolean isExpanded = false;
   boolean isNonModal = false;
   int peekHeightDp = 0;
   int cornerRadiusDp = 0;
   int maxWidthDp = 0;
   int nestedScrollChildId = 0;

   // --- Window & Background ---
   boolean autoAdjustKeyboard = false;
   int dimColor = Color.parseColor("#80000000");
   boolean isBlurEnabled = false;
   int blurRadius = 15;

   // --- State & Rotation ---
   boolean autoHandleRotation = true;
   static final String KEY_IS_SHOWING = "sf_is_showing";
   SheetFlow.OnStateChangeListener stateChangeListener;

   // --- Typography & Colors ---
   Integer titleColor = null;
   Integer messageColor = null;
   Integer iconColor = null;
   Integer itemsIconColor = null;      // ← أضف هذا (لون أيقونات العناصر)
   int titleFontResId = 0;
   int messageFontResId = 0;
   int itemsFontResId = 0;

   // --- Drag Handle ---
   boolean isDragHandleVisible = true;
   Integer dragHandleColor = null;

   // --- Animation ---
   int windowAnimationResId = 0;
}