package com.example.gym.updates;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gym.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


// The fragment of the list
public class UpdatesListFragment extends Fragment {

    private UpdateController updateController = new UpdateController();
    final ArrayList<UpdateViewItemModel> updates = new ArrayList<>();
    private ListSectionAdapter adapter;

    public UpdatesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ListSectionAdapter(getContext(), updates);
        updateUpdatesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_updates_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView list = (ListView) view.findViewById(R.id.list_updates);
        list.setAdapter(adapter);
    }

    // Take the updates from the db and put them in the view
    private void updateUpdatesList() {
        updateController.getUpdates()
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            updates.clear();
                            Date lastDate = null;
                            ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                            ArrayList<Update> updatesList = new ArrayList<>();
                            data.forEach(d -> updatesList.add(new Update(d)));

                            for (Update updateObj : updatesList) {
                                UpdateViewItemModel updateViewObj = new UpdateViewItemModel(updateObj);
                                if (lastDate == null || lastDate.toString().compareTo(updateObj.date.toString()) != 0) {
                                    updates.add(new UpdateViewItemModel(updateObj.date));
                                    lastDate = updateObj.date;
                                }
                                updates.add(updateViewObj);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}


// The adapter- between the fragment and the view
class ListSectionAdapter extends ArrayAdapter<UpdateViewItemModel> {

    LayoutInflater inflater;

    public ListSectionAdapter(Context context, ArrayList<UpdateViewItemModel> items) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        UpdateViewItemModel item = (UpdateViewItemModel) getItem(position);

        //If the cell is a section header we inflate the header layout
        if (item.isSectionHeader()) {
            v = inflater.inflate(R.layout.updates_date_section_header, null);

            v.setClickable(false);

            TextView header = (TextView) v.findViewById(R.id.section_header);
            header.setText(item.getPrettyDate());
        } else {
            v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView updateContent = (TextView) v.findViewById(android.R.id.text1);

            updateContent.setText(item.content);

        }
        return v;
    }
}
