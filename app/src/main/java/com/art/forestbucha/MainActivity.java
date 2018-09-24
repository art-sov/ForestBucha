package com.art.forestbucha;

import android.support.v4.app.Fragment;

import com.art.forestbucha.base.SingleFragmentActivity;
import com.art.forestbucha.view.fragment.LoginFragment;
import com.art.forestbucha.view.fragment.OnLoginFragmentListener;

public class MainActivity extends SingleFragmentActivity implements OnLoginFragmentListener {

    @Override
    public Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    public void hideProgressDialog() {
        hideProgressDialog();
    }
}
