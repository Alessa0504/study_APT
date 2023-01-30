package com.example.study_apt;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.apt.annotations.Builder;
import com.example.apt.annotations.Optional;
import com.example.apt.annotations.Required;

/**
 * @author zouji
 * @Description:
 * @date 2023/1/28
 */
@Builder
public class UserActivity extends Activity {

    @Required
    String name;

    @Required
    int age;

    @Optional
    String title;

    @Optional
    String company;

    @Optional
    String workPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ((TextView) findViewById(R.id.nameView)).setText(name);
        ((TextView) findViewById(R.id.ageView)).setText(String.valueOf(age));
        ((TextView) findViewById(R.id.titleView)).setText(title);
        ((TextView) findViewById(R.id.companyView)).setText(company);
        ((TextView) findViewById(R.id.workPlaceView)).setText(workPlace);
    }
}
