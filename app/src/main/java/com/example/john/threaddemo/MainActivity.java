package com.example.john.threaddemo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//1.extends Thread
//2.implements Runnable
//-Handler
//-Looper
//-AsyncTask, HanlderThread, ThreadPoolExecutor

//-All UI components run on the UI thread-sometimes called the main thread
//-The main thread is run on a message queue
//-message queue- accepts work- it runs in a loop- and checks the message
//  queue constantly for work
//-other threads(not main thread)-They run differently than main thread
//  A thread is started-it does its task-then it closes-no loop-no msg queue
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTextView;
    private Handler mainHandler = new Handler();//from android.os package
    //Error:Only the original thread that created a view can touch its view
    //Handler is a android class that makes it easier to pass work between
    //  threads-Handler is responsible for giving work to the thread its
    //  associated with-by default its associated with the thread's message
    //  queue that it's instantiated on
    //  -so our mainHandler is associated with the UI thread's message queue

    private volatile boolean stopThread = false;
    //volatile-means all our threads try to access the most recent up to
    //  date version of this variable
    //-Booleans if not given a value are false by default-but setting it
    //  to false makes it clearer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);

        Button startBtn = findViewById(R.id.start_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = false;
                //Run a heavy operation that takes 10 seconds to finish
                //   -all operations run on the UI thread by default
                //   -when an operation is run on the UI thread-no other
                //   UI components/operations can run on that thread
//                loopFor(10);//clogs the UI thread for 10 seconds

                //Create a thread by instantiating a Thread class object
                //  -then calling .start() on that thread object
//                new ExampleThread().start();

                //Create a thread object and pass it a Runnable object
                //  -then call .start() on the thread object
                new Thread(new ExampleRunnable()).start();
                Thread thread = new Thread(new ExampleRunnable());
                //Executing on the main thread using the Runnable interface
//                new ExampleRunnable().run();

                //Anonymous class
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loopFor(10);
//                    }
//                }).start();

            }
        });

        Button stop = findViewById(R.id.stop_button);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when stopThread is true, threads stop
                stopThread = true;
            }
        });
    }
    //1.One way to run a thread is create a class extend Thread class
    class ExampleThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "Inside ExampleThread extends Thread");
            //Run a new thread-thats not going to be on the UI thread
            loopFor(10);
        }
    }
    //2.Another way to run a thread is create a class that implements Runnable
    class ExampleRunnable implements Runnable{
        @Override
        public void run() {
            Log.d(TAG, "Inside ExampleRunnable implements Runnable: ");
            loopFor(10);
        }
        //Implementing Runnable is the prefered way because we don't want
        //  to change the behavior of Thread class Run() method, instead
        //  we just want to run a thread/pass it some work to execute.
        //  -also we can execute ExampleRunnable on the main thread
        //   if we wanted to with new ExampleRunnable.run()-where
        //   the thread will be whatever thread the code is run on
        //         (no need for new Thread(...) )
    }
    //-Takes up a thread for a certain amount of seconds(int seconds)
    private void loopFor(int seconds){
        for(int i = 0; i < seconds; i++){
            if(stopThread) return;//-use stopThread boolean to stop threads
            Log.d(TAG, "run: "+i);
            //Error:Only the orginal thread that created a view can touch its view
            //   -so if the UI thread created a textView-only that UI thread can
            //   change contents of that view
            //-if(i == 5) mTextView.setText("50%"); //causes error
            if(i == 5){
                //post a task to the handler-which gives it to the message
                //  queue it's associated to
                //-new Handler(Looper.getMainLooper()) - gets the looper on
                //main thread using a differen't way
//                Handler mainHandler = new Handler(Looper.getMainLooper());
//
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //A Runnable object does start/run itself
//                        //  It's just work to be done
//                        mTextView.setText("50%");
//                    }
//                });
                //-post a runnable object straight to the UI view
//                mTextView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTextView.setText("50%");
//                    }
//                });

                //runOnUiThread() is an Activity method- thats why we don't have
                //  to call anything before it-Activity.runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("50%");
                    }
                });
                //-Running seperate threads & worrying the threads looper can get
                //  confusing as our app grows
                //-AsyncTask was implemented to make life easier by abstracting
                //  this functionality
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
