package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.RequestMentorForm;
import com.blue_stingray.healthy_life_app.net.form.SearchMentorForm;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.ui.adapter.UserListAdapter;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BillZeng on 2/8/15.
 */
public class CreateMentorFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.mentors)
    private LinearList mentorList;

    @InjectView(R.id.blank_message)
    private View blankMessage;

    private List<User> mentors;

    private SearchView searchView;

    private UserListAdapter userListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_create_mentor, container,false);
        getActivity().setTitle("Create Mentor");
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Mentor", "ViewCreated");
        mentors = new ArrayList<>();
        blankMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_mentor_fragment_actions, menu);
        searchView = (SearchView) menu.findItem(R.id.action_mentor_search).getActionView();
        searchView.setOnQueryTextListener(new MentorSearchQueryListener());
        searchView.setIconified(false);
        searchView.setQueryHint("email or user name");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class MentorSearchQueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            searchMentors(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

        private void searchMentors(String query) {
            searchView.clearFocus();
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");
            rest.searchMentor(
                new SearchMentorForm(query),
                new Callback<List<User>>() {
                    @Override
                    public void success(List<User> users, Response response) {
                        loading.cancel();
                        // hide/show blank message
                        if (users.size() == 0) {
                            blankMessage.setVisibility(View.VISIBLE);
                        }
                        else {
                            blankMessage.setVisibility(View.GONE);
                        }
                        // show mentor list
                        mentors.clear();
                        mentors.addAll(users);
                        mentorList.removeAllViews();
                        userListAdapter = new UserListAdapter(getActivity(), mentors);
                        mentorList.setAdapter(
                                userListAdapter,
                                new MentorListClickListener());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        loading.cancel();
                        // show blank message
                    }
                }
            );
        }
    }

    private class MentorListClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final User mentor = mentors.get((int) v.getTag());
            final AlertDialog choiceDialog = DialogHelper.createYesNoDialog(
                getActivity(),
                "Send mentor request?",
                "Yes",
                "Cancel",
                // send request
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");
                        rest.requestMentor(
                            new RequestMentorForm(mentor.getId()),
                            new Callback<Object>() {
                                @Override
                                public void success(Object o, Response response) {
                                    loading.cancel();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    loading.cancel();
                                }
                            }
                        );
                    }
                },
                // cancel sending
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            choiceDialog.show();
        }
    }
}
