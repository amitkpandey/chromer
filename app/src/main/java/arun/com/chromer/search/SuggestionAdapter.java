package arun.com.chromer.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import arun.com.chromer.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arun on 03/08/2016.
 */
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionItemHolder> {
    private final Context mContext;

    private SuggestionClickListener mCallback = new SuggestionClickListener() {
        @Override
        public void onSuggestionClicked(@NonNull String suggestion) {
            // no op
        }
    };
    @NonNull
    private final List<SuggestionItem> mSuggestions = new ArrayList<>();

    private static Drawable sSearch;

    public SuggestionAdapter(@NonNull final Context context, @Nullable SuggestionClickListener listener) {
        mContext = context.getApplicationContext();
        setHasStableIds(true);
        mCallback = listener;

        if (sSearch == null)
            sSearch = new IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_magnify)
                    .color(ContextCompat.getColor(context, R.color.material_dark_light))
                    .sizeDp(18);
    }

    @Override
    public SuggestionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SuggestionItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item_template, parent, false));
    }

    @Override
    public void onBindViewHolder(SuggestionItemHolder holder, int position) {
        holder.suggestion.setText(mSuggestions.get(position).suggestion);
        SuggestionItem item = mSuggestions.get(position);
        switch (item.type) {
            case SuggestionItem.COPY:
                holder.icon.setImageDrawable(new IconicsDrawable(mContext)
                        .icon(CommunityMaterial.Icon.cmd_content_copy)
                        .color(ContextCompat.getColor(mContext, R.color.md_cyan_600))
                        .sizeDp(18));
                break;
            case SuggestionItem.GOOGLE:
                holder.icon.setImageDrawable(sSearch);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSuggestions.size();
    }

    @Override
    public long getItemId(int position) {
        return mSuggestions.get(position).hashCode();
    }

    public void updateSuggestions(@NonNull List<SuggestionItem> newSuggestions) {
        final SuggestionDiff suggestionDiff = new SuggestionDiff(mSuggestions, newSuggestions);

        //Benchmark.start("Diff Calculation");
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(suggestionDiff, true);
        // Benchmark.end();

        mSuggestions.clear();
        mSuggestions.addAll(newSuggestions);

        diffResult.dispatchUpdatesTo(this);
    }

    public void clear() {
        mSuggestions.clear();
        notifyDataSetChanged();
    }

    class SuggestionItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.suggestions_text)
        TextView suggestion;
        @BindView(R.id.suggestion_icon)
        ImageView icon;

        SuggestionItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mCallback.onSuggestionClicked(mSuggestions.get(position).suggestion);
                    }
                }
            });
        }
    }

    public interface SuggestionClickListener {
        void onSuggestionClicked(@NonNull final String suggestion);
    }

    private class SuggestionDiff extends DiffUtil.Callback {

        private final List<SuggestionItem> mNewList;
        private final List<SuggestionItem> mOldList;

        SuggestionDiff(@NonNull List<SuggestionItem> oldList, @NonNull List<SuggestionItem> newList) {
            mOldList = oldList;
            mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).suggestion.
                    equalsIgnoreCase(mNewList.get(newItemPosition).suggestion);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).suggestion.
                    equalsIgnoreCase(mNewList.get(newItemPosition).suggestion);
        }
    }
}
