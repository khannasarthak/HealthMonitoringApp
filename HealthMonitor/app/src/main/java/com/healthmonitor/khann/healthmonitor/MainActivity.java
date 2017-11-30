package com.healthmonitor.khann.healthmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.Viewport;
import java.util.Random;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.util.Log;

// Wherever not marked as reference, is our code.
public class MainActivity extends AppCompatActivity implements OnClickListener {
    // Declare all the private variables for this class
    private LineGraphSeries mSeries1 = new LineGraphSeries<>();
    private LineGraphSeries mSeries2 = new LineGraphSeries<>();

    Random rand = new Random();
    int x_value = 0;
    Button runButton, stopButton;
    GraphView graphView;
    boolean firstRun = true;

    // Reference to implement own Runnable class:
    // https://www.tutorialspoint.com/java/java_thread_control.htm
    class RunnableDemo implements Runnable {
        public Thread thread;
        public boolean suspended = false;

        public void run() {
            // we add 5000 new entries
            for (int i = 0; i < 5000; i++) {
                runOnUiThread(new RunnableDemo() {
                    @Override
                    public void run() {
                        addEntry();
                    }
                });

                // sleep to slow down the add of entries
                try {
                    Thread.sleep(500);
                    synchronized (this) {
                        while (suspended) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("Exception caught: " + e);
                }
            }
        }

        void start () {
            if (thread == null) {
                thread = new Thread (this);
                thread.start ();
            }
        }

        void suspend() {
            suspended = true;
        }

        synchronized void resume() {
            suspended = false;
            notify();
        }
    }


    RunnableDemo R1 = new RunnableDemo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // All initializations should go here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runButton = (Button) findViewById(R.id.run);
        runButton.setOnClickListener(this);
        stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        graphView = (GraphView) findViewById(R.id.graph);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(2000);
        viewport.setScrollable(true);
        viewport.setBackgroundColor(-16777216);
        viewport.setDrawBorder(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(40);
        viewport.setScalable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.run: {
                // Triggered when run button is clicked
                Log.d("MR.bool", "Run was clicked ");
                // runButton.setEnabled(false);
                // stopButton.setEnabled(true);
                graphView.addSeries(mSeries1);
                if (firstRun) {
                    // Start graph for the first time if run clicked in the start
                    R1.start();
                } else {
                    // If run is clicked again, resume drawing graph from when it was stopped
                    R1.resume();
                }
                break;
            }
            case R.id.stop: {
                //Triggered when stop button is clicked
                Log.d("MR.bool", "Stop was clicked ");
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                //runButton.setEnabled(true);
                // stopButton.setEnabled(false);
                firstRun = false;
                R1.suspend();
                break;
            }
        }
    }

    // Appends series objects to list after sleep in thread.
    // http://www.ssaurel.com/blog/create-a-real-time-line-graph-in-android-with-graphview/
    private void addEntry() {
        float y = rand.nextFloat() * (2000 - 20) + 20;
        mSeries1.appendData(new DataPoint(x_value, y), true, 500);
        x_value += 1;
    }
}