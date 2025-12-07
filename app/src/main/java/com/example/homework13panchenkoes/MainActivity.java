package com.example.homework13panchenkoes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homework13panchenkoes.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // SharedPreferences
    private static final String PREFS_NAME = "notes_prefs";
    private static final String KEY_NOTES_JSON = "notes_json";

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private final List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Подключаем SplashScreen до super.onCreate()
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Тулбар как ActionBar
        setSupportActionBar(binding.toolbar);

        // Настраиваем RecyclerView
        adapter = new NoteAdapter(notes, this);
        binding.recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerNotes.setAdapter(adapter);

        // FAB для добавления новой заметки
        binding.fabAddNote.setOnClickListener(v -> openAddNoteScreen());

        // Загружаем заметки из SharedPreferences
        loadNotes();
    }

    // Открываем экран создания новой заметки
    private void openAddNoteScreen() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NOTE);
    }

    // Реализация интерфейса адаптера: пользователь выбрал "Изменить"
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
        saveNotes();
    }

    // Получаем результат от EditNoteActivity (добавление / редактирование)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return; // пользователь нажал "назад" или ничего не вернул
        }

        String title = data.getStringExtra(EXTRA_NOTE_TITLE);
        String text = data.getStringExtra(EXTRA_NOTE_TEXT);
        int position = data.getIntExtra(EXTRA_NOTE_POSITION, -1);

        if (title == null) {
            title = "";
        }
        if (text == null) {
            text = "";
        }

        if (requestCode == REQUEST_ADD_NOTE) {
            // Добавляем новую заметку
            Note note = new Note(title, text);
            notes.add(note);
            int newIndex = notes.size() - 1;
            adapter.notifyItemInserted(newIndex);
            binding.recyclerNotes.scrollToPosition(newIndex);
            saveNotes();
        } else if (requestCode == REQUEST_EDIT_NOTE && position >= 0 && position < notes.size()) {
            // Обновляем существующую заметку (создаём новый объект Note)
            Note updated = new Note(title, text);
            notes.set(position, updated);
            adapter.notifyItemChanged(position);
            saveNotes();
        }
    }

    // Загрузка заметок из SharedPreferences
    private void loadNotes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTES_JSON, null);

        notes.clear();

        if (json != null && !json.isEmpty()) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String title = obj.optString("title", "");
                    String text = obj.optString("text", "");
                    notes.add(new Note(title, text));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Если сохранённых заметок нет – пара демонстрационных
        if (notes.isEmpty()) {
            notes.add(new Note("Первая заметка", "Это пример заметки для проверки списка."));
            notes.add(new Note("Идея для проекта", "Сделать своё приложение заметок с синхронизацией."));
        }

        adapter.notifyDataSetChanged();
    }

    // Сохранение заметок в SharedPreferences в виде JSON
    private void saveNotes() {
        JSONArray array = new JSONArray();
        for (Note note : notes) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", note.getTitle());
                obj.put("text", note.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_NOTES_JSON, array.toString())
                .apply();
    }
}
