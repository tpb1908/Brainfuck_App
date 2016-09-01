package com.tpb.brainfuck_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Main";
    private ProgramRecyclerAdapter mAdapter;
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main.this, Editor.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.program_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProgramRecyclerAdapter(Storage.instance(this));
        rv.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.remove((CoordinatorLayout) Main.this.findViewById(R.id.coordinator), viewHolder.getAdapterPosition());

            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }


    private class ProgramRecyclerAdapter extends RecyclerView.Adapter<ProgramRecyclerAdapter.ProgramViewHolder> {
        private static final String TAG = "ProgramRecycler";
        private Storage storage;
        private ArrayList<Program> programs;

        public ProgramRecyclerAdapter(Storage storage) {
            this.storage = storage;
            getData();
        }

        public void getData() {
            if(storage.updatePerformed(lastUpdate)) {
                programs = storage.getAll();
                notifyDataSetChanged();
            }
            lastUpdate = System.nanoTime();
        }

        public void remove(CoordinatorLayout cl, final int pos) {
            Log.i(TAG, "remove: " + programs.get(pos));
            final Program p = programs.get(pos);
            programs.remove(pos);
            notifyItemRemoved(pos);
            final Snackbar snackbar = Snackbar
                    .make(cl, p.name + " deleted" , Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if(event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                storage.remove(p);
                            }
                            super.onDismissed(snackbar, event);
                        }
                    })
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            programs.add(pos, p);
                            notifyItemInserted(pos);
                        }
                    });
            snackbar.show();

        }

        @Override
        public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.program_card, parent, false
            ));
        }

        @Override
        public void onBindViewHolder(ProgramViewHolder holder, int position) {
            final Program p = programs.get(position);
            Log.i(TAG, "onBindViewHolder: " + p.toString());
            holder.program = p;
            holder.title.setText(p.name);
            if(p.desc != null) {
                holder.desc.setText(p.desc);
            } else {
                holder.desc.setText("No description");
            }
        }

        @Override
        public int getItemCount() {
            return programs.size();
        }

        class ProgramViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView desc;
            private ImageButton mFastRunButton;
            private Program program;

            public ProgramViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.text_title);
                desc = (TextView) itemView.findViewById(R.id.text_desc);
                mFastRunButton = (ImageButton) itemView.findViewById(R.id.button_quick_run);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.getData();
        Log.i(TAG, "onResume: ");
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_camera) {
            // Handle the camera action
        } else if(id == R.id.nav_gallery) {

        } else if(id == R.id.nav_slideshow) {

        } else if(id == R.id.nav_manage) {

        } else if(id == R.id.nav_share) {

        } else if(id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
