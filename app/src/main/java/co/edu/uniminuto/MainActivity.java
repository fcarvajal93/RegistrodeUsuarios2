package co.edu.uniminuto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.edu.uniminuto.entity.User;
import co.edu.uniminuto.model.UserDao;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Validate";

    private EditText etDocumento;
    private EditText etUsuario;
    private EditText etNombres;
    private EditText etApellidos;
    private EditText etContra;
    private ListView listUsers;
    private User currentUser;
    SQLiteDatabase baseDatos;
    private int documento;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String contra;

    private Context context;
    private Button btnSave;
    private Button btnListUsers;
    private Button btnLimpiar;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context = this;
        initObject();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        btnListUsers.setOnClickListener(this::ListUserShow);
        btnLimpiar.setOnClickListener(view -> clearInputFields());
        btnBuscar.setOnClickListener(this::searchUsers);
        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) parent.getItemAtPosition(position);
                currentUser = (User) parent.getItemAtPosition(position);
                etDocumento.setText(String.valueOf(selectedUser.getDocument()));
                etUsuario.setText(selectedUser.getUser());
                etNombres.setText(selectedUser.getNames());
                etApellidos.setText(selectedUser.getLastNames());
                etContra.setText(selectedUser.getPassword());
                btnSave.setText("Actualizar");
                currentUser = selectedUser;
            }
        });
    }

    private void searchUsers(View view) {
        String searchDocumento = etDocumento.getText().toString();
        String searchUsuario = etUsuario.getText().toString();
        String searchNombres = etNombres.getText().toString();
        String searchApellidos = etApellidos.getText().toString();

        String searchQuery = searchDocumento + " " + searchUsuario + " " + searchNombres + " " + searchApellidos;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            UserDao dao = new UserDao(this, listUsers);
            ArrayList<User> result = dao.compositeSearchUsers(searchQuery.trim());

            // Ahora se publica los resultados
            handler.post(() -> {
                ArrayAdapter<User> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, result);
                listUsers.setAdapter(adapter);
            });
        });
    }



    private void ListUserShow(View view) {
        UserDao dao = new UserDao(context,findViewById(R.id.lvLista));
        ArrayList<User> users= dao.getUserList();
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,users);
        listUsers.setAdapter(adapter);
    }
    private void clearInputFields(){
        EditText documentInput = findViewById(R.id.etDocumento);
        EditText userInput = findViewById(R.id.etUsuario);
        EditText namesInput = findViewById(R.id.etNombres);
        EditText lastNamesInput = findViewById(R.id.etApellidos);
        EditText passwordInput = findViewById(R.id.etContra);

        documentInput.setText("");
        userInput.setText("");
        namesInput.setText("");
        lastNamesInput.setText("");
        passwordInput.setText("");

        if (listUsers != null) {
            listUsers.setAdapter(null);
        }

    }

    private void createUser1(View view) {
        getData();
        User user = new User(documento,nombres,apellidos,usuario,contra);
        UserDao dao = new UserDao(context,view);
        dao.insertUser(user);
        ListUserShow(view);
    }
    private void refreshUserList(){
        UserDao dao = new UserDao(context, findViewById(R.id.lvLista));
        ArrayList<User> users = dao.getUserList();
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        listUsers.setAdapter((ListAdapter) adapter);
        currentUser = null;
        btnSave.setText("Registrar");
    }
    private void createUser() {
        getData(); // Obtén los datos de los EditText

        if (currentUser != null) {
            // Estás en modo de edición
            currentUser.setDocument(documento);
            currentUser.setUser(usuario);
            currentUser.setNames(nombres);
            currentUser.setLastNames(apellidos);
            currentUser.setPassword(contra); // Asegúrate de manejar la contraseña con cuidado

            UserDao dao = new UserDao(context, findViewById(R.id.lvLista));
            dao.updateUser(currentUser);

            refreshUserList(); // Refresca la lista para mostrar los datos actualizados
            clearInputFields(); // Limpia los campos o resetea el formulario como sea necesario
            currentUser = null;
            btnSave.setText("Registrar");// Restablece el usuario actual para permitir registros nuevos
        } else {
            // Estás en modo de creación
            User newUser = new User(documento, nombres, apellidos, usuario, contra);
            UserDao dao = new UserDao(context, findViewById(R.id.lvLista));
            dao.insertUser(newUser);

            refreshUserList(); // Refresca la lista para incluir el usuario recién creado
            clearInputFields(); // Limpia los campos para un nuevo registro
        }
    }


    private void getData(){
        String documentoStr = etDocumento.getText().toString();
        if (!documentoStr.isEmpty()){
            documento = Integer.parseInt(documentoStr);
        }else {
            documento = 0;
        }
        documento = Integer.parseInt(etDocumento.getText().toString());
        usuario = etUsuario.getText().toString();
        nombres = etNombres.getText().toString();
        apellidos = etApellidos.getText().toString();
        contra = etContra.getText().toString();

        //Validación de datos con expresiones regulares

    }

    private void initObject(){
        btnSave = findViewById(R.id.btnRegistrar);
        btnListUsers = findViewById(R.id.btnListar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnBuscar = findViewById(R.id.btnBuscar);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etDocumento = findViewById(R.id.etDocumento);
        etUsuario = findViewById(R.id.etUsuario);
        etContra = findViewById(R.id.etContra);
        listUsers = findViewById(R.id.lvLista);
    }
}