package com.example.homework13panchenkoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Адаптер связывает список Note с item_note.xml
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    // Холдер хранит ссылки на view внутри одной карточки
    static class NoteViewHolder extends RecyclerView.ViewHolder {

        final TextView textTitle;
        final TextView textBody;
        final CardView cardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            textTitle = itemView.findViewById(R.id.textTitle);
            textBody = itemView.findViewById(R.id.textBody);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textTitle.setText(note.getTitle());
        holder.textBody.setText(note.getText());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
