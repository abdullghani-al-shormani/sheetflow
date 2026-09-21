# SheetFlow 🌊

[![JitPack](https://jitpack.io/v/abdullghani-al-shormani/sheetflow.svg)](https://jitpack.io/#abdullghani-al-shormani/sheetflow)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**SheetFlow** is a lightweight, fluid, and highly customizable Android BottomSheet library built using the Fluent Builder pattern. It simplifies creating complex BottomSheet dialogs—ranging from dynamic lists and action menus to custom layouts, alert confirmations, and persistent peek-height sheets—with minimal boilerplate code. Seamlessly compatible with both **Java** and **Kotlin**.

---

## ✨ Features

* **Full Java & Kotlin Support**: Seamless interoperability with 100% support for both Kotlin (including trailing lambdas) and Java codebases.
* **Fluent Builder API**: Chainable methods (`SheetFlow.with(context)`) for clean and readable code.
* **Custom Views & Layouts**: Easily inflate custom XML layouts with interactive view callbacks.
* **Dynamic Lists & Action Menus**: Built-in support for single-choice lists, multi-select items, and action menus with icons.
* **Alert & Confirmation Sheets**: Pre-configured dialog styles for quick alert and confirmation sheets.
* **Behavior & Peek Height Controls**: Flexible peek heights, full-screen expanded states, and non-modal modes (touch pass-through).
* **Visual Effects**: Integrated background blur and dim color customization.
* **Rotation & Lifecycle Aware**: Built-in state preservation across orientation changes.

---

## 📦 Installation

Add JitPack to your project's `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url '[https://jitpack.io](https://jitpack.io)' }
    }
}
```

Then, add the dependency to your module **build.gradle** file:
```groovy
dependencies {
    implementation 'com.github.abdullghani-al-shormani:sheetflow:1.0.0'
}
```

## Usage & Examples
**1. Quick Dynamic List**
Display a quick selection list with minimal setup.

**Java**
```java
List<String> options = Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4");

SheetFlow.with(this)
        .setTitle("Select Category")
        .setMessage("Choose an option from the list below to proceed.")
        .setCornerRadius(24)
        .setItems(options, (position, item) -> {
            Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
        })
        .show();
```

**Kotlin**
```kotlin
val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

SheetFlow.with(this)
    .setTitle("Select Category")
    .setMessage("Choose an option from the list below to proceed.")
    .setCornerRadius(24)
    .setItems(options) { position, item ->
        Toast.makeText(this, "Selected: $item", Toast.LENGTH_SHORT).show()
    }
    .show()
```

**2. Action Menu Sheet**
Create an itemized menu with icons for quick user actions.

**Java**
```java
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
```

**Kotlin**
```kotlin
SheetFlow.with(this)
    .setTitle("Manage Content")
    .setCornerRadius(20)
    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    .addItem(android.R.drawable.ic_menu_edit, "Edit Post") {
        Toast.makeText(this, "Edit action clicked", Toast.LENGTH_SHORT).show()
    }
    .addItem(android.R.drawable.ic_menu_share, "Share Link") {
        Toast.makeText(this, "Share action clicked", Toast.LENGTH_SHORT).show()
    }
    .addItem(android.R.drawable.ic_menu_delete, "Delete Item") {
        Toast.makeText(this, "Delete action clicked", Toast.LENGTH_SHORT).show()
    }
    .show()
```

**3. Custom Layout View**
Inflate custom XML layouts and attach interactive click listeners smoothly.

**Java**
```java
SheetFlow.with(this)
        .setView(R.layout.dialog_custom_comment)
        .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        .onViewCreated((sheet, view) -> {
            TextView tvItem1 = view.findViewById(R.id.tvItem1);
            TextView tvItem2 = view.findViewById(R.id.tvItem2);

            tvItem1.setOnClickListener(v -> {
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                sheet.dismiss();
            });

            tvItem2.setOnClickListener(v -> {
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                sheet.dismiss();
            });
        })
        .show();
```

**Kotlin**
```kotlin
SheetFlow.with(this)
    .setView(R.layout.dialog_custom_comment)
    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    .onViewCreated { sheet, view ->
        val tvItem1 = view.findViewById<TextView>(R.id.tvItem1)
        val tvItem2 = view.findViewById<TextView>(R.id.tvItem2)

        tvItem1.setOnClickListener {
            Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show()
            sheet.dismiss()
        }

        tvItem2.setOnClickListener {
            Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show()
            sheet.dismiss()
        }
    }
    .show()
```

**4. Full Screen / Expanded Sheet**
Force the BottomSheet to open fully expanded upon display.

**Java**
```java
List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6");

SheetFlow.with(this)
        .setTitle("Expanded View")
        .setExpanded(true)
        .setCornerRadius(20)
        .setItems(items, (position, item) -> {
            Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
        })
        .show();
```

**Kotlin**
```kotlin
val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6")

SheetFlow.with(this)
    .setTitle("Expanded View")
    .setExpanded(true)
    .setCornerRadius(20)
    .setItems(items) { position, item ->
        Toast.makeText(this, "Selected: $item", Toast.LENGTH_SHORT).show()
    }
    .show()
```

**5. Confirmation Alert Sheet**
Display alert and confirmation dialogs with positive/negative action buttons.

**Java**
```java
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
```

**Kotlin**
```kotlin
SheetFlow.with(this)
    .setIcon(android.R.drawable.ic_dialog_alert)
    .setTitle("Delete Account")
    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
    .setCornerRadius(16)
    .setPositiveButton("Yes, Delete") { sheet ->
        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
        sheet.dismiss()
    }
    .setNegativeButton("Cancel") { sheet -> sheet.dismiss() }
    .show()
```

**6. Multi-Select Sheet**
Display multi-selection option lists.

**Java**
```java
List<String> filters = Arrays.asList("Newest First", "Highest Rated", "Free Items", "In Stock Only");

SheetFlow.with(this)
        .setTitle("Filter Results")
        .setCornerRadius(20)
        .setMultiChoiceItems(filters, selectedIndices -> {
            // Returns selected indices list
        })
        .setPositiveButton("Apply Filter", sheet -> {
            Toast.makeText(this, "Filters applied successfully", Toast.LENGTH_SHORT).show();
            sheet.dismiss();
        })
        .setNegativeButton("Cancel", SheetFlow::dismiss)
        .show();
```

**Kotlin**
```kotlin
val filters = listOf("Newest First", "Highest Rated", "Free Items", "In Stock Only")

SheetFlow.with(this)
    .setTitle("Filter Results")
    .setCornerRadius(20)
    .setMultiChoiceItems(filters) { selectedIndices ->
        // Returns selected indices list
    }
    .setPositiveButton("Apply Filter") { sheet ->
        Toast.makeText(this, "Filters applied successfully", Toast.LENGTH_SHORT).show()
        sheet.dismiss()
    }
    .setNegativeButton("Cancel") { sheet -> sheet.dismiss() }
    .show()
```

**7. Auto Keyboard Adjustment**
Automatically adjust bottom sheet layout and panned content when the soft keyboard appears.

**Java**
```java
SheetFlow.with(this)
        .setTitle("Form Input")
        .autoAdjustForKeyboard(true)
        .setCornerRadius(20)
        .setPositiveButton("Save", SheetFlow::dismiss)
        .show();
```

**Kotlin**
```kotlin
SheetFlow.with(this)
    .setTitle("Form Input")
    .autoAdjustForKeyboard(true)
    .setCornerRadius(20)
    .setPositiveButton("Save") { sheet -> sheet.dismiss() }
    .show()
```

**8. Background Blur Effect**
Apply dynamic background window blur behind the sheet surface.

**Java**
```java
List<String> options = Arrays.asList("Blur Option A", "Blur Option B", "Blur Option C");

SheetFlow.with(this)
        .setTitle("Blur Background Effect")
        .setBlurBackground(true, 20)
        .setCornerRadius(20)
        .setItems(options, (position, item) -> {
            Toast.makeText(this, "Selected: " + item, Toast.LENGTH_SHORT).show();
        })
        .show();
```

**Kotlin**
```kotlin
val options = listOf("Blur Option A", "Blur Option B", "Blur Option C")

SheetFlow.with(this)
    .setTitle("Blur Background Effect")
    .setBlurBackground(true, 20)
    .setCornerRadius(20)
    .setItems(options) { position, item ->
        Toast.makeText(this, "Selected: $item", Toast.LENGTH_SHORT).show()
    }
    .show()
```

**9. Persistent Peek Height Sheet & State Listening**
Configure fixed peek heights, non-modal behavior, state transitions, and programmatic expansion control.

**Java**
```java
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

            tvToggleExpand.setOnClickListener(v -> sheet.collapse());

            btnCloseExpandedOnly.setOnClickListener(v -> {
                sheet.toggle();
                Toast.makeText(this, "Toggled expansion state", Toast.LENGTH_SHORT).show();
            });

            btnCloseAll.setOnClickListener(v -> {
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
```

**Kotlin**
```kotlin
SheetFlow.with(this)
    .setView(R.layout.dialog_collapsible_sheet)
    .setPeekHeightDp(100)
    .setCornerRadius(20)
    .setCancelable(false)
    .setNonModal(true)
    .onViewCreated { sheet, view ->
        val btnCloseExpandedOnly = view.findViewById<Button>(R.id.btnCloseExpandedOnly)
        val btnCloseAll = view.findViewById<Button>(R.id.btnCloseAll)
        val tvToggleExpand = view.findViewById<TextView>(R.id.tvToggleExpand)

        tvToggleExpand.setOnClickListener { sheet.collapse() }

        btnCloseExpandedOnly.setOnClickListener {
            sheet.toggle()
            Toast.makeText(this, "Toggled expansion state", Toast.LENGTH_SHORT).show()
        }

        btnCloseAll.setOnClickListener {
            sheet.dismiss()
            Toast.makeText(this, "Sheet dismissed entirely", Toast.LENGTH_SHORT).show()
        }
    }
    .setOnStateChangeListener { sheet, newState ->
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                Toast.makeText(this, "Sheet fully expanded", Toast.LENGTH_SHORT).show()
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                Toast.makeText(this, "Sheet collapsed to peek height", Toast.LENGTH_SHORT).show()
            }
        }
    }
    .show()
```

**10. Custom XML Theme Styling**
Pass custom style theme resources directly into SheetFlow.

**Java**
```java
SheetFlow.with(this, R.style.MyCustomAppSheetTheme)
        .setTitle("Custom Styled Sheet")
        .setMessage("This layout automatically follows the XML theme configuration.")
        .setCornerRadius(20)
        .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        .show();
```

**Kotlin**
```kotlin
SheetFlow.with(this, R.style.MyCustomAppSheetTheme)
    .setTitle("Custom Styled Sheet")
    .setMessage("This layout automatically follows the XML theme configuration.")
    .setCornerRadius(20)
    .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    .show()
```

**📄 License**
Copyright 2026 E.Abdullghani Al-Shormani

Licensed under the MIT License; you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    [https://opensource.org/licenses/MIT]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
