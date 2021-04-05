package com.example.reqspotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import Connectors.UserService;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;        //Compartir Datos
    private SharedPreferences msharedPreferences;   //Compartir Datos

    private RequestQueue queue;                     //cola de Request, para el API

    private static final String CLIENT_ID = "69a48a944b0e4e8f908d0198a668fcdd";   //Id del cliente
    private static final String REDIRECT_URI = "reqspotify://callback";           //Redirect del cliente
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,playlist-modify-public,playlist-modify-private,playlist-read-private,playlist-read-collaborative,user-follow-modify,user-follow-read,user-library-modify,user-library-read";  //Permisos que ocupa la app para funcionar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);


        authenticateSpotify();  //Busca el token de autenticaión del spotify
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);
    }


    /**
     * Añade un request de autenticacion al queue
     */
    private void authenticateSpotify() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /**
     * Espera el response del request de autenticación
     */
    private void waitForUserInfo() {
        UserService userService = new UserService(queue, msharedPreferences); //Busca el Usuario por medio del UserService
        userService.get(() -> {
            User user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);   //Recibe el userID
            editor.putString("userName", user.display_name); //Recibe elusername
            Log.d("STARTING", "GOT USER INFORMATION");
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();  //Hace un commit al editor
            startMainActivity(); //Inicia el main activity
        });
    }

    /**
     * Inicia el main activity
     */
    private void startMainActivity() {
        Intent newintent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(newintent);
    }

    //Maneja los resultados de los request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            //Ya se autentico en Spotify, busca la autenticacion
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                                                        // Respuesta fue exitosa, busca el token de acceso
                case TOKEN:
                                                        //Guarda el token de acceso en el editor
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    Log.d("STARTING", "GOT AUTH TOKEN" + response.getAccessToken());
                    editor.apply();
                    waitForUserInfo();                  //Espera por la informacion del usuario
                    break;

                // El flujo de autorizacion llego a un error
                case ERROR:
                    Log.d("STARTING", "Whooouuups.. There was a problem... With logIn");
                    Toast.makeText(this, "Whoooups, there was a problem, please close and open the app!", Toast.LENGTH_LONG).show();
                    break;
                // El flujo de autorizacion fue cancelado
                default:
                    Log.d("STARTING", "Something happened...Don't know what...");
                    Toast.makeText(this, "Whoooups, there was a problem, please close and open the app!", Toast.LENGTH_LONG).show();
            }
        }
    }


}