package com.laughingstock.ritwick.shutup.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.laughingstock.ritwick.shutup.R;

import java.util.ArrayList;


public class bwcontactsAdapter extends BaseAdapter
{

    Context context;

    ArrayList<String> contactnames, contactnumbers,contactphotos;

    private static LayoutInflater inflater = null;

    public bwcontactsAdapter(Context context, ArrayList<String> contactnames, ArrayList<String> contactnumbers, ArrayList<String> contactphotos)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.contactnames=contactnames;
        this.contactnumbers=contactnumbers;
        this.contactphotos=contactphotos;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_in_bwlist,parent,false);
        TextView contactnametext = vi.findViewById(R.id.contactname);
        TextView contactnumbertext= vi.findViewById(R.id.contactnumber);
        final ImageButton contactdeletebutton= vi.findViewById(R.id.contactdeletebutton);
        ImageView contactphotoimage= vi.findViewById(R.id.contactphotopic);

        contactdeletebutton.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view)
            {
                contactnames.remove(position);
                contactnumbers.remove(position);
                contactphotos.remove(position);

                SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                Gson gson = new Gson();
                String contactnamejson = gson.toJson(contactnames);
                String contactnumberjson = gson.toJson(contactnumbers);
                String contactphotojson = gson.toJson(contactphotos);

                editor.putString("listcontactnamespref", contactnamejson);
                editor.putString("listcontactnumberspref", contactnumberjson);
                editor.putString("listcontactphotospref", contactphotojson);
                editor.apply();

                notifyDataSetChanged();
            }
        });


       // if (contactphotos.get(position) != null)
        {
            Uri temp = Uri.parse(contactphotos.get(position));
            contactphotoimage.setImageURI(temp);
        }


        contactnametext.setText(contactnames.get(position));
        contactnumbertext.setText(contactnumbers.get(position));
        return vi;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return contactnames.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        String[] temp={contactnames.get(position),contactnumbers.get(position)};
        return temp;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}