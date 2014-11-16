package com.blue_stingray.healthy_life_app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;

public class CreateGoalFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_goal, container,false);
        getActivity().setTitle("Create Goal");

        // app spinner
        Spinner appSpinner = (Spinner) view.findViewById(R.id.app_spinner);

        // goal type spinner
        Spinner goalTypeSpinner = (Spinner) view.findViewById(R.id.goal_type_spinner);
        ArrayAdapter<CharSequence> goalTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.goal_type, android.R.layout.simple_list_item_1);
        goalTypeSpinner.setAdapter(goalTypeAdapter);

        // time limit spinner
        Spinner timeLimitSpinner = (Spinner) view.findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> timeLimitAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.time_limit, android.R.layout.simple_list_item_1);
        timeLimitSpinner.setAdapter(timeLimitAdapter);

        // create goal on click
        Button button = (Button) view.findViewById(R.id.create_goal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "todo", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
