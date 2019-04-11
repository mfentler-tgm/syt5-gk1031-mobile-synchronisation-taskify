package com.taskify.syt.taskify;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Tasks extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "kurwaa";
    private DatabaseReference tDatabase;
    private ListView listView;
    private static Tasks instance;
    private Timer T;
    private static boolean oneTaskActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogActivity dialogFragment = DialogActivity.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.wtf(TAG, "creating ...");
        /**
         try {
         populateData();
         } catch (ParseException e) {
         e.printStackTrace();
         }
         */
        bindArrayAdapter();
        setListeners();
        instance = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_signOut) {

            EmailPasswordActivity epa = EmailPasswordActivity.getInstance();
            epa.signOut(getBaseContext());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Log.wtf(TAG, "clicking");
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String taskTitle = ((TextView) view.findViewById(R.id.taskDescription)).getText().toString();
                Log.d(TAG, taskTitle);
            }
        });
        View taskView = getLayoutInflater().inflate(R.layout.custom_taskview,null);
        Button button = taskView.findViewById(R.id.startStopButton);
        Log.d(TAG, button.getText().toString());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"on click button");
                Log.d(TAG,v.toString());
                startTaskTimer(v);
            }
        });
    }

    public void bindArrayAdapter() {
        listView = findViewById(R.id.listviewtasks);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Task> items = new ArrayList<Task>();
        //final ArrayAdapter<String> itemsadapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,items);
        final ArrayAdapter<Task> itemsadapter = new CustomTaskAdapter(this, items);
        listView.setAdapter(itemsadapter);

        //Get userID of currently logged in User
        String userID = getCurrentUserID();

        db.collection("user").document(userID).collection("tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException exception) {
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> listOfDocuments = queryDocumentSnapshots.getDocuments();
                    List<Task> taskList = new ArrayList<Task>();
                    for (DocumentSnapshot doc : listOfDocuments) {
                        taskList.add(doc.toObject(Task.class));
                        items.add(doc.toObject(Task.class));
                        //items.add(doc.toObject(Task.class).getDescription());
                    }

                    itemsadapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


    }

    public void populateData(Task t) throws ParseException {
        //tDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //Date date = new Date();
        //String newdateString = dateFormat.format(date);
        //Date newdate = dateFormat.parse(newdateString);


        if (getCurrentUserID() != null) {
            //Task t = new Task("Task 1: "+mAuth.getCurrentUser().getEmail(), newdate, userID);
            db.collection("user").document(getCurrentUserID()).collection("tasks").add(t);
        }
    }

    public String getCurrentUserID() {
        //Get userID of current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public void startTaskTimer(final View v) {
        if(!oneTaskActive) {
            oneTaskActive = true;
            T = new Timer();
            LayoutInflater layoutInflater = LayoutInflater.from(Tasks.this);
            final View parentView = layoutInflater.inflate(R.layout.custom_taskview, null);
            Log.d(TAG, parentView.toString());
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int count = Integer.parseInt(((TextView) v.findViewById(R.id.taskDuration)).getText().toString());
                            Log.d(TAG, String.valueOf(count));
                            count++;
                            ((TextView) v.findViewById(R.id.taskDuration)).setText(String.valueOf(count));

                            //Enable Finish Button Disable Start Button
                            (v.findViewById(R.id.startStopButton)).setEnabled(false);
                            (v.findViewById(R.id.finishButton)).setEnabled(true);

                            //Set Background Color of active Task
                            ((TextView) v.findViewById(R.id.taskStatus)).setText("active");
                            ((TextView) v.findViewById(R.id.taskStatus)).setBackgroundColor(Color.rgb(155, 244, 66));
                        }
                    });
                }
            }, 1000, 1000);
        }
    }

    public void stopTaskTimer(final View v) {
        if(oneTaskActive) {
            T.cancel();
            LayoutInflater layoutInflater = LayoutInflater.from(Tasks.this);
            final View parentView = layoutInflater.inflate(R.layout.custom_taskview, null);
            Log.d(TAG, parentView.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int count = Integer.parseInt(((TextView) v.findViewById(R.id.taskDuration)).getText().toString());
                    Log.d(TAG, String.valueOf(count));

                    //Enable Finish Button Disable Start Button
                    (v.findViewById(R.id.startStopButton)).setEnabled(true);
                    (v.findViewById(R.id.finishButton)).setEnabled(false);

                    //Set Background Color of active Task
                    ((TextView) v.findViewById(R.id.taskStatus)).setText("paused");
                    ((TextView) v.findViewById(R.id.taskStatus)).setBackgroundColor(Color.rgb(255, 255, 255));
                }
            });
            oneTaskActive = false;
        }
    }

    public static Tasks getInstance() {
        return instance;
    }

}
