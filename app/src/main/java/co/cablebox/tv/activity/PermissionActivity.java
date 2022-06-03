package co.cablebox.tv.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import co.cablebox.tv.R;

public class PermissionActivity extends AppCompatActivity {

    // Banderas de permisos
    private boolean tienePermisoTelefono = false,
            tienePermisoUbicacion = false,
            tienePermisoAlmacenamiento = false;

    // Código de permiso
    private static final int CODIGO_PERMISOS_TELEFONO = 1,
            CODIGO_PERMISOS_UBICACION = 2,
            CODIGO_PERMISOS_ALMACENAMIENTO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        verificarYPedirPermisosDeTelefono();

        if(tienePermisoAlmacenamiento && tienePermisoTelefono && tienePermisoUbicacion) {
            //ServiceProgramActivity.openLive(PermissionActivity.this);
            openMainActivity();
            finish();
        }
    }

    //Permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_TELEFONO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Concedido Telefono");
                    permisoDeTelefonoConcedido();
                    verificarYPedirPermisosDeUbicacion();
                } else {
                    permisoDeTelefonoDenegado();
                    verificarYPedirPermisosDeTelefono();
                }
                break;

            case CODIGO_PERMISOS_UBICACION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeUbicacionConcedido();
                    verificarYPedirPermisosDeAlmacenamiento();
                } else {
                    permisoDeUbicacionDenegado();
                    verificarYPedirPermisosDeUbicacion();
                }
                break;

            case CODIGO_PERMISOS_ALMACENAMIENTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeAlmacenamientoConcedido();
                    //ServiceProgramActivity.openLive(PermissionActivity.this);
                    openMainActivity();
                    finish();
                } else {
                    permisoDeAlmacenamientoDenegado();
                    verificarYPedirPermisosDeAlmacenamiento();
                }
                break;
            // Aquí más casos dependiendo de los permisos
        }
    }

    private void verificarYPedirPermisosDeTelefono() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            permisoDeTelefonoConcedido();
            verificarYPedirPermisosDeUbicacion();
        } else {
            ActivityCompat.requestPermissions(PermissionActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    CODIGO_PERMISOS_TELEFONO);
        }
    }

    private void verificarYPedirPermisosDeUbicacion() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            permisoDeUbicacionConcedido();
            verificarYPedirPermisosDeAlmacenamiento();
        } else {
            ActivityCompat.requestPermissions(PermissionActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PERMISOS_UBICACION);
        }
    }

    private void verificarYPedirPermisosDeAlmacenamiento() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            permisoDeAlmacenamientoConcedido();
            //ServiceProgramActivity.openLive(PermissionActivity.this);
            openMainActivity();
            finish();
        } else {
            ActivityCompat.requestPermissions(PermissionActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODIGO_PERMISOS_ALMACENAMIENTO);
        }
    }


    private void permisoDeTelefonoConcedido() {
        //Toast.makeText(PermissionActivity.this, "El permiso para el telefono está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoTelefono = true;
    }

    private void permisoDeTelefonoDenegado() {
        Toast.makeText(PermissionActivity.this, "Permiso de Teléfono está denegado", Toast.LENGTH_SHORT).show();
        Toast.makeText(PermissionActivity.this, "Porfavor vaya a los permisos de la aplicacion en las configuracion " +
                "o desinstale e instale de nuevo la aplicacion", Toast.LENGTH_LONG).show();
    }

    private void permisoDeUbicacionConcedido() {
        //Toast.makeText(PermissionActivity.this, "El permiso para el Ubicacion está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoUbicacion = true;
    }

    private void permisoDeUbicacionDenegado() {
        Toast.makeText(PermissionActivity.this, "Permiso de Ubicación está denegado", Toast.LENGTH_SHORT).show();
        Toast.makeText(PermissionActivity.this, "Porfavor vaya a los permisos de la aplicacion en las configuracion " +
                "o desinstale e instale de nuevo la aplicacion", Toast.LENGTH_LONG).show();
    }

    private void permisoDeAlmacenamientoConcedido() {
        //Toast.makeText(PermissionActivity.this, "El permiso para el almacenamiento está concedido", Toast.LENGTH_SHORT).show();
        tienePermisoAlmacenamiento = true;
    }

    private void permisoDeAlmacenamientoDenegado() {
        Toast.makeText(PermissionActivity.this, "Permiso Almacenamiento está denegado", Toast.LENGTH_SHORT).show();
        Toast.makeText(PermissionActivity.this, "Porfavor vaya a los permisos de la aplicacion en las configuracion " +
                "o desinstale e instale de nuevo la aplicacion", Toast.LENGTH_LONG).show();
    }

    private void openMainActivity(){
        Intent i= new Intent(this, MainActivity.class);
        //i.putExtra("errorType",errorType); //pass props to the activity
        startActivity(i);
    }

}
