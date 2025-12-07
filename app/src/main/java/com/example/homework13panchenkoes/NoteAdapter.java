package com.example.homework13panchenkoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework13panchenkoes.databinding.ItemNoteBinding;

import java.util.List;

// Адаптер для списка заметок на RecyclerView
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteActionsListener {
        void onEdit(Note note, int position);
        void onDelete(Note note, int position);
    }

    private final List<Note> notes;
    private final OnNoteActionsListener actionsListener;

    public NoteAdapter(List<Note> notes, OnNoteActionsListener actionsListener) {
        this.notes = notes;
        this.actionsListener = actionsListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNoteBinding binding = ItemNoteBinding.inflate(inflater, parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteBinding binding;

        NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Долгое нажатие по карточке → показываем контекстное меню
            binding.getRoot().setOnLongClickListener(v -> {
                showContextMenu(v, getAdapterPosition());
                return true;
            });
        }

        void bind(Note note) {
            binding.textTitle.setText(note.getTitle());
            binding.textBody.setText(note.getText());
        }

        private void showContextMenu(View anchor, int position) {
            if (position == RecyclerView.NO_POSITION) return;

            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.getMenuInflater().inflate(R.menu.menu_note_context, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (actionsListener == null) return false;

                int id = item.getItemId();
                Note note = notes.get(position);

                if (id == R.id.action_edit) {
                    actionsListener.onEdit(note, position);
                    return true;
                } else if (id == R.id.action_delete) {
                    actionsListener.onDelete(note, position);
                    return true;
                }

                return false;
            });

            popup.show();
        }
    }
}
