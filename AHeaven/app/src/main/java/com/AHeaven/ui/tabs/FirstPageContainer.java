package com.AHeaven.ui.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.AHeaven.MainActivity;
import com.AHeaven.R;

import java.util.Objects;

/**
 * класс фрагмента, который будет хранить в себе шкаф либо коробку по очереди
 */
public class FirstPageContainer extends Fragment {

    public static FirstPageContainer newInstance() {
        return new FirstPageContainer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.container, container, false);
        if (((MainActivity) Objects.requireNonNull(getActivity())).wardrobeFragmentNow)
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.container_first, PlaylistFragment.newInstance())  //добавляем фрагмент шкафа по умолчанию
                    .commit();
        return root;
    }
}
