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
    private static boolean thereIsActiveTask = false;
    private static Task activeTask = null;

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
        Button button = taskView.findViewById(R.id.startButton);
        Log.d(TAG, button.getText().toString());
        /**
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"on click button");
                Log.d(TAG,v.toString());
                startTaskTimer(v);
            }
        });
         */
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
                    items.clear();
                    List<DocumentSnapshot> listOfDocuments = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : listOfDocuments) {

                        Task t = doc.toObject(Task.class);
                        //Setting docID field in db if not already set
                        if(t.getDocumentID() == null){
                            //Log.d(TAG,"Task Object has no docID yet ...");
                            t.setDocumentID(doc.getId());
                            updateTaskInDb(t);
                        }
                        //Log.d(TAG,"Task Object should have docID now "+t.getDocumentID());
                        //if(t.getState().equals("active")) {
                            //items.add(0,t);
                        //} else {
                            items.add(t);
                        //}

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(getCurrentUserID() != null) {
            db.collection("user").document(getCurrentUserID()).collection("tasks").add(t);
        }
    }

    public String getCurrentUserID() {
        //Get userID of current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public void startTaskTimer(final View v, final Task task) {
        if(!thereIsActiveTask) {
            thereIsActiveTask = true;
            activeTask = task;
            T = new Timer();

            //Layoutreferenz zum Task
            //LayoutInflater layoutInflater = LayoutInflater.from(Tasks.this);
            //final View parentView = layoutInflater.inflate(R.layout.custom_taskview, null);

            //Andere Buttons disablen
            enableDisableAllButtons(true);

            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int count = Integer.parseInt(((TextView) v.findViewById(R.id.taskDuration)).getText().toString());
                            count++;
                            ((TextView) v.findViewById(R.id.taskDuration)).setText(String.valueOf(count));

                            //Enable Finish Button Disable Start Button
                            (v.findViewById(R.id.startButton)).setEnabled(false);
                            (v.findViewById(R.id.stopButton)).setEnabled(true);
                            (v.findViewById(R.id.finishButton)).setEnabled(true);

                            //Set Background Color of active Task
                            ((TextView) v.findViewById(R.id.taskStatus)).setText("active");
                            ((TextView) v.findViewById(R.id.taskStatus)).setBackgroundColor(Color.rgb(155, 244, 66));

                            task.setTaskDuration(count);
                            task.setState("active");
                            updateTaskInDb(task);
                        }
                    });
                }
            }, 1000, 1000);
        }
    }

    public void stopTaskTimer(final View v, final Task task) {
        if(thereIsActiveTask) {
            T.cancel();
            //LayoutInflater layoutInflater = LayoutInflater.from(Tasks.this);
            //final View parentView = layoutInflater.inflate(R.layout.custom_taskview, null);

            enableDisableAllButtons(false);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int count = Integer.parseInt(((TextView) v.findViewById(R.id.taskDuration)).getText().toString());

                    //Enable Finish Button Disable Start Button
                    (v.findViewById(R.id.startButton)).setEnabled(true);
                    (v.findViewById(R.id.stopButton)).setEnabled(false);

                    //Set Background Color of active Task
                    ((TextView) v.findViewById(R.id.taskStatus)).setText("paused");
                    ((TextView) v.findViewById(R.id.taskStatus)).setBackgroundColor(Color.rgb(244, 209, 66));


                    task.setTaskDuration(count);
                    task.setState("paused");
                    updateTaskInDb(task);
                }
            });
            thereIsActiveTask = false;
            activeTask = null;
        }
    }

    public void finishTaskTimer(final View v, final Task task) {
        if(thereIsActiveTask) {
            T.cancel();
        }
        //LayoutInflater layoutInflater = LayoutInflater.from(Tasks.this);
        //final View parentView = layoutInflater.inflate(R.layout.custom_taskview, null);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int count = Integer.parseInt(((TextView) v.findViewById(R.id.taskDuration)).getText().toString());

                //Set Background Color of active Task
                ((TextView) v.findViewById(R.id.taskStatus)).setText("finished");
                ((TextView) v.findViewById(R.id.taskStatus)).setBackgroundColor(Color.rgb(255, 0, 0));

                task.setTaskDuration(count);
                task.setState("finished");
                updateTaskInDb(task);
            }
        });

        thereIsActiveTask = false;
        activeTask = null;
        enableDisableAllButtons(false);
        bindArrayAdapter();
        setListeners();
    }

    public static Tasks getInstance() {
        return instance;
    }

    public void setOneTaskActive(boolean status){
        thereIsActiveTask = status;

        //Setting the state of the task to paused when logging out of app
        if(status == false && activeTask!=null){
            activeTask.setState("paused");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //Get userID of currently logged in User
            String userID = getCurrentUserID();
            db.collection("user").document(userID).collection("tasks").document(activeTask.getDocumentID()).set(activeTask);
        }
    }

    public void enableDisableAllButtons(boolean status) {
        for (int i = 0; i < listView.getChildCount(); i++) {
            Log.d(TAG,((TextView)listView.getChildAt(i).findViewById(R.id.taskStatus)).getText().toString());
            //if (((TextView)listView.getChildAt(i).findViewById(R.id.taskStatus)).getText().toString().equals("finished")) {

                //listView.getChildAt(i).findViewById(R.id.startButton).setEnabled(false);
                //listView.getChildAt(i).findViewById(R.id.stopButton).setEnabled(false);
                //listView.getChildAt(i).findViewById(R.id.finishButton).setEnabled(false);

            //}else{
                if (status == true) {
                    listView.getChildAt(i).findViewById(R.id.startButton).setEnabled(false);
                    listView.getChildAt(i).findViewById(R.id.stopButton).setEnabled(false);
                }else if(status==false){
                    listView.getChildAt(i).findViewById(R.id.startButton).setEnabled(true);
                    listView.getChildAt(i).findViewById(R.id.finishButton).setEnabled(false);
                }
            //}
        }
    }

    public void manuallyStopTimerOnExit(){
        try {
            this.T.cancel();
        }catch(NullPointerException e){};
    }

    private void updateTaskInDb(Task t){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String docID = t.getDocumentID();
        //Log.d(TAG,"Updating called, task id is: " + docID);
        if (getCurrentUserID() != null) {
            if(t.getState().toString().equals("finished")){
                //First add the finished task to other collection
                db.collection("user").document(getCurrentUserID()).collection("finishedTasks").document(docID).set(t)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Document successfully added to finishedTasks!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error while adding document to finished tasks", e);
                            }
                        });
                //Then remove task from active tasks
                db.collection("user").document(getCurrentUserID()).collection("tasks").document(docID).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Document successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error while deleting document", e);
                            }
                        });
            }else {
                db.collection("user").document(getCurrentUserID()).collection("tasks").document(docID).set(t)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Document successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error while updating document", e);
                            }
                        });
            }

        }
    }
}
