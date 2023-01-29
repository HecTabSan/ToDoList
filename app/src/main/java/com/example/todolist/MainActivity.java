package com.example.todolist;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUsuario;

    ListView listViewTareas;
    List<String> listaTareas = new ArrayList<>();
    List<String> listaIdTareas = new ArrayList<>();
    ArrayAdapter<String> mAdapterTareas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        emailUsuario = mAuth.getCurrentUser().getEmail();
        listViewTareas = findViewById(R.id.ListView);

        //actualizar la UI con las tareas del usuario logueado
        actualizarUi();
    }

    private void actualizarUi(){
        db.collection("Tareas")
                .whereEqualTo("emailUsuario", emailUsuario)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        listaTareas.clear();
                        listaIdTareas.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            listaIdTareas.add(doc.getId());
                            listaTareas.add(doc.getString("nombreTarea"));
                        }

                        if(listaTareas.size() == 0){
                            listViewTareas.setAdapter(null);
                        }else {
                            mAdapterTareas = new ArrayAdapter<>(MainActivity.this, R.layout.item_tarea,R.id.nombreTarea,listaTareas);
                            listViewTareas.setAdapter(mAdapterTareas);
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.mas:
                //activar el cuadro de dialogo para añadir tarea
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Nueva tarea")
                        .setMessage("Introduce la tarea")
                        .setView(taskEditText)
                        .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Añadir tarea en la base de datos
                                String miTarea = taskEditText.getText().toString();

                                Map<String, Object> tarea = new HashMap<>();
                                tarea.put("nombreTarea", miTarea );
                                tarea.put("emailUsuario", emailUsuario);

                                db.collection("Tareas").add(tarea);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();
                return true;

            case R.id.logout:
                //llamar a firebase para cerrar sesión
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                //onBackPressed(); no funciona bien porque deja usuario y contraseña
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void borrarTarea(View view){
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.nombreTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);
        toastTareaRealizada("Tarea Realizada");

        db.collection("Tareas").document(listaIdTareas.get(posicion)).delete();
    }

    public void toastTareaRealizada(String mensaje){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast_tarea_realizada, (ViewGroup) findViewById(R.id.toastTareaRealizada));

        TextView txtMensaje = view.findViewById(R.id.toastTextoTarea);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void editarTarea(View view){
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.nombreTarea);
        String tareaOriginal = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tareaOriginal);

        final EditText taskEditText = new EditText(this);
        taskEditText.setText(tareaOriginal);

        AlertDialog editar = new AlertDialog.Builder(this)
                .setTitle("Editar tarea")
                .setView(taskEditText)
                .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Añadir tarea en la base de datos
                        String mitarea = taskEditText.getText().toString();

                        Map<String, Object> tarea = new HashMap<>();
                        tarea.put("nombreTarea", mitarea);
                        tarea.put("emailUsuario", emailUsuario);

                        db.collection("Tareas").document(listaIdTareas.get(posicion)).update("nombreTarea", mitarea);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        editar.show();

    }
}