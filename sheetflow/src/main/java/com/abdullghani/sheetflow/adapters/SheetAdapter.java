package com.abdullghani.sheetflow.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullghani.sheetflow.R;
import com.abdullghani.sheetflow.callbacks.OnMultiChoiceListener;
import com.abdullghani.sheetflow.models.SheetItem;

import java.util.ArrayList;
import java.util.List;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position, SheetItem item);
    }

    private final List<SheetItem> items;
    private final boolean isMultiSelect;
    private final List<Integer> selectedIndices = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnMultiChoiceListener multiChoiceListener;

    @Nullable
    private Integer iconColor; // ← لون الأيقونات

    public SheetAdapter(List<SheetItem> items, boolean isMultiSelect) {
        this.items = items;
        this.isMultiSelect = isMultiSelect;
    }

    public SheetAdapter(List<SheetItem> items, boolean isMultiSelect, @Nullable @ColorInt Integer iconColor) {
        this.items = items;
        this.isMultiSelect = isMultiSelect;
        this.iconColor = iconColor;
    }

    public void setIconColor(@Nullable @ColorInt Integer iconColor) {
        this.iconColor = iconColor;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnMultiChoiceListener(OnMultiChoiceListener listener) {
        this.multiChoiceListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sf_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SheetItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());

        if (item.getIconRes() != 0) {
            holder.imgIcon.setImageResource(item.getIconRes());
            holder.imgIcon.setVisibility(View.VISIBLE);

            // تطبيق لون الأيقونة
            if (iconColor != null) {
                holder.imgIcon.setImageTintList(ColorStateList.valueOf(iconColor));
            } else {
                holder.imgIcon.setImageTintList(null);
            }
        } else {
            holder.imgIcon.setVisibility(View.GONE);
        }

        if (isMultiSelect) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedIndices.contains(position));
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            if (isMultiSelect) {
                if (selectedIndices.contains(currentPos)) {
                    selectedIndices.remove(Integer.valueOf(currentPos));
                } else {
                    selectedIndices.add(currentPos);
                }
                notifyItemChanged(currentPos);
                if (multiChoiceListener != null) {
                    multiChoiceListener.onSelectionChanged(selectedIndices);
                }
            } else {
                if (clickListener != null) {
                    clickListener.onItemClick(currentPos, item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.sf_item_icon);
            tvTitle = itemView.findViewById(R.id.sf_item_title);
            checkBox = itemView.findViewById(R.id.sf_item_checkbox);
        }
    }
}