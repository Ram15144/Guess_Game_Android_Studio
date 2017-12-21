# Guess_Game_Android_Studio

A strategy game called Guess Four in which each of two players sets up a secret sequence of four decimal digits. The two players take turns guessing the sequence of digits of their opponent. A player responds to an opponents guess by specifying the number of digits that were successfully guessed in the correct position and the number of digits that were successfully guessed but in the wrong positions. Thus, if a players chosen number is 2017 and the opponent guesses 1089, the opponent would be told that one digit was guessed correctly in the correct position (i.e., the 0), and another digit was guessed in the wrong position, (i.e., the 1). The implementation involves two background threads that play against each other while also updating the user interface with their moves. The first thread to correctly guess the opposing thread’s number wins the game. The UI thread is responsible for creating and starting the two worker threads and for maintaining and updating the display. Each worker thread will take turns taking the following actions:
1. Waiting for a short time (1-2 seconds) in order for a human viewer to take note of the previous move on the display.
2. Figuring out the next guess of this thread.
3. Communicating this guess to the opponent thread.
4. Waiting for a response from the opponent thread.
5. Communicating both the guess and the response from the opposite thread to the UI thread.
While carrying out the above steps each worker thread is able to respond to guesses from the opponent thread. Whenever a guess from the opponent thread is received, a worker thread responds by communicating the number of correctly positioned and incorrectly positioned digits contained in the guess to the opponent thread. Furthermore, the game must proceeds in lockstep between the two threads. A thread is not allowed to make two consecutive guesses without handling an intervening guess from the opponent thread. The UI thread is specifically responsible for the following functionality:
1. Showing the two initial numbers chosen by the worker threads.
2. Receiving notifications of guesses and their outcomes by the worker threads.
3. Displaying the guesses and responses of each worker thread in an appropriate format as each worker thread communicates this information to the UI thead.

4. Displaying a button to start the game. Pressing this button while a game is in progress will void the current game and start a new game from scratch.
5. Checking on the status of the game, by determining whether one thread has won or the game needs to continue.
6. Halting the game after each thread has made 20 guesses without identifying the opponent’s number.
7. Signaling the two worker threads that the game is over; the two threads stops their execution as a result of this action. Displaying the outcome of the game in the UI.

Implementation:
1. Handlers are used to implement the communication between the three threads involved. Each thread has a handler, a job queue and a looper.
2. Both runnables and messages are included in the job queue of the worker threads.
3. The two worker threads uses different strategies for winning the game.
4. The game is played at such a speed that a human user can clearly see and understand the move of each thread.
5. The app’s display has two layouts or fragments, one for each worker thread, lying side-by-side and occupying the entire screen width. Each side displays the secret number of the corresponding thread at the top and a scrollable list of the thread’s guesses and the opponent thread’s responses in the remainder of the vertical space.
7. The two worker threads will use a random number generator to create their secret number. These numbers are communicated to the UI thread, which will immediately display both numbers. 