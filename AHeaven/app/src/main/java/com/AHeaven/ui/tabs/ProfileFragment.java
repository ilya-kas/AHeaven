package com.AHeaven.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.AHeaven.R;

/**
 * фрагмент, гранящий в себе вкладку профиля
 */
public class ProfileFragment extends Fragment {
    //private static final String ARG_TAB_NUMBER = "tab_number";

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        /*Bundle bundle = new Bundle();   //хранилище ключ-значение
        bundle.putInt(ARG_TAB_NUMBER, index);
        fragment.setArguments(bundle);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_TAB_NUMBER);
        }*/
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        final TextView textView = root.findViewById(R.id.label);
        return root;
    }
}