package com.teamkassvi.ascend;

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


public class TestFragment extends Fragment {


    public static final String AUDIO_FILE = "audio_file";
    private Button logOut;
    ImageButton btn_static, btn_realtime;
    View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static TestFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        TestFragment firstFragment = new TestFragment();
        firstFragment.setArguments(args);
        return firstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.activity_test_fragment, container, false);
        rootView.getBackground().setAlpha(30);

//        Toast.makeText(getActivity(), "in Test", Toast.LENGTH_SHORT).show();

        btn_static =(ImageButton)rootView.findViewById(R.id.btn_static);
        btn_realtime =(ImageButton)rootView.findViewById(R.id.btn_realtime);
//        btn_realtime=(ImageButton)rootView.findViewById(R.id.cc);

        btn_static.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),StaticActivity.class);
                intent.putExtra(AUDIO_FILE,"null");
                startActivity(intent);
            }
        });
        btn_realtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),RealtimeActivity.class);
                intent.putExtra(AUDIO_FILE,"null");
                startActivity(intent);
            }
        });

//        btn_realtime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().startActivity(new Intent(getContext(),StaticActivity.class));
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