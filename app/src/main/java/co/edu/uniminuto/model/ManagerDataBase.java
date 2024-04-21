package co.edu.uniminuto.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ManagerDataBase extends SQLiteOpenHelper {
    // Es buena idea tener constantes para los nombres de las columnas también.
    public static final String COLUMN_DOCUMENT = "use_document";
    public static final String COLUMN_USER = "use_user";
    public static final String COLUMN_NAMES = "use_names";
    public static final String COLUMN_LASTNAMES = "use_lastNames";
    public static final String COLUMN_PASSWORD = "use_password";
    public static final String COLUMN_STATUS = "use_status";
    private static final String TABLE_USERS = "users";
    private static final String DATA_BASE = "dbUsers";
    private static final int VERSION = 1;

    // Usar estas constantes en la creación de la tabla para evitar errores tipográficos.
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_DOCUMENT + " INTEGER PRIMARY KEY NOT NULL, " +
            COLUMN_USER + " varchar(50) NOT NULL, " +
            COLUMN_NAMES + " varchar(150) NOT NULL, " +
            COLUMN_LASTNAMES + " varchar(150) NOT NULL, " +
            COLUMN_PASSWORD + " varchar(25) NOT NULL, " +
            COLUMN_STATUS + " varchar(1) NOT NULL);";
    private static final String DELETE_TABLE="DROP TABLE IF EXISTS "+TABLE_USERS;
    public ManagerDataBase (Context context) {super(context, DATA_BASE, null, VERSION);}


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Agregar métodos para abrir y cerrar la base de datos podría ser útil.
    public void openDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Manejo adicional si es necesario
    }

    public void closeDataBase() {
        this.close();
    }

    // ...
}

