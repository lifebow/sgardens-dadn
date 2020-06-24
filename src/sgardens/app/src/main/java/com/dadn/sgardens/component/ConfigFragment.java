package com.dadn.sgardens.component;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dadn.sgardens.MQTTHelper;
import com.dadn.sgardens.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ConfigFragment extends Fragment {
    MQTTHelper mqttHelper;
    NumberPicker np,np2;
    int counter = 0;
    int max=255;
    int max2=5000;
    int current1=0;
    int current2=0;
    int min=0;
    int timereset=10;
    Timer timer;
    Switch switch1,switch2,switchserver;
    SeekBar seek1,seek2;
    TextView light1,light2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        counter = 0;
        startMQTT();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.config_fragment, container, false);
        np=(NumberPicker) view.findViewById(R.id.number_picker1);
        np2=(NumberPicker) view.findViewById(R.id.number_picker2);
        seek1= (SeekBar) view.findViewById(R.id.seekBar1);
        seek2= (SeekBar) view.findViewById(R.id.seekBar2);
        light1=(TextView) view.findViewById(R.id.textView2);
        light2=(TextView) view.findViewById(R.id.textView8);
        switch1=(Switch) view.findViewById(R.id.switch1);
        switch2=(Switch) view.findViewById(R.id.switch2);
        switchserver=(Switch) view.findViewById(R.id.switchserver);
        light1.setText("off");
        light2.setText("off");
        seek1.setMax(max);
        seek2.setMax(max);
        int minValue = 0;
        int maxValue = 500;
        final int step = 10;
        String[] numberValues = new String[maxValue - minValue + 1];
        for (int i = 0; i <= maxValue - minValue; i++) {
            numberValues[i] = String.valueOf((minValue + i) * step);
        }

        np.setMinValue(minValue);
        np.setMaxValue(maxValue);

        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(numberValues);
        np2.setMinValue(minValue);
        np2.setMaxValue(maxValue);

        np2.setWrapSelectorWheel(false);
        np2.setDisplayedValues(numberValues);
        timer = new Timer();
//        numberPicker.setFormatter(getString(R.string.number_picker_formatter));
//        numberPicker.setFormatter(R.string.number_picker_formatter);
//        // Set selected text size
//
//// Set selected typeface
//        numberPicker.setSelectedTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));
//        numberPicker.setSelectedTypeface(getString(R.string.roboto_light), Typeface.NORMAL);
//        numberPicker.setSelectedTypeface(getString(R.string.roboto_light));
//        numberPicker.setSelectedTypeface(R.string.roboto_light, Typeface.NORMAL);
//        numberPicker.setSelectedTypeface(R.string.roboto_light);
//
//
//// Set text size
//        numberPicker.setTextSize(getResources().getDimension(R.dimen.text_size));
//        numberPicker.setTextSize(R.dimen.text_size);
//        // Set fading edge enabled
//        numberPicker.setFadingEdgeEnabled(true);
//
//// Set scroller enabled
//        numberPicker.setScrollerEnabled(true);
//
//// Set wrap selector wheel
//        numberPicker.setWrapSelectorWheel(true);
//
//// Set accessibility description enabled
//        numberPicker.setAccessibilityDescriptionEnabled(true);
//        numberPicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Value", "Click on current value");
//            }
//        });   timer = new Timer();
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timer.cancel();
                JsonArray value=new JsonArray();
                value.add(switch1.isChecked() ? "1" : "0");
                value.add(Integer.toString(np.getValue()*step));
                sendDataToMQTT("Speaker",value,"Topic/Speaker");
                timer = new Timer();
                timer.schedule(new Turnoff(),timereset*1000);
            }
        });
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                JsonArray value=new JsonArray();
                value.add(switch2.isChecked() ? "1" : "0");
                value.add(Integer.toString(np.getValue()*step));
                sendDataToMQTT("Speaker",value,"Topic/Speaker");
                timer.cancel();
                timer = new Timer();
                timer.schedule(new Turnoff(),timereset*1000);
            }
        });
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                current1=progress+min;
                light1.setText(""+current1);
                if(current1==0) light1.setText("off");
                boolean checked=current1==0?true:false;
                JsonArray value=new JsonArray();
                value.add(!checked ? "1" : "0");
                value.add(Integer.toString(current1));
                Log.d("Value", value.toString());
                sendDataToMQTT("LightD",value,"Topic/LightD");

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                current2=progress+min;
                light2.setText(""+current2);
                if(current2==0) light2.setText("off");
                boolean checked=current2==0?true:false;
                JsonArray value=new JsonArray();
                value.add(!checked ? "1" : "0");
                value.add(Integer.toString(current2));
                Log.d("Value", value.toString());
                sendDataToMQTT("LightD",value,"Topic/LightD");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JsonArray value=new JsonArray();
                value.add(isChecked ? "1" : "0");
                value.add(Integer.toString(np.getValue()*step));
                sendDataToMQTT("Speaker",value,"Topic/Speaker");
                timer.cancel();
                timer = new Timer();
                timer.schedule(new Turnoff(),timereset*1000);
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JsonArray value=new JsonArray();
                value.add(isChecked ? "1" : "0");
                value.add(Integer.toString(np2.getValue()*step));

                sendDataToMQTT("Speaker",value,"Topic/Speaker");
                timer.cancel();
                timer = new Timer();
                timer.schedule(new Turnoff(),timereset*1000);
            }
        });
        switchserver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //do something with server
            }
        });
        return view;
    }
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment with the ProductGrid theme
//        View view = inflater.inflate(R.layout.config_fragment, container, false);
//        numberPicker=(NumberPicker) view.findViewById(R.id.number_picker1);
//        final SeekBar seek1= (SeekBar) view.findViewById(R.id.seekBar1);
//        seek1.setMax(max1);
//        final TextView textView2=(TextView) view.findViewById(R.id.textView2);
//        textView2.setText(""+current1);
////       set value numberpicker;
//        numberPicker.setMaxValue(5000);
//        numberPicker.setMinValue(0);
//        numberPicker.setValue(2500);
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Log.d("TAG", String.format( "oldVal: %d, newVal: %d", oldVal, newVal));
//            }
//        });
//
//        final SeekBar seek2= (SeekBar) view.findViewById(R.id.seekBar3);
//        seek2.setMax(max2);
//        final TextView textView4=(TextView) view.findViewById(R.id.textView6);
//        textView4.setText(""+current2);
//        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                current2=progress+min;
//                textView4.setText(""+current2);
//                if(current2==0) textView4.setText("off");
//                boolean checked=current2==0?true:false;
//                JsonArray value=new JsonArray();
//                value.add(!checked ? "1" : "0");
//                value.add(Integer.toString(current2));
//                sendDataToMQTT("Speaker",value,"Topic/Speaker");
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        return view;
//    }
    class Turnoff extends TimerTask{
    @Override
    public void run() {
        JsonArray value=new JsonArray();
        value.add("0");
        value.add("0");
        sendDataToMQTT("Speaker",value,"Topic/Speaker");
    }
}
    private void sendDataToMQTT(String ID, JsonArray value, String TS){

        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(1);
        msg.setRetained(true);
        JsonArray jsonArray= new JsonArray();
        JsonObject data=new JsonObject();
        data.addProperty("device_id",ID);
        data.add("values",value);
        jsonArray.add(data);
        byte[] b = jsonArray.toString().getBytes(Charset.forName("UTF-8"));
        Log.d("Value", jsonArray.toString());
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(TS, msg);

        }catch (MqttException e) {
        }
    }
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getActivity().getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

}
