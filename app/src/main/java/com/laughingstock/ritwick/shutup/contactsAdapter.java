package com.laughingstock.ritwick.shutup;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class contactsAdapter extends BaseAdapter
{

    Context context;

    ArrayList<String> contactnames, contactnumbers;

    private static LayoutInflater inflater = null;

    public contactsAdapter(Context context, ArrayList<String> contactnames,ArrayList<String> contactnumbers)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.contactnames=contactnames;
        this.contactnumbers=contactnumbers;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_in_blacklist,parent,false);
        TextView contactnametext = (TextView) vi.findViewById(R.id.contactname);
        TextView contactnumbertext=(TextView) vi.findViewById(R.id.contactnumber);
        final ImageButton contactdeletebutton=(ImageButton) vi.findViewById(R.id.contactdeletebutton);

        contactdeletebutton.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view)
            {
                contactnames.remove(position);
                contactnumbers.remove(position);

                SharedPreferences preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                Set<String> contactnameset = new HashSet<String>();
                Set<String> contactnumberset = new HashSet<String>();
                contactnameset.addAll(contactnames);
                contactnumberset.addAll(contactnumbers);
                editor.putStringSet("blacklistcontactnamespref", contactnameset);
                editor.putStringSet("blacklistcontactnumberspref", contactnumberset);
                editor.apply();

                notifyDataSetChanged();
            }
        });

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