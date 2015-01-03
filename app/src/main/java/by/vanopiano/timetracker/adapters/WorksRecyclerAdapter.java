package by.vanopiano.timetracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import by.vanopiano.timetracker.R;
import by.vanopiano.timetracker.models.Task;
import by.vanopiano.timetracker.models.Work;

/**
 * Created by De_Vano on 3 Jan, 2015
 */

public class WorksRecyclerAdapter extends RecyclerView.Adapter<WorksRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private List<Work> mWorks;

    public WorksRecyclerAdapter(Context context, Task task) {
        mContext = context;
        mWorks = task.works();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Work w = mWorks.get(position);
        viewHolder.mDate.setText(w.getDate());
        viewHolder.mTime.setText(w.workedTime);
    }

    public void notifyTaskAdded(Work work) {
        mWorks.add(work);
        notifyItemInserted(mWorks.indexOf(work) + 1);
    }

    public void removeItem(int position) {
        mWorks.get(position).delete();
        mWorks.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mWorks.size();
    }

    public long getTaskId(int position) {
        return mWorks.get(position).getId();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDate, mTime;

        public ViewHolder(View v) {
            super(v);
            mTime = (TextView) v.findViewById(R.id.work_small_time);
            mDate = (TextView) v.findViewById(R.id.work_small_date);
        }
    }
}
