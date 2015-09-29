package solmovsa.com.demovideo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by JulianRamiro on 04/09/2015.
 */
public class AdaptadorVideo  extends ArrayAdapter<String> {
    private List<String> modelo;
    public Context actividad;
    public String iteMen;
    public AdaptadorVideo(Context actividad, List<String> modelo) {
        super(actividad, android.R.layout.simple_expandable_list_item_1, modelo);
        this.modelo = modelo;
        this.actividad = actividad;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fila = convertView;
        if(fila == null){
            // Inflater o redefinicion
            LayoutInflater layInf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            fila = layInf.inflate(R.layout.fila_video , null);

        }
        //recupero la fila
        iteMen =modelo.get(position);
        //recupero los componentes de mi fila menu y actualizo los valores

        TextView vid = (TextView) fila.findViewById(R.id.nombreArchivo);

        vid.setText(iteMen);
        ImageView imageView = (ImageView)fila.findViewById(R.id.imageViewPlay);
        imageView.setOnClickListener(new myClickListener(getItemId(position)));

        Button del =(Button)fila.findViewById(R.id.butDel);
        del.setOnClickListener(new myClickListener1(getItemId(position)));

        return fila;

    }
    class myClickListener implements View.OnClickListener {
        long id;
        public myClickListener(long id){
            this.id = id;

        }
        @Override
        public void onClick(View v) {

            iteMen = modelo.get(Integer.parseInt(String.valueOf(id)));
            Intent intent = new Intent(actividad, VideoActivity.class);
            intent.putExtra("path",iteMen.toString());
            actividad.startActivity(intent);
        }
    }
    class myClickListener1 implements View.OnClickListener {
        long id;
        public myClickListener1(long id){
            this.id = id;

        }
        @Override
        public void onClick(View v) {

            //iteMen = modelo.get(Integer.parseInt(String.valueOf(id)));
            modelo.remove(Integer.parseInt(String.valueOf(id)));
            if(actividad instanceof MainActivity){
                ((MainActivity)actividad).listaCebecera = modelo;
                ((MainActivity)actividad).cargarValoresHistoria();

            }

        }
    }
}
