package co.edu.uniminuto.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import co.edu.uniminuto.entity.User;

public class UserDao {
    private ManagerDataBase managerDataBase;
    Context context;
    View view;
    private User user;

    public UserDao(Context context, View view) {
        this.context = context;
        this.view = view;
        managerDataBase = new ManagerDataBase(context);
    }
    public void insertUser(User user){
        try {
            SQLiteDatabase db = managerDataBase.getWritableDatabase();
            if (db != null){
                ContentValues values =new ContentValues();
                values.put("use_document",user.getDocument());
                values.put("use_user", user.getUser());
                values.put("use_names", user.getNames());
                values.put("use_lastNames", user.getLastNames());
                values.put("use_password", user.getPassword());
                values.put("use_status","1");
                long cod = db.insert("users",null,values);
                Snackbar.make(this.view,"Se ha registrado el ususario:"+cod,Snackbar.LENGTH_LONG).show();
            }else {
                Snackbar.make(this.view,"No se ha registrado el ususario:",Snackbar.LENGTH_LONG).show();
            }

        }catch (SQLException e){
            Log.i("BD",""+e);
        }

    }
    public void updateUser (User user){
        SQLiteDatabase db = managerDataBase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("use_document",user.getDocument());
        values.put("use_user",user.getUser());
        values.put("use_names",user.getNames());
        values.put("use_lastNames",user.getLastNames());
        values.put("use_password",user.getPassword());

        //Actualizar fila
        int updateStatus = db.update("users",values,"use_document = ?",new String[]{ String.valueOf(user.getDocument())});
        if (updateStatus == -1) {
            Log.e("UserDao","Fallo al actualizar el usuario");
        }else {
            Log.i("UserDao", "Usuario actualizado correctamente");
        }
    }
    public ArrayList<User> searchUsers(String searchQuery) {
        ArrayList<User> searchResults = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = managerDataBase.getReadableDatabase();
            String query = "SELECT * FROM users WHERE use_document LIKE ? OR use_user LIKE ? OR use_names LIKE ? OR use_lastNames LIKE ?";
            String[] args = {"%" + searchQuery + "%", "%" + searchQuery + "%", "%" + searchQuery + "%", "%" + searchQuery + "%"};
            cursor = db.rawQuery(query, args);

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setDocument(cursor.getInt(cursor.getColumnIndexOrThrow("use_document")));
                    user.setUser(cursor.getString(cursor.getColumnIndexOrThrow("use_user")));
                    user.setNames(cursor.getString(cursor.getColumnIndexOrThrow("use_names")));
                    user.setLastNames(cursor.getString(cursor.getColumnIndexOrThrow("use_lastNames")));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("UserDao", "El usuario no se encuentra", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return searchResults;
    }

    public ArrayList<User> getUserList(){
        ArrayList<User> ListUsers = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = managerDataBase.getReadableDatabase();
            String query = "select * from users where use_status = 1;";
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()){
                do{
                    User user1 = new User();
                    user1.setDocument(cursor.getInt(0));
                    user1.setUser(cursor.getString(1));
                    user1.setNames(cursor.getString(2));
                    user1.setLastNames(cursor.getString(3));
                    user1.setPassword(cursor.getString(4));
                    ListUsers.add(user1);
                }while (cursor.moveToNext());
            }
        }catch (SQLException e){
            Log.i("BD",""+e);
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            if (db != null && db.isOpen()){
                db.close();
            }
        }
        return ListUsers;
    }

    public ArrayList<User> compositeSearchUsers(String searchQuery) {
        ArrayList<User> searchResults = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = managerDataBase.getReadableDatabase();

            String[] searchWords = searchQuery.split(" ");

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM users WHERE ");
            for (int i = 0; i < searchWords.length; i++) {
                String word = searchWords[i];


                if (i > 0) queryBuilder.append(" OR ");

                queryBuilder.append("use_document LIKE '%").append(word).append("%' ")
                        .append("OR use_user LIKE '%").append(word).append("%' ")
                        .append("OR use_names LIKE '%").append(word).append("%' ")
                        .append("OR use_lastNames LIKE '%").append(word).append("%'");
            }

            String query = queryBuilder.toString();
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setDocument(cursor.getInt(cursor.getColumnIndexOrThrow("use_document")));
                    user.setUser(cursor.getString(cursor.getColumnIndexOrThrow("use_user")));
                    user.setNames(cursor.getString(cursor.getColumnIndexOrThrow("use_names")));
                    user.setLastNames(cursor.getString(cursor.getColumnIndexOrThrow("use_lastNames")));
                    // Añadir cualquier otro campo que necesites
                    searchResults.add(user);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("UserDao", "Error searching users", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return searchResults;
    }
}

