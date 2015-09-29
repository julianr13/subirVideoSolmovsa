package solmovsa.com.demovideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final int VIDEO_CAPTURE = 101;
    Uri vid;
    ProgressDialog prgDialog;
    public List<String> listaCebecera = new ArrayList<String>();
    private AdaptadorVideo adaptadorHis = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton =
                (Button) findViewById(R.id.button2);
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        if (!hasCamera())
            recordButton.setEnabled(false);
try{
    HashMap < String, Object> savedValues
            = (HashMap<String, Object>)this.getLastNonConfigurationInstance();
    listaCebecera = (List<String>) savedValues.get("someKey");
    cargarValoresHistoria();
}catch (Exception e){

        }

    }
    public void cargarValoresHistoria() {
        //cargar los valores de la lista
        //for del resultado

        //crear el adaptador
        adaptadorHis = new AdaptadorVideo(MainActivity.this, listaCebecera);
        //recuperar la lista y cargar adaptador
        ListView lisVieM = (ListView)findViewById(R.id.lisVieVeh);
        lisVieM.setAdapter(adaptadorHis);



    }
    public void Guardar(View view) {

        int contadorLista = listaCebecera.size();
        for(int i = 0; i< contadorLista;i++){
            String nombre = listaCebecera.get(i);
            Uri vidF = Uri.parse(nombre);
            File file = new File(vidF.getPath());

                    RequestParams params = new RequestParams();

            try {
                params.put("file0",file);
                invokeWS(params);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        listaCebecera.clear();
    }
    public void Grabar(View view){
        int monto = listaCebecera.size();
        if(monto<5){
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //startActivityForResult(intent, VIDEO_CAPTURE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            timeStamp = timeStamp.replace(".","-");
            String videoFileName = "/VIDEO_"+ "Jrosales"+ "_" + timeStamp + ".mp4";
            File mediaFile =
                    new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + videoFileName);

            Uri videoUri = Uri.fromFile(mediaFile);
            vid=videoUri;

            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, VIDEO_CAPTURE);
        }else{
            Toast.makeText(this, "No puede subir mas de 5 videos",
                    Toast.LENGTH_LONG).show();
        }

    }
    public void verVideo(String uri) {
       //llamar a la actividad video
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        intent.putExtra("path",uri.toString());
        startActivity(intent);
    }

        private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try{
                    Toast.makeText(this, "Video Guardado ", Toast.LENGTH_LONG).show();
                    //actualizar grillas
                    listaCebecera.add(vid.toString());
                    cargarValoresHistoria();
                }catch (Exception e){
                    Toast.makeText(this, "Fallo en grabar el video",
                            Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video cancelado.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Fallo en grabar el video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

  @Override
    public Object onRetainNonConfigurationInstance() {
      HashMap<String, Object> savedValues = new HashMap<String, Object>();
        savedValues.put("someKey", listaCebecera);
        return savedValues;
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://umpacto.somee.com/DemoVideo/api/video/insertar", params, new com.loopj.android.http.AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
