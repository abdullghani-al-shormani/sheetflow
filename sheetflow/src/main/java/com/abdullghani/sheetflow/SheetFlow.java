package com.abdullghani.sheetflow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullghani.sheetflow.adapters.SheetAdapter;
import com.abdullghani.sheetflow.callbacks.OnActionListener;
import com.abdullghani.sheetflow.callbacks.OnMultiChoiceListener;
import com.abdullghani.sheetflow.callbacks.OnStringClickListener;
import com.abdullghani.sheetflow.callbacks.OnViewCreatedListener;
import com.abdullghani.sheetflow.models.SheetItem;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    private final WeakReference<Context> contextRef;
    private BottomSheetDialog dialog;

    // --- Content & Views ---
    private String title;
    private String message;
    private int iconRes = 0;
    private int customLayoutRes = 0;
    private OnViewCreatedListener onViewCreatedListener;
    private Integer backgroundColor = null;
    // --- Buttons ---
    private String positiveBtnText;
    private OnActionListener positiveListener;
    private String negativeBtnText;
    private OnActionListener negativeListener;

    // --- List & Adapter ---
    private final List<SheetItem> itemsList = new ArrayList<>();
    private OnStringClickListener simpleItemClickListener;
    private OnMultiChoiceListener multiChoiceListener;
    private boolean isMultiSelect = false;

    // --- Configurations & Styling ---
    private int themeResId = 0;
    private boolean isCancelable = true;
    private boolean isDraggable = true;
    private boolean isExpanded = false;
    private boolean isNonModal = false;
    private int peekHeightDp = 0;
    private int cornerRadiusDp = 0;
    private int maxWidthDp = 0;
    private int nestedScrollChildId = 0;

    // --- Window & Background ---
    private boolean autoAdjustKeyboard = false;
    private int dimColor = Color.parseColor("#80000000");
    private boolean isBlurEnabled = false;
    private int blurRadius = 15;

    // --- State & Rotation Handling ---
    private boolean autoHandleRotation = true;
    private static final String KEY_IS_SHOWING = "sf_is_showing";
    private OnStateChangeListener stateChangeListener;
    private androidx.core.util.Consumer<Configuration> configChangeListener;

    private SheetFlow(Context context) {
        this.contextRef = new WeakReference<>(context);
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
        flow.themeResId = themeResId;
        return flow;
    }
    // --- Builder Setters ---

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
        this.themeResId = themeResId;
        return this;
    }

    /**
     * Sets the title text to be displayed in the header section of the BottomSheet dialog.
     *
     * @param title The title text string to display.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the body or message text displayed below the header in the BottomSheet dialog.
     *
     * @param message The message text string to display.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets an icon resource to be displayed in the header section alongside or above the title.
     *
     * @param iconRes The drawable resource ID of the icon to display (e.g., {@code R.drawable.ic_info}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setIcon(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
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
        this.customLayoutRes = layoutRes;
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
        this.onViewCreatedListener = listener;
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
        this.itemsList.clear();
        for (String item : items) {
            this.itemsList.add(new SheetItem(item, 0));
        }
        this.simpleItemClickListener = listener;
        this.isMultiSelect = false;
        return this;
    }

    /**
     * Appends an actionable item with an optional icon and runnable click listener to the sheet.
     *
     * @param iconRes  The drawable resource ID for the item icon (use 0 for no icon).
     * @param title    The text title of the item.
     * @param action   The {@link Runnable} action to execute when the item is clicked.
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
        this.itemsList.add(new SheetItem(title, iconRes, action));
        this.isMultiSelect = false;
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
        this.itemsList.clear();
        for (String item : items) {
            this.itemsList.add(new SheetItem(item, 0));
        }
        this.multiChoiceListener = listener;
        this.isMultiSelect = true;
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
        this.positiveBtnText = text;
        this.positiveListener = listener;
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
        this.negativeBtnText = text;
        this.negativeListener = listener;
        return this;
    }

    /**
     * Sets whether the dialog can be canceled via back button or touching outside.
     *
     * @param cancelable {@code true} if the sheet is cancelable; {@code false} otherwise.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        return this;
    }

    /**
     * Sets whether the BottomSheet can be dragged up or down by user touch gestures.
     *
     * @param draggable {@code true} to enable drag gestures; {@code false} to lock sheet position.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDraggable(boolean draggable) {
        this.isDraggable = draggable;
        return this;
    }

    /**
     * Sets whether the BottomSheet should open fully expanded upon display.
     *
     * @param expanded {@code true} to display the sheet fully expanded; {@code false} to collapse to peek height.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setExpanded(boolean expanded) {
        this.isExpanded = expanded;
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
        this.isNonModal = isNonModal;
        return this;
    }

    /**
     * Sets the height of the BottomSheet when it is in a collapsed (peek) state, measured in density-independent pixels (dp).
     *
     * @param peekHeightDp The peek height in DP.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setPeekHeightDp(int peekHeightDp) {
        this.peekHeightDp = peekHeightDp;
        return this;
    }

    /**
     * Sets the top corner radius of the BottomSheet in density-independent pixels (dp).
     *
     * @param radiusDp The corner radius in DP.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setCornerRadius(int radiusDp) {
        this.cornerRadiusDp = radiusDp;
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
        this.maxWidthDp = widthDp;
        return this;
    }

    /**
     * Specifies a scrollable child view ID inside a custom layout to handle nested scrolling smoothly with the sheet.
     *
     * @param resId The view resource ID of the nested scroll child (e.g., {@code R.id.recyclerView} or {@code R.id.nestedScrollView}).
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setNestedScrollChild(@IdRes int resId) {
        this.nestedScrollChildId = resId;
        return this;
    }

    /**
     * Configures automatic window pan or resize behavior when the soft keyboard is displayed.
     *
     * @param adjust {@code true} to automatically adjust sheet layout for soft keyboard; {@code false} otherwise.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow autoAdjustForKeyboard(boolean adjust) {
        this.autoAdjustKeyboard = adjust;
        return this;
    }

    /**
     * Sets a custom color for the window dim/scrim overlay behind the BottomSheet.
     *
     * @param color The ARGB color integer for the dim background.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setDimColor(int color) {
        this.dimColor = color;
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
        this.isBlurEnabled = enable;
        this.blurRadius = radius;
        return this;
    }

    /**
     * Registers a callback to monitor sheet state transitions (e.g., expanded, collapsed, hidden, dragging).
     *
     * @param listener The {@link OnStateChangeListener} callback instance.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setOnStateChangeListener(OnStateChangeListener listener) {
        this.stateChangeListener = listener;
        return this;
    }

    /**
     * Configures whether the dialog should automatically re-render and preserve configuration during screen rotation.
     *
     * @param enable {@code true} to automatically handle rotation state changes; {@code false} to disable.
     * @return This {@link SheetFlow} instance for method chaining.
     */
    public SheetFlow setAutoHandleRotation(boolean enable) {
        this.autoHandleRotation = enable;
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
        if (retain && savedInstanceState != null && savedInstanceState.getBoolean(KEY_IS_SHOWING, false)) {
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
            outState.putBoolean(KEY_IS_SHOWING, isShowing());
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
        this.backgroundColor = color;

        // إذا كان الـ Dialog معروضاً بالفعل، قم بتطبيق اللون فوراً
        if (dialog != null && dialog.isShowing()) {
            applyBackgroundColor();
        }
        return this;
    }

    private void applyBackgroundColor() {
        if (dialog == null || backgroundColor == null) return;

        View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal != null) {
            // نستخدم setBackgroundTintList للحفاظ على الـ ShapeDrawable (الحواف الدائرية)
            bottomSheetInternal.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

            // ملاحظة: إذا كنت تعتمد على Drawable مخصص بالكامل في الكود الخاص بك
            // ولا تستخدم MaterialShapeDrawable، يمكنك استبدال السطر السابق بـ:
            // bottomSheetInternal.setBackgroundColor(backgroundColor);
        }
    }

    // --- Core Methods ---

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
        Context context = contextRef.get();
        if (context == null) return this;

        dialog = (themeResId != 0)
                ? new BottomSheetDialog(context, themeResId)
                : new BottomSheetDialog(context);

        dialog.setCancelable(isCancelable);

        View root = LayoutInflater.from(context).inflate(R.layout.sf_layout_bottom_sheet, null);

        setupHeader(root);
        setupMessage(root);
        setupContent(context, root);
        setupButtons(root);

        dialog.setContentView(root);

        configureWindow();
        configureBehavior(context, root);

        if (autoHandleRotation) {
            attachRotationListener(context);
        }

        dialog.show();
        applyOrientationAdjustments(context);
        applyBackgroundColor(); // أضف هذا السطر هنا
        return this;
    }

    /**
     * Dismisses the active BottomSheet dialog and detaches registered system listeners.
     */
    public void dismiss() {
        detachRotationListener();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Checks whether the BottomSheet dialog is currently visible on screen.
     *
     * @return {@code true} if the dialog is showing; {@code false} otherwise.
     */
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * Retrieves the underlying {@link BottomSheetBehavior} instance attached to the dialog view container.
     *
     * @return The {@link BottomSheetBehavior} associated with the dialog sheet, or {@code null} if the dialog is not created or visible.
     */
    public BottomSheetBehavior<View> getBehavior() {
        if (dialog != null) {
            View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                return BottomSheetBehavior.from(bottomSheetInternal);
            }
        }
        return null;
    }

    /**
     * Programmatically expands the sheet to its full height state ({@link BottomSheetBehavior#STATE_EXPANDED}).
     */
    public void expand() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * Programmatically collapses the sheet to its peek height state ({@link BottomSheetBehavior#STATE_COLLAPSED}).
     */
    public void collapse() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    /**
     * Toggles the current sheet state between expanded and collapsed.
     */
    public void toggle() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            int targetState = (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    ? BottomSheetBehavior.STATE_COLLAPSED
                    : BottomSheetBehavior.STATE_EXPANDED;
            behavior.setState(targetState);
        }
    }


    private void attachRotationListener(Context context) {
        if (context instanceof androidx.activity.ComponentActivity) {
            androidx.activity.ComponentActivity activity = (androidx.activity.ComponentActivity) context;

            configChangeListener = newConfig -> applyOrientationAdjustments(context);
            activity.addOnConfigurationChangedListener(configChangeListener);

            activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    dismiss();
                }
            });
        }
    }

    private void detachRotationListener() {
        Context context = contextRef.get();
        if (context instanceof androidx.activity.ComponentActivity && configChangeListener != null) {
            ((androidx.activity.ComponentActivity) context).removeOnConfigurationChangedListener(configChangeListener);
            configChangeListener = null;
        }
    }

    private void applyOrientationAdjustments(Context context) {
        if (dialog == null || !dialog.isShowing()) return;

        View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal == null) return;

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
        boolean isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        ViewGroup.LayoutParams layoutParams = bottomSheetInternal.getLayoutParams();

        // اجعل الارتفاع دائما بحسب المحتوى (WRAP_CONTENT)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        bottomSheetInternal.setLayoutParams(layoutParams);

        // تفعيل التكيّف التلقائي مع المحتوى
        behavior.setFitToContents(true);

        if (isLandscape) {
            // في الوضع الأفقي: حدد أقصى ارتفاع متاح للشاشة لمنع الاقتصاص إذا كانت القائمة كبيرة
            int displayHeight = context.getResources().getDisplayMetrics().heightPixels;
            behavior.setMaxHeight(displayHeight);

            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            // في الوضع الرأسي: إعادة إلغاء أقصى ارتفاع ليأخذ راحته
            behavior.setMaxHeight(Integer.MAX_VALUE);

            if (peekHeightDp > 0) {
                float density = context.getResources().getDisplayMetrics().density;
                behavior.setFitToContents(false); // السماح بالـ Collapsed state إذا تم تحديد PeekHeight
                behavior.setPeekHeight((int) (peekHeightDp * density));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (isExpanded) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void setupHeader(View root) {
        LinearLayout headerLayout = root.findViewById(R.id.sf_header_layout);
        ImageView imgIcon = root.findViewById(R.id.sf_img_icon);
        TextView tvTitle = root.findViewById(R.id.sf_tv_title);

        if (headerLayout == null) return;

        boolean hasTitle = title != null && !title.trim().isEmpty();
        boolean hasIcon = iconRes != 0;

        if (hasTitle || hasIcon) {
            headerLayout.setVisibility(View.VISIBLE);

            if (tvTitle != null) {
                tvTitle.setText(hasTitle ? title : "");
                tvTitle.setVisibility(hasTitle ? View.VISIBLE : View.GONE);

                if (themeResId != 0) {
                    Context context = root.getContext();
                    TypedArray a = context.obtainStyledAttributes(themeResId, new int[]{R.attr.sheetTitleStyle});
                    int titleStyleRes = a.getResourceId(0, 0);
                    a.recycle();

                    if (titleStyleRes != 0) {
                        androidx.core.widget.TextViewCompat.setTextAppearance(tvTitle, titleStyleRes);
                    }
                } else {
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    tvTitle.setTypeface(null, Typeface.BOLD);
                    tvTitle.setTextColor(Color.BLACK);
                }
            }

            if (imgIcon != null) {
                if (hasIcon) imgIcon.setImageResource(iconRes);
                imgIcon.setVisibility(hasIcon ? View.VISIBLE : View.GONE);
            }
        } else {
            headerLayout.setVisibility(View.GONE);
        }
    }

    private void setupMessage(View root) {
        TextView tvMessage = root.findViewById(R.id.sf_tv_message);
        if (tvMessage == null) return;

        boolean hasMessage = message != null && !message.trim().isEmpty();
        if (hasMessage) {
            tvMessage.setText(message);
            tvMessage.setVisibility(View.VISIBLE);

            if (themeResId != 0) {
                Context context = root.getContext();
                TypedArray a = context.obtainStyledAttributes(themeResId, new int[]{R.attr.sheetMessageStyle});
                int messageStyleRes = a.getResourceId(0, 0);
                a.recycle();

                if (messageStyleRes != 0) {
                    androidx.core.widget.TextViewCompat.setTextAppearance(tvMessage, messageStyleRes);
                }
            } else {
                tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvMessage.setTypeface(null, Typeface.NORMAL);
                tvMessage.setTextColor(Color.parseColor("#757575"));
            }
        } else {
            tvMessage.setVisibility(View.GONE);
        }
    }

    private void setupContent(Context context, View root) {
        FrameLayout customContainer = root.findViewById(R.id.sf_custom_container);
        RecyclerView recyclerView = root.findViewById(R.id.sf_recycler_view);

        if (customLayoutRes != 0 && customContainer != null) {
            customContainer.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);

            View customView = LayoutInflater.from(context).inflate(customLayoutRes, customContainer, false);
            customContainer.addView(customView);

            if (onViewCreatedListener != null) {
                onViewCreatedListener.onViewCreated(this, customView);
            }
        } else if (!itemsList.isEmpty() && recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
            if (customContainer != null) customContainer.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            SheetAdapter adapter = new SheetAdapter(itemsList, isMultiSelect);
            adapter.setOnItemClickListener((position, item) -> {
                if (item.getAction() != null) {
                    item.getAction().run();
                } else if (simpleItemClickListener != null) {
                    simpleItemClickListener.onItemClick(position, item.getTitle());
                }
                dismiss();
            });

            if (multiChoiceListener != null) {
                adapter.setOnMultiChoiceListener(multiChoiceListener);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    private void setupButtons(View root) {
        LinearLayout buttonsLayout = root.findViewById(R.id.sf_buttons_layout);
        Button btnPositive = root.findViewById(R.id.sf_btn_positive);
        Button btnNegative = root.findViewById(R.id.sf_btn_negative);

        if (buttonsLayout == null) return;

        if (positiveBtnText != null || negativeBtnText != null) {
            buttonsLayout.setVisibility(View.VISIBLE);

            if (btnPositive != null) {
                if (positiveBtnText != null) {
                    btnPositive.setText(positiveBtnText);
                    btnPositive.setVisibility(View.VISIBLE);
                    btnPositive.setOnClickListener(v -> {
                        if (positiveListener != null) positiveListener.onAction(this);
                    });
                } else {
                    btnPositive.setVisibility(View.GONE);
                }
            }

            if (btnNegative != null) {
                if (negativeBtnText != null) {
                    btnNegative.setText(negativeBtnText);
                    btnNegative.setVisibility(View.VISIBLE);
                    btnNegative.setOnClickListener(v -> {
                        if (negativeListener != null) negativeListener.onAction(this);
                    });
                } else {
                    btnNegative.setVisibility(View.GONE);
                }
            }
        } else {
            buttonsLayout.setVisibility(View.GONE);
        }
    }

    private void configureWindow() {
        if (dialog.getWindow() == null) return;

        if (isNonModal) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.getWindow().setDimAmount(0.5f);

            if (isBlurEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                try {
                    Field field = WindowManager.LayoutParams.class.getField("blurBehindRadius");
                    field.set(params, blurRadius);
                    dialog.getWindow().setAttributes(params);
                } catch (Exception ignored) {
                }
            }
        }

        if (autoAdjustKeyboard) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureBehavior(Context context, View root) {
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bsd = (BottomSheetDialog) d;
            View bottomSheetInternal = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheetInternal == null) return;

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
            behavior.setDraggable(isDraggable);

            if (isNonModal) {
                View touchOutside = bsd.findViewById(com.google.android.material.R.id.touch_outside);
                if (touchOutside != null) {
                    touchOutside.setOnTouchListener((v, event) -> {
                        if (context instanceof Activity) {
                            return ((Activity) context).dispatchTouchEvent(event);
                        }
                        return false;
                    });
                }
            }

            float density = context.getResources().getDisplayMetrics().density;

            if (maxWidthDp > 0) {
                ViewGroup.LayoutParams layoutParams = bottomSheetInternal.getLayoutParams();
                layoutParams.width = (int) (maxWidthDp * density);
                bottomSheetInternal.setLayoutParams(layoutParams);
            }

            if (nestedScrollChildId != 0) {
                View nestedChild = root.findViewById(nestedScrollChildId);
                if (nestedChild != null) {
                    ViewCompat.setNestedScrollingEnabled(nestedChild, true);
                }
            }

            if (cornerRadiusDp > 0) {
                bottomSheetInternal.setBackgroundColor(Color.TRANSPARENT);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.WHITE);
                float px = cornerRadiusDp * density;
                drawable.setCornerRadii(new float[]{px, px, px, px, 0, 0, 0, 0});
                ViewCompat.setBackground(bottomSheetInternal, drawable);
            }

            // تطبيق قياسات الاتجاه الأولي (Landscape / Portrait)
            applyOrientationAdjustments(context);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (stateChangeListener != null) {
                        stateChangeListener.onStateChanged(SheetFlow.this, newState);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        });
    }
}