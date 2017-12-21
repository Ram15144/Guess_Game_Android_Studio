package com.example.ram.numberguessingthreads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity {

    private static final int START_GAME = 1;
    private static final int GUESS_NEXT_WORD = 2;
    private static final int UPDATE_MOVE_1_ON_UI = 3;
    private static final int UPDATE_MOVE_2_ON_UI = 4;
    private static final int RESTART_GAME = 5;

    TextView p1val;
    TextView p2val;

    //The number of each player that the other player should guess
    int player1no[],player2no[];

    // Counter to intelligently guess the numbers
    int p2c[];

    //Counter that keeps track of the guesses
    int counter1,counter2;

    // Holds player's correct and false guesses
    boolean p1[];
    boolean p2[];

    //Array adapters to hold the guesses to print in UI
    ArrayList<String> guesslist1 = new ArrayList<String>();
    ArrayAdapter<String> guessadapter1;
    ArrayList<String> guesslist2 = new ArrayList<String>();
    ArrayAdapter<String> guessadapter2;
    ListView p1print,p2print;

    //TextView to print the winner
    TextView winner;
    //Holds the digits at the correct location for each player
    int number_p1[];
    int number_p2[];

    Handler handler;
    private Handler p1Handler;
    private Handler p2Handler;
    Thread player1, player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p1val=(TextView)findViewById(R.id.Player1no);
        p2val=(TextView)findViewById(R.id.Player2no);
        p1print=(ListView)findViewById(R.id.Player1Guessesprint);
        p2print=(ListView)findViewById(R.id.Player2Guessesprint);
        winner=(TextView)findViewById(R.id.winner);

        //Setting the new numbers to guess
        player1no=new int[4];
        player2no=new int[4];
        p2c=new int[4];
        //Random r=new Random();
        /*for(int i=0;i<4;i++)
        {
            player1no[i]=r.nextInt(10);
            p1val.append(Integer.toString(player1no[i]));
            player2no[i]=r.nextInt(10);
            p2val.append(Integer.toString(player2no[i]));
        }*/

        guessadapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, guesslist1);
        p1print.setAdapter(guessadapter1);
        guessadapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, guesslist2);
        p2print.setAdapter(guessadapter2);
        counter1=20;
        counter2=20;

        //Setting the initial values
        p1=new boolean[4];
        p2=new boolean[4];
        number_p1= new int[4];
        number_p2=new int[4];
        /*for(int i=0;i<4;i++)
        {
            p1[i]=false;
            p2[i]=false;
            number_p1[i]=-1;
            number_p2[i]=-1;
        }*/

        /* reset the game variables*/
        //resetGame();

        /*  Create handler for UI Thread*/
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Message message;
                switch (msg.what){

                    case START_GAME:{
                            /* Send message to player 1 to start game */
                        Log.i("Main", "Game started!!!!!!");
                        message = p1Handler.obtainMessage(GUESS_NEXT_WORD);
                        p1Handler.sendMessage(message);
                        break;

                    }

                    case UPDATE_MOVE_1_ON_UI:{

                        /* Update Player 1 move on UI */
                        makePlayer1Guess(msg.arg1);
                        Log.i("Main", "Move 1 updated!!!");

                        if(counter1 ==0 && counter2 ==0)
                        {
                            Log.i("Exit", "Game");
                            player1.interrupt();
                            player2.interrupt();
                            handler.removeCallbacksAndMessages(null);
                            p1Handler.removeCallbacksAndMessages(null);
                            p2Handler.removeCallbacksAndMessages(null);
                            p1Handler.getLooper().quitSafely();
                            p2Handler.getLooper().quitSafely();
                            ((TextView) findViewById(R.id.winner)).setText("IT IS A DRAW");
                        }
                        /* Check if player 1 has won */
                        if (checkWin() == 1) {
                            Log.i("Exit", "Game");
                            player1.interrupt();
                            player2.interrupt();
                            handler.removeCallbacksAndMessages(null);
                            p1Handler.removeCallbacksAndMessages(null);
                            p2Handler.removeCallbacksAndMessages(null);
                            p1Handler.getLooper().quitSafely();
                            p2Handler.getLooper().quitSafely();
                            ((TextView) findViewById(R.id.winner)).setText("PLAYER 1 WINS!!!");
                        }

                            /* Post a runnable to Player 2 to wait for 2 seconds */
                        p2Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.i("Thread 1", " Sleeping");
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    Log.i("ThreadsGuessNumber", "*****THREAD INTERRUPTED TO RESTART GAME*****");
                                    e.printStackTrace();
                                }
                            }
                        });

                            /* Notify Player 2 to get next move */
                        message = p2Handler.obtainMessage(GUESS_NEXT_WORD);
                        p2Handler.sendMessage(message);
                        break;
                    }
                    case UPDATE_MOVE_2_ON_UI:{

                        /* Update Player 2 move on UI */
                        makePlayer2Guess(msg.arg1);
                        Log.i("Main", "Move 2 updated!!!");

                        if(counter1 ==0 && counter2 ==0)
                        {
                            Log.i("Exit", "Game");
                            player1.interrupt();
                            player2.interrupt();
                            handler.removeCallbacksAndMessages(null);
                            p1Handler.removeCallbacksAndMessages(null);
                            p2Handler.removeCallbacksAndMessages(null);
                            p1Handler.getLooper().quitSafely();
                            p2Handler.getLooper().quitSafely();
                            ((TextView) findViewById(R.id.winner)).setText("IT IS A DRAW");
                        }

                        /* Check if player 2 has won */
                        if( checkWin() == 2){
                            Log.i("Exit","Game");
                            player1.interrupt();
                            player2.interrupt();
                            handler.removeCallbacksAndMessages(null);
                            p1Handler.removeCallbacksAndMessages(null);
                            p2Handler.removeCallbacksAndMessages(null);
                            p1Handler.getLooper().quitSafely();
                            p2Handler.getLooper().quitSafely();
                            ((TextView)findViewById(R.id.winner)).setText("PLAYER 2 WINS!!!");
                            break;
                        }

                            /* Post a runnable to Player 1 to wait for 2 seconds */
                        p1Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.i("Thread 2", " Sleeping");
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    Log.i("ThreadsGuessNumber","*****THREAD INTERRUPTED TO RESTART GAME*****");
                                    e.printStackTrace();
                                }
                            }
                        });

                        /* Notify Player 1 to get next move */
                        message = p1Handler.obtainMessage(GUESS_NEXT_WORD);
                        p1Handler.sendMessage(message);
                        break;

                    }

                    case RESTART_GAME:{
                        if(p1Handler != null && p2Handler != null){
                            p1Handler.removeCallbacksAndMessages(null);
                            p2Handler.removeCallbacksAndMessages(null);

                            try {
                                player1.join();
                                player2.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    }
                    default:
                        break;
                }
            }
        };

        /* Set click listener for start button*/
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* Interrupt the player threads and clear callbacks
                * Reset the game variable in order to restart the game*/
                if (player1!=null && player2!=null){
                    player1.interrupt();
                    player2.interrupt();
                    handler.removeCallbacksAndMessages(null);
                    p1Handler.removeCallbacksAndMessages(null);
                    p2Handler.removeCallbacksAndMessages(null);
                    p1Handler.getLooper().quitSafely();
                    p1Handler.getLooper().quitSafely();
                    resetGame();
                }
                else
                {
                    resetGame();
                }

                /* Create player 1 thread */
                player1 = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /* Prepare Looper */
                        Looper.prepare();

                        /* Set handler for player 1 thread */
                        p1Handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                Message message;
                                switch (msg.what){
                                    case GUESS_NEXT_WORD:{

                                        /* Get move for player 1 and send move as argument in the message
                                        * Send message to UI thread to update move on UI */
                                        message = handler.obtainMessage(UPDATE_MOVE_1_ON_UI);
                                        message.arg1 = getMoveForPlayer1();
                                        handler.sendMessage(message);
                                        break;
                                    }

                                    default:
                                        break;
                                }
                            }

                        };
                        Log.i("P1", "Handler created");

                        /*  Send message to UI thread to start the game */
                        Message message = handler.obtainMessage(START_GAME);
                        handler.sendMessage(message);
                        Looper.loop();
                    }
                });


                player2 = new Thread(new Runnable() {
                    @Override
                    public  void run() {

                         /* Prepare Looper */
                        Looper.prepare();
                         /* Set handler for player 1 thread */
                        p2Handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                Message message;
                                switch (msg.what){
                                    case GUESS_NEXT_WORD:{

                                        /* Get move for player 2 and send move as argument in the message
                                        *  Message to UI thread to update move on UI is sent from method getSmartMoveForPlayer2*/
                                        /* Get move for player 1 and send move as argument in the message
                                        * Send message to UI thread to update move on UI */
                                        message = handler.obtainMessage(UPDATE_MOVE_2_ON_UI);
                                        message.arg1 = getMoveForPlayer2();
                                        handler.sendMessage(message);
                                        break;
                                    }
                                    default:{

                                        break;
                                    }
                                }
                            }
                        };

                        Log.i("P2", "Handler created");
                        Looper.loop();

                    }
                });
                player1.start();
                player2.start();
            }
        });


    }

    /*  Method to get random move for player 1 */
    public int getMoveForPlayer1()
    {
        counter1--;
        Log.i("Thread 1", " Awake");
        Log.i("Thread  1 ", "Getting move from thread 1 !!");

        int guess=0;
        Random r = new Random();
        for(int i=0;i<4;i++)
        {
            if(i==0)
            {
                if(p1[i])
                {
                    guess = number_p1[i];
                }
                else
                {
                    guess = r.nextInt(10);
                    if(guess==player2no[i])
                    {
                        p1[0]=true;
                        number_p1[0]=guess;
                    }
                }
            }
            else {
                if (p1[i])
                {
                    guess = guess * 10 + number_p1[i];
                }
                else
                {
                    int val=r.nextInt(10);
                    guess = guess * 10 + val;
                    if(val==player2no[i])
                    {
                        p1[i]=true;
                        number_p1[i]=val;
                    }
                }
            }
        }

        return guess;
    }

    /*  Method to get random move for player 2 */
    public int getMoveForPlayer2()
    {
        counter2--;
        Log.i("Thread 2", " Awake");
        Log.i("Thread  2 ", "Getting move from thread 1 !!");

        int guess=0;
        Random r = new Random();
        for(int i=0;i<4;i++)
        {
            if(i==0)
            {
                if(p2[i])
                {
                    guess = number_p2[i];
                }
                else
                {
                    guess = p2c[0];
                    p2c[0]++;
                    if(guess==player1no[i])
                    {
                        p2[0]=true;
                        number_p2[0]=guess;
                    }
                }
            }
            else {
                if (p2[i])
                {
                    guess = guess * 10 + number_p2[i];
                }
                else
                {
                    int val=p2c[i];
                    p2c[i]++;
                    guess = guess * 10 + val;
                    if(val==player1no[i])
                    {
                        p2[i]=true;
                        number_p2[i]=val;
                    }

                }
            }
        }
        return guess;
    }

    /*  Method to update Player 1 move on UI */
    public void makePlayer1Guess(int guess)
    {
        System.out.println("Player 1 :"+ guess);

        int d1,d2,d3,d4;
        d1=guess/1000;
        d2=(guess/100)%10;
        d3=(guess/10)%10;
        d4=guess%10;

        int correct_guesses=0;
        int wrong_guesses=0;
        for(int i=0;i<4;i++)
        {
            if(p1[i])
                correct_guesses++;
            else
                wrong_guesses++;
        }
        String str="\nTurns left: "+counter1+"\nNumber Guessed:"+ d1+d2+d3+d4+"\n-> Correct guesses:"+ correct_guesses+"\n-> Wrong guesses:"+ wrong_guesses+"\n";
        guesslist1.add(str);
        guessadapter1.notifyDataSetChanged();
        //p1print.append("Number Guessed:"+ guess+"\n Correct guesses:"+ correct_guesses+"\n Wrong guesses:"+ wrong_guesses+"\n\n");
    }

    /*  Method to update Player 2 move on UI */
    public void makePlayer2Guess(int guess)
    {
        System.out.println("Player 2 :"+ guess);

        int d1,d2,d3,d4;
        d1=guess/1000;
        d2=(guess/100)%10;
        d3=(guess/10)%10;
        d4=guess%10;

        int correct_guesses=0;
        int wrong_guesses=0;
        for(int i=0;i<4;i++)
        {
            if(p2[i])
                correct_guesses++;
            else
                wrong_guesses++;
        }
        String str="\nTurns left: "+counter2+"\nNumber Guessed:"+ d1+d2+d3+d4+"\n-> Correct guesses:"+ correct_guesses+"\n-> Wrong guesses:"+ wrong_guesses+"\n";
        guesslist2.add(str);
        guessadapter2.notifyDataSetChanged();
        //p2print.append("Number Guessed:"+ guess+"\n Correct guesses:"+ correct_guesses+"\n Wrong guesses:"+ wrong_guesses+"\n\n");
    }

    // Resets game with 20 turns remaining for each player
    public void resetGame(){

        /* Reset guesses */
        for(int i=0;i<4;i++)
        {
            p1[i]=false;
            p2[i]=false;
            number_p1[i]=-1;
            number_p2[i]=-1;
            p2c[i]=0;
        }

        Random r=new Random();
        p1val.setText("");
        p2val.setText("");
        for(int i=0;i<4;i++)
        {
            player1no[i]=r.nextInt(10);
            p1val.append(Integer.toString(player1no[i]));
            player2no[i]=r.nextInt(10);
            p2val.append(Integer.toString(player2no[i]));
        }
        guesslist1.clear();
        guessadapter1.notifyDataSetChanged();
        guesslist2.clear();
        guessadapter2.notifyDataSetChanged();
        int val1=0,val2=0;
        for(int i=0;i<4;i++)
        {
            val1=val1 * 10 + player1no[i];
            val2=val2 * 10 + player2no[i];
        }
        winner.setText("Guess who'll be the winner");
        //((TextView)findViewById(R.id.winner)).setText("Guess who'll be the winner");
        //((TextView)findViewById(R.id.Player1no)).setText(val1);
        //((TextView)findViewById(R.id.Player2no)).setText(val2);

    }

    /*  Method to check if any of the players has won */
    public int checkWin(){

        int check_p1win=0;
        int check_p2win=0;
        for(int i=0;i<4;i++)
        {
            if(p1[i])
                check_p1win++;
            if(p2[i])
                check_p2win++;
        }

        if(check_p1win==4)
            return 1;
        else if(check_p2win==4)
            return 2;
        else
            return 0;
    }
}
