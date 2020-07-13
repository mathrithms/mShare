package com.mathrithms.mShare.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.mathrithms.mShare.R;
import com.mathrithms.mShare.app.Activity;
import com.mathrithms.mShare.dialog.ShareAppDialog;
import com.mathrithms.mShare.fragment.HomeFragment;
import com.mathrithms.mShare.fragment.TransferGroupListFragment;
import com.mathrithms.mShare.mShare.Webview_Fragment;
import com.mathrithms.mShare.object.NetworkDevice;
import com.mathrithms.mShare.ui.callback.PowerfulActionModeSupport;
import com.mathrithms.mShare.util.AppUtils;
import com.mathrithms.mShare.util.UpdateUtils;
import com.genonbeta.android.framework.widget.PowerfulActionMode;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity
        extends Activity
        implements NavigationView.OnNavigationItemSelectedListener, PowerfulActionModeSupport
{
    public static final int REQUEST_PERMISSION_ALL = 1;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private PowerfulActionMode mActionMode;
    private HomeFragment mHomeFragment;
    private IntentFilter mFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;

    private long mExitPressTime;
    private int mChosenMenuItemId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHomeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.activitiy_home_fragment);
        mActionMode = findViewById(R.id.content_powerful_action_mode);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //mTrustZoneToggle = mNavigationView.getMenu().findItem(R.id.menu_activity_trustzone);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.text_navigationDrawerOpen, R.string.text_navigationDrawerClose);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //mFilter.addAction(CommunicationService.ACTION_TRUSTZONE_STATUS);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener()
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                applyAwaitingDrawerAction();
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);
        mActionMode.setOnSelectionTaskListener(new PowerfulActionMode.OnSelectionTaskListener()
        {
            @Override
            public void onSelectionTask(boolean started, PowerfulActionMode actionMode)
            {
                toolbar.setVisibility(!started ? View.VISIBLE : View.GONE);
            }
        });

        if (UpdateUtils.hasNewVersion(this))
            highlightUpdater(getDefaultPreferences().getString("availableVersion", null));

        if (!AppUtils.isLatestChangeLogSeen(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.mesg_versionUpdatedChangelog)
                    .setPositiveButton(R.string.butn_yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            AppUtils.publishLatestChangelogSeen(HomeActivity.this);
                            startActivity(new Intent(HomeActivity.this, ChangelogActivity.class));
                        }
                    })
                    .setNeutralButton(R.string.butn_never, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getDefaultPreferences().edit()
                                    .putBoolean("show_changelog_dialog", false)
                                    .apply();
                        }
                    })
                    .setNegativeButton(R.string.butn_no, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            AppUtils.publishLatestChangelogSeen(HomeActivity.this);
                            Toast.makeText(HomeActivity.this, R.string.mesg_versionUpdatedChangelogRejected, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        createHeaderView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //registerReceiver(mReceiver = new ActivityReceiver(), mFilter);
        //requestTrustZoneStatus();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mReceiver != null)
            unregisterReceiver(mReceiver);

        mReceiver = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        mChosenMenuItemId = item.getItemId();

        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (mHomeFragment.onBackPressed())
            return;

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else if ((System.currentTimeMillis() - mExitPressTime) < 2000)
            super.onBackPressed();
        else {
            mExitPressTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.mesg_secureExit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserProfileUpdated()
    {
        createHeaderView();
    }

    private void applyAwaitingDrawerAction()
    {
        if (mChosenMenuItemId == 0) {
            // Do nothing
        }  else if (R.id.menu_activity_main_about == mChosenMenuItemId) {
            FragmentManager fml = getFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Webview_Fragment wf = new Webview_Fragment();
            ft.replace(R.id.activitiy_home_fragment,wf);
            ft.commit();
            //webview added
        } else if (R.id.menu_activity_main_send_application == mChosenMenuItemId) {
            FragmentManager fml = getFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Webview_Fragment wf = new Webview_Fragment();
            ft.replace(R.id.activitiy_home_fragment,wf);
            ft.commit();
            //new ShareAppDialog(HomeActivity.this)
              //      .show();
        } else if (R.id.menu_activity_main_preferences == mChosenMenuItemId) {
            startActivity(new Intent(this, PreferencesActivity.class));
        } else if (R.id.menu_activity_main_home == mChosenMenuItemId){
            FragmentManager fml = getFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            TransferGroupListFragment tgf = new TransferGroupListFragment();
            ft.replace(R.id.activitiy_home_fragment,tgf);
            ft.commit();
        } else if (R.id.menu_activity_main_exit == mChosenMenuItemId) {
            exitApp();
        }
        mChosenMenuItemId = 0;
    }

    private void createHeaderView()
    {
        View headerView = mNavigationView.getHeaderView(0);
        //MenuItem surveyItem = mNavigationView.getMenu().findItem(R.id.menu_activity_main_dev_survey);
        Configuration configuration = getApplication().getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList list = configuration.getLocales();

            if (list.size() > 0)
                for (int pos = 0; pos < list.size(); pos++)
                    if (list.get(pos).toLanguageTag().startsWith("en")) {
                        //surveyItem.setVisible(true);
                        break;
                    }
        } //else
            //surveyItem.setVisible(configuration.locale.toString().startsWith("en"));

        if (headerView != null) {
            NetworkDevice localDevice = AppUtils.getLocalDevice(getApplicationContext());

            ImageView imageView = headerView.findViewById(R.id.mshare_layout_profile_picture_image_default);
            ImageView editImageView = headerView.findViewById(R.id.mshare_layout_profile_picture_image_preferred);
            TextView deviceNameText = headerView.findViewById(R.id.header_default_device_name_text);
            TextView versionText = headerView.findViewById(R.id.header_default_device_version_text);

            deviceNameText.setText(localDevice.nickname);
            versionText.setText(localDevice.versionName);
            loadProfilePictureInto(localDevice.nickname, imageView);

            editImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startProfileEditor();
                }
            });
        }
    }

    @Override
    public PowerfulActionMode getPowerfulActionMode()
    {
        return mActionMode;
    }

    private void highlightUpdater(String availableVersion)
    {
        MenuItem item = mNavigationView.getMenu().findItem(R.id.menu_activity_main_about);
        item.setTitle(R.string.text_newVersionAvailable);
    }
}
