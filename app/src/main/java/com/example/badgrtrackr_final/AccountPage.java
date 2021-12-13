package com.example.badgrtrackr_final;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.badgrtrackr_final.api.UserAPI;
import com.example.badgrtrackr_final.data_types.User;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class AccountPage extends Fragment {
    TextView nameView;
    TextView emailView;
    TextView favLocView;
    TextView common1;
    TextView common2;
    UserAPI user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.account_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputStream is = getResources().openRawResource(R.raw.user_data);
        user = new UserAPI(is);
        List<String[]> temp = user.getUser().getCommonLocations();
        Log.d("uuu", temp.toString());

        nameView = view.findViewById(R.id.user_name);
        nameView.setText(user.getUser().getFullName());
        emailView = view.findViewById(R.id.email_text);
        emailView.setText(user.getUser().getEmail());
        favLocView = view.findViewById(R.id.favLocView);
        favLocView.setText(user.getUser().getFavoriteLocationId());
    }

    /*
        List<String[]> relevantLocData = user.getUser().getCommonLocations();
        Log.d("@@@", relevantLocData.get(0)[1]);
        favLocView = view.findViewById(R.id.favLocView);
        favLocView.setText("" + relevantLocData.get(0)[1]);
        common1 = view.findViewById(R.id.common1);
        common1.setText("" + relevantLocData.get(1)[1]);
        common2 = view.findViewById(R.id.common2);
        common2.setText("" + relevantLocData.get(2)[1]);
     */

}