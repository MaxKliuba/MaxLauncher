package com.maxclub.android.maxlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MaxLauncherFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public static MaxLauncherFragment newInstance() {
        return new MaxLauncherFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_max_launcher, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        Intent setupIntent = new Intent(Intent.ACTION_MAIN);
        setupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(setupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();

                return String.CASE_INSENSITIVE_ORDER.compare(
                        o1.loadLabel(pm).toString(),
                        o2.loadLabel(pm).toString()
                );
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(activities));
        int count = activities.size();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar()
                .setSubtitle(getResources().getQuantityString(R.plurals.subtitle_app_quantity, count, count));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private ImageView mIconImageView;
        private TextView mNameTextView;
        private TextView mSizeTextView;

        public ActivityHolder(View itemView) {
            super(itemView);

            mIconImageView = (ImageView) itemView.findViewById(R.id.app_icon_view);
            mNameTextView = (TextView) itemView.findViewById(R.id.app_name_view);
            mSizeTextView = (TextView) itemView.findViewById(R.id.app_size_view);

            itemView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            new AppIconAsuncTask().execute(packageManager);
            /* Drawable appIcon = mResolveInfo.loadIcon(packageManager);
            mIconImageView.setImageDrawable(appIcon); */
            String appName = mResolveInfo.loadLabel(packageManager).toString();
            mNameTextView.setText(appName);
            String appSize = AppDetails.formatSize(AppDetails.getApkSize(mResolveInfo));
            mSizeTextView.setText(appSize);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }

        private class AppIconAsuncTask extends AsyncTask<PackageManager, Void, Drawable> {

            @Override
            protected Drawable doInBackground(PackageManager... packageManagers) {
                return mResolveInfo.loadIcon(packageManagers[0]);
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                mIconImageView.setImageDrawable(drawable);
            }
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> mActivities;

        private ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @NonNull
        @NotNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.activity_item_list, parent, false);

            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MaxLauncherFragment.ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }
}