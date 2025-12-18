package com.example.homework13panchenkoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework13panchenkoes.databinding.ItemNoteBinding;

import java.util.ArrayList;
import java.util.List;

// Адаптер для списка заметок на RecyclerView
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteActionsListener {
        void onEdit(Note note, int position);
        void onDelete(Note note, int position);
    }

    private final List<Note> notes = new ArrayList<>();
    private final OnNoteActionsListener actionsListener;

    public NoteAdapter(OnNoteActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public void setNotes(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
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

            // Долгое нажатие по карточке → небольшая анимация и контекстное меню
            binding.getRoot().setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return true;

                v.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setDuration(80)
                        .withEndAction(() -> v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .withEndAction(() -> showContextMenu(v, pos))
                                .start()
                        )
                        .start();

                return true;
            });
        }

        void bind(Note note) {
            binding.textTitle.setText(note != null ? note.getTitle() : "");
            binding.textBody.setText(note != null ? note.getText() : "");
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
