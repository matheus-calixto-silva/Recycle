package com.pdm.recycle.view;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdm.recycle.R;
import com.pdm.recycle.control.ConfiguracaoFirebase;
//import com.pdm.recycle.databinding.ActivityHomeBinding;
import com.pdm.recycle.databinding.ActivityDescarteLocationBinding;
import com.pdm.recycle.helper.Base64Custom;
import com.pdm.recycle.model.Descarte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DescarteLocalizacaoActivity extends FragmentActivity implements OnMapReadyCallback {

    /* LAYOUT ESTÁ NA ACTIVITY_HOME.XML */

    private ArrayList<String> discardSelectArray;
    private Double latitude,longitude;
    private String latlongString;
    private GoogleMap mMap;
    private boolean touchMaps=false;
    //private ActivityHomeBinding binding;
    private ActivityDescarteLocationBinding binding;

    private static final int FINE_LOCATION_REQUEST = 1;
    private boolean fine_location;
    private FirebaseAuth autenticacao;
    private String emailUserAutenticado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //binding = ActivityHomeBinding.inflate(getLayoutInflater());
        binding = ActivityDescarteLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestPermission();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser usuarioAtual =  autenticacao.getCurrentUser();
        emailUserAutenticado = usuarioAtual.getEmail();

        Bundle bundle = getIntent().getExtras();
        discardSelectArray = bundle.getStringArrayList("residuosSelecionados");
    }

    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        this.fine_location = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        if (this.fine_location) return;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_REQUEST);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        this.fine_location = (requestCode == FINE_LOCATION_REQUEST) && granted;

        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(this.fine_location);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ifRecife = new LatLng(-8.058320, -34.950611);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                touchMaps     = true;
                latitude      = latLng.latitude;
                longitude     = latLng.longitude;
                latlongString = String.valueOf(latLng);

                Toast toast = Toast.makeText(DescarteLocalizacaoActivity.this,
                        "Marcado Local de Descarte! " +
                                "\nTipo Residuo: " + discardSelectArray +
                                "\n lat: " + latitude +
                                "\n lng: " + longitude,
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();

                mMap.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title("Local")
                                .snippet("Descrição: " +
                                        " Tipo de resíduo: " + discardSelectArray +
                                        "\n"+ latLng)
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_recycle_24))
                                .icon(vectorToBitmap(R.drawable.pin_icon_recycle))
                                .draggable(true) //draggable(true) permite arrastar o marcador.  Problema para corrigir - ao arrastar o marcado, não é atualizado o novo local
                );

            }
        });

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(ifRecife, 15)
        );

        mMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Toast.makeText(DescarteLocalizacaoActivity.this,
                                "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

        mMap.setOnMyLocationClickListener(
                new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        Toast.makeText(DescarteLocalizacaoActivity.this,
                                "Você está aqui!", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(this.fine_location);

    }

    public void currentLocation(View view) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    Toast.makeText(DescarteLocalizacaoActivity.this, "Localização atual: \n" +
                            "Lat: " + location.getLatitude() + " " +
                            "Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

 public void concludeDiscard(View v) {

        if (!touchMaps){
            Toast toastTouch = Toast.makeText(DescarteLocalizacaoActivity.this,
                    "Toque no mapa o local de descarte do residuo!",
                    Toast.LENGTH_LONG);
            toastTouch.setGravity(Gravity.CENTER, 0, 0);
            toastTouch.show();
        }else {
            Descarte descarte = new Descarte();
            descarte.setLatitude(latitude);
            descarte.setLongitude(longitude);
            descarte.setTipoResiduo(String.valueOf(discardSelectArray));
            descarte.setStatus("Não Coletado");
            descarte.setUserEmail(emailUserAutenticado);

            Date data = new Date(System.currentTimeMillis());
            SimpleDateFormat formatarDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //"yyyy-MM-dd"
            formatarDate.format(data);
            descarte.setDataDescarte(formatarDate.format(data));

            //Código usado para gerar identificador alfanumero que é salvo no firebase
            String identificadorDescarte = Base64Custom.codificarBase64(latlongString);
            descarte.setidDescarte(identificadorDescarte);

            descarte.salvarDescarte();
            finish();
            abrirMenuPrincipal();
        }
    }

    public void abrirMenuPrincipal(){
        Toast toastResgistroDescarte = Toast.makeText(DescarteLocalizacaoActivity.this,
                "Descarte registrado com Sucesso! ",
                Toast.LENGTH_LONG);
        toastResgistroDescarte.setGravity(Gravity.CENTER, 0, 0);
        toastResgistroDescarte.show();

        Intent intent = new Intent(this, MainHomeActivity.class);
        startActivity( intent );
    }

    public void redirectDescarte(View v) {
        Intent intent = new Intent(this, DescarteSelectActivity.class);
        startActivity(intent);
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}