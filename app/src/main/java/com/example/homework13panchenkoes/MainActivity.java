package com.example.homework13panchenkoes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homework13panchenkoes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteActionsListener {

    // Ключи для обмена данными между активити
    public static final String EXTRA_NOTE_TITLE = "extra_note_title";
    public static final String EXTRA_NOTE_TEXT = "extra_note_text";
    public static final String EXTRA_NOTE_POSITION = "extra_note_position";

    // Коды запросов
    private static final int REQUEST_ADD_NOTE = 1;
    private static final int REQUEST_EDIT_NOTE = 2;

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private final List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Тулбар как ActionBar
        setSupportActionBar(binding.toolbar);

        // Настраиваем RecyclerView
        adapter = new NoteAdapter(notes, this);
        binding.recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerNotes.setHasFixedSize(true);
        binding.recyclerNotes.setAdapter(adapter);
        binding.recyclerNotes.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        // FAB для добавления новой заметки
        binding.fabAddNote.setOnClickListener(v -> openAddNoteScreen());

        // Пара демо-заметок, чтобы список не был пустым при первом запуске
        seedDemoNotesIfEmpty();
    }

    // Открываем экран создания новой заметки
    private void openAddNoteScreen() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NOTE);
    }

    // Пользователь выбрал "Изменить" в контекстном меню
    @Override
    public void onEdit(Note note, int position) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE_TITLE, note.getTitle());
        intent.putExtra(EXTRA_NOTE_TEXT, note.getText());
        intent.putExtra(EXTRA_NOTE_POSITION, position);
        startActivityForResult(intent, REQUEST_EDIT_NOTE);
    }

    // Пользователь выбрал "Удалить" в контекстном меню
    @Override
    public void onDelete(Note note, int position) {
        notes.remove(position);
        adapter.notifyItemRemoved(position);
    }

    // Получаем результат от EditNoteActivity (добавление / редактирование)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            // Пользователь нажал «Назад» или не вернул данные
            return;
        }

        String title = data.getStringExtra(EXTRA_NOTE_TITLE);
        String text = data.getStringExtra(EXTRA_NOTE_TEXT);
        int position = data.getIntExtra(EXTRA_NOTE_POSITION, -1);

        if (title == null) title = "";
        if (text == null) text = "";

        if (requestCode == REQUEST_ADD_NOTE) {
            // Добавляем новую заметку
            Note note = new Note(title, text);
            notes.add(note);
            int newIndex = notes.size() - 1;
            adapter.notifyItemInserted(newIndex);
            binding.recyclerNotes.scrollToPosition(newIndex);

        } else if (requestCode == REQUEST_EDIT_NOTE &&
                position >= 0 && position < notes.size()) {
            // Обновляем существующую заметку
            Note note = notes.get(position);
            note.setTitle(title);
            note.setText(text);
            adapter.notifyItemChanged(position);
        }
    }

    // Пара стартовых заметок для разработки
    private void seedDemoNotesIfEmpty() {
        if (!notes.isEmpty()) return;

        notes.add(new Note("Первая заметка", "Это пример заметки для проверки списка."));
        notes.add(new Note("Идея для проекта", "Сделать своё приложение заметок с синхронизацией."));

        // Так как список был пустой, вставили диапазон элементов с 0-го
        adapter.notifyItemRangeInserted(0, notes.size());
    }
}
