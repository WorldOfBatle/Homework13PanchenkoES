package com.example.homework13panchenkoes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homework13panchenkoes.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteActionsListener {

    // Ключи для обмена данными между активити
    public static final String EXTRA_NOTE_TITLE = "extra_note_title";
    public static final String EXTRA_NOTE_TEXT = "extra_note_text";
    public static final String EXTRA_NOTE_POSITION = "extra_note_position";

    // Коды запросов
    private static final int REQUEST_ADD_NOTE = 1;
    private static final int REQUEST_EDIT_NOTE = 2;

    // Permission для уведомлений (Android 13+)
    private static final int REQUEST_POST_NOTIFICATIONS = 101;

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private NotesViewModel viewModel;

    // Отложенное уведомление (если сначала нужно запросить permission)
    private int pendingNotificationId = 0;
    private String pendingNotificationTitle = null;
    private String pendingNotificationText = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Подключаем SplashScreen до super.onCreate()
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Канал уведомлений
        NotificationUtils.ensureNotesChannel(this);

        // Тулбар как ActionBar
        setSupportActionBar(binding.toolbar);

        // RecyclerView + Adapter
        adapter = new NoteAdapter(this);
        binding.recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerNotes.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        // Подписываемся на список заметок (LiveData)
        viewModel.getNotes().observe(this, this::renderNotes);

        // FAB для добавления новой заметки
        binding.fabAddNote.setOnClickListener(v -> openAddNoteScreen());
    }

    private void renderNotes(List<Note> notes) {
        adapter.setNotes(notes);
    }

    // Открываем экран создания новой заметки
    private void openAddNoteScreen() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NOTE);
    }

    @Override
    public void onEdit(Note note, int position) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE_TITLE, note != null ? note.getTitle() : "");
        intent.putExtra(EXTRA_NOTE_TEXT, note != null ? note.getText() : "");
        intent.putExtra(EXTRA_NOTE_POSITION, position);
        startActivityForResult(intent, REQUEST_EDIT_NOTE);
    }

    @Override
    public void onDelete(Note note, int position) {
        Note removed = viewModel.deleteNote(position);

        String body = safeTitleForNotification(removed != null ? removed.getTitle() : "");
        notifyWithPermission(
                getString(R.string.notif_deleted_title),
                body
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        String title = data.getStringExtra(EXTRA_NOTE_TITLE);
        String text = data.getStringExtra(EXTRA_NOTE_TEXT);
        int position = data.getIntExtra(EXTRA_NOTE_POSITION, -1);

        if (title == null) title = "";
        if (text == null) text = "";

        if (requestCode == REQUEST_ADD_NOTE) {
            int newIndex = viewModel.addNote(title, text);

            // Прокрутка к добавленной заметке
            int finalIndex = newIndex;
            binding.recyclerNotes.post(() -> binding.recyclerNotes.scrollToPosition(finalIndex));

            notifyWithPermission(
                    getString(R.string.notif_added_title),
                    safeTitleForNotification(title)
            );

        } else if (requestCode == REQUEST_EDIT_NOTE) {
            viewModel.updateNote(position, title, text);

            notifyWithPermission(
                    getString(R.string.notif_updated_title),
                    safeTitleForNotification(title)
            );
        }
    }

    private String safeTitleForNotification(String title) {
        String trimmed = title == null ? "" : title.trim();
        return trimmed.isEmpty() ? getString(R.string.notif_empty_title) : trimmed;
    }

    private void notifyWithPermission(String notifTitle, String notifText) {
        int id = (int) (System.currentTimeMillis() & 0x7fffffff);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int state = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
            if (state == PackageManager.PERMISSION_GRANTED) {
                NotificationUtils.showNoteNotification(this, id, notifTitle, notifText);
            } else {
                pendingNotificationId = id;
                pendingNotificationTitle = notifTitle;
                pendingNotificationText = notifText;

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_POST_NOTIFICATIONS
                );
            }
        } else {
            NotificationUtils.showNoteNotification(this, id, notifTitle, notifText);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (granted && pendingNotificationTitle != null) {
                NotificationUtils.showNoteNotification(
                        this,
                        pendingNotificationId,
                        pendingNotificationTitle,
                        pendingNotificationText != null ? pendingNotificationText : ""
                );
            }

            pendingNotificationId = 0;
            pendingNotificationTitle = null;
            pendingNotificationText = null;
        }
    }
}
