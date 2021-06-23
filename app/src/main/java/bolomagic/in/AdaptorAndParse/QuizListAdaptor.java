package bolomagic.in.AdaptorAndParse;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bolomagic.in.R;

public class QuizListAdaptor extends ArrayAdapter<QuizListParse> {

    public QuizListAdaptor(Context context, ArrayList<QuizListParse> quizListParseArrayList){
        super(context,0,quizListParseArrayList);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.quiz_list_view, viewGroup, false);
        }

        QuizListParse quizListParse = getItem(position);

        TextView textView1 = view.findViewById(R.id.textView1);
        textView1.setText(quizListParse.getQuizName());

        TextView textView2 = view.findViewById(R.id.textView2);
        textView2.setText("₹ "+quizListParse.getEntryFee());

        TextView textView3 = view.findViewById(R.id.textView3);
        String startTime = quizListParse.getQuizStartTime();
        String endTime = quizListParse.getQuizEndTime();
        textView3.setText(startTime+" - "+endTime);
        if (endTime.equals("Default")){
            textView3.setText(startTime);
        }

        TextView textView4 = view.findViewById(R.id.textView4);
        int joined = Integer.parseInt(quizListParse.getTotalJoined());
        int maxJoined = Integer.parseInt(quizListParse.getMaxJoined());
        textView4.setText(joined+"/"+maxJoined);
        if (joined == maxJoined){
            textView4.setTextColor(Color.RED);
        }

        TextView textView5 = view.findViewById(R.id.textView5);
        textView5.setText("Minimum: "+quizListParse.getMinimumJoin());
        textView5.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("!! Notice !!")
                .setMessage("If minimum "+quizListParse.getMinimumJoin()+" player(s) don't register in this quiz then your quiz will be canceled and entry will be refunded.")
                .show());

        Button button1 = view.findViewById(R.id.button1);
        if (quizListParse.getIsJoined().equals("true")){
            button1.setText("Joined");
            button1.setBackgroundResource(R.drawable.round_green_bg);
            button1.setOnClickListener(v -> {
                String message = "Loading results...";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }else {
            button1.setOnClickListener(v -> {
                String message = "Joining...";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }

        ImageView imageView1 = view.findViewById(R.id.imageView1);
        Picasso.get().load(quizListParse.getQuizType()).into(imageView1);

        return view;
    }

    @Override
    public int getViewTypeCount(){
        return getCount();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }
}
