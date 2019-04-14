////////////////////////////////////////////////////////////////////////////////
//
//  Tuner - An Android Tuner written in Java.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.tuner;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.billthefarmer.tuner.client.ClientActivity;
import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.BluetoothUtils;
import util.StringUtils;

// Main Activity
public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, Test
{

    public static Test t;
    public static double cent = 0;
    private static final String PREF_INPUT = "pref_input";
    private static final String PREF_REFERENCE = "pref_reference";
    private static final String PREF_TRANSPOSE = "pref_transpose";

    private static final String PREF_FUND = "pref_fund";
    private static final String PREF_FILTER = "pref_filter";
    private static final String PREF_FILTERS = "pref_filters";
    private static final String PREF_DOWNSAMPLE = "pref_downsample";
    private static final String PREF_MULTIPLE = "pref_multiple";
    private static final String PREF_SCREEN = "pref_screen";
    private static final String PREF_STROBE = "pref_strobe";
    private static final String PREF_ZOOM = "pref_zoom";
    private static final String PREF_DARK = "pref_dark";

    private static final String PREF_NOTE = "pref_note";
    private static final String PREF_OCTAVE = "pref_octave";

    private static final String PREF_COLOUR = "pref_colour";
    private static final String PREF_CUSTOM = "pref_custom";

    private static final int VERSION_M = 23;

    private static final String TAG = "ClientActivity";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;


    BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private Handler mHandler;
   // private Handler mLogHandler;
    private Map<String, BluetoothDevice> mScanResults;

    private boolean mConnected;
    private boolean mEchoInitialized;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mGatt;

    // private GlobalVariable globalVariable;

    Handler handler = new Handler(Looper.myLooper());

    String wavPrevious="";

    // Note values for display
    private static final String notes[] = {
            "C", "C", "D", "E", "E", "F", "F", "G", "A", "A", "B", "B"
    };

    private static final String sharps[] = {
            "", "\u266F", "", "\u266D", "", "", "\u266F", "", "\u266D", "", "\u266D", ""
    };

    private Spectrum spectrum;
    private Display display;
    private Strobe strobe;
    private Status status;
    private Meter meter;
    private Scope scope;

    private Audio audio;
    private Toast toast;

    private boolean dark;

    public static String ipAddress = "192.168.1.14";// ur ip
    public static int portNumber = 80;// portnumber

    private Socket client;

    private static OutputStreamWriter printwriter;

    private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();

    public static void runButNotOn(Runnable toRun, Thread notOn)
    {
        if (Thread.currentThread() == notOn)
        {
            THREADPOOL.submit(toRun);
        }
        else
        {
            toRun.run();
        }
    }

    private void setUpSocket()
    {
        try
        {
            client = new Socket(ipAddress, portNumber);
            printwriter = new OutputStreamWriter(client.getOutputStream(), "ISO-8859-1");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void processImplementation(final String message)
    {
        // ... the actual work.
        try
        {
            printwriter.write(message);
            Log.d("MainActivity", "=processImplementation==");
        }
        catch (UnknownHostException e)
        {
            Log.d("MainActivity", "=UnknownHostException==");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("MainActivity", "=IOException==");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void processItem(final String itemId)
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
                Log.d("MainActivity", "=processitemblank==");
                Log.d("MainActivity", "=id==" + itemId);
                processImplementation(itemId);
            }
        };
        runButNotOn(task, Looper.getMainLooper().getThread());
    }

    // On Create
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setUpSocket();
        getPreferences();

        if (dark) setTheme(R.style.AppDarkTheme);

        t = this;
        setContentView(R.layout.activity_main);


        // Find the views, not all may be present
        spectrum = findViewById(R.id.spectrum);
        display = findViewById(R.id.display);
        strobe = findViewById(R.id.strobe);
        status = findViewById(R.id.status);
        meter = findViewById(R.id.meter);
        scope = findViewById(R.id.scope);

        // Add custom view to action bar
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.signal_view);
        actionBar.setDisplayShowCustomEnabled(true);

        SignalView signal = (SignalView) actionBar.getCustomView();

        // Create audio
        audio = new Audio();
        addMediaPlayer();
        // Connect views to audio
        if (spectrum != null) spectrum.audio = audio;

        if (display != null) display.audio = audio;

        if (strobe != null) strobe.audio = audio;

        if (status != null) status.audio = audio;

        if (signal != null) signal.audio = audio;

        if (meter != null)
        {
            meter.audio = audio;
            Log.d("tagggg", "=audio=" + audio);
        }
        if (scope != null) scope.audio = audio;

        // Set up the click listeners
        setClickListeners();

        mConnected = ((GlobalVariable)this.getApplicationContext()).getConnected();
        mGatt = ((GlobalVariable)this.getApplicationContext()).getGatt();
        mEchoInitialized = ((GlobalVariable)this.getApplicationContext()).getEchoInitialized();
    }

    // Messaging

    private void sendMessage(double cents) {

        if (!mConnected || !mEchoInitialized) {

            return;
        }

        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
            disconnectGattServer();
            return;
        }

        String message = Integer.toString((int) cents);

        byte[] messageBytes = StringUtils.bytesFromString(message);
        if (messageBytes.length == 0) {
            return;
        }

        characteristic.setValue(messageBytes);
        boolean success = mGatt.writeCharacteristic(characteristic);
        if (success) {

        } else {
        }
    }

    public void disconnectGattServer() {
        mConnected = false;
        mEchoInitialized = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    // On create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);

        return true;
    }

    // Set click listeners
    void setClickListeners()
    {
        // Scope
        if (scope != null)
        {
            scope.setOnClickListener(this);
            scope.setOnLongClickListener(this);
        }

        // Spectrum
        if (spectrum != null)
        {
            spectrum.setOnClickListener(this);
            spectrum.setOnLongClickListener(this);
        }

        // Display
        if (display != null)
        {
            display.setOnClickListener(this);
            display.setOnLongClickListener(this);
        }

        // Strobe
        if (strobe != null)
        {
            strobe.setOnClickListener(this);
            strobe.setOnLongClickListener(this);
        }

        // Meter
        if (meter != null)
        {
            meter.setOnClickListener(this);
            meter.setOnLongClickListener(this);
        }
    }

    // On click
    @Override
    public void onClick(View v)
    {
        // Get id
        int id = v.getId();
        switch (id)
        {
            // Scope
            case R.id.scope:
                audio.filter = !audio.filter;

                if (audio.filter) showToast(R.string.filter_on);
                else showToast(R.string.filter_off);
                break;

            // Spectrum
            case R.id.spectrum:
                audio.zoom = !audio.zoom;

                if (audio.zoom) showToast(R.string.zoom_on);
                else showToast(R.string.zoom_off);
                break;

            // Display
            case R.id.display:
                audio.lock = !audio.lock;
                if (display != null) display.invalidate();

                if (audio.lock) showToast(R.string.lock_on);
                else showToast(R.string.lock_off);
                break;

            // Strobe
            case R.id.strobe:
                audio.strobe = !audio.strobe;

                if (audio.strobe) showToast(R.string.strobe_on);

                else showToast(R.string.strobe_off);
                break;

            // Meter
            case R.id.meter:
                audio.copyToClipboard();
                showToast(R.string.copied_clip);
                break;
        }
    }

    // On long click
    @Override
    public boolean onLongClick(View v)
    {
        // Get id
        int id = v.getId();
        switch (id)
        {
            // Scope
            case R.id.scope:
                audio.fund = !audio.fund;

                if (audio.fund) showToast(R.string.fund_on);
                else showToast(R.string.fund_off);
                break;

            // Spectrum
            case R.id.spectrum:
                audio.downsample = !audio.downsample;

                if (audio.downsample) showToast(R.string.downsample_on);
                else showToast(R.string.downsample_off);
                break;

            // Display
            case R.id.display:
                audio.multiple = !audio.multiple;

                if (audio.multiple) showToast(R.string.multiple_on);

                else showToast(R.string.multiple_off);
                break;

            // Strobe
            case R.id.strobe:
                dark = !dark;

                if (Build.VERSION.SDK_INT != VERSION_M) recreate();

                else if (dark) showToast(R.string.dark_theme);

                else showToast(R.string.light_theme);
                break;

            // Meter
            case R.id.meter:
                audio.screen = !audio.screen;

                if (audio.screen) showToast(R.string.screen_on);

                else showToast(R.string.screen_off);

                Window window = getWindow();

                if (audio.screen) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
        }
        return true;
    }

    // On options item
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
            // Help
            case R.id.help:
                return onHelpClick(item);

            // Settings
            case R.id.settings:
                return onSettingsClick(item);

            case R.id.connect:
                return onConnectionClick(item);

            default:
                return false;
        }
    }

    // On Connection click
    private boolean onConnectionClick(MenuItem item)
    {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);

        return true;
    }

    // On help click
    private boolean onHelpClick(MenuItem item)
    {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);

        return true;
    }

    // On settings click
    private boolean onSettingsClick(MenuItem item)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        return true;
    }

    // Show toast.
    void showToast(int key)
    {
        Resources resources = getResources();
        String text = resources.getString(key);

        // Cancel the last one
        if (toast != null) toast.cancel();

        // Make a new one
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // Update status
        if (status != null) status.invalidate();
    }

    // On Resume
    @Override
    protected void onResume()
    {
        super.onResume();

        boolean theme = dark;

        // Get preferences
        getPreferences();

        if (dark != theme && Build.VERSION.SDK_INT != VERSION_M) recreate();

        // Update status
        if (status != null) status.invalidate();

        // Start the audio thread
        audio.start();

    }

    // onPause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Save preferences
        savePreferences();

        // Stop audio thread
        audio.stop();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            printwriter.flush();
            printwriter.close();
            client.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Save preferences
    void savePreferences()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PREF_DARK, dark);

        if (audio != null)
        {
            editor.putBoolean(PREF_FUND, audio.fund);
            editor.putBoolean(PREF_FILTER, audio.filter);
            editor.putBoolean(PREF_FILTERS, audio.filters);
            editor.putBoolean(PREF_DOWNSAMPLE, audio.downsample);
            editor.putBoolean(PREF_MULTIPLE, audio.multiple);
            editor.putBoolean(PREF_SCREEN, audio.screen);
            editor.putBoolean(PREF_STROBE, audio.strobe);
            editor.putBoolean(PREF_ZOOM, audio.zoom);
        }

        editor.apply();
    }

    // Get preferences
    void getPreferences()
    {
        // Get preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set preferences
        dark = preferences.getBoolean(PREF_DARK, false);

        if (audio != null)
        {
            audio.input = Integer.parseInt(preferences.getString(PREF_INPUT, "0"));
            audio.reference = preferences.getInt(PREF_REFERENCE, 440);
            audio.transpose = Integer.parseInt(preferences.getString(PREF_TRANSPOSE, "0"));

            audio.fund = preferences.getBoolean(PREF_FUND, false);
            audio.filter = preferences.getBoolean(PREF_FILTER, false);
            audio.filters = preferences.getBoolean(PREF_FILTERS, false);
            audio.downsample = preferences.getBoolean(PREF_DOWNSAMPLE, false);
            audio.multiple = preferences.getBoolean(PREF_MULTIPLE, false);
            audio.screen = preferences.getBoolean(PREF_SCREEN, false);
            audio.strobe = preferences.getBoolean(PREF_STROBE, false);
            audio.zoom = preferences.getBoolean(PREF_ZOOM, true);

            // Note filter
            Set<String> notes = preferences.getStringSet(PREF_NOTE, null);
            if (notes != null)
            {
                for (int index = 0; index < audio.noteFilter.length; index++)
                    audio.noteFilter[index] = false;

                for (String note : notes)
                {
                    int index = Integer.parseInt(note);
                    audio.noteFilter[index] = true;
                }
            }

            // Octave filter
            Set<String> octaves = preferences.getStringSet(PREF_OCTAVE, null);
            if (octaves != null)
            {
                for (int index = 0; index < audio.octaveFilter.length; index++)
                    audio.octaveFilter[index] = false;

                for (String octave : octaves)
                {
                    int index = Integer.parseInt(octave);
                    audio.octaveFilter[index] = true;
                }
            }

            // Check screen
            if (audio.screen)
            {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else
            {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        // Check for strobe before setting colours
        if (strobe != null)
        {
            strobe.colour = Integer.parseInt(preferences.getString(PREF_COLOUR, "0"));

            if (strobe.colour == 3)
            {
                JSONArray custom;

                try
                {
                    custom = new JSONArray(preferences.getString(PREF_CUSTOM, null));
                    strobe.foreground = custom.getInt(0);
                    strobe.background = custom.getInt(1);
                }
                catch (Exception e)
                {
                }
            }

            // Ensure the view dimensions have been set
            if (strobe.width > 0 && strobe.height > 0) strobe.createShaders();
        }
    }

    // Show alert
    void showAlert(int title, int message)
    {
        // Create an alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title, message and button
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, (dialog, which) ->
        {
            // Dismiss dialog
            dialog.dismiss();
        });
        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show it
        dialog.show();
    }

    // Log2
    protected double log2(double d)
    {
        return Math.log(d) / Math.log(2.0);
    }

    @Override
    public void onAbc(String wav)
    {

        if (!wav.equals(wavPrevious))
        playAudio(wav);

        wavPrevious=wav;

    }

    // Audio
    protected class Audio implements Runnable
    {
        // Preferences
        protected int input;
        protected int transpose;

        protected boolean lock;
        protected boolean zoom;
        protected boolean fund;
        protected boolean filter;
        protected boolean screen;
        protected boolean strobe;
        protected boolean filters;
        protected boolean multiple;
        protected boolean downsample;

        protected boolean noteFilter[] = {
                true, true, true, true, true, true, true, true, true, true, true, true
        };

        protected boolean octaveFilter[] = {
                true, true, true, true, true, true, true, true, true
        };

        protected double reference;

        // Data
        protected Thread thread;
        protected double buffer[];
        protected short data[];
        protected int sample;

        // Output data
        protected double lower;
        protected double higher;
        protected double nearest;
        protected double frequency;
        protected double difference;
        protected double cents;
        protected double fps;

        protected int count;
        protected int note;

        // Private data
        private long timer;
        private int divisor = 1;

        private AudioRecord audioRecord;

        private static final int MAXIMA = 8;
        private static final int OVERSAMPLE = 16;
        private static final int SAMPLES = 16384;
        private static final int RANGE = SAMPLES * 7 / 16;
        private static final int STEP = SAMPLES / OVERSAMPLE;
        private static final int SIZE = 4096;

        private static final int OCTAVE = 12;
        private static final int C5_OFFSET = 57;
        private static final long TIMER_COUNT = 24;
        private static final double MIN = 0.5;

        private static final double G = 3.023332184e+01;
        private static final double K = 0.9338478249;

        private double xv[];
        private double yv[];

        private Complex x;

        protected float signal;

        protected Maxima maxima;

        protected double xa[];

        private double xp[];
        private double xf[];
        private double dx[];

        private double x2[];
        private double x3[];
        private double x4[];
        private double x5[];

        // Constructor
        protected Audio()
        {
            buffer = new double[SAMPLES];

            xv = new double[2];
            yv = new double[2];

            x = new Complex(SAMPLES);

            maxima = new Maxima(MAXIMA);

            xa = new double[RANGE];
            xp = new double[RANGE];
            xf = new double[RANGE];
            dx = new double[RANGE];

            x2 = new double[RANGE / 2];
            x3 = new double[RANGE / 3];
            x4 = new double[RANGE / 4];
            x5 = new double[RANGE / 5];
        }

        // Start audio
        protected void start()
        {
            // Start the thread
            thread = new Thread(this, "Audio");
            thread.start();
        }

        // Run
        @Override
        public void run()
        {
            processAudio();
        }

        // Stop
        protected void stop()
        {
            // Stop and release the audio recorder
            cleanUpAudioRecord();

            Thread t = thread;
            thread = null;

            // Wait for the thread to exit
            while (t != null && t.isAlive()) Thread.yield();
        }

        // Stop and release the audio recorder
        private void cleanUpAudioRecord()
        {
            if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
            {
                try
                {
                    if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                    {
                        audioRecord.stop();
                    }

                    audioRecord.release();
                }
                catch (Exception e)
                {
                }
            }
        }

        // Process Audio
        protected void processAudio()
        {
            // Sample rates to try
            Resources resources = getResources();

            int rates[] = resources.getIntArray(R.array.sample_rates);
            int divisors[] = resources.getIntArray(R.array.divisors);

            int size = 0;
            int state = 0;
            int index = 0;
            for (int rate : rates)
            {
                // Check sample rate
                size = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                // Loop if invalid sample rate
                if (size == AudioRecord.ERROR_BAD_VALUE)
                {
                    index++;
                    continue;
                }

                // Check valid input selected, or other error
                if (size == AudioRecord.ERROR)
                {
                    runOnUiThread(() -> showAlert(R.string.app_name, R.string.error_buffer));

                    thread = null;
                    return;
                }

                // Set divisor
                divisor = divisors[index];

                // Create the AudioRecord object
                try
                {
                    audioRecord = new AudioRecord(input, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, Math.max(size, SIZE * divisor));
                }

                // Exception
                catch (Exception e)
                {
                    runOnUiThread(() -> showAlert(R.string.app_name, R.string.error_init));

                    thread = null;
                    return;
                }

                // Check state
                state = audioRecord.getState();
                if (state != AudioRecord.STATE_INITIALIZED)
                {
                    audioRecord.release();
                    index++;
                    continue;
                }

                // Must be a valid sample rate
                sample = rate;
                break;
            }

            // Check valid sample rate
            if (size == AudioRecord.ERROR_BAD_VALUE)
            {
                runOnUiThread(() -> showAlert(R.string.app_name, R.string.error_buffer));

                thread = null;
                return;
            }

            // Check AudioRecord initialised
            if (state != AudioRecord.STATE_INITIALIZED)
            {
                runOnUiThread(() -> showAlert(R.string.app_name, R.string.error_init));

                audioRecord.release();
                thread = null;
                return;
            }

            // Calculate fps and expect
            fps = ((double) sample / divisor) / SAMPLES;
            final double expect = 2.0 * Math.PI * STEP / SAMPLES;

            // Create buffer for input data
            data = new short[STEP * divisor];

            // Start recording
            audioRecord.startRecording();

            // Max data
            double dmax = 0.0;

            // Continue until the thread is stopped
            while (thread != null)
            {
                // Read a buffer of data
                // NOTE: audioRecord.read(short[], int, int) can block
                // indefinitely, until audioRecord.stop() is called
                // from another thread
                size = audioRecord.read(data, 0, STEP * divisor);

                // Stop the thread if no data or error state
                if (size <= 0)
                {
                    thread = null;
                    break;
                }

                // If display not locked update scope
                if (scope != null && !lock) scope.postInvalidate();

                // Move the main data buffer up
                System.arraycopy(buffer, STEP, buffer, 0, SAMPLES - STEP);

                // Max signal
                double rm = 0;

                // Butterworth filter, 3dB/octave
                for (int i = 0; i < STEP; i++)
                {
                    xv[0] = xv[1];
                    xv[1] = data[i * divisor] / G;

                    yv[0] = yv[1];
                    yv[1] = (xv[0] + xv[1]) + (K * yv[0]);

                    // Choose filtered/unfiltered data
                    buffer[(SAMPLES - STEP) + i] = audio.filter ? yv[1] : data[i * divisor];

                    // Find root mean signal
                    double v = data[i * divisor] / 32768.0;
                    rm += v * v;
                }

                // Signal value
                rm /= STEP;
                signal = (float) Math.sqrt(rm);

                // Maximum value
                if (dmax < 4096.0) dmax = 4096.0;

                // Calculate normalising value
                double norm = dmax;

                dmax = 0.0;

                // Copy data to FFT input arrays for tuner
                for (int i = 0; i < SAMPLES; i++)
                {
                    // Find the magnitude
                    if (dmax < Math.abs(buffer[i])) dmax = Math.abs(buffer[i]);

                    // Calculate the window
                    double window = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * i / SAMPLES);

                    // Normalise and window the input data
                    x.r[i] = buffer[i] / norm * window;
                }

                // do FFT for tuner
                fftr(x);

                // Process FFT output for tuner
                for (int i = 1; i < RANGE; i++)
                {
                    double real = x.r[i];
                    double imag = x.i[i];

                    xa[i] = Math.hypot(real, imag);

                    // Do frequency calculation
                    double p = Math.atan2(imag, real);
                    double dp = xp[i] - p;

                    xp[i] = p;

                    // Calculate phase difference
                    dp -= i * expect;

                    int qpd = (int) (dp / Math.PI);

                    if (qpd >= 0) qpd += qpd & 1;

                    else qpd -= qpd & 1;

                    dp -= Math.PI * qpd;

                    // Calculate frequency difference
                    double df = OVERSAMPLE * dp / (2.0 * Math.PI);

                    // Calculate actual frequency from slot frequency plus
                    // frequency difference and correction value
                    xf[i] = i * fps + df * fps;

                    // Calculate differences for finding maxima
                    dx[i] = xa[i] - xa[i - 1];
                }

                // Downsample
                if (downsample)
                {
                    // x2 = xa << 2
                    for (int i = 0; i < RANGE / 2; i++)
                    {
                        x2[i] = 0.0;

                        for (int j = 0; j < 2; j++)
                            x2[i] += xa[(i * 2) + j] / 2.0;
                    }

                    // x3 = xa << 3
                    for (int i = 0; i < RANGE / 3; i++)
                    {
                        x3[i] = 0.0;

                        for (int j = 0; j < 3; j++)
                            x3[i] += xa[(i * 3) + j] / 3.0;
                    }

                    // x4 = xa << 4
                    for (int i = 0; i < RANGE / 4; i++)
                    {
                        x4[i] = 0.0;

                        for (int j = 0; j < 4; j++)
                            x2[i] += xa[(i * 4) + j] / 4.0;
                    }

                    // x5 = xa << 5
                    for (int i = 0; i < RANGE / 5; i++)
                    {
                        x5[i] = 0.0;

                        for (int j = 0; j < 5; j++)
                            x5[i] += xa[(i * 5) + j] / 5.0;
                    }

                    // Add downsamples
                    for (int i = 1; i < RANGE; i++)
                    {
                        if (i < RANGE / 2) xa[i] += x2[i];

                        if (i < RANGE / 3) xa[i] += x3[i];

                        if (i < RANGE / 4) xa[i] += x4[i];

                        if (i < RANGE / 5) xa[i] += x5[i];

                        // Recalculate differences
                        dx[i] = xa[i] - xa[i - 1];
                    }
                }

                // Maximum FFT output
                double max = 0.0;

                count = 0;
                int limit = RANGE - 1;

                // Find maximum value, and list of maxima
                for (int i = 1; i < limit; i++)
                {
                    // Cents relative to reference
                    double cf = -12.0 * log2(reference / xf[i]);

                    // Note number
                    int n = (int) (Math.round(cf) + C5_OFFSET);

                    // Don't use if negative
                    if (n < 0) continue;

                    // Check fundamental
                    if (fund && (count > 0) && ((n % OCTAVE) != (maxima.n[0] % OCTAVE))) continue;

                    if (filters)
                    {
                        // Get note and octave
                        int note = n % OCTAVE;
                        int octave = n / OCTAVE;

                        // Don't use if too high
                        if (octave >= octaveFilter.length) continue;

                        // Check the filters
                        if (!noteFilter[note] || !octaveFilter[octave]) continue;
                    }

                    // Find maximum value
                    if (xa[i] > max)
                    {
                        max = xa[i];
                        frequency = xf[i];
                    }

                    // If display not locked, find maxima and add to list
                    if (!lock && count < MAXIMA && xa[i] > MIN && xa[i] > (max / 4.0) && dx[i] > 0.0 && dx[i + 1] < 0.0)
                    {
                        maxima.f[count] = xf[i];

                        // Note number
                        maxima.n[count] = n;

                        // Reference note
                        maxima.r[count] = reference * Math.pow(2.0, Math.round(cf) / 12.0);

                        // Set limit to octave above
                        if (!downsample && (limit > i * 2)) limit = i * 2 - 1;

                        count++;
                    }
                }

                // Found flag
                boolean found = false;

                // Do the note and cents calculations
                if (max > MIN)
                {
                    found = true;

                    // Frequency
                    if (!downsample) frequency = maxima.f[0];

                    // Cents relative to reference
                    double cf = -12.0 * log2(reference / frequency);

                    // Don't count silly values
                    if (Double.isNaN(cf))
                    {
                        cf = 0.0;
                        found = false;
                    }

                    // Reference note
                    nearest = audio.reference * Math.pow(2.0, Math.round(cf) / 12.0);

                    // Lower and upper freq
                    lower = reference * Math.pow(2.0, (Math.round(cf) - 0.55) / 12.0);
                    higher = reference * Math.pow(2.0, (Math.round(cf) + 0.55) / 12.0);

                    // Note number
                    note = (int) Math.round(cf) + C5_OFFSET;

                    if (note < 0)
                    {
                        note = 0;
                        found = false;
                    }

                    // Find nearest maximum to reference note
                    double df = 1000.0;

                    for (int i = 0; i < count; i++)
                    {
                        if (Math.abs(maxima.f[i] - nearest) < df)
                        {
                            df = Math.abs(maxima.f[i] - nearest);
                            frequency = maxima.f[i];
                        }
                    }

                    // Cents relative to reference note
                    cents = -12.0 * log2(nearest / frequency) * 100.0;

                    // Ignore silly values
                    if (Double.isNaN(cents))
                    {
                        cents = 0.0;
                        found = false;
                    }

                    // Ignore if not within 50 cents of reference note
                    if (Math.abs(cents) > 50.0)
                    {
                        cents = 0.0;
                        found = false;
                    }

                    // Difference
                    difference = frequency - nearest;
                }

                // Found
                if (found)
                {
                    // If display not locked
                    if (!lock)
                    {
                        // Update spectrum
                        if (spectrum != null) spectrum.postInvalidate();

                        // Update display
                        if (display != null) display.postInvalidate();

                       sendMessage(cents);
                    }

                    // Reset count;
                    timer = 0;
                }
                else
                {
                    // If display not locked
                    if (!lock)
                    {
                        if (timer > TIMER_COUNT)
                        {
                            difference = 0.0;
                            frequency = 0.0;
                            nearest = 0.0;
                            higher = 0.0;
                            lower = 0.0;
                            cents = 0.0;
                            count = 0;
                            note = 0;

                            // Update display
                            if (display != null) display.postInvalidate();
                        }

                        // Update spectrum
                        if (spectrum != null) spectrum.postInvalidate();
                    }
                }

                timer++;
            }

            cleanUpAudioRecord();
        }

        // Real to complex FFT, ignores imaginary values in input array
        private void fftr(Complex a)
        {
            final int n = a.r.length;
            final double norm = Math.sqrt(1.0 / n);

            for (int i = 0, j = 0; i < n; i++)
            {
                if (j >= i)
                {
                    double tr = a.r[j] * norm;

                    a.r[j] = a.r[i] * norm;
                    a.i[j] = 0.0;

                    a.r[i] = tr;
                    a.i[i] = 0.0;
                }

                int m = n / 2;
                while (m >= 1 && j >= m)
                {
                    j -= m;
                    m /= 2;
                }
                j += m;
            }

            for (int mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax)
            {
                double delta = (Math.PI / mmax);
                for (int m = 0; m < mmax; m++)
                {
                    double w = m * delta;
                    double wr = Math.cos(w);
                    double wi = Math.sin(w);

                    for (int i = m; i < n; i += istep)
                    {
                        int j = i + mmax;
                        double tr = wr * a.r[j] - wi * a.i[j];
                        double ti = wr * a.i[j] + wi * a.r[j];
                        a.r[j] = a.r[i] - tr;
                        a.i[j] = a.i[i] - ti;
                        a.r[i] += tr;
                        a.i[i] += ti;
                    }
                }
            }
        }

        // Copy to clipboard
        protected void copyToClipboard()
        {
            String text = "";

            if (multiple)
            {
                for (int i = 0; i < count; i++)
                {
                    // Calculate cents
                    double cents = -12.0 * log2(maxima.r[i] / maxima.f[i]) * 100.0;
                    // Ignore silly values
                    if (Double.isNaN(cents)) continue;

                    text += String.format(Locale.getDefault(), "%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n", notes[(maxima.n[i] - transpose + OCTAVE) % OCTAVE], sharps[(maxima.n[i] - transpose + OCTAVE) % OCTAVE], (maxima.n[i] - transpose) / OCTAVE, cents, maxima.r[i], maxima.f[i], maxima.r[i] - maxima.f[i]);
                }

                if (count == 0)
                    text = String.format(Locale.getDefault(), "%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n", notes[(note - transpose + OCTAVE) % OCTAVE], sharps[(note - transpose + OCTAVE) % OCTAVE], (note - transpose) / OCTAVE, cents, nearest, frequency, difference);
            }
            else
                text = String.format(Locale.getDefault(), "%s%s%d\t%+5.2f\u00A2\t%4.2fHz\t%4.2fHz\t%+5.2fHz\n", notes[(note - transpose + OCTAVE) % OCTAVE], sharps[(note - transpose + OCTAVE) % OCTAVE], (note - transpose) / OCTAVE, cents, nearest, frequency, difference);

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            clipboard.setPrimaryClip(ClipData.newPlainText("Tuner clip", text));
        }
    }

    // These two objects replace arrays of structs in the C version
    // because initialising arrays of objects in Java is, IMHO, barmy

    // Complex
    private class Complex
    {
        double r[];
        double i[];

        private Complex(int l)
        {
            r = new double[l];
            i = new double[l];
        }
    }

    // Maximum
    protected class Maxima
    {
        double f[];
        double r[];
        int n[];

        protected Maxima(int l)
        {
            f = new double[l];
            r = new double[l];
            n = new int[l];
        }
    }

    static MediaPlayer mp;
    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;

    public void addMediaPlayer()
    {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        //  mp = MediaPlayer.create(this, R.raw.c);
// Set data source -
        Log.d("Taggg", "=package= " + getPackageName());

// Play audio

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                loaded = true;


                // Is the sound loaded already?
                if (loaded)
                {
                    soundPool.play(soundID, volume, volume, 1, 0, 1f);
                    Log.e("Test", "Played sound");
                }

            }
        });
      //  soundID = soundPool.load(this, R.raw.a1, 1);

    }

    static String filename = "android.resource://" + "com.dev.musictuner" + "/raw/c.mp3";

    public void playAudio(String wav)
    {

        wav = wav.replace("#", "hash").toLowerCase();

        int waveName=getWave(wav);
        

        soundID = soundPool.load(this,waveName, 1);

      /*  AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        // Is the sound loaded already?
        if (loaded)
        {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            Log.e("Test", "Played sound");
        }
*/
        // mp = MediaPlayer.create(this, R.raw.c);
        // Pause audio
        // mp.start();

    }

    private int getWave(String wav)
    {

        int wavValue=R.raw.a1;

        wavValue=wav.equals("a1")?R.raw.a1:wav.equals("a2")?R.raw.a2:wav.equals("a3")?R.raw.a3:wav.equals("a4")?R.raw.a4:
                wav.equals("a5")?R.raw.a5:wav.equals("a6")?R.raw.a6:wav.equals("ahash1")?R.raw.ahash1:wav.equals("ahash2")?R.raw.ahash2:
                        wav.equals("ahash3")?R.raw.ahash3:wav.equals("ahash4")?R.raw.ahash4:wav.equals("ahash5")?R.raw.ahash5:wav.equals("ahash6")?R.raw.ahash6:
                                String.valueOf(wav.charAt(0)).equals("a")?R.raw.a1:

                                        wav.equals("b1")?R.raw.b1:wav.equals("b2")?R.raw.b2:wav.equals("b3")?R.raw.b3:
                                        wav.equals("b4")?R.raw.b4:wav.equals("b5")?R.raw.b5:wav.equals("b6")?R.raw.b6:String.valueOf(wav.charAt(0)).equals("b")?R.raw.b1:

                                                wav.equals("c1")?R.raw.c1:wav.equals("c2")?R.raw.c2:wav.equals("c3")?R.raw.c3:wav.equals("c4")?R.raw.c4:wav.equals("c5")?R.raw.c5:
                                                        wav.equals("c6")?R.raw.c6:wav.equals("chash1")?R.raw.chash1:wav.equals("chash2")?R.raw.chash2:wav.equals("chash3")?R.raw.chash3:
                                                                wav.equals("chash4")?R.raw.chash4:wav.equals("chash5")?R.raw.chash5:wav.equals("chash6")?R.raw.chash6:String.valueOf(wav.charAt(0)).equals("c")?R.raw.c1:

                                                                        wav.equals("d1")?R.raw.d1:wav.equals("d2")?R.raw.d2:wav.equals("d3")?R.raw.d3:wav.equals("d4")?R.raw.d4:wav.equals("d5")?R.raw.d5:wav.equals("d6")?R.raw.d6:
                                                                                wav.equals("dhash1")?R.raw.dhash1:wav.equals("dhash2")?R.raw.dhash2:wav.equals("dhash3")?R.raw.dhash3:wav.equals("dhash4")?R.raw.dhash4:wav.equals("dhash5")?R.raw.dhash5:wav.equals("dhash6")?R.raw.dhash6:String.valueOf(wav.charAt(0)).equals("d")?R.raw.d1:

                                                                                        wav.equals("e1")?R.raw.e1:  wav.equals("e2")?R.raw.e2:  wav.equals("e3")?R.raw.e3:  wav.equals("e4")?R.raw.e4:  wav.equals("e5")?R.raw.e5:  wav.equals("e6")?R.raw.e6:String.valueOf(wav.charAt(0)).equals("e")?R.raw.e1:

                                                                                                wav.equals("f1")?R.raw.f1:  wav.equals("f2")?R.raw.f2:  wav.equals("f3")?R.raw.f3:  wav.equals("f4")?R.raw.f4:  wav.equals("f5")?R.raw.f5:  wav.equals("f6")?R.raw.f6:String.valueOf(wav.charAt(0)).equals("f")?R.raw.f1:
                                                                                                        wav.equals("fhash1")?R.raw.fhash1:wav.equals("fhash2")?R.raw.fhash2:wav.equals("fhash3")?R.raw.fhash3:wav.equals("fhash4")?R.raw.fhash4:wav.equals("fhash5")?R.raw.fhash5:wav.equals("fhash6")?R.raw.fhash6:


                                                                                                                wav.equals("g1")?R.raw.g1:  wav.equals("g2")?R.raw.g2:  wav.equals("g3")?R.raw.g3:  wav.equals("g4")?R.raw.g4:  wav.equals("g5")?R.raw.g5:  wav.equals("g6")?R.raw.g6:String.valueOf(wav.charAt(0)).equals("f")?R.raw.g1:
                                                                                                                        wav.equals("ghash1")?R.raw.ghash1:wav.equals("ghash2")?R.raw.ghash2:wav.equals("ghash3")?R.raw.ghash3:wav.equals("ghash4")?R.raw.ghash4:wav.equals("ghash5")?R.raw.ghash5:wav.equals("ghash6")?R.raw.ghash6:R.raw.a1;




        return wavValue;
    }

    public void stopAudio()
    {
        // Pause audio
        mp.pause();

// Reset mediaplayer
        mp.reset();

    }

    // Not be testing yet
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
