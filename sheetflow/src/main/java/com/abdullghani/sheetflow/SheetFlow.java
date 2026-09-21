package com.abdullghani.sheetflow;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StyleRes;

import com.abdullghani.sheetflow.callbacks.OnActionListener;
import com.abdullghani.sheetflow.callbacks.OnMultiChoiceListener;
import com.abdullghani.sheetflow.callbacks.OnStringClickListener;
import com.abdullghani.sheetflow.callbacks.OnViewCreatedListener;
import com.abdullghani.sheetflow.models.SheetItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

/**
 * A customizable, responsive, and fluent Material BottomSheetDialog wrapper for Android.
 * <p>
 * {@code SheetFlow} simplifies the creation and management of BottomSheet dialogs with advanced features, including:
 * <ul>
 *   <li>Dynamic orientation adjustments for seamless layout behavior in Portrait and Landscape modes.</li>
 *   <li>Automatic wrap-content height optimization based on content size without unwanted layout stretching.</li>
 *   <li>Custom background color support while preserving default rounded corner shapes.</li>
 *   <li>Support for simple item lists, multi-choice items, action items, and custom layout inflations.</li>
 *   <li>Flexible window configurations including background dimming, window blur, keyboard adjustment, and non-modal modes.</li>
 *   <li>Lifecycle-aware state retention and automatic orientation handling.</li>
 * </ul>
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * // Simple Action Sheet Example
 * SheetFlow.with(context)
 *         .setTitle("Select an Option")
 *         .setMessage("Choose one of the actions below to proceed.")
 *         .addItem(R.drawable.ic_edit, "Edit Profile", () -> {
 *             // Handle edit action
 *         })
 *         .addItem(R.drawable.ic_delete, "Delete Account", () -> {
 *             // Handle delete action
 *         })
 *         .setBackgroundColor(Color.WHITE)
 *         .setCornerRadius(16)
 *         .setCancelable(true)
 *         .show();
 * }</pre>
 */
public class SheetFlow {

    public interface OnStateChangeListener {
        void onStateChanged(SheetFlow sheet, int newState);
    }

    private final Context context;
    private final SheetParams params;
    private SheetController controller;

    private SheetFlow(Context context) {
        this.context = context;
        this.params = new SheetParams();
    }

    /**
     * Creates and initializes a new {@link SheetFlow} builder instance using the provided {@link Context}.
     *
     * @param context The application or activity context.
     * @return A new {@link SheetFlow} instance for fluent configuration.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .setTitle("Quick Action")
     *         .show();
     * }</pre>
     */
    public static SheetFlow with(Context context) {
        return new SheetFlow(context);
    }

    /**
     * Creates and initializes a new {@link SheetFlow} builder instance with a custom style theme resource.
     *
     * @param context    The application or activity context.
     * @param themeResId The style resource ID (e.g., {@code R.style.CustomBottomSheetTheme}).
     * @return A new {@link SheetFlow} instance configured with the specified theme.
     */
    public static SheetFlow with(Context context, @StyleRes int themeResId) {
        SheetFlow flow = new SheetFlow(context);
        flow.params.themeResId = themeResId;
        return flow;
    }

    /**
     * Sets a custom style theme for the BottomSheet dialog.
     * <p>
     * This method allows applying custom XML styles to modify dialog appearance, such as animations,
     * window attributes, or material themes.
     * </p>
     *
     * @param themeResId The style resource identifier (e.g., {@code R.style.CustomBottomSheetDialogTheme}).
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .setStyle(R.style.MyCustomBottomSheetTheme)
     *         .setTitle("Styled Sheet")
     *         .show();
     * }</pre>
     */
    public SheetFlow setStyle(@StyleRes int themeResId) {
        params.themeResId = themeResId;
        return this;
    }

    /**
     * Sets the title text to be displayed in the header section of the BottomSheet dialog.
     *
     * @param title The title text string to display.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setTitle(String title) {
        params.title = title;
        return this;
    }

    /**
     * Sets the body or message text displayed below the header in the BottomSheet dialog.
     *
     * @param message The message text string to display.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMessage(String message) {
        params.message = message;
        return this;
    }

    /**
     * Sets an icon resource to be displayed in the header section alongside or above the title.
     *
     * @param iconRes The drawable resource ID of the icon to display (e.g., {@code R.drawable.ic_info}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setIcon(@DrawableRes int iconRes) {
        params.iconRes = iconRes;
        return this;
    }

    /**
     * Inflates a custom layout resource inside the BottomSheet content area.
     * <p>
     * Use this method when you want to display a fully customized UI layout instead of the
     * default item list.
     * </p>
     *
     * @param layoutRes The XML layout resource ID to inflate (e.g., {@code R.layout.custom_sheet_layout}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setView(@LayoutRes int layoutRes) {
        params.customLayoutRes = layoutRes;
        return this;
    }

    /**
     * Registers a callback listener to interact with views created inside a custom layout.
     * <p>
     * This callback is invoked immediately after the custom layout specified in {@link #setView(int)}
     * is inflated, allowing initialization of view references and click listeners.
     * </p>
     *
     * @param listener The {@link OnViewCreatedListener} callback to handle the inflated root view.
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .setView(R.layout.dialog_custom_input)
     *         .onViewCreated((sheet, view) -> {
     *             EditText input = view.findViewById(R.id.edit_text);
     *             Button submitBtn = view.findViewById(R.id.btn_submit);
     *             submitBtn.setOnClickListener(v -> sheet.dismiss());
     *         })
     *         .show();
     * }</pre>
     */
    public SheetFlow onViewCreated(OnViewCreatedListener listener) {
        params.onViewCreatedListener = listener;
        return this;
    }

    /**
     * Sets a list of simple string items for single-selection in the BottomSheet.
     * <p>
     * Replaces any existing items and configures the sheet for single-choice interaction.
     * </p>
     *
     * @param items    The list of text items to display.
     * @param listener The callback triggered when an item is selected.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setItems(List<String> items, OnStringClickListener listener) {
        params.itemsList.clear();
        for (String item : items) {
            params.itemsList.add(new SheetItem(item, 0));
        }
        params.simpleItemClickListener = listener;
        params.isMultiSelect = false;
        return this;
    }

    /**
     * Appends an actionable item with an optional icon and runnable click listener to the sheet.
     *
     * @param iconRes The drawable resource ID for the item icon (use 0 for no icon).
     * @param title   The text title of the item.
     * @param action  The {@link Runnable} action to execute when the item is clicked.
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .addItem(R.drawable.ic_share, "Share", () -> shareContent())
     *         .addItem(R.drawable.ic_delete, "Delete", () -> deleteContent())
     *         .show();
     * }</pre>
     */
    public SheetFlow addItem(@DrawableRes int iconRes, String title, Runnable action) {
        params.itemsList.add(new SheetItem(title, iconRes, action));
        params.isMultiSelect = false;
        return this;
    }

    /**
     * Sets a list of string items configured for multi-selection with checkboxes or check states.
     * <p>
     * Replaces any existing items and flags the sheet mode as multi-select.
     * </p>
     *
     * @param items    The list of text options to display.
     * @param listener The callback triggered when selection state changes.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMultiChoiceItems(List<String> items, OnMultiChoiceListener listener) {
        params.itemsList.clear();
        for (String item : items) {
            params.itemsList.add(new SheetItem(item, 0));
        }
        params.multiChoiceListener = listener;
        params.isMultiSelect = true;
        return this;
    }

    /**
     * Configures the primary (positive) action button displayed in the sheet footer.
     *
     * @param text     The text label for the positive button.
     * @param listener The callback invoked when the positive button is clicked.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setPositiveButton(String text, OnActionListener listener) {
        params.positiveBtnText = text;
        params.positiveListener = listener;
        return this;
    }

    /**
     * Configures the secondary (negative) action button displayed in the sheet footer.
     *
     * @param text     The text label for the negative button.
     * @param listener The callback invoked when the negative button is clicked.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setNegativeButton(String text, OnActionListener listener) {
        params.negativeBtnText = text;
        params.negativeListener = listener;
        return this;
    }

    /**
     * Sets whether the dialog can be canceled via back button or touching outside.
     *
     * @param cancelable {@code true} if the sheet is cancelable; {@code false} otherwise.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setCancelable(boolean cancelable) {
        params.isCancelable = cancelable;
        return this;
    }

    /**
     * Sets whether the BottomSheet can be dragged up or down by user touch gestures.
     *
     * @param draggable {@code true} to enable drag gestures; {@code false} to lock sheet position.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDraggable(boolean draggable) {
        params.isDraggable = draggable;
        return this;
    }

    /**
     * Sets whether the BottomSheet should open fully expanded upon display.
     *
     * @param expanded {@code true} to display the sheet fully expanded; {@code false} to collapse to peek height.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setExpanded(boolean expanded) {
        params.isExpanded = expanded;
        return this;
    }

    /**
     * Sets whether the BottomSheet operates in non-modal mode.
     * <p>
     * Non-modal mode allows touch events to pass through to the underlying activity while the sheet remains visible.
     * </p>
     *
     * @param isNonModal {@code true} to enable non-modal behavior; {@code false} for default modal behavior.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setNonModal(boolean isNonModal) {
        params.isNonModal = isNonModal;
        return this;
    }

    /**
     * Sets the height of the BottomSheet when it is in a collapsed (peek) state, measured in density-independent pixels (dp).
     *
     * @param peekHeightDp The peek height in DP.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setPeekHeightDp(int peekHeightDp) {
        params.peekHeightDp = peekHeightDp;
        return this;
    }

    /**
     * Sets the top corner radius of the BottomSheet in density-independent pixels (dp).
     *
     * @param radiusDp The corner radius in DP.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setCornerRadius(int radiusDp) {
        params.cornerRadiusDp = radiusDp;
        return this;
    }

    /**
     * Sets the maximum width constraint for the BottomSheet dialog in density-independent pixels (dp).
     * <p>
     * Useful for maintaining clean UI layouts on large screens or tablet devices.
     * </p>
     *
     * @param widthDp The maximum width in DP.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMaxWidthDp(int widthDp) {
        params.maxWidthDp = widthDp;
        return this;
    }

    /**
     * Specifies a scrollable child view ID inside a custom layout to handle nested scrolling smoothly with the sheet.
     *
     * @param resId The view resource ID of the nested scroll child (e.g., {@code R.id.recyclerView} or {@code R.id.nestedScrollView}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setNestedScrollChild(@IdRes int resId) {
        params.nestedScrollChildId = resId;
        return this;
    }

    /**
     * Configures automatic window pan or resize behavior when the soft keyboard is displayed.
     *
     * @param adjust {@code true} to automatically adjust sheet layout for soft keyboard; {@code false} otherwise.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow autoAdjustForKeyboard(boolean adjust) {
        params.autoAdjustKeyboard = adjust;
        return this;
    }

    /**
     * Sets a custom color for the window dim/scrim overlay behind the BottomSheet.
     *
     * @param color The ARGB color integer for the dim background.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDimColor(int color) {
        params.dimColor = color;
        return this;
    }

    /**
     * Enables or disables dynamic background blur behind the BottomSheet window.
     *
     * @param enable {@code true} to apply window blur; {@code false} to disable.
     * @param radius The blur radius value determining blur intensity.
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .setBlurBackground(true, 25)
     *         .show();
     * }</pre>
     */
    public SheetFlow setBlurBackground(boolean enable, int radius) {
        params.isBlurEnabled = enable;
        params.blurRadius = radius;
        return this;
    }

    /**
     * Registers a callback to monitor sheet state transitions (e.g., expanded, collapsed, hidden, dragging).
     *
     * @param listener The {@link OnStateChangeListener} callback instance.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setOnStateChangeListener(OnStateChangeListener listener) {
        params.stateChangeListener = listener;
        return this;
    }

    /**
     * Configures whether the dialog should automatically re-render and preserve configuration during screen rotation.
     *
     * @param enable {@code true} to automatically handle rotation state changes; {@code false} to disable.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setAutoHandleRotation(boolean enable) {
        params.autoHandleRotation = enable;
        return this;
    }

    /**
     * Restores dialog visibility across activity re-creation (such as screen orientation changes) if it was previously active.
     *
     * @param retain             {@code true} to restore visibility if previously shown.
     * @param savedInstanceState The {@link Bundle} containing state passed from {@code onCreate} or {@code onViewCreated}.
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     *     super.onCreate(savedInstanceState);
     *
     *     sheetFlow = SheetFlow.with(this)
     *             .setTitle("Persistent Sheet")
     *             .retainStateOnRotation(true, savedInstanceState);
     * }
     * }</pre>
     */
    public SheetFlow retainStateOnRotation(boolean retain, Bundle savedInstanceState) {
        if (retain && savedInstanceState != null && savedInstanceState.getBoolean(SheetParams.KEY_IS_SHOWING, false)) {
            show();
        }
        return this;
    }

    /**
     * Saves current dialog state flags into the provided {@link Bundle} to support activity state preservation.
     *
     * @param outState The {@link Bundle} in which to save state data (typically called within {@code onSaveInstanceState}).
     */
    public void saveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putBoolean(SheetParams.KEY_IS_SHOWING, isShowing());
        }
    }

    /**
     * Sets the sheet surface background color.
     * <p>
     * If the dialog is actively showing on screen, the background color is updated immediately.
     * </p>
     *
     * @param color The ARGB color integer (e.g., {@code Color.WHITE} or {@code ContextCompat.getColor(context, R.color.bg)}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setBackgroundColor(@ColorInt int color) {
        params.backgroundColor = color;
        return this;
    }

    /**
     * Sets the text color for the title.
     *
     * @param color The ARGB color integer (e.g., Color.RED or ContextCompat.getColor(context, R.color.my_color)).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setTitleColor(@ColorInt int color) {
        params.titleColor = color;
        return this;
    }

    /**
     * Sets the custom font family for the title using a font resource ID.
     *
     * @param fontResId The font resource identifier (e.g., R.font.arial or R.font.cairo_bold).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setTitleFont(@FontRes int fontResId) {
        params.titleFontResId = fontResId;
        return this;
    }

    /**
     * Sets the text color for the message.
     *
     * @param color The ARGB color integer.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMessageColor(@ColorInt int color) {
        params.messageColor = color;
        return this;
    }

    /**
     * Sets the custom font family for the message using a font resource ID.
     *
     * @param fontResId The font resource identifier (e.g., R.font.arial).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMessageFont(@FontRes int fontResId) {
        params.messageFontResId = fontResId;
        return this;
    }

    /**
     * Sets the custom font family for the list items using a font resource ID.
     *
     * @param fontResId The font resource identifier (e.g., R.font.arial).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setItemsFont(@FontRes int fontResId) {
        params.itemsFontResId = fontResId;
        return this;
    }

    /**
     * Sets whether the top drag handle bar is visible.
     *
     * @param visible {@code true} to show the drag handle bar; {@code false} to hide it.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDragHandleVisible(boolean visible) {
        params.isDragHandleVisible = visible;
        return this;
    }

    /**
     * Sets a custom color for the top drag handle bar.
     *
     * @param color The ARGB color integer (e.g., Color.GRAY or ContextCompat.getColor(context, R.color.my_color)).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDragHandleColor(@ColorInt int color) {
        params.dragHandleColor = color;
        return this;
    }

    /**
     * Sets a custom animation style resource for the BottomSheet dialog entrance and exit.
     *
     * @param animationStyleResId A style resource ID containing windowEnterAnimation and windowExitAnimation
     *                            (e.g., R.style.DialogAnimation or R.anim.custom_anim via style).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setAnimation(@StyleRes int animationStyleResId) {
        params.windowAnimationResId = animationStyleResId;
        return this;
    }

    // ==================== Core Methods ====================

    /**
     * Builds and displays the BottomSheet dialog on screen.
     * <p>
     * Constructs the underlying {@link BottomSheetDialog}, inflates layout views, applies window and
     * behavior configurations (orientation listeners, colors, dimming, and state listeners), and presents the dialog.
     * </p>
     *
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow show() {
        if (controller == null) {
            controller = new SheetController(context, params, this);
        }
        return controller.show();
    }

    /**
     * Dismisses the active BottomSheet dialog and detaches registered system listeners.
     */
    public void dismiss() {
        if (controller != null) {
            controller.dismiss();
        }
    }

    /**
     * Checks whether the BottomSheet dialog is currently visible on screen.
     *
     * @return {@code true} if the dialog is showing; {@code false} otherwise.
     */
    public boolean isShowing() {
        return controller != null && controller.isShowing();
    }

    /**
     * Retrieves the underlying {@link BottomSheetBehavior} instance attached to the dialog view container.
     *
     * @return The {@link BottomSheetBehavior} associated with the dialog sheet, or {@code null} if the dialog is not created or visible.
     */
    public BottomSheetBehavior<View> getBehavior() {
        return controller != null ? controller.getBehavior() : null;
    }

    /**
     * Programmatically expands the sheet to its full height state ({@link BottomSheetBehavior#STATE_EXPANDED}).
     */
    public void expand() {
        if (controller != null) controller.expand();
    }

    /**
     * Programmatically collapses the sheet to its peek height state ({@link BottomSheetBehavior#STATE_COLLAPSED}).
     */
    public void collapse() {
        if (controller != null) controller.collapse();
    }

    /**
     * Toggles the current sheet state between expanded and collapsed.
     */
    public void toggle() {
        if (controller != null) controller.toggle();
    }


    /**
     * Sets the tint color for the header icon.
     * <p>
     * This color will be applied as an image tint to the icon.
     * </p>
     *
     * @param color The ARGB color integer (e.g., {@code ContextCompat.getColor(context, R.color.my_color)} or {@code Color.RED}).
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .setIcon(R.drawable.ic_info)
     *         .setIconColor(ContextCompat.getColor(context, R.color.primary))
     *         .setTitle("Information")
     *         .show();
     * }</pre>
     */
    public SheetFlow setIconColor(@ColorInt int color) {
        params.iconColor = color;
        return this;
    }


    /**
     * Sets the tint color for all item icons in the list.
     *
     * @param color The ARGB color integer (e.g., {@code ContextCompat.getColor(context, R.color.primary)}).
     * @return This {@link SheetFlow} instance for method chaining.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>{@code
     * SheetFlow.with(context)
     *         .addItem(R.drawable.ic_edit, "Edit", () -> {})
     *         .addItem(R.drawable.ic_delete, "Delete", () -> {})
     *         .setItemsIconColor(ContextCompat.getColor(context, R.color.red))
     *         .show();
     * }</pre>
     */
    public SheetFlow setItemsIconColor(@ColorInt int color) {
        params.itemsIconColor = color;
        return this;
    }
}