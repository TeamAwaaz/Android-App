package teamawaazdtu.com.awaaz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;


public class StartActivityFragment extends Fragment {


    public static final String AUDIO_FILE = "audio_file";
    private Button logOut;
    ImageButton b1,b2;
    View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static StartActivityFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        StartActivityFragment firstFragment = new StartActivityFragment();
        firstFragment.setArguments(args);
        return firstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         rootView = inflater.inflate(R.layout.activity_fragment_first, container, false);

        b1=(ImageButton)rootView.findViewById(R.id.bb);
        b2=(ImageButton)rootView.findViewById(R.id.cc);
//        b2=(ImageButton)rootView.findViewById(R.id.cc);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra(AUDIO_FILE,"null");
                startActivity(intent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),RealtimeActivity.class);
                intent.putExtra(AUDIO_FILE,"null");
                startActivity(intent);
            }
        });

//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().startActivity(new Intent(getContext(),MainActivity.class));
//            }
//        });


//        logOut=(Button) rootView.findViewById(R.id.btn_logout);
//
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.signOut();
//            }
//        });
//
//
//        mAuth=FirebaseAuth.getInstance( );
//        mAuthListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if(firebaseAuth.getCurrentUser()==null){
//                    startActivity(new Intent(getActivity(),LoginActivity.class));
//                }
//            }
//        };
        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }



    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

}