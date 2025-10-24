package com.faraz.meditrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {
    private Context context;
    private List<Report> fullList;
    private List<Report> reports;

    public ReportsAdapter(Context context, List<Report> fullList){
        this.context = context;
        this.fullList = fullList;
        this.reports = new ArrayList<>(fullList);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @NonNull
    @Override
    public ReportsAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reports_layout, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.title.setText(report.title);
        holder.date.setText(report.date);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Report")
                        .setMessage("Are you sure you want to delete this report")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteReport(holder.getBindingAdapterPosition());
                        })
                        .setNegativeButton("No", (dialog, which) -> {})
                        .show();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewReports.class);
                intent.putExtra("reportUri", reports.get(holder.getBindingAdapterPosition()).imageUri);
                ((MainActivity) context).startActivity(intent);
            }
        });
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView delete;
        CardView cardView;
        public ReportViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.reportsTitleTxt);
            date = view.findViewById(R.id.reportsDateTxt);
            delete = view.findViewById(R.id.deleteReportImg);
            cardView = view.findViewById(R.id.reportCard);
        }
    }

    private void deleteReport(int position){
        Report reportToDelete = reports.get(position);
        new Thread(() -> {
            ReportDatabase database = Room.databaseBuilder(context, ReportDatabase.class, "reports").build();
            ReportsDao dao = database.reportsDao();
            dao.delete(reportToDelete);

            ((MainActivity) context).runOnUiThread(() -> {
                fullList.remove(reportToDelete);
                reports.remove(position);
                notifyItemRemoved(position);
            });
        }).start();
    }

    public void filter(String query){
        query = query.toLowerCase().trim();
        reports.clear();
        if(query.isEmpty()) reports.addAll(fullList);
        else{
            for(Report report:fullList){
                if(report.title.toLowerCase().contains(query)) reports.add(report);
            }
        }
        notifyDataSetChanged();
    }
}
