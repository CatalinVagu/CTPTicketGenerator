package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import others.SmsInserter;

public class LinesAdapter extends RecyclerView.Adapter<LinesAdapter.TextViewHolder> implements View.OnClickListener {

    private List<String> lines;
    private SmsInserter smsInserter;

    public LinesAdapter() {
        lines = new ArrayList<>();
    }

    public LinesAdapter(List<String> lines) {
        this.lines = lines;
    }

    public void setSmsInserter(SmsInserter smsInserter) {
        this.smsInserter = smsInserter;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        notifyDataSetChanged();
    }

    public void addLine(String line) {
        if (!lines.contains(line)) {
            this.lines.add(line);
            notifyItemInserted(lines.size() - 1);
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextViewHolder(new Button(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        holder.button.setText(lines.get(position));
        holder.button.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    @Override
    public void onClick(View v) {
        smsInserter.insertSms(((Button) v).getText().toString());
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        private Button button;

        public TextViewHolder(Button parent) {
            super(parent);
            button = parent;
        }
    }
}
