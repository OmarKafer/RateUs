package pi.rateusteam.rateus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Fragments.LoginFragment;
import pi.rateusteam.rateus.Fragments.VotacionFragment;
import pi.rateusteam.rateus.Interfaces.NavigationHost;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private GestorFirebase gestorFirebase;
    public static boolean permitirAtras = true;
    private VotacionFragment votacionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        gestorFirebase = new GestorFirebase(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }

        /*GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.background);

        int[] colores = {Color.BLUE, Color.GREEN};
        drawable.setColors(colores);*/


    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        permitirAtras = true;
        if(fragment instanceof VotacionFragment) {
            this.votacionFragment = (VotacionFragment) fragment;
        }
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_in, R.anim.left_out)
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(permitirAtras) {
                return super.onKeyDown(keyCode, event);
            } else {
                navigateTo(new VotacionFragment(), false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}