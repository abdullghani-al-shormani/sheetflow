package com.abdullghani.sheetflowsample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.abdullghani.sheetflow.SheetFlow;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Quick Dynamic List Case
        findViewById(R.id.btnQuickList).setOnClickListener(v -> {
            List<String> options = Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4");

            SheetFlow.with(this)
                    .setTitle("Select Category")
                    .setMessage("Choose an option from the list below to proceed.")
                    .setCornerRadius(24)
                    .setItems(options, (position, item) -> {
                        Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        // 2. Action Menu Sheet Case
        findViewById(R.id.btnActionMenu).setOnClickListener(v -> {
            SheetFlow.with(this)
                    .setTitle("Manage Content")
                    .setCornerRadius(20)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    .addItem(android.R.drawable.ic_menu_edit, "Edit Post", () -> {
                        Toast.makeText(this, "Edit action clicked", Toast.LENGTH_SHORT).show();
                    })
                    .addItem(android.R.drawable.ic_menu_share, "Share Link", () -> {
                        Toast.makeText(this, "Share action clicked", Toast.LENGTH_SHORT).show();
                    })
                    .addItem(android.R.drawable.ic_menu_delete, "Delete Item", () -> {
                        Toast.makeText(this, "Delete action clicked", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        // 3. Custom Layout View Case
        findViewById(R.id.btnCustomLayout).setOnClickListener(v -> {
            SheetFlow.with(this)
                    .setView(R.layout.dialog_custom_comment)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    .onViewCreated((sheet, view) -> {
                        TextView tvItem1 = view.findViewById(R.id.tvItem1);
                        TextView tvItem2 = view.findViewById(R.id.tvItem2);

                        tvItem1.setOnClickListener(vv -> {
                            Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                            sheet.dismiss();
                        });

                        tvItem2.setOnClickListener(vv -> {
                            Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                            sheet.dismiss();
                        });
                    })
                    .show();
        });

        // 4. Full Screen / Expanded Sheet Case
        findViewById(R.id.btnExpandedSheet).setOnClickListener(v -> {
            List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6");

            SheetFlow.with(this)
                    .setTitle("Expanded View")
                    .setExpanded(true)
                    .setCornerRadius(20)
                    .setItems(items, (position, item) -> {
                        Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        // 5. Confirmation Alert Sheet Case
        findViewById(R.id.btnAlertSheet).setOnClickListener(v -> {
            SheetFlow.with(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setCornerRadius(16)
                    .setPositiveButton("Yes, Delete", sheet -> {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        sheet.dismiss();
                    })
                    .setNegativeButton("Cancel", SheetFlow::dismiss)
                    .show();
        });

        // 6. Multi-Select Sheet Case
        findViewById(R.id.btnMultiSelect).setOnClickListener(v -> {
            List<String> filters = Arrays.asList("Newest First", "Highest Rated", "Free Items", "In Stock Only");

            SheetFlow.with(this)
                    .setTitle("Filter Results")
                    .setCornerRadius(20)
                    .setMultiChoiceItems(filters, selectedIndices -> {
                        // Contains list of selected indices
                    })
                    .setPositiveButton("Apply Filter", sheet -> {
                        Toast.makeText(this, "Filters applied successfully", Toast.LENGTH_SHORT).show();
                        sheet.dismiss();
                    })
                    .setNegativeButton("Cancel", SheetFlow::dismiss)
                    .show();
        });

        // 7. Auto Keyboard Adjustment Case
        findViewById(R.id.btnKeyboardAdjust).setOnClickListener(v -> {
            SheetFlow.with(this)
                    .setTitle("Form Input")
                    .autoAdjustForKeyboard(true)
                    .setCornerRadius(20)
                    .setPositiveButton("Save", SheetFlow::dismiss)
                    .show();
        });

        // 8. Blur Background Effect Case
        findViewById(R.id.btnBlurBackground).setOnClickListener(v -> {
            List<String> options = Arrays.asList("Blur Option A", "Blur Option B", "Blur Option C");

            SheetFlow.with(this)
                    .setTitle("Blur Background Effect")
                    .setBlurBackground(true, 20)
                    .setCornerRadius(20)
                    .setItems(options, (position, item) -> {
                        Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        // 9. Persistent / Peek Height Sheet Case
        findViewById(R.id.btnPeekHeightSheet).setOnClickListener(v -> {
            SheetFlow.with(this)
                    .setView(R.layout.dialog_collapsible_sheet)
                    .setPeekHeightDp(100)
                    .setCornerRadius(20)
                    .setCancelable(false)
                    .setNonModal(true)
                    .onViewCreated((sheet, view) -> {
                        Button btnCloseExpandedOnly = view.findViewById(R.id.btnCloseExpandedOnly);
                        Button btnCloseAll = view.findViewById(R.id.btnCloseAll);
                        TextView tvToggleExpand = view.findViewById(R.id.tvToggleExpand);

                        tvToggleExpand.setOnClickListener(vv -> sheet.collapse());

                        btnCloseExpandedOnly.setOnClickListener(vv -> {
                            sheet.toggle();
                            Toast.makeText(this, "Toggled expansion state", Toast.LENGTH_SHORT).show();
                        });

                        btnCloseAll.setOnClickListener(vv -> {
                            sheet.dismiss();
                            Toast.makeText(this, "Sheet dismissed entirely", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setOnStateChangeListener((sheet, newState) -> {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_EXPANDED:
                                Toast.makeText(MainActivity.this, "Sheet fully expanded", Toast.LENGTH_SHORT).show();
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                Toast.makeText(MainActivity.this, "Sheet collapsed to peek height", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    })
                    .show();
        });

        // 10. Styled Sheet Case
        findViewById(R.id.btnSheetWithStyles).setOnClickListener(view -> {
            SheetFlow.with(this, R.style.MyCustomAppSheetTheme)
                    .setTitle("Custom Styled Sheet")
                    .setMessage("This layout automatically follows the XML theme configuration.")
                    .setCornerRadius(20)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    .show();
        });
    }
}