package com.art.forestbucha;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.art.forestbucha.base.SingleFragmentActivity;
import com.art.forestbucha.view.fragment.LoginFragment;
import com.art.forestbucha.view.fragment.MainFragment;
import com.art.forestbucha.view.fragment.OnLoginFragmentListener;

public class MainActivity extends SingleFragmentActivity implements OnLoginFragmentListener {

    FragmentManager fm;
    SharedPreferences preferences;

    @Override
    public Fragment createFragment() {
        preferences = getPreferences(MODE_PRIVATE);
        String userId = preferences.getString(LoginFragment.USER_ID, "");
        if (TextUtils.isEmpty(userId)) {
            return LoginFragment.newInstance();
        }
        else {
            return MainFragment.newInstance();
        }
    }

    @Override
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }

    @Override
    public void showProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    public void startMainFragment() {
        fm = getSupportFragmentManager();
        Fragment mainFragment = new MainFragment();
        fm.beginTransaction().replace(R.id.container, mainFragment).commit();
    }
}
