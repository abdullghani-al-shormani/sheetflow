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

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullghani.sheetflow.adapters.SheetAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Internal controller responsible for creating the dialog, binding views,
 * applying styles, configuring window attributes, and handling orientation changes.
 * <p>
 * This class is not part of the public API.
 * </p>
 */
class SheetController {

    private final WeakReference<Context> contextRef;
    private final SheetParams params;
    private final SheetFlow sheetFlow;

    private BottomSheetDialog dialog;
    private androidx.core.util.Consumer<Configuration> configChangeListener;

    SheetController(Context context, SheetParams params, SheetFlow sheetFlow) {
        this.contextRef = new WeakReference<>(context);
        this.params = params;
        this.sheetFlow = sheetFlow;
    }

    SheetFlow show() {
        Context context = contextRef.get();
        if (context == null) return sheetFlow;

        dialog = (params.themeResId != 0)
                ? new BottomSheetDialog(context, params.themeResId)
                : new BottomSheetDialog(context);

        dialog.setCancelable(params.isCancelable);

        if (params.windowAnimationResId != 0 && dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = params.windowAnimationResId;
        }

        View root = LayoutInflater.from(context).inflate(R.layout.sf_layout_bottom_sheet, null);

        setupDragHandle(root);
        setupHeader(root);
        setupMessage(root);
        setupContent(context, root);
        setupButtons(root);

        dialog.setContentView(root);

        configureWindow();
        configureBehavior(context, root);

        if (params.autoHandleRotation) {
            attachRotationListener(context);
        }

        dialog.show();
        applyOrientationAdjustments(context);
        applyBackgroundColor();
        return sheetFlow;
    }

    void dismiss() {
        detachRotationListener();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    BottomSheetBehavior<View> getBehavior() {
        if (dialog != null) {
            View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                return BottomSheetBehavior.from(bottomSheetInternal);
            }
        }
        return null;
    }

    void expand() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void collapse() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    void toggle() {
        BottomSheetBehavior<View> behavior = getBehavior();
        if (behavior != null) {
            int targetState = (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    ? BottomSheetBehavior.STATE_COLLAPSED
                    : BottomSheetBehavior.STATE_EXPANDED;
            behavior.setState(targetState);
        }
    }

    // ==================== Private Setup Methods ====================

    private void setupDragHandle(View root) {
        View dragHandle = root.findViewById(R.id.sf_drag_handle);
        if (dragHandle == null) return;

        dragHandle.setVisibility(params.isDragHandleVisible ? View.VISIBLE : View.GONE);

        if (params.isDragHandleVisible && params.dragHandleColor != null) {
            dragHandle.setBackgroundTintList(ColorStateList.valueOf(params.dragHandleColor));
        }
    }

    private void setupHeader(View root) {
        LinearLayout headerLayout = root.findViewById(R.id.sf_header_layout);
        ImageView imgIcon = root.findViewById(R.id.sf_img_icon);
        TextView tvTitle = root.findViewById(R.id.sf_tv_title);

        if (headerLayout == null) return;

        boolean hasTitle = params.title != null && !params.title.trim().isEmpty();
        boolean hasIcon = params.iconRes != 0;

        if (hasTitle || hasIcon) {
            headerLayout.setVisibility(View.VISIBLE);

            if (tvTitle != null) {
                tvTitle.setText(hasTitle ? params.title : "");
                tvTitle.setVisibility(hasTitle ? View.VISIBLE : View.GONE);

                if (params.themeResId != 0) {
                    Context context = root.getContext();
                    TypedArray a = context.obtainStyledAttributes(params.themeResId, new int[]{R.attr.sheetTitleStyle});
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

                if (params.titleColor != null) {
                    tvTitle.setTextColor(params.titleColor);
                }

                if (params.titleFontResId != 0) {
                    try {
                        Typeface typeface = androidx.core.content.res.ResourcesCompat.getFont(root.getContext(), params.titleFontResId);
                        if (typeface != null) {
                            tvTitle.setTypeface(typeface);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (imgIcon != null) {
                if (hasIcon) {
                    imgIcon.setImageResource(params.iconRes);

                    // تطبيق لون الأيقونة إن وُجد
                    if (params.iconColor != null) {
                        imgIcon.setImageTintList(ColorStateList.valueOf(params.iconColor));
                    } else {
                        imgIcon.setImageTintList(null); // إزالة أي tint سابق
                    }
                }
                imgIcon.setVisibility(hasIcon ? View.VISIBLE : View.GONE);
            }
        } else {
            headerLayout.setVisibility(View.GONE);
        }
    }

    private void setupMessage(View root) {
        TextView tvMessage = root.findViewById(R.id.sf_tv_message);
        if (tvMessage == null) return;

        boolean hasMessage = params.message != null && !params.message.trim().isEmpty();
        if (hasMessage) {
            tvMessage.setText(params.message);
            tvMessage.setVisibility(View.VISIBLE);

            if (params.themeResId != 0) {
                Context context = root.getContext();
                TypedArray a = context.obtainStyledAttributes(params.themeResId, new int[]{R.attr.sheetMessageStyle});
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

            if (params.messageColor != null) {
                tvMessage.setTextColor(params.messageColor);
            }

            if (params.messageFontResId != 0) {
                try {
                    Typeface typeface = androidx.core.content.res.ResourcesCompat.getFont(root.getContext(), params.messageFontResId);
                    if (typeface != null) {
                        tvMessage.setTypeface(typeface);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            tvMessage.setVisibility(View.GONE);
        }
    }

    private void setupContent(Context context, View root) {
        FrameLayout customContainer = root.findViewById(R.id.sf_custom_container);
        RecyclerView recyclerView = root.findViewById(R.id.sf_recycler_view);

        if (params.customLayoutRes != 0 && customContainer != null) {
            customContainer.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);

            View customView = LayoutInflater.from(context).inflate(params.customLayoutRes, customContainer, false);
            customContainer.addView(customView);

            if (params.onViewCreatedListener != null) {
                params.onViewCreatedListener.onViewCreated(sheetFlow, customView);
            }
        } else if (!params.itemsList.isEmpty() && recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
            if (customContainer != null) customContainer.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            SheetAdapter adapter = new SheetAdapter(params.itemsList, params.isMultiSelect, params.itemsIconColor);
            adapter.setOnItemClickListener((position, item) -> {
                if (item.getAction() != null) {
                    item.getAction().run();
                } else if (params.simpleItemClickListener != null) {
                    params.simpleItemClickListener.onItemClick(position, item.getTitle());
                }
                dismiss();
            });

            if (params.multiChoiceListener != null) {
                adapter.setOnMultiChoiceListener(params.multiChoiceListener);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    private void setupButtons(View root) {
        LinearLayout buttonsLayout = root.findViewById(R.id.sf_buttons_layout);
        Button btnPositive = root.findViewById(R.id.sf_btn_positive);
        Button btnNegative = root.findViewById(R.id.sf_btn_negative);

        if (buttonsLayout == null) return;

        if (params.positiveBtnText != null || params.negativeBtnText != null) {
            buttonsLayout.setVisibility(View.VISIBLE);

            if (btnPositive != null) {
                if (params.positiveBtnText != null) {
                    btnPositive.setText(params.positiveBtnText);
                    btnPositive.setVisibility(View.VISIBLE);
                    btnPositive.setOnClickListener(v -> {
                        if (params.positiveListener != null) {
                            params.positiveListener.onAction(sheetFlow);
                        }
                    });
                } else {
                    btnPositive.setVisibility(View.GONE);
                }
            }

            if (btnNegative != null) {
                if (params.negativeBtnText != null) {
                    btnNegative.setText(params.negativeBtnText);
                    btnNegative.setVisibility(View.VISIBLE);
                    btnNegative.setOnClickListener(v -> {
                        if (params.negativeListener != null) {
                            params.negativeListener.onAction(sheetFlow);
                        }
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

        if (params.isNonModal) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.getWindow().setDimAmount(0.5f);

            if (params.isBlurEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                WindowManager.LayoutParams windowParams = dialog.getWindow().getAttributes();
                try {
                    Field field = WindowManager.LayoutParams.class.getField("blurBehindRadius");
                    field.set(windowParams, params.blurRadius);
                    dialog.getWindow().setAttributes(windowParams);
                } catch (Exception ignored) {
                }
            }
        }

        if (params.autoAdjustKeyboard) {
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
            behavior.setDraggable(params.isDraggable);

            if (params.isNonModal) {
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

            if (params.maxWidthDp > 0) {
                ViewGroup.LayoutParams layoutParams = bottomSheetInternal.getLayoutParams();
                layoutParams.width = (int) (params.maxWidthDp * density);
                bottomSheetInternal.setLayoutParams(layoutParams);
            }

            if (params.nestedScrollChildId != 0) {
                View nestedChild = root.findViewById(params.nestedScrollChildId);
                if (nestedChild != null) {
                    ViewCompat.setNestedScrollingEnabled(nestedChild, true);
                }
            }

            if (params.cornerRadiusDp > 0) {
                bottomSheetInternal.setBackgroundColor(Color.TRANSPARENT);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.WHITE);
                float px = params.cornerRadiusDp * density;
                drawable.setCornerRadii(new float[]{px, px, px, px, 0, 0, 0, 0});
                ViewCompat.setBackground(bottomSheetInternal, drawable);
            }

            applyOrientationAdjustments(context);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (params.stateChangeListener != null) {
                        params.stateChangeListener.onStateChanged(sheetFlow, newState);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        });
    }

    private void applyBackgroundColor() {
        if (dialog == null || params.backgroundColor == null) return;

        View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal != null) {
            bottomSheetInternal.setBackgroundTintList(ColorStateList.valueOf(params.backgroundColor));
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
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        bottomSheetInternal.setLayoutParams(layoutParams);

        behavior.setFitToContents(true);

        if (isLandscape) {
            int displayHeight = context.getResources().getDisplayMetrics().heightPixels;
            behavior.setMaxHeight(displayHeight);
            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setMaxHeight(Integer.MAX_VALUE);

            if (params.peekHeightDp > 0) {
                float density = context.getResources().getDisplayMetrics().density;
                behavior.setFitToContents(false);
                behavior.setPeekHeight((int) (params.peekHeightDp * density));
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (params.isExpanded) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}