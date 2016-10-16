package com.tpb.brainfuck_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class Main extends AppCompatActivity {
    private static final String TAG = "Main";
    private ProgramRecyclerAdapter mAdapter;
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main.this, Editor.class));
            }
        });
        if(getSharedPreferences("firstRun", MODE_PRIVATE).getBoolean("firstRun", true)) {
            Storage.instance(this).restoreDefaultPrograms();
            getSharedPreferences("firstRun", MODE_PRIVATE).edit().putBoolean("firstRun", false).apply();
        }


        RecyclerView rv = (RecyclerView) findViewById(R.id.program_recycler);
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 && fab.isShown()) {
                    fab.hide();
                } else if(dy < 0 && !fab.isShown()) {
                    fab.show();
                }
            }

        });
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
        new ItemTouchHelper(callback).attachToRecyclerView(rv);
    }


    private class ProgramRecyclerAdapter extends RecyclerView.Adapter<ProgramRecyclerAdapter.ProgramViewHolder> {
        private static final String TAG = "ProgramRecycler";
        private Storage storage;
        private ArrayList<Program> programs;

        ProgramRecyclerAdapter(Storage storage) {
            this.storage = storage;
            getData();
        }

        void getData() {
            if(storage.updatePerformed(lastUpdate)) {
                programs = storage.getAll();
                notifyDataSetChanged();
            }
            lastUpdate = System.nanoTime();
        }

        void remove(CoordinatorLayout cl, final int pos) {
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
        public void onBindViewHolder(final ProgramViewHolder holder, int position) {
            final Program p = programs.get(position);
            Log.i(TAG, "onBindViewHolder: " + p);
            holder.program = p;
            holder.title.setText(p.name);
            if(p.desc != null) {
                holder.desc.setText(p.desc);
            } else {
                holder.desc.setText("No description");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent i = new Intent(Main.this, Editor.class);
                    i.putExtra("prog", holder.program);
                    startActivity(i);
                }
            });
            holder.mFastRunButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent i = new Intent(Main.this, Runner.class);
                    i.putExtra("prog", holder.program);
                    startActivity(i);
                }
            });
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
        if(id == R.id.action_restore_defaults) {
            Storage.instance(this).restoreDefaultPrograms();
            mAdapter.getData();
            return true;
        } else if(id == R.id.action_help) {
            new HelpDialog().show(getFragmentManager(), "Help");
        }

        return super.onOptionsItemSelected(item);
    }

}
