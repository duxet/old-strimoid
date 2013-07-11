package com.duxet.strimoid.fragments;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.duxet.strimoid.R;
import com.duxet.strimoid.models.Data;
import com.duxet.strimoid.models.Strim;
import com.duxet.strimoid.ui.StrimsAdapter;
import com.duxet.strimoid.utils.HTTPClient;
import com.duxet.strimoid.utils.Parser;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class StrimChooserFragment extends SherlockDialogFragment {

    StrimsAdapter strimsAdapter;
    
    public StrimChooserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(SherlockDialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Wybierz strim");
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_strimchooser, container, false);

        ExpandableListView list = (ExpandableListView) v.findViewById(R.id.list);
        strimsAdapter = new StrimsAdapter(inflater, Data.getStrims());
        list.setAdapter(strimsAdapter);
        list.setGroupIndicator(null);
        
        list.setOnGroupClickListener(onStrimChoosed);
        list.setOnChildClickListener(onChildStrimChoosed);

        return v;
    }
    
    OnGroupClickListener onStrimChoosed = new OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            final Strim strim = Data.getStrims().get(groupPosition);
            
            if (strim.isGroup()) {
                String[] name = strim.getName().split("/");
                
                // Fix for subscribbed strims
                if (name == null || name.length < 2) {
                    name = new String[2];
                    name[0] = "s";
                    name[1] = "Subskrybowane";
                }

                Strim loading = new Strim("", "Ładowanie...", "", 0, false);
                strim.addChildren(loading);
                
                HTTPClient.get("ajax/utility/submenu?section_type=" + name[0] + "&section_name=" + name[1],
                        null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        strim.getChildrens().clear();
                        strim.addChildrens(new Parser(response).getSubstrims());
                        strimsAdapter.notifyDataSetChanged();
                    }
                });
                
                return false;
            }
            
            ((onStrimSelectedListener) getParentFragment()).onStrimSelected(strim);
            dismiss();

            return true;
        }
    };
    
    OnChildClickListener onChildStrimChoosed = new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Strim strim = Data.getStrims().get(groupPosition).getChildrens().get(childPosition);
            ((onStrimSelectedListener) getParentFragment()).onStrimSelected(strim);
            dismiss();
            return true;
        }
    };
    
    public interface onStrimSelectedListener {
        public void onStrimSelected(Strim strim);
    }

}
