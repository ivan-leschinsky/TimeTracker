package by.vanopiano.timetracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.vanopiano.timetracker.models.Task;

/**
 * Created by De_Vano on 31 dec, 2014
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private List<Task> mTasks;

    public RecyclerViewAdapter(Context context) {
        mContext = context;
        mTasks = Task.getAll();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Task t = mTasks.get(position);
        viewHolder.mTitle.setText(t.name);
        viewHolder.mTime.setText(t.getCurrentDiff());
    }

    public void notifyTaskAdded(Task task) {
        mTasks.add(task);
        notifyItemInserted(mTasks.indexOf(task) + 1);
    }

    public void removeItem(int position) {
        mTasks.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public long getTaskId(int position) {
        return mTasks.get(position).getId();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mTime;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.task_small_title);
            mTime = (TextView) v.findViewById(R.id.task_small_time);
        }
    }
}
